package in.tech_camp.pictweet.system;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import in.tech_camp.pictweet.PicTweetApplication;
import in.tech_camp.pictweet.entity.TweetEntity;
import in.tech_camp.pictweet.entity.UserEntity;
import in.tech_camp.pictweet.form.TweetForm;
import in.tech_camp.pictweet.form.UserForm;
import in.tech_camp.pictweet.repository.TweetRepository;
import in.tech_camp.pictweet.service.UserService;
import jakarta.servlet.http.HttpSession;
import in.tech_camp.pictweet.factory.TweetFormFactory;
import in.tech_camp.pictweet.factory.UserFormFactory;

@ActiveProfiles("test")
@SpringBootTest(classes = PicTweetApplication.class)
@AutoConfigureMockMvc
public class TweetIntegrationTest {

    private UserForm userForm;
    private UserEntity userEntity;

    //@AllArgsConstructorを使用してしまうとUserFormなど他のクラス変数にも影響が出てエラーになってしまうので@Autowiredを使用しております！
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


       MvcResult loginResult = mockMvc.perform(post("/login")
               .contentType(MediaType.APPLICATION_FORM_URLENCODED)
               .param("username", userForm.getEmail())
               .param("password", userForm.getPassword())
               .with(csrf()))
               .andExpect(status().isFound())
               .andExpect(redirectedUrl("/"))
               .andReturn();


       HttpSession session = loginResult.getRequest().getSession();


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


       MvcResult loginResult = mockMvc.perform(post("/login")
               .contentType(MediaType.APPLICATION_FORM_URLENCODED)
               .param("username", userForm.getEmail())
               .param("password", userForm.getPassword())
               .with(csrf()))
               .andExpect(status().isFound())
               .andExpect(redirectedUrl("/"))
               .andReturn();


       HttpSession session = loginResult.getRequest().getSession();


       // ツイートを投稿
       mockMvc.perform(post("/tweets")
               .param("text", originalTweetText)
               .param("image", "test.png")
               .with(csrf())
               .session((MockHttpSession) session))
               .andExpect(status().isFound())
               .andExpect(redirectedUrl("/"));




       List<TweetEntity> tweets = tweetRepository.findByUserId(userEntity.getId());


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
       tweetRepository.insert(tweet);


       // 他のユーザーが投稿したツイートの編集画面へ遷移しようとする
       MvcResult loginResult = mockMvc.perform(post("/login")
               .contentType(MediaType.APPLICATION_FORM_URLENCODED)
               .param("username", userForm.getEmail())
               .param("password", userForm.getPassword())
               .with(csrf()))
               .andExpect(status().isFound())
               .andExpect(redirectedUrl("/"))
               .andReturn();


       HttpSession session = loginResult.getRequest().getSession();


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
     MvcResult loginResult = mockMvc.perform(post("/login")
     .contentType(MediaType.APPLICATION_FORM_URLENCODED)
     .param("username", userForm.getEmail())
     .param("password", userForm.getPassword())
     .with(csrf()))
     .andExpect(status().isFound())
     .andExpect(redirectedUrl("/"))
     .andReturn();

     HttpSession session = loginResult.getRequest().getSession();

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

     List<TweetEntity> tweets = tweetRepository.findByUserId(userEntity.getId());


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


}