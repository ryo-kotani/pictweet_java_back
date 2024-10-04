package in.tech_camp.pictweet;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.One;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.mapping.FetchType;


@Mapper
public interface TweetRepository {
    @Select("SELECT * FROM tweets WHERE id = #{id}")
    @Results(value = {
        @Result(property = "user", column = "user_id",
                one = @One(select = "in.tech_camp.pictweet.UserRepository.findById", fetchType = FetchType.LAZY))
    })
    TweetEntity findById(Integer id);

    @Select("SELECT * FROM tweets WHERE text LIKE CONCAT('%', #{text}, '%')")
    @Results(value = {
        @Result(property = "user", column = "user_id",
                one = @One(select = "in.tech_camp.pictweet.UserRepository.findById", fetchType = FetchType.LAZY))
    })
    List<TweetEntity> findByTextContaining(String text);

    @Select("SELECT * FROM tweets WHERE user_id = #{id}")
    @Results(value = {
        @Result(property = "user", column = "user_id",
                one = @One(select = "in.tech_camp.pictweet.UserRepository.findById", fetchType = FetchType.LAZY))
    })
    List<TweetEntity> findByUserId(Integer id);

    @Select("SELECT t.*, u.* FROM tweets t LEFT JOIN users u ON t.user_id = u.id")
    @Results(value = {
        @Result(property = "user", column = "user_id",
                one = @One(select = "in.tech_camp.pictweet.UserRepository.findById", fetchType = FetchType.LAZY))
    })
    List<TweetEntity> findAll();

    @Insert("INSERT INTO tweets (text, image, user_id) VALUES (#{text}, #{image}, #{user.id})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(TweetEntity tweet);

    @Update("UPDATE tweets SET text = #{text}, image = #{image} WHERE id = #{id}")
    void update(TweetEntity tweet);

    @Delete("DELETE FROM tweets WHERE id = #{id}")
    void deleteById(Integer id);
}