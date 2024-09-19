package in.tech_camp.pictweet;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
public class TweetController {
    private final TweetRepository tweetRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    @GetMapping("/tweets/search")
    public String searchTweets(Model model, @ModelAttribute("searchForm") SearchForm form) {
        List<TweetEntity> tweets = tweetRepository.findByTextContaining(form.getText());
        model.addAttribute("tweetList", tweets);
        model.addAttribute("searchForm", form);
        return "tweets/search";
    }


    @GetMapping("/tweets/new")
    public String showTweetNew(Model model){
        model.addAttribute("tweetForm", new TweetForm());
        return "tweets/new";
    }

    @PostMapping("/tweets")
    public String createTweet(@ModelAttribute("tweetForm") @Validated(GroupOrder.class) TweetForm tweetForm,
                        BindingResult result,
                        Authentication authentication,
                        Model model) {

        UserEntity user;
        try {
            user = userRepository.findByEmail(authentication.getName()).orElseThrow(() -> new EntityNotFoundException("User not found"));
        } catch (EntityNotFoundException ex) {
            return "redirect:/";
        }

        if (result.hasErrors()) {
            List<String> errorMessages = result.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.toList());
            model.addAttribute("tweetForm", tweetForm);
            model.addAttribute("errorMessages", errorMessages);
            return "tweets/new";
        }

        TweetEntity tweet = new TweetEntity();
        tweet.setUser(user);
        tweet.setText(tweetForm.getText());
        tweet.setImage(tweetForm.getImage());

        try {
            tweetRepository.save(tweet);
        } catch (Exception e) {
            return "redirect:/";
        }
        return "redirect:/";
    }

    @GetMapping
    public String showTweets(Model model, @ModelAttribute SearchForm form) {

        List<TweetEntity> tweets = tweetRepository.findAll();
        model.addAttribute("tweetList", tweets);
        model.addAttribute("form", form);
        return "tweets/index";
    }


    @GetMapping("/tweets/{tweetId}")
    public String showTweetDetail(@PathVariable("tweetId") Integer tweetId,
                            @ModelAttribute("commentForm") CommentForm commentForm,
                            Model model) {
        TweetEntity tweet = null;
        List<CommentEntity> comments = new ArrayList<>();

        try {
            tweet = tweetRepository.findById(tweetId).orElseThrow(() -> new EntityNotFoundException("Tweet not found: " + tweetId));
            comments = commentRepository.findByTweet(tweet);
        } catch (EntityNotFoundException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "redirect:/";
        }
        model.addAttribute("commentForm",commentForm);
        model.addAttribute("comments",comments);
        model.addAttribute("tweet", tweet);

        return "tweets/detail";
    }

    @GetMapping("/tweets/{tweetId}/edit")
    public String edit(Authentication authentication,@PathVariable("tweetId") Integer tweetId, Model model) {
        TweetEntity tweet;
        try {
            tweet = tweetRepository.findById(tweetId)
                    .orElseThrow(() -> new EntityNotFoundException("Tweet not found: " + tweetId));
        } catch (EntityNotFoundException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "redirect:/";
        }
        // 現在のユーザーがツイートの所有者であるかを確認
        if (!tweet.getUser().getEmail().equals(authentication.getName())) {
            return "redirect:/";
        }
        model.addAttribute("tweetEntity", tweet);
        return "tweets/edit";
    }


    @PostMapping("/tweets/{tweetId}/update")
    public String update(Authentication authentication,
                    @ModelAttribute("tweetForm") @Validated(GroupOrder.class) TweetForm tweetForm,
                    BindingResult result,
                    @PathVariable("tweetId") Integer tweetId,
                    Model model) {
        TweetEntity tweet;
        UserEntity user;

        try {
            tweet = tweetRepository.findById(tweetId).orElseThrow(() -> new EntityNotFoundException("Tweet not found: " + tweetId));
            user = userRepository.findByEmail(authentication.getName()).orElseThrow(() -> new EntityNotFoundException("User not found"));
        } catch (EntityNotFoundException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "redirect:/";
        }
        // ユーザ認証チェック
        if (!tweet.getUser().getEmail().equals(authentication.getName())) {
            model.addAttribute("errorMessage", "tweetの投稿者と一致しません。");
            return "redirect:/";
        }
        // バリデーションエラーチェック
        if (result.hasErrors()) {
            List<String> errorMessages = result.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.toList());
            model.addAttribute("errorMessages", errorMessages);
            model.addAttribute("tweetEntity", tweet);
            return "tweets/edit";
        }
        // tweetForm からエンティティに値を設定
        tweet.setText(tweetForm.getText());
        tweet.setImage(tweetForm.getImage());

        try {
            tweetRepository.save(tweet);
        } catch (Exception e) {
            return "redirect:/";
        }

        return "redirect:/";
    }

    @PostMapping("/tweets/{tweetId}/delete")
    public String delete(Authentication authentication,
                    @PathVariable("tweetId") Integer tweetId) {
        try {
            tweetRepository.findById(tweetId).orElseThrow(() -> new EntityNotFoundException("Tweet not found: " + tweetId));
            userRepository.findByEmail(authentication.getName()).orElseThrow(() -> new EntityNotFoundException("User not found"));
        } catch (EntityNotFoundException ex) {
            return "redirect:/";
        }

        try {
            tweetRepository.deleteById(tweetId);
        } catch (Exception e) {
            return "redirect:/";
        }
        return "redirect:/";
    }
}