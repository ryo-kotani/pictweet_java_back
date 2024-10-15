package in.tech_camp.pictweet.form;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    @Test
    public void nicknameが空では登録できない () {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        UserForm userForm = new UserForm();
        userForm.setNickname(""); // 空のニックネーム
        userForm.setEmail("test@example.com");
        userForm.setPassword("password");
        userForm.setPasswordConfirmation("password");

        Set<ConstraintViolation<UserForm>> violations = validator.validate(userForm, ValidGroup1.class);

        assertEquals(1, violations.size());
        assertEquals("Nickname can't be blank", violations.iterator().next().getMessage());
    }
}
