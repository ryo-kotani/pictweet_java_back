package in.tech_camp.pictweet.system;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import in.tech_camp.pictweet.PicTweetApplication;
import in.tech_camp.pictweet.entity.CommentEntity;
import in.tech_camp.pictweet.entity.TweetEntity;
import in.tech_camp.pictweet.entity.UserEntity;
import in.tech_camp.pictweet.factory.UserFormFactory;
import in.tech_camp.pictweet.form.UserForm;
import in.tech_camp.pictweet.service.UserService;
import jakarta.servlet.http.HttpSession;

@ActiveProfiles("test")
@SpringBootTest(classes = PicTweetApplication.class)
@AutoConfigureMockMvc
public class CommentIntegrationTest {
  private UserForm userForm;
  private UserEntity userEntity;

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
      userEntity.setNickname(userForm.getNickname()); // 必要であれば他のフィールドもセット
      userEntity.setPassword(userForm.getPassword());

      userService.registerNewUser(userEntity);
  }
   @Test
 public void ログインしたユーザーはツイート詳細ページでコメント投稿できる() throws Exception {
     // ユーザーがログインする
     MvcResult loginResult = mockMvc.perform(post("/login")
     .contentType(MediaType.APPLICATION_FORM_URLENCODED)
     .param("username", userForm.getEmail())
     .param("password", userForm.getPassword())
     .with(csrf()))
     .andExpect(status().isFound())
     .andExpect(redirectedUrl("/"))
     .andReturn();


     HttpSession session = loginResult.getRequest().getSession();
 }
}