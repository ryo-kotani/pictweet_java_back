package in.tech_camp.pictweet.java2023;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import in.tech_camp.pictweet.CommentEntity;
import in.tech_camp.pictweet.CommentForm;
import in.tech_camp.pictweet.CommentRepository;
import in.tech_camp.pictweet.TweetController;
import in.tech_camp.pictweet.TweetEntity;
import in.tech_camp.pictweet.TweetRepository;

@ExtendWith(MockitoExtension.class)
public class TweetControllerUnitTest {
    @Mock
    private TweetRepository tweetRepository;

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private TweetController tweetController = new TweetController();

    @Test
    public void ツイート詳細テスト() {
        TweetEntity expectedTweet = new TweetEntity();

        List<CommentEntity> expectedComments = new ArrayList<>();

        CommentEntity comment1 = new CommentEntity();
        comment1.setId(1);
        comment1.setText("This is a comment.");
        expectedComments.add(comment1);

        when(tweetRepository.findById(any(Integer.class))).thenReturn(Optional.of(expectedTweet));
        when(commentRepository.findByTweet_id(any(Integer.class))).thenReturn(expectedComments);

        Model model = new ExtendedModelMap();
        String result = tweetController.showTweetDetail(1, new CommentForm(), model);

        assertThat(result, is("tweets/detail"));
        assertThat(model.getAttribute("tweet"), is(expectedTweet));
        assertThat(model.getAttribute("comments"), is(expectedComments));
    }

}
