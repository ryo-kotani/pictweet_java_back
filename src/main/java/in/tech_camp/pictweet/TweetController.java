package in.tech_camp.pictweet;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class TweetController {
    @Autowired
    private TweetRepository tweetRepository;


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;



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

    if (user == null) {
        model.addAttribute("errorMessage", "User not found.");
        return showTweetNew(model);
    }

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

    log.info(tweetForm.toString());

    MultipartFile imageFile = tweetForm.getImage();

    if (imageFile != null && !imageFile.isEmpty()) {
        try {
            // 画像保存処理
            String uploadDir = "src/main/resources/static/uploads";
            String fileName = user.getId().toString() + "_" + LocalDateTime.now() + "_" + imageFile.getOriginalFilename();
            Path imgFilePath = Path.of(uploadDir, fileName);
            Files.copy(imageFile.getInputStream(), imgFilePath);
            log.info("imageFile" + imageFile.toString());
            log.info("imageFile.getInputStream" + imageFile.getInputStream().toString());
            tweetEntity.setImage("/uploads/" + fileName);
        } catch (IOException e) {
            model.addAttribute("errorMessage", "画像アップロードに失敗しました。");
            return showTweetNew(model);
        }
    }

    try {
        tweetRepository.save(tweetEntity);
    } catch (Exception e) {
        model.addAttribute("errorMessage", e.getMessage());
        return showTweetNew(model);
    }

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

    try {
        tweet = tweetRepository.findById(tweetId).orElseThrow(() -> new EntityNotFoundException("Tweet not found: " + tweetId));
    } catch (EntityNotFoundException ex) {
        model.addAttribute("errorMessage", ex.getMessage());
        return "tweets/index";
    }

    List<CommentEntity> comments = commentRepository.findByTweet_id(tweetId);
    model.addAttribute("commentForm",commentForm);
    model.addAttribute("comments",comments);
    model.addAttribute("tweet", tweet);

    return "tweets/detail";
    }

    @GetMapping("/user/{userId}/tweets/{tweetId}/edit")
    public String edit(Authentication authentication,@PathVariable("tweetId") Integer tweetId, Model model) {
    TweetEntity tweet;

    try {
        tweet = tweetRepository.findById(tweetId)
                .orElseThrow(() -> new EntityNotFoundException("Tweet not found: " + tweetId));
    } catch (EntityNotFoundException ex) {
        model.addAttribute("errorMessage", ex.getMessage());
        return "/";
    }

    if (tweet == null) {
        System.out.println("Debug: tweetEntity is null");
    } else {
        System.out.println("Debug: tweetEntity object - " + tweet);
    }

    // 現在のユーザーがツイートの所有者であるかを確認
    if (!tweet.getUser().getEmail().equals(authentication.getName())) {
        return "redirect:/"; // トップページへリダイレクト
    }

    model.addAttribute("tweetEntity", tweet);

    return "tweets/edit";
    }


    @PostMapping("/user/{userId}/tweets/{tweetId}/update")
    public String update(Authentication authentication,
                    @ModelAttribute("tweetForm") @Validated(GroupOrder.class) TweetForm tweetForm,
                    BindingResult result,
                    @PathVariable("userId") Integer userId,
                    @PathVariable("tweetId") Integer tweetId,
                    Model model) {
    // 認証ユーザ情報を取得
    String email = authentication.getName();

    TweetEntity tweet;
    UserEntity user;

    try {
        tweet = tweetRepository.findById(tweetId).orElseThrow(() -> new EntityNotFoundException("Tweet not found: " + tweetId));
        user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found: " + userId));
    } catch (EntityNotFoundException ex) {
        model.addAttribute("errorMessage", ex.getMessage());
        return "/";
    }
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

    // 古い画像のパスを保持
    String oldImagePath = tweet.getImage();

    // 新しい画像がアップロードされている場合
    MultipartFile imageFile = tweetForm.getImage();
    if (imageFile != null && !imageFile.isEmpty()) {
        try {
            String uploadDir = "src/main/resources/static/uploads";
            String fileName = user.getId().toString() + "_" + LocalDateTime.now() + "_" + imageFile.getOriginalFilename();
            Path imgFilePath = Path.of(uploadDir, fileName);
            Files.copy(imageFile.getInputStream(), imgFilePath);
            tweet.setImage("/uploads/" + fileName); // 新しい画像パスをセット
        } catch (IOException e) {
            model.addAttribute("errorMessage", "画像アップロードに失敗しました。");
            model.addAttribute("tweetEntity", tweet);
            return edit(authentication,tweetId, model);
        }
    }
    try {
        tweetRepository.save(tweet);
        if (oldImagePath != null && (imageFile != null && !imageFile.isEmpty())) {
            Path oldImage = Path.of("src/main/resources/static/uploads", oldImagePath.substring("/uploads/".length()));
            Files.deleteIfExists(oldImage); // 古い画像が存在する場合削除
        }
    } catch (Exception e) {
        model.addAttribute("errorMessage", e.getMessage());
        model.addAttribute("tweetEntity", tweet);
        return edit(authentication,tweetId, model);
    }
    return "redirect:/";
    }

    @PostMapping("/user/{userId}/tweets/{tweetId}/delete")
    public String delete(Authentication authentication,
                    @PathVariable("userId") Integer userId,
                    @PathVariable("tweetId") Integer tweetId,
                    Model model) {

    String email = authentication.getName();
    UserEntity user;

    try {
        user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found: " + userId));
    } catch (EntityNotFoundException ex) {
        model.addAttribute("errorMessage", ex.getMessage());
        return "error";
    }

    if (user.getEmail().equals(email)) {
        try {
            TweetEntity tweet = tweetRepository.findById(tweetId).orElseThrow(() -> new EntityNotFoundException("Tweet not found: " + tweetId));
            String imagePath = tweet.getImage();
            if (imagePath != null) {
                Path imageToDelete = Path.of("src/main/resources/static/uploads", imagePath.substring("/uploads/".length()));
                Files.deleteIfExists(imageToDelete); // 画像削除
            }
            tweetRepository.deleteById(tweetId);
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "error";
        }
    } else {
        model.addAttribute("errorMessage", "tweetの投稿者と一致しません。");
        return "error";
    }
    return "redirect:/";
    }

}