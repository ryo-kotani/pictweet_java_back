package in.tech_camp.pictweet;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

@Entity
@Table(name = "comments")
@Data
public class CommentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String text;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.EAGER)
    private UserEntity user;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.EAGER)
    private TweetEntity tweet;
}
