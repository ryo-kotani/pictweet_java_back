package in.tech_camp.pictweet.system;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import in.tech_camp.pictweet.PicTweetApplication;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import in.tech_camp.pictweet.entity.UserEntity;
import in.tech_camp.pictweet.repository.UserRepository;

@ActiveProfiles("test")
@SpringBootTest(classes = PicTweetApplication.class)
@AutoConfigureMockMvc
public class UserRegistrationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Test
    void
    正しい情報を入力すればユーザー新規登録ができてトップページに移動する() throws Exception {
        // トップページにアクセス
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("tweets/index")) // トップページのビューを確認
                // トップページにサインアップページへ遷移するボタンが存在することを確認
                .andExpect(content().string(org.hamcrest.Matchers.containsString("新規登録"))); // サインアップボタンが存在するかチェック

        // 新規登録ページにアクセス
        mockMvc.perform(get("/registerForm"))
                .andExpect(status().isOk())
                .andExpect(view().name("users/register"));

        // 新規登録情報を送信
        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("nickname", "太郎")
                .param("email", "test@example.com")
                .param("password", "testPassword")
                .param("passwordConfirmation", "testPassword")
                .with(csrf())) // CSRFトークンを含める
                .andExpect(redirectedUrl("/"))  // トップページにリダイレクトされることを確認
                .andExpect(status().isFound());

        // 新規登録に成功するとユーザーモデルのカウントが1上がる
        List<UserEntity> users = userRepository.findAll();
        Integer count = users.size();
        assertEquals(1, count);
    }

    @Test
    void 誤った情報ではユーザー新規登録ができずに新規登録ページへ戻ってくる() throws Exception {
        // トップページにアクセス
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("tweets/index")) // トップページのビューを確認
                // トップページにサインアップページへ遷移するボタンが存在することを確認
                .andExpect(content().string(org.hamcrest.Matchers.containsString("新規登録"))); // サインアップボタンが存在するかチェック
        // 新規登録ページにアクセス
        mockMvc.perform(get("/registerForm"))
                .andExpect(status().isOk())
                .andExpect(view().name("users/register"));

        // 誤った情報を使って新規登録を試みる
        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("nickname", "")
                .param("email", "test@example.com")
                .param("password", "testPassword")
                .param("passwordConfirmation", "testPassword")
                .with(csrf())) // CSRFトークンを含める
                .andExpect(status().isOk()) // 新規登録ページが表示されることを確認
                .andExpect(view().name("users/register")); // 新規登録ページに戻ることを確認

        // 新規登録に失敗したらユーザーモデルのカウントは上がらない
        List<UserEntity> users = userRepository.findAll();
        Integer count = users.size();
        assertEquals(1, count);
    }
}
