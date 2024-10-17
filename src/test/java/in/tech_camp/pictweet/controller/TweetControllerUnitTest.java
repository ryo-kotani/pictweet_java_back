package in.tech_camp.pictweet.controller;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import in.tech_camp.pictweet.entity.TweetEntity;
import in.tech_camp.pictweet.form.SearchForm;
import in.tech_camp.pictweet.repository.TweetRepository;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class TweetControllerUnitTest {

    @Mock
    private TweetRepository tweetRepository;

    @InjectMocks
    private TweetController tweetController;

    @Test
    public void 投稿一覧機能にリクエストすると正常にレスポンスが返ってくる() {
        // モックの設定（等）
        SearchForm form = new SearchForm();
        Model model = new ExtendedModelMap();

        String result = tweetController.showTweets(model, form);

        assertThat(result, is("tweets/index"));
    }

    @Test
    public void 投稿一覧機能にリクエストするとレスポンスに投稿済みのツイートのテキストが存在する() {
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
}
