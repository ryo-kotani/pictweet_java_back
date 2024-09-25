package in.tech_camp.pictweet;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.One;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;


@Mapper
public interface CommentRepository {
    @Select("SELECT * FROM comments WHERE id = #{id}")
    @Results(value = {
        @Result(property = "id", column = "id"),
        @Result(property = "text", column = "text"),
        @Result(property = "user", column = "user_id",
                one = @One(select = "in.tech_camp.pictweet.UserRepository.findById")),
        @Result(property = "tweet", column = "tweet_id",
                one = @One(select = "in.tech_camp.pictweet.TweetRepository.findById"))
    })
    CommentEntity findById(Integer id);

    @Select("SELECT * FROM comments WHERE tweet_id = #{tweetId}")
    List<CommentEntity> findByTweetId(Integer tweetId);

    @Select("SELECT * FROM comments WHERE user_id = #{userId}")
    List<CommentEntity> findByUserId(Integer userId);

    @Insert("INSERT INTO comments (text, user_id, tweet_id) VALUES (#{text}, #{user.id}, #{tweet.id})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(CommentEntity comment);
}