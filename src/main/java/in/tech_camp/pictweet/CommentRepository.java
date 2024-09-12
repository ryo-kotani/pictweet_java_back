package in.tech_camp.pictweet;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<CommentEntity, Integer> {
    List<CommentEntity> findByTweet_id(Integer tweetId);
}