package in.tech_camp.pictweet;

import jakarta.persistence.EntityNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Controller
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @GetMapping("/registerForm")
    public String register(@ModelAttribute("user")UserForm userForm){
        return "users/register";
    }

    @PostMapping("/register")
    public String registerNewUser(@ModelAttribute("user") @Validated(GroupOrder.class) UserForm userForm, BindingResult result, Model model) {
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
        try {
            UserEntity userEntity = new UserEntity();

            userEntity.setNickname(userForm.getNickname());
            userEntity.setEmail(userForm.getEmail());
            userEntity.setPassword(userForm.getPassword());

            userService.registerNewUser(userEntity);
        } catch (Exception e) {
            model.addAttribute("errorMessage",e.getMessage());
            return "users/register";
        }
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
        UserEntity user;
        try {
            user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Useer not found: " + userId));
        } catch (EntityNotFoundException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "error";
        }
        model.addAttribute("user", user);
        return "users/detail";
    }
}