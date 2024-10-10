package in.tech_camp.pictweet.form;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import in.tech_camp.pictweet.UserForm;
import in.tech_camp.pictweet.ValidGroup1;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

@ActiveProfiles("test")
@SpringBootTest
public class UserFormUnitTest {

    private UserForm userForm;
    private Validator validator;

    @BeforeEach
    public void setUp() {
        // UserFormのインスタンスを初期化
        userForm = new UserForm();

        // バリデータの初期化
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void nicknameが空では登録できない() {
        userForm.setNickname(""); // 空のニックネーム
        Set<ConstraintViolation<UserForm>> violations = validator.validate(userForm, ValidGroup1.class);
    }
}
