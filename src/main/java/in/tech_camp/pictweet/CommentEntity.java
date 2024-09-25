package in.tech_camp.pictweet;

import lombok.Data;


@Data
public class CommentEntity {
    private Integer id;
    private String text;

    private UserEntity user;
    private TweetEntity tweet;
}
