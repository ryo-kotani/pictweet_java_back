package in.tech_camp.pictweet.factories;

import org.springframework.mock.web.MockMultipartFile;

import com.github.javafaker.Faker;

import in.tech_camp.pictweet.TweetForm;

public class TweetFormFactory {
  private static final Faker faker = new Faker();

  public static TweetForm createTweet() {
    TweetForm tweetForm = new TweetForm();
    tweetForm.setText(faker.lorem().sentence(10));

    // Fakerを使って画像データを生成 (これはfakerではなく固定のモックを使う)
    tweetForm.setImage(new MockMultipartFile("image", "image.jpg", "image/jpeg", faker.avatar().image().getBytes()));
    return tweetForm;
  }
}
