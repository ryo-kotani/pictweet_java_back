package in.tech_camp.pictweet;

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
    model.addAttribute("tweetForm", new TweetForm()); // "tweet"としてモデル属性を追加
    return "tweets/new";
    }

    @PostMapping("/tweets")
    public String createTweet(@ModelAttribute("tweetForm") @Validated(GroupOrder.class) TweetForm tweetForm,
                        BindingResult result,
                        Authentication authentication,
                        Model model) {

        String email = authentication.getName();
        UserEntity user = userRepository.findByEmail(email);

        if (result.hasErrors()) {
            List<String> errorMessages = result.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.toList());
            model.addAttribute("errorMessages", errorMessages);
            return showTweetNew(model);
        }

        TweetEntity tweetEntity = new TweetEntity();
        tweetEntity.setUser(user);
        tweetEntity.setText(tweetForm.getText());
        tweetEntity.setImage(tweetForm.getImage());

        tweetRepository.save(tweetEntity);
        return "redirect:/";
    }

    @GetMapping
    public String showTweets(Model model, @ModelAttribute SearchForm form) {

        List<TweetEntity> tweetList = tweetRepository.findAll();
        model.addAttribute("tweetList", tweetList);
        model.addAttribute("form", form);
        return "tweets/index";
    }


    @GetMapping("/tweets/{tweetId}")
    public String showTweetDetail(@PathVariable("tweetId") Integer tweetId,
                            @ModelAttribute("commentForm") CommentForm commentForm,
                            Model model) {
        TweetEntity tweet;
        tweet = tweetRepository.findById(tweetId)
        .orElseThrow(() -> new RuntimeException("Tweetが見つかりませんでした。"));


        List<CommentEntity> comments = commentRepository.findByTweet_id(tweetId);
        model.addAttribute("commentForm",commentForm);
        model.addAttribute("comments",comments);
        model.addAttribute("tweet", tweet);

        return "tweets/detail";
    }

    @GetMapping("/tweets/{tweetId}/edit")
    public String edit(Authentication authentication,@PathVariable("tweetId") Integer tweetId, Model model) {
        TweetEntity tweet;
        tweet = tweetRepository.findById(tweetId)
            .orElseThrow(() -> new RuntimeException("Tweetが見つかりませんでした。"));

        // 現在のユーザーがツイートの所有者であるかを確認
        if (!tweet.getUser().getEmail().equals(authentication.getName())) {
            return "redirect:/"; // トップページへリダイレクト
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
        // 認証ユーザ情報を取得
        String email = authentication.getName();
        UserEntity user = userRepository.findByEmail(email);

        TweetEntity tweet;
        tweet = tweetRepository.findById(tweetId)
            .orElseThrow(() -> new RuntimeException("Tweetが見つかりませんでした。"));

        user = userRepository.findById(user.getId())
            .orElseThrow(() -> new RuntimeException("Userが見つかりませんでした。"));

        // ユーザ認証チェック
        if (!user.getEmail().equals(email)) {
            model.addAttribute("errorMessage", "tweetの投稿者と一致しません。");
            return "/";
        }
        // バリデーションエラーチェック
        if (result.hasErrors()) {
            List<String> errorMessages = result.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.toList());
            model.addAttribute("errorMessages", errorMessages);
            model.addAttribute("tweetEntity", tweet);
            return edit(authentication,tweetId, model);
        }
        // tweetForm からエンティティに値を設定
        tweet.setText(tweetForm.getText());
        tweet.setImage(tweetForm.getImage());

        tweetRepository.save(tweet);

        return "redirect:/";
    }

    @PostMapping("/tweets/{tweetId}/delete")
    public String delete(Authentication authentication,
                    @PathVariable("tweetId") Integer tweetId) {

        tweetRepository.deleteById(tweetId);
        return "redirect:/";
    }
}