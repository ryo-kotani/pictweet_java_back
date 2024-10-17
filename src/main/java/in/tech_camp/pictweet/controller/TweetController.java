package in.tech_camp.pictweet.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import in.tech_camp.pictweet.entity.TweetEntity;
import in.tech_camp.pictweet.entity.UserEntity;
import in.tech_camp.pictweet.custom_user.CustomUserDetail;
import in.tech_camp.pictweet.entity.CommentEntity;
import in.tech_camp.pictweet.form.CommentForm;
import in.tech_camp.pictweet.form.SearchForm;
import in.tech_camp.pictweet.form.TweetForm;
import in.tech_camp.pictweet.repository.TweetRepository;
import in.tech_camp.pictweet.repository.UserRepository;
import in.tech_camp.pictweet.validation.GroupOrder;
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
                        @AuthenticationPrincipal CustomUserDetail currentUser,
                        Model model) {

        UserEntity user = userRepository.findById(currentUser.getId());
        if (user == null) {
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
            tweetRepository.insert(tweet);
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


        TweetEntity tweet = tweetRepository.findById(tweetId);
        if (tweet == null) {
            return "redirect:/";
        }
        List<CommentEntity> comments = commentRepository.findByTweetId(tweet.getId());
        model.addAttribute("commentForm",commentForm);
        model.addAttribute("comments",comments);
        model.addAttribute("tweet", tweet);

        return "tweets/detail";
    }

    @GetMapping("/tweets/{tweetId}/edit")
    public String edit(@AuthenticationPrincipal CustomUserDetail currentUser,@PathVariable("tweetId") Integer tweetId, Model model) {
        TweetEntity tweet = tweetRepository.findById(tweetId);
        if (tweet == null) {
            return "redirect:/";
        }
        // 現在のユーザーがツイートの所有者であるかを確認
        if (!tweet.getUser().getEmail().equals(currentUser.getUsername())) {
            return "redirect:/";
        }
        model.addAttribute("tweetEntity", tweet);
        return "tweets/edit";
    }


    @PostMapping("/tweets/{tweetId}/update")
    public String update(@AuthenticationPrincipal CustomUserDetail currentUser,
                    @ModelAttribute("tweetForm") @Validated(GroupOrder.class) TweetForm tweetForm,
                    BindingResult result,
                    @PathVariable("tweetId") Integer tweetId,
                    Model model) {
        TweetEntity tweet = tweetRepository.findById(tweetId);
        UserEntity user = userRepository.findById(currentUser.getId());

        if (tweet == null || user == null){
            return "redirect:/";
        }
        // ユーザ認証チェック
        if (!tweet.getUser().getEmail().equals(currentUser.getUsername())) {
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
            tweetRepository.update(tweet);
        } catch (Exception e) {
            return "redirect:/";
        }

        return "redirect:/";
    }

    @PostMapping("/tweets/{tweetId}/delete")
    public String delete(@AuthenticationPrincipal CustomUserDetail currentUser,
                    @PathVariable("tweetId") Integer tweetId) {

        TweetEntity tweet = tweetRepository.findById(tweetId);
        UserEntity user = userRepository.findById(currentUser.getId());

        if (tweet == null || user == null){
            return "redirect:/";
        }
        // ユーザ認証チェック
        if (!tweet.getUser().getEmail().equals(currentUser.getUsername())) {
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