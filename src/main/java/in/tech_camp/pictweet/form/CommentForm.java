package in.tech_camp.pictweet.form;

import in.tech_camp.pictweet.validation.ValidGroup1;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CommentForm {
    @NotBlank(message = "Comment can't be blank", groups = ValidGroup1.class)
    private String text;
}
