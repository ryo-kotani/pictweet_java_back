package in.tech_camp.pictweet.models;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import in.tech_camp.pictweet.UserForm;
import in.tech_camp.pictweet.UserRepository;
import in.tech_camp.pictweet.ValidGroup1;
import in.tech_camp.pictweet.ValidGroup2;
import in.tech_camp.pictweet.factories.UserFormFactory;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

@ActiveProfiles("test")
@SpringBootTest
public class UserFormUnitTest {
    private UserForm userForm;
    private Validator validator;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        userForm = UserFormFactory.createUser();
    }

    @Test
    public void nicknameが空では登録できない () {
        userForm.setNickname(""); // 空のニックネーム
        Set<ConstraintViolation<UserForm>> violations = validator.validate(userForm, ValidGroup1.class);
        assertEquals(1, violations.size());
        assertEquals("Nickname can't be blank", violations.iterator().next().getMessage());
    }

    @Test
    public void nicknameが7文字以上では登録できない() {
        userForm.setNickname("TooLong"); // ニックネームが7文字以上
        Set<ConstraintViolation<UserForm>> violations = validator.validate(userForm, ValidGroup2.class);
        assertEquals(1, violations.size());
        assertEquals("Nickname is too long (maximum is 6 characters)", violations.iterator().next().getMessage());
    }

    @Test
    public void emailが空では登録できない() {
        userForm.setEmail(""); // 空のメール
        Set<ConstraintViolation<UserForm>> violations = validator.validate(userForm, ValidGroup1.class);
        assertEquals(1, violations.size());
        assertEquals("Email can't be blank", violations.iterator().next().getMessage());
    }

    // バリデーションで重複のチェックはないためテストは不要？
    // @Test
    // void 重複したemailが存在する場合は登録できない() {
    //     UserEntity user = new UserEntity();
    //     user.setNickname(userForm.getNickname());
    //     user.setEmail(userForm.getEmail());
    //     user.setPassword(userForm.getPassword());

    //     userRepository.saveAndFlush(user);

    //      // 異なるユーザーを作成（同じメールアドレスを使用）
    //     UserEntity anotherUser = new UserEntity();
    //     anotherUser.setNickname("test2");
    //     anotherUser.setEmail(userForm.getEmail()); // 同じメールアドレス
    //     anotherUser.setPassword("password");

    //     assertThatThrownBy(() -> userRepository.saveAndFlush(anotherUser))
    //         .isInstanceOf(DataIntegrityViolationException.class)
    //         .hasCauseInstanceOf(org.hibernate.exception.ConstraintViolationException.class);
    // }

    @Test
    public void emailは無効なメールでは登録できない() {
        userForm.setEmail("invalidEmail"); // 無効なメール
        Set<ConstraintViolation<UserForm>> violations = validator.validate(userForm, ValidGroup2.class);
        assertEquals(1, violations.size());
        assertEquals("Email should be valid", violations.iterator().next().getMessage());
    }

    @Test
    public void passwordが空では登録できない() {
        userForm.setPassword(""); // 空のパスワード

        Set<ConstraintViolation<UserForm>> violations = validator.validate(userForm, ValidGroup1.class);

        assertEquals(1, violations.size());
        assertEquals("Password can't be blank", violations.iterator().next().getMessage());
    }

    @Test
    public void passwordが5文字以下では登録できない() {
        userForm.setPassword("a"); // 長さが5未満
        Set<ConstraintViolation<UserForm>> violations = validator.validate(userForm, ValidGroup2.class);
        violations.forEach(violation -> System.out.println(violation.getMessage()));
        assertEquals(1, violations.size());
        assertEquals("Password should be between 6 and 128 characters", violations.iterator().next().getMessage());
    }

    @Test
    public void passwordが129文字以上では登録できない() {
        String password = "a".repeat(129);
        userForm.setPassword(password); // 長すぎるパスワード
        Set<ConstraintViolation<UserForm>> violations = validator.validate(userForm, ValidGroup2.class);
        violations.forEach(violation -> System.out.println(violation.getMessage()));
        assertEquals(1, violations.size());
        assertEquals("Password should be between 6 and 128 characters", violations.iterator().next().getMessage());
    }
}
