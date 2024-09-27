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
    @Select("SELECT * FROM comments WHERE tweet_id = #{tweetId}")
    @Results(value = {
        @Result(property = "user", column = "user_id",
                one = @One(select = "in.tech_camp.pictweet.UserRepository.findById"))
    })
    List<CommentEntity> findByTweetId(Integer tweetId);

    @Insert("INSERT INTO comments (text, user_id, tweet_id) VALUES (#{text}, #{user.id}, #{tweet.id})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(CommentEntity comment);
}