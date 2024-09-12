package in.tech_camp.pictweet;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TweetForm {
    @NotBlank(message = "Text can't be blank", groups = ValidGroup1.class)
    private String text;

    @NotNull(message = "Image can't be blank", groups = ValidGroup1.class)
    private MultipartFile image;

    // @NotNull(message = "User ID must not be null")
    // private Integer userId;
}