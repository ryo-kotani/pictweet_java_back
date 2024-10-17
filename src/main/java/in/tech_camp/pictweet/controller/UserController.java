package in.tech_camp.pictweet.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import in.tech_camp.pictweet.entity.TweetEntity;
import in.tech_camp.pictweet.entity.UserEntity;
import in.tech_camp.pictweet.form.UserForm;
import in.tech_camp.pictweet.repository.TweetRepository;
import in.tech_camp.pictweet.repository.UserRepository;
import in.tech_camp.pictweet.service.UserService;
import in.tech_camp.pictweet.validation.GroupOrder;
import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final TweetRepository tweetRepository;
    private final UserService userService;

    @GetMapping("/registerForm")
    public String register(@ModelAttribute("user")UserForm userForm){
        return "users/register";
    }

    @PostMapping("/register")
    public String registerNewUser(@ModelAttribute("user") @Validated(GroupOrder.class) UserForm userForm, BindingResult result, Model model) {
        userForm.validatePasswords(result);
        if (userRepository.existsByEmail(userForm.getEmail())) {
            result.rejectValue("email", "error.user", "Email already exists");
        }
        // バリデーションエラーチェック
        if (result.hasErrors()) {
            List<String> errorMessages = result.getAllErrors().stream()
            .map(DefaultMessageSourceResolvable::getDefaultMessage)
            .collect(Collectors.toList());

            // モデルにエラーメッセージを追加
            model.addAttribute("errorMessages", errorMessages);
            // フォームデータもモデルに戻す
            model.addAttribute("user", userForm);
            return register(userForm);
        }
        UserEntity userEntity = new UserEntity();

        userEntity.setNickname(userForm.getNickname());
        userEntity.setEmail(userForm.getEmail());
        userEntity.setPassword(userForm.getPassword());

        userService.registerNewUser(userEntity);
        return "redirect:/";
    }

    @GetMapping("/loginForm")
    public String loginForm(){
        return "users/login";
    }

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("loginError", "メールアドレスかパスワードが間違っています。");
        }
        return "users/login";
    }


    @GetMapping("/users/{userId}")
    public String userProfile(@PathVariable("userId") Integer userId, Model model) {
        UserEntity user = userRepository.findById(userId);
        List<TweetEntity> tweets = tweetRepository.findByUserId(user.getId());
        if (user == null) {
            return "redirect:/";
        }
        model.addAttribute("user", user);
        model.addAttribute("tweets", tweets);
        return "users/detail";
    }
}