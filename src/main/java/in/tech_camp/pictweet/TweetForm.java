package in.tech_camp.pictweet;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TweetForm {
    @NotBlank(message = "Text can't be blank", groups = ValidGroup1.class)
    private String text;

    @NotBlank(message = "Image can't be blank", groups = ValidGroup1.class)
    private String image;
}