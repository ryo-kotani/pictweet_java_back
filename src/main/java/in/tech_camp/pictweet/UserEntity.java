package in.tech_camp.pictweet;


import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.ToString;

@Entity
@Table(name="users")
@Data
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nickname;

    @Column(unique = true)
    private String email;

    private String password;

    @ToString.Exclude
    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private List<TweetEntity> tweets;

    @ToString.Exclude
    @OneToMany(mappedBy = "user",fetch = FetchType.EAGER)
    private List<CommentEntity> comments;

}