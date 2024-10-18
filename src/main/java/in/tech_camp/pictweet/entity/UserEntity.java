package in.tech_camp.pictweet.entity;


import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.ToString;

@Data
public class UserEntity {
    private Integer id;
    private String nickname;
    private String email;
    private String password;

    @ToString.Exclude
    private List<TweetEntity> tweets;

    @ToString.Exclude
    private List<CommentEntity> comments = new ArrayList<>();

}