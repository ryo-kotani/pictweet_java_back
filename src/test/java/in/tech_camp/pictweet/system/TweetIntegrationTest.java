package in.tech_camp.pictweet.system;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import in.tech_camp.pictweet.PicTweetApplication;
import in.tech_camp.pictweet.TweetEntity;
import in.tech_camp.pictweet.TweetForm;
import in.tech_camp.pictweet.TweetRepository;
import in.tech_camp.pictweet.UserEntity;
import in.tech_camp.pictweet.UserForm;
import in.tech_camp.pictweet.UserService;
import in.tech_camp.pictweet.factories.TweetFormFactory;
import in.tech_camp.pictweet.factories.UserFormFactory;
import in.tech_camp.pictweet.support.LoginSupport;
import jakarta.servlet.http.HttpSession;

@ActiveProfiles("test")
@SpringBootTest(classes = PicTweetApplication.class)
@AutoConfigureMockMvc
public class TweetIntegrationTest {
    private UserForm userForm;
    private UserEntity userEntity;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private TweetRepository tweetRepository;

    @BeforeEach
    public void setup() {
        // テスト用のユーザー情報をセットアップ
        userForm = UserFormFactory.createUser();
        userEntity = new UserEntity();
        userEntity.setEmail(userForm.getEmail());
        userEntity.setNickname(userForm.getNickname());
        userEntity.setPassword(userForm.getPassword());

        userService.registerNewUser(userEntity);
    }

    @Test
    public void ログインしたユーザーは新規投稿できる() throws Exception {
        String tweetText = "テスト1";

        HttpSession session = LoginSupport.login(mockMvc, userForm);

        // 新規投稿ページへ遷移
        mockMvc.perform(get("/tweets/new").session((MockHttpSession) session))
                .andExpect(status().isOk())
                .andExpect(view().name("tweets/new"));

        // 新規投稿を行う
        mockMvc.perform(post("/tweets")
                .param("text", tweetText)
                .param("image", "test.png")
                .with(csrf())
                .session((MockHttpSession) session)) // セッション情報を指定
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/")); // リダイレクト先を確認

        // Tweetモデルのカウントを確認
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(tweetText))); // トップページにツイートがあることを確認
    }

    @Test
    public void ログインしていないと新規投稿ページに遷移できない() throws Exception {
        // ログインしていない状態で新規投稿ページにアクセスしようとする
        mockMvc.perform(get("/tweets/new"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/loginForm")); // トップページにリダイレクトされることを確認

        // トップページには新規投稿ボタンがないことを確認
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("新規投稿")))); // ボタンが含まれていないことを確認
    }

    @Test
    public void ログインしたユーザーは自分が投稿したツイートの編集ができる() throws Exception {
        // ユーザーがツイートを投稿する
        String originalTweetText = "テスト2";

        HttpSession session = LoginSupport.login(mockMvc, userForm);

        // ツイートを投稿
        mockMvc.perform(post("/tweets")
                .param("text", originalTweetText)
                .param("image", "test.png")
                .with(csrf())
                .session((MockHttpSession) session))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/"));


        List<TweetEntity> tweets = tweetRepository.findByUser(userEntity);

        // 編集リンクの確認
        mockMvc.perform(get("/").session((MockHttpSession) session)) // トップページに移動
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("編集"))); // 編集リンクがあることを確認

        // 編集ページに遷移
        mockMvc.perform(get("/tweets/{tweetId}/edit" ,tweets.get(0).getId())
                .session((MockHttpSession) session))
                .andExpect(status().isOk())
                .andExpect(view().name("tweets/edit")) // 編集ページに正しく遷移
                .andExpect(content().string(containsString(originalTweetText))); // フォームに元の内容が入っていることを確認

        // ツイート内容を編集
        String updatedTweetText = "更新後のツイート";
        mockMvc.perform(post("/tweets/{tweetId}/update" ,tweets.get(0).getId()) // 編集処理
                .param("text", updatedTweetText)
                .param("image", "test.png")
                .with(csrf())
                .session((MockHttpSession) session))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/"));

        // ツイートモデルのカウントが変わらないことの確認
        mockMvc.perform(get("/")) // 再度ツイートを確認
                .andExpect(content().string(containsString(updatedTweetText))) // 編集後の内容が含まれている
                .andExpect(content().string(not(containsString(originalTweetText)))); // 元の内容は含まれていない
    }

    @Test
    public void ログインしたユーザーは自分以外が投稿したツイートの編集画面には遷移できない() throws Exception {
        UserForm anotherUserForm = UserFormFactory.createUser();
        UserEntity anotherUser = new UserEntity();
        anotherUser.setEmail(anotherUserForm.getEmail());
        anotherUser.setNickname(anotherUserForm.getNickname());
        anotherUser.setPassword(anotherUserForm.getPassword());
        userService.registerNewUser(anotherUser);

        TweetForm tweetForm = TweetFormFactory.createTweet();
        TweetEntity tweet = new TweetEntity();
        tweet.setUser(anotherUser);
        tweet.setImage(tweetForm.getImage());
        tweet.setText(tweetForm.getText());
        tweetRepository.save(tweet);

        // 他のユーザーが投稿したツイートの編集画面へ遷移しようとする
        HttpSession session = LoginSupport.login(mockMvc, userForm);

        Integer tweetId = tweet.getId();

        mockMvc.perform(get("/tweets/{tweetId}/edit", tweetId)
                .session((MockHttpSession) session))
                .andExpect(redirectedUrl("/")); // アクセス拒否が期待される

        // リダイレクトを確認
        mockMvc.perform(get("/")) // トップページに移動
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("編集")))); // 編集リンクがないことを確認
    }

    @Test
    public void ログインしていないとツイートの編集画面には遷移できない() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("編集")))); // 編集リンクがないことを確認
    }

    //
    //ツイート削除機能
    //
    @Test
    public void ログインしたユーザーは自らが投稿したツイートの削除ができる() throws Exception {
      // ツイートを投稿したユーザーでログインする
      HttpSession session = LoginSupport.login(mockMvc, userForm);

      // ツイートを投稿
      String tweetText = "テスト3";
      mockMvc.perform(post("/tweets")
              .param("text", tweetText)
              .param("image", "test.png")
              .with(csrf())
              .session((MockHttpSession) session))
              .andExpect(status().isFound())
              .andExpect(redirectedUrl("/"));

      // 削除リンクが表示されることを確認
      mockMvc.perform(get("/").session((MockHttpSession) session))
              .andExpect(status().isOk())
              .andExpect(content().string(containsString("削除"))); // ツイートに「削除」リンクがあるかの確認



      List<TweetEntity> tweets = tweetRepository.findByUser(userEntity);

      // ツイートを削除
      List<TweetEntity> tweetsBeforeDeletion = tweetRepository.findAll();
      Integer initialCount = tweetsBeforeDeletion.size();

      mockMvc.perform(post("/tweets/{tweetId}/delete", userEntity.getId(), tweets.get(0).getId()) // ツイートIDを取得して削除
              .with(csrf())
              .session((MockHttpSession) session))
              .andExpect(status().isFound())
              .andExpect(redirectedUrl("/"));

      // レコードの数が1減ったことを確認
      List<TweetEntity> tweetsAfterDeletion = tweetRepository.findAll();
      Integer afterCount = tweetsAfterDeletion.size();
      assertEquals(initialCount - 1, afterCount);

      // トップページにツイートの内容が存在しないことを確認
      mockMvc.perform(get("/"))
              .andExpect(status().isOk())
              .andExpect(content().string(not(containsString(tweetText))));
    }

  @Test
  public void ログインしたユーザーは自分以外が投稿したツイートの削除ができない() throws Exception {
      // 別のユーザーがツイートを作成
      UserForm anotherUserForm = UserFormFactory.createUser();
      UserEntity anotherUser = new UserEntity();
      anotherUser.setEmail(anotherUserForm.getEmail());
      anotherUser.setNickname(anotherUserForm.getNickname());
      anotherUser.setPassword(anotherUserForm.getPassword());
      userService.registerNewUser(anotherUser);

      // ツイートの作成
      String tweetText = "テスト4";
      TweetEntity tweet = new TweetEntity();
      tweet.setUser(anotherUser);
      tweet.setText(tweetText);
      tweetRepository.save(tweet);

      // ツイートを投稿したユーザーでログイン
      HttpSession session = LoginSupport.login(mockMvc, userForm);

      // ツイートに「削除」リンクがないことを確認
      mockMvc.perform(get("/").session((MockHttpSession) session))
              .andExpect(status().isOk())
              .andExpect(content().string(not(containsString("削除")))); // ツイートには「削除」リンクがないことを確認

      // ログインしていない状態で削除ボタンがないことも確認
      mockMvc.perform(get("/"))
              .andExpect(status().isOk())
              .andExpect(content().string(not(containsString("削除")))); // トップページに「削除」リンクがないことを確認
  }


  @Test
  public void ログインしたユーザーはツイート詳細ページに遷移してコメント投稿欄が表示される() throws Exception {
      // ユーザーがログインする
      HttpSession session = LoginSupport.login(mockMvc, userForm);

      // ツイートを投稿
      String tweetText = "テスト5";
      mockMvc.perform(post("/tweets")
              .param("text", tweetText)
              .param("image", "test.png")
              .with(csrf())
              .session((MockHttpSession) session))
              .andExpect(status().isFound())
              .andExpect(redirectedUrl("/"));

      // 詳細ページに遷移するためのテスト
      List<TweetEntity> tweets = tweetRepository.findByUser(userEntity); // 自分のツイートを取得

      // 詳細ページに遷移
      mockMvc.perform(get("/tweets/{tweetId}", tweets.get(0).getId()) // ツイートIDで詳細ページに遷移
              .session((MockHttpSession) session))
              .andExpect(status().isOk())
              .andExpect(content().string(containsString(tweetText))) // ツイートの内容が含まれているか確認
              .andExpect(content().string(containsString("コメントする"))); // コメント用のフォームが存在することを確認
  }

  @Test
  public void ログインしていない状態でツイート詳細ページに遷移できるもののコメント投稿欄が表示されない() throws Exception {
      // ユーザーがログインする
      HttpSession session = LoginSupport.login(mockMvc, userForm);

      // ツイートを投稿
      String tweetText = "テスト6";
      mockMvc.perform(post("/tweets")
              .param("text", tweetText)
              .param("image", "test.png")
              .with(csrf())
              .session((MockHttpSession) session))
              .andExpect(status().isFound())
              .andExpect(redirectedUrl("/"));

      // トップページに移動する
      mockMvc.perform(get("/"))
              .andExpect(status().isOk())
              .andExpect(content().string(containsString("詳細"))); // ツイートに「詳細」へのリンクがあることを確認

      // 投稿されたツイートのリストを取得
      List<TweetEntity> tweets = tweetRepository.findAll(); // ツイートを取得

      // 詳細ページに遷移
      mockMvc.perform(get("/tweets/{tweetId}", tweets.get(0).getId())) // ツイートIDで詳細ページに遷移
              .andExpect(status().isOk())
              .andExpect(content().string(containsString(tweets.get(0).getText()))) // ツイート内容の確認
              .andExpect(content().string(not(containsString("コメントする")))) // フォームが存在しないことを確認
              .andExpect(content().string(containsString("コメントの投稿には新規登録/ログインが必要です"))); // メッセージが表示されていることを確認
  }


}
