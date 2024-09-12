package in.tech_camp.pictweet.java2023;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class TweetControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testShowTweets() throws Exception {
        mockMvc.perform(get("/")) // ①
                .andExpect(status().isOk()) // ②
                .andExpect(view().name("tweets/index")) // ③
                .andExpect(model().attributeExists("tweetList")); // ④
    }
}
