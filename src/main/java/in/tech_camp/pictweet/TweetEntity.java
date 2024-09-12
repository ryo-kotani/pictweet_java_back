package in.tech_camp.pictweet;
// import lombok.AllArgsConstructor; 削除
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.util.List;


@Data
@Table(name = "tweets")
@Entity
public class TweetEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String text;

    private String image;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    //cascade→親のIssueEntityが削除された時に、関連する子CommentEntityも自動的に削除
    @ToString.Exclude
    @OneToMany(mappedBy = "tweet",cascade = CascadeType.REMOVE,fetch = FetchType.EAGER)
    private List<CommentEntity> comments;
}