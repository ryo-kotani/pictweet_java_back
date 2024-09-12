package in.tech_camp.pictweet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class UserAuthenticationService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(email);
        if (userEntity == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }
        userEntity.getTweets().size(); // 明示的にコレクションを初期化
//        ここのUserはspring securityのUserなのでUserEntityの引数とは異なる
//        return new User(userEntity.getNickname(), userEntity.getPassword(), Collections.emptyList());
        return new CustomUserDetail(userEntity);
    }
}
