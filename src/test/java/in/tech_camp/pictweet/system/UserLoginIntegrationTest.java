package in.tech_camp.pictweet.system;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import in.tech_camp.pictweet.PicTweetApplication;
import in.tech_camp.pictweet.entity.UserEntity;
import in.tech_camp.pictweet.factory.UserFormFactory;
import in.tech_camp.pictweet.form.UserForm;
import in.tech_camp.pictweet.service.UserService;


@ActiveProfiles("test")
@SpringBootTest(classes = PicTweetApplication.class)
@AutoConfigureMockMvc
public class UserLoginIntegrationTest {
  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private UserService userService;

  private UserForm userForm;

  @BeforeEach
  public void setup() {
    // テスト用のユーザー情報をセットアップ
    userForm = UserFormFactory.createUser();
    UserEntity userEntity = new UserEntity();
    userEntity.setEmail(userForm.getEmail());
    userEntity.setNickname(userForm.getNickname());
    userEntity.setPassword(userForm.getPassword());

    userService.registerNewUser(userEntity);
  }

  @Nested
  class ユーザーログインができるとき {
    @Test
    public void 保存されているユーザーの情報と合致すればログインができる() throws Exception {
      // トップページに移動する
      // トップページにログインページへ遷移するボタンがあることを確認する
      // ログインページに遷移する
      // 正しいユーザー情報を入力してログインを試みる
      // トップページへ遷移することを確認する
      // ログアウトボタンが表示されることを確認する
      // 新規登録ページへ遷移するボタンやログインページへ遷移するボタンが表示されていないことを確認
    }
  }

  @Nested
  class ユーザーログインができないとき{
    @Test
    public void 保存されているユーザーの情報と合致しないとログインができない() throws Exception {
      // トップページに移動する
      // トップページにログインページへ遷移するボタンがあることを確認する
      // ログインページに遷移する
      // 間違ったユーザー情報でログインを試みる
      // 再度ログインページにリダイレクトされることを確認する
    }
  }
}