package in.tech_camp.pictweet.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.anyInt;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import in.tech_camp.pictweet.CommentEntity;
import in.tech_camp.pictweet.CommentForm;
import in.tech_camp.pictweet.CommentRepository;
import in.tech_camp.pictweet.SearchForm;
import in.tech_camp.pictweet.TweetController;
import in.tech_camp.pictweet.TweetEntity;
import in.tech_camp.pictweet.TweetRepository;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class TweetControllerUnitTest {

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

    @Nested
    class 投稿一覧機能のテスト {
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
    @Nested
    class 投稿詳細機能のテスト {
        @Test
        public void showアクションにリクエストすると正常にレスポンスが返ってくる() throws Exception {
            TweetEntity tweet = new TweetEntity();
            tweet.setId(1);
            tweet.setText("ツイート1");
            tweet.setImage("image1.jpg");

            List<CommentEntity> expectedComments = new ArrayList<>();
            when(tweetRepository.findById(anyInt())).thenReturn(tweet);
            when(commentRepository.findByTweetId(tweet.getId())).thenReturn(expectedComments);

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
            when(tweetRepository.findById(anyInt())).thenReturn(tweet);
            when(commentRepository.findByTweetId(tweet.getId())).thenReturn(expectedComments);

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
            when(tweetRepository.findById(anyInt())).thenReturn(tweet);
            when(commentRepository.findByTweetId(tweet.getId())).thenReturn(expectedComments);

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

            when(tweetRepository.findById(anyInt())).thenReturn((tweet));
            when(commentRepository.findByTweetId(tweet.getId())).thenReturn(expectedComments);

            tweetController.showTweetDetail(1, new CommentForm(), model);

            assertThat(model.getAttribute("comments"), is(expectedComments));
            assertThat(((List<CommentEntity>) model.getAttribute("comments")).get(0).getText(), is("This is a comment."));
        }
    }
}