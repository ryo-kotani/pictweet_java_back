package in.tech_camp.pictweet.system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import in.tech_camp.pictweet.PicTweetApplication;
import in.tech_camp.pictweet.CommentEntity;
import in.tech_camp.pictweet.CommentRepository;
import in.tech_camp.pictweet.TweetEntity;
import in.tech_camp.pictweet.TweetRepository;
import in.tech_camp.pictweet.UserEntity;
import in.tech_camp.pictweet.UserForm;
import in.tech_camp.pictweet.UserService;
import in.tech_camp.pictweet.factories.UserFormFactory;
import in.tech_camp.pictweet.support.LoginSupport;
import jakarta.servlet.http.HttpSession;

@ActiveProfiles("test")
@SpringBootTest(classes = PicTweetApplication.class)
@AutoConfigureMockMvc
public class CommentIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  private UserForm userForm;
  private UserEntity userEntity;

  private MockMultipartFile imageFile;

  @Autowired
  private UserService userService;

  @Autowired
  private TweetRepository tweetRepository;

  @Autowired
  private CommentRepository commentRepository;

  @BeforeEach
  public void setup() {
      // テスト用のユーザー情報をセットアップ
      userForm = UserFormFactory.createUser();
      userEntity = new UserEntity();
      userEntity.setEmail(userForm.getEmail());
      userEntity.setNickname(userForm.getNickname()); // 必要であれば他のフィールドもセット
      userEntity.setPassword(userForm.getPassword());

      userService.registerNewUser(userEntity);

      // 画像ファイルの作成
      imageFile = new MockMultipartFile(
      "image", // フィールド名
      "test_image.png", // ファイル名
      "image/png", // コンテンツタイプ
      new byte[] {} // バイナリデータの配列（ここでは空の配列にしています）
      );
  }
  @Test
  public void ログインしたユーザーはツイート詳細ページでコメント投稿できる() throws Exception {
      // ユーザーがログインする
      HttpSession session = LoginSupport.login(mockMvc, userForm);

      // ツイートを投稿
      String tweetText = "テストツイート詳細";
      mockMvc.perform(multipart("/tweets")
              .file(imageFile)
              .param("text", tweetText)
              .with(csrf())
              .session((MockHttpSession) session))
              .andExpect(status().isFound())
              .andExpect(redirectedUrl("/"));

      // 投稿されたツイートを取得
      // ツイート1を削除
      List<TweetEntity> tweets = tweetRepository.findAll();
      Integer tweetId = tweets.get(0).getId();

      // 詳細ページに遷移
      mockMvc.perform(get("/tweets/{tweetId}", tweetId)
              .session((MockHttpSession) session))
              .andExpect(status().isOk())
              .andExpect(content().string(containsString(tweetText))); // ツイートの内容が含まれているか確認

      // コメントを送信
      String commentText = "これはテストコメントです";
      mockMvc.perform(post("/tweets/{tweetId}/comment", tweetId)
              .param("text", commentText) // コメントのテキストを設定
              .with(csrf())
              .session((MockHttpSession) session))
              .andExpect(status().isFound())
              .andExpect(redirectedUrl("/tweets/" + tweetId)); // 詳細ページにリダイレクトされることを確認

      // コメントモデルのカウントが1増加しているか確認
      List<CommentEntity>  comments = commentRepository.findByTweet_id(tweetId); // コメントのカウントを取得するメソッドが必要
      Integer commentCount = comments.size();
      assertEquals(1, commentCount); // 初回なのでカウントは1

      // 詳細ページに再度アクセスして、コメント内容を確認
      mockMvc.perform(get("/tweets/{tweetId}", tweetId)
              .session((MockHttpSession) session))
              .andExpect(status().isOk())
              .andExpect(content().string(containsString(commentText))); // コメント内容が含まれているか確認
  }

}
