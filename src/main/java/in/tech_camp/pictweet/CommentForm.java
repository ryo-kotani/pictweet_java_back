package in.tech_camp.pictweet;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CommentForm {
    @NotBlank(message = "Comment can't be blank", groups = ValidGroup1.class)
    private String text;
}
