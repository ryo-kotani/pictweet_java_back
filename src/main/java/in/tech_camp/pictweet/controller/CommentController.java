package in.tech_camp.pictweet.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import in.tech_camp.pictweet.custom_user.CustomUserDetail;
import in.tech_camp.pictweet.entity.CommentEntity;
import in.tech_camp.pictweet.entity.TweetEntity;
import in.tech_camp.pictweet.entity.UserEntity;
import in.tech_camp.pictweet.form.CommentForm;
import in.tech_camp.pictweet.repository.CommentRepository;
import in.tech_camp.pictweet.repository.TweetRepository;
import in.tech_camp.pictweet.repository.UserRepository;
import in.tech_camp.pictweet.validation.GroupOrder;
import lombok.AllArgsConstructor;

@Controller
@RequestMapping("/tweets")
@AllArgsConstructor
public class CommentController {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final TweetRepository tweetRepository;

    @PostMapping("/{tweetId}/comment")
    public String postComment(@ModelAttribute("commentForm") @Validated(GroupOrder.class) CommentForm commentForm,
                              BindingResult result,
                              @AuthenticationPrincipal CustomUserDetail currentUser,
                              @PathVariable("tweetId") Integer tweetId,
                              Model model)
    {
        TweetEntity tweet = tweetRepository.findById(tweetId);
        UserEntity user = userRepository.findById(currentUser.getId());

        if (user == null || tweet == null) {
            return "/";
        }

        // バリデーションエラーがあるかチェックする
        if (result.hasErrors()) {
            List<CommentEntity> comments= commentRepository.findByTweetId(tweet.getId());
            model.addAttribute("errorMessages", result.getAllErrors());
            model.addAttribute("tweet", tweet);
            model.addAttribute("comments", comments);
            model.addAttribute("commentForm", commentForm);
            return "tweets/detail";
        }

        // バリデーションエラーがなければ、コメントを保存する
        CommentEntity comment = new CommentEntity();
        comment.setText(commentForm.getText());
        comment.setTweet(tweet);
        comment.setUser(user);

        try {
            commentRepository.insert(comment);
        } catch (Exception e) {
            model.addAttribute("tweet", tweet);
            model.addAttribute("comments", commentRepository.findByTweetId(tweet.getId()));
            model.addAttribute("commentForm", commentForm);
            return "tweets/detail";
        }
        // 成功時には詳細画面にリダイレクトする
        return "redirect:/tweets/" + tweetId;
    }
}
