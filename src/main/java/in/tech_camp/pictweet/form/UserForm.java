package in.tech_camp.pictweet.form;

import org.hibernate.validator.constraints.Length;
import org.springframework.validation.BindingResult;

import in.tech_camp.pictweet.validation.ValidGroup1;
import in.tech_camp.pictweet.validation.ValidGroup2;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserForm {
    @NotBlank(message = "Nickname can't be blank",groups = ValidGroup1.class)
    @Length(max = 6, message = "Nickname is too long (maximum is 6 characters)", groups = ValidGroup2.class)
    private String nickname;

    @NotBlank(message = "Email can't be blank",groups = ValidGroup1.class)
    @Email(message = "Email should be valid", groups = ValidGroup2.class)
    private String email;

    @NotBlank(message = "Password can't be blank",groups = ValidGroup1.class)
    @Length(min = 6, max = 128, message = "Password should be between 6 and 128 characters",groups = ValidGroup2.class)
    private String password;

    private String passwordConfirmation;

    public void validatePasswords(BindingResult result) {
        if (!password.equals(passwordConfirmation)) {
            result.rejectValue("passwordConfirmation", "error.user", "Password confirmation doesn't match Password");
        }
    }
}
