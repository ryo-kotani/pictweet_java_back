package in.tech_camp.pictweet.form;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import in.tech_camp.pictweet.UserForm;
import in.tech_camp.pictweet.ValidGroup1;
import jakarta.validation.ConstraintViolation;

@ActiveProfiles("test")
@SpringBootTest
public class UserFormUnitTest {
  @Test
  public void nicknameが空では登録できない () {
      userForm.setNickname(""); // 空のニックネーム
      Set<ConstraintViolation<UserForm>> violations = validator.validate(userForm, ValidGroup1.class);
      assertEquals(1, violations.size());
      assertEquals("Nickname can't be blank", violations.iterator().next().getMessage());
  }
}
