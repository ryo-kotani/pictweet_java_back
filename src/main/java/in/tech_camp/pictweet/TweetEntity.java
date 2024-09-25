package in.tech_camp.pictweet;
import java.util.List;

import lombok.Data;


@Data
public class TweetEntity {
    private Integer id;

    private String text;

    private String image;

    private UserEntity user;            // ユーザーのプロパティ
    private List<CommentEntity> comments; // コメントのプロパティ
}