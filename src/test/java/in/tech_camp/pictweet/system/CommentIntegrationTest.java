package in.tech_camp.pictweet.system;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import in.tech_camp.pictweet.PicTweetApplication;
import in.tech_camp.pictweet.entity.UserEntity;
import in.tech_camp.pictweet.factory.UserFormFactory;
import in.tech_camp.pictweet.form.UserForm;
import in.tech_camp.pictweet.service.UserService;

@ActiveProfiles("test")
@SpringBootTest(classes = PicTweetApplication.class)
@AutoConfigureMockMvc
public class CommentIntegrationTest {
  private UserForm userForm;
  private UserEntity userEntity;

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
}