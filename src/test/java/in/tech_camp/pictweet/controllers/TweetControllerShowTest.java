package in.tech_camp.pictweet.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

import in.tech_camp.pictweet.CommentEntity;
import in.tech_camp.pictweet.CommentForm;
import in.tech_camp.pictweet.CommentRepository;
import in.tech_camp.pictweet.TweetController;
import in.tech_camp.pictweet.TweetEntity;
import in.tech_camp.pictweet.TweetRepository;

@ExtendWith(MockitoExtension.class)
public class TweetControllerShowTest {

    @Mock
    private TweetRepository tweetRepository;

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private TweetController tweetController;

    private Model model;

    @BeforeEach
    public void setUp() {
        model = new ExtendedModelMap();
    }

    @Test
    public void showアクションにリクエストすると正常にレスポンスが返ってくる() throws Exception {
        TweetEntity tweet = new TweetEntity();
        tweet.setId(1);
        tweet.setText("ツイート1");
        tweet.setImage("image1.jpg");

        List<CommentEntity> expectedComments = new ArrayList<>();
        when(tweetRepository.findById(anyInt())).thenReturn(Optional.of(tweet));
        when(commentRepository.findByTweet(tweet)).thenReturn(expectedComments);

        String result = tweetController.showTweetDetail(1, new CommentForm(), model);
        assertThat(result, is("tweets/detail"));
    }

    @Test
    public void showアクションにリクエストするとレスポンスに投稿済みのツイートのテキストが存在する() throws Exception {
        TweetEntity tweet = new TweetEntity();
        tweet.setId(1);
        tweet.setText("ツイート1");
        tweet.setImage("image1.jpg");

        List<CommentEntity> expectedComments = new ArrayList<>();
        when(tweetRepository.findById(anyInt())).thenReturn(Optional.of(tweet));
        when(commentRepository.findByTweet(tweet)).thenReturn(expectedComments);

        tweetController.showTweetDetail(1, new CommentForm(), model);

        assertThat(model.getAttribute("tweet"), is(tweet));
        assertThat(((TweetEntity) model.getAttribute("tweet")).getText(), is("ツイート1"));
    }

    @Test
    public void showアクションにリクエストするとレスポンスに投稿済みのツイートの画像URLが存在する() throws Exception {
        TweetEntity tweet = new TweetEntity();
        tweet.setId(1);
        tweet.setText("ツイート1");
        tweet.setImage("image1.jpg");

        List<CommentEntity> expectedComments = new ArrayList<>();
        when(tweetRepository.findById(anyInt())).thenReturn(Optional.of(tweet));
        when(commentRepository.findByTweet(tweet)).thenReturn(expectedComments);

        tweetController.showTweetDetail(1, new CommentForm(), model);

        assertThat(model.getAttribute("tweet"), is(tweet));
        assertThat(((TweetEntity) model.getAttribute("tweet")).getImage(), is("image1.jpg"));
    }

    @Test
    public void showアクションにリクエストするとレスポンスにコメント一覧表示部分が存在する() throws Exception {
        TweetEntity tweet = new TweetEntity();
        tweet.setId(1);
        tweet.setText("ツイート1");
        tweet.setImage("image1.jpg");

        List<CommentEntity> expectedComments = new ArrayList<>();
        CommentEntity comment1 = new CommentEntity();
        comment1.setId(1);
        comment1.setText("This is a comment.");
        expectedComments.add(comment1);

        when(tweetRepository.findById(anyInt())).thenReturn(Optional.of(tweet));
        when(commentRepository.findByTweet(tweet)).thenReturn(expectedComments);

        tweetController.showTweetDetail(1, new CommentForm(), model);

        assertThat(model.getAttribute("comments"), is(expectedComments));
        assertThat(((List<CommentEntity>) model.getAttribute("comments")).get(0).getText(), is("This is a comment."));
    }
}
