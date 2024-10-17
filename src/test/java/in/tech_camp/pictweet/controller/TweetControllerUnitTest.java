package in.tech_camp.pictweet.controller;

import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import in.tech_camp.pictweet.repository.UserRepository;
import in.tech_camp.pictweet.repository.TweetRepository;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class TweetControllerUnitTest {

    @Mock
    private TweetRepository tweetRepository;

    @Mock
    private  UserRepository userRepository;

    @InjectMocks
    private TweetController tweetController;
}
