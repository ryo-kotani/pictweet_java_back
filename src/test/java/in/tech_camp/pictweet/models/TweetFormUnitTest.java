package in.tech_camp.pictweet.models;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import in.tech_camp.pictweet.TweetForm;
import in.tech_camp.pictweet.ValidGroup1;
import in.tech_camp.pictweet.factories.TweetFormFactory;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

@SpringBootTest
@ActiveProfiles("test")
public class TweetFormUnitTest {

    @Autowired
    private Validator validator;

    private TweetForm tweetForm;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        tweetForm = TweetFormFactory.createTweet();
    }

    @Test
    public void テキストが空では投稿できない() {
        tweetForm.setText("");

        Set<ConstraintViolation<TweetForm>> violations = validator.validate(tweetForm, ValidGroup1.class);
        assertEquals(1, violations.size());
        assertEquals("Text can't be blank", violations.iterator().next().getMessage());
    }

    @Test
    public void 画像が空では投稿できない() {
        tweetForm.setImage(null);

        Set<ConstraintViolation<TweetForm>> violations = validator.validate(tweetForm, ValidGroup1.class);
        assertEquals(1, violations.size());
        assertEquals("Image can't be blank", violations.iterator().next().getMessage());
    }
}
