package in.tech_camp.pictweet.custom_user;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import in.tech_camp.pictweet.entity.UserEntity;
import lombok.Data;

@Data
public class CustomUserDetail implements UserDetails {
    private final UserEntity user;

    public CustomUserDetail(UserEntity user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    public Integer getId() {
        return user.getId(); // IDを取得するメソッドを追加
    }

    public String getNickname() {
        return user.getNickname(); // IDを取得するメソッドを追加
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // アカウントが期限切れでないこと
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // アカウントがロックされていないこと
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 認証情報が期限切れでないこと
    }

    @Override
    public boolean isEnabled() {
        return true; // アカウントが有効であること
    }
}
