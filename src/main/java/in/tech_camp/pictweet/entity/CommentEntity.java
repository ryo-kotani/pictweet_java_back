package in.tech_camp.pictweet.entity;

import lombok.Data;
import lombok.ToString;


@Data
public class CommentEntity {
    private Integer id;
    private String text;

    @ToString.Exclude
    private UserEntity user;

    @ToString.Exclude
    private TweetEntity tweet;
}
