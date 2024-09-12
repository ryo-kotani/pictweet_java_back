package in.tech_camp.pictweet.system;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
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
import in.tech_camp.pictweet.UserEntity;
import in.tech_camp.pictweet.UserForm;
import in.tech_camp.pictweet.UserService;
import in.tech_camp.pictweet.factories.UserFormFactory;
import in.tech_camp.pictweet.support.LoginSupport;
import jakarta.servlet.http.HttpSession;


@ActiveProfiles("test")
@SpringBootTest(classes = PicTweetApplication.class)
@AutoConfigureMockMvc
public class UserLoginIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private UserForm userForm;

    @Autowired
    private UserService userService;

    @BeforeEach
    public void setup() {
        // テスト用のユーザー情報をセットアップ
        userForm = UserFormFactory.createUser();
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(userForm.getEmail());
        userEntity.setNickname(userForm.getNickname()); // 必要であれば他のフィールドもセット
        userEntity.setPassword(userForm.getPassword());

        userService.registerNewUser(userEntity);
    }

    @Test
    public void 保存されているユーザーの情報と合致すればログインができる() throws Exception {
        // トップページにアクセスする
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("tweets/index"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("ログイン")));

        // ログインページへ遷移
        mockMvc.perform(get("/loginForm"))
                .andExpect(status().isOk())
                .andExpect(view().name("users/login"));

        // 正しいユーザー情報でログインを試みる
        HttpSession session = LoginSupport.login(mockMvc, userForm);
        // トップページにリダイレクトされることを確認し、ログアウトボタンが存在することを確認
        mockMvc.perform(get("/").session((MockHttpSession) session)) //session情報持たせないと次のmockMvc時にはログイン情報がなくなっている
                .andExpect(status().isOk())
                .andExpect(view().name("tweets/index"))
                .andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("新規登録"))))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("マイページ")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("logout-btn")));
    }

    @Test
    public void 保存されているユーザーの情報と合致しないとログインができない() throws Exception {
        // トップページにアクセスする
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("tweets/index"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("ログイン")));  // "ログイン" というテキストが含まれているか確認

        // ログインページへ遷移
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("users/login"));

        // 間違ったユーザー情報でログインを試みる
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("username", "test")
                        .param("password", "")
                        .with(csrf()))  // CSRFトークンを含める
                .andExpect(redirectedUrl("/login?error"))
                .andExpect(status().isFound());

        // 再度ログインページにリダイレクトされることを確認
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("users/login"));
    }
}
