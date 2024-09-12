package in.tech_camp.pictweet;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TweetRepository extends JpaRepository<TweetEntity, Integer> {
    List<TweetEntity> findByTextContaining(String text);
    List<TweetEntity> findByUser(UserEntity user);
}