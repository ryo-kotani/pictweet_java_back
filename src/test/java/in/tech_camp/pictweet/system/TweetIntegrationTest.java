package in.tech_camp.pictweet.system;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.context.ActiveProfiles;

import in.tech_camp.pictweet.PicTweetApplication;

import in.tech_camp.pictweet.entity.UserEntity;
import in.tech_camp.pictweet.form.UserForm;
import in.tech_camp.pictweet.service.UserService;
import in.tech_camp.pictweet.factory.UserFormFactory;
import lombok.AllArgsConstructor;

@ActiveProfiles("test")
@SpringBootTest(classes = PicTweetApplication.class)
@AutoConfigureMockMvc
@AllArgsConstructor
public class TweetIntegrationTest {
    private UserForm userForm;
    private UserEntity userEntity;

    private final UserService userService;

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

}