package in.tech_camp.pictweet;

import org.hibernate.validator.constraints.Length;

import jakarta.persistence.*;
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
    @Column(unique = true)
    private String email;

    @NotBlank(message = "Password can't be blank",groups = ValidGroup1.class)
    @Length(min = 6, max = 128, message = "Password should be between 6 and 128 characters",groups = ValidGroup2.class)
    private String password;
}
