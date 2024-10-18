package in.tech_camp.pictweet.entity;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;


@Data
public class TweetEntity {
    private Integer id;
    private String text;
    private String image;
    private Timestamp createdAt;
    private UserEntity user;
    private List<CommentEntity> comments = new ArrayList<>();
}