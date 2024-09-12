package in.tech_camp.pictweet.controllers;

import java.util.Arrays;
import java.util.List;
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
import static org.mockito.Mockito.when;

import in.tech_camp.pictweet.SearchForm;
import in.tech_camp.pictweet.TweetController;
import in.tech_camp.pictweet.TweetEntity;
import in.tech_camp.pictweet.TweetRepository;

@ExtendWith(MockitoExtension.class)
public class TweetControllerIndexTest {

    @Mock
    private TweetRepository tweetRepository;

    @InjectMocks
    private TweetController tweetController;

    private Model model;

    @BeforeEach
    public void setUp() {
        model = new ExtendedModelMap();
    }

    @Test
    public void indexアクションにリクエストすると正常にレスポンスが返ってくる() {
        TweetEntity tweet1 = new TweetEntity();
        tweet1.setId(1);
        tweet1.setText("ツイート1");
        tweet1.setImage("image1.jpg");

        TweetEntity tweet2 = new TweetEntity();
        tweet2.setId(2);
        tweet2.setText("ツイート2");
        tweet2.setImage("image2.jpg");

        List<TweetEntity> expectedTweetList = Arrays.asList(tweet1, tweet2);

        when(tweetRepository.findAll()).thenReturn(expectedTweetList);

        SearchForm form = new SearchForm();

        String result = tweetController.showTweets(model, form);

        assertThat(result, is("tweets/index"));
    }

    @Test
    public void indexアクションにリクエストするとレスポンスに投稿済みのツイートのテキストが存在する() {
        TweetEntity tweet1 = new TweetEntity();
        tweet1.setId(1);
        tweet1.setText("ツイート1");
        tweet1.setImage("image1.jpg");

        TweetEntity tweet2 = new TweetEntity();
        tweet2.setId(2);
        tweet2.setText("ツイート2");
        tweet2.setImage("image2.jpg");

        List<TweetEntity> expectedTweetList = Arrays.asList(tweet1, tweet2);

        when(tweetRepository.findAll()).thenReturn(expectedTweetList);

        SearchForm form = new SearchForm();

        tweetController.showTweets(model, form);

        List<TweetEntity> actualTweetList = (List<TweetEntity>) model.getAttribute("tweetList");
        assertThat(actualTweetList.get(0).getText(), is("ツイート1"));
        assertThat(actualTweetList.get(1).getText(), is("ツイート2"));
    }

    @Test
    public void indexアクションにリクエストするとレスポンスに投稿済みのツイートの画像URLが存在する() {
        TweetEntity tweet1 = new TweetEntity();
        tweet1.setId(1);
        tweet1.setText("ツイート1");
        tweet1.setImage("image1.jpg");

        TweetEntity tweet2 = new TweetEntity();
        tweet2.setId(2);
        tweet2.setText("ツイート2");
        tweet2.setImage("image2.jpg");

        List<TweetEntity> expectedTweetList = Arrays.asList(tweet1, tweet2);

        when(tweetRepository.findAll()).thenReturn(expectedTweetList);

        SearchForm form = new SearchForm();

        tweetController.showTweets(model, form);

        List<TweetEntity> actualTweetList = (List<TweetEntity>) model.getAttribute("tweetList");
        assertThat(actualTweetList.get(0).getImage(), is("image1.jpg"));
        assertThat(actualTweetList.get(1).getImage(), is("image2.jpg"));
    }

    @Test
    public void indexアクションにリクエストするとレスポンスに投稿検索フォームが存在する() {
        TweetEntity tweet1 = new TweetEntity();
        tweet1.setId(1);
        tweet1.setText("ツイート1");
        tweet1.setImage("image1.jpg");

        TweetEntity tweet2 = new TweetEntity();
        tweet2.setId(2);
        tweet2.setText("ツイート2");
        tweet2.setImage("image2.jpg");

        List<TweetEntity> expectedTweetList = Arrays.asList(tweet1, tweet2);

        when(tweetRepository.findAll()).thenReturn(expectedTweetList);

        SearchForm form = new SearchForm();

        tweetController.showTweets(model, form);

        assertThat(model.getAttribute("form"), is(form));
    }
}
