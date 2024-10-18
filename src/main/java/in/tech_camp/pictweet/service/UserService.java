package in.tech_camp.pictweet.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import in.tech_camp.pictweet.entity.UserEntity;
import in.tech_camp.pictweet.repository.UserRepository;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void registerNewUser(UserEntity userEntity){
        String password = userEntity.getPassword();
        String encodedPassword = passwordEncoder.encode(password);

        userEntity.setPassword(encodedPassword); // 既存のuserEntityにパスワードを設定
        userRepository.insert(userEntity); // 既存のuserEntityを保存
    }


}