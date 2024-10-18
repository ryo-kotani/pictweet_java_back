package in.tech_camp.pictweet.system;

import static org.hamcrest.Matchers.containsString;
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

import in.tech_camp.pictweet.entity.UserEntity;
import in.tech_camp.pictweet.form.UserForm;
import in.tech_camp.pictweet.service.UserService;
import jakarta.servlet.http.HttpSession;
import in.tech_camp.pictweet.factory.UserFormFactory;
import lombok.AllArgsConstructor;

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
}