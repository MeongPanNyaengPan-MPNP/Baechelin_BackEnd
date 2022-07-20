package com.mpnp.baechelin.login.oauth.entity;

import com.mpnp.baechelin.user.domain.User;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

// 인증된 사용자의 정보를 담기 위한 클래스
@Getter @Setter
@RequiredArgsConstructor
@Builder @AllArgsConstructor
public class UserPrincipal implements OAuth2User, UserDetails, OidcUser {

    private final String userId;
    private final String password;
    private final ProviderType providerType;
    private final RoleType roleType;
    private final Collection<GrantedAuthority> authorities; // 인증 주체에게 부여된 권한들 (roles, scopes, etc.)
    private Map<String, Object> attributes;

    // 사용자 정보
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    // 사용자 권한
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getName() {
        return userId;
    }

    @Override
    public String getUsername() {
        return userId;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Map<String, Object> getClaims() {
        return null;
    }

    @Override
    public OidcUserInfo getUserInfo() {
        return null;
    }

    @Override
    public OidcIdToken getIdToken() {
        return null;
    }

    // spring security -> userService(CustomOAuth2UserService)에서 사용자의 속성을 가져오기 위한 전처리.
    // spring security -> userDetailService 설정을 위해 사용
    public static UserPrincipal create(User user) {
        SimpleGrantedAuthority simpleGrantedAuthority;
        if (user.getRoleType().getCode().equals("ROLE_ADMIN")) {
            simpleGrantedAuthority = new SimpleGrantedAuthority(RoleType.ADMIN.getCode());
        } else {
            simpleGrantedAuthority = new SimpleGrantedAuthority(RoleType.USER.getCode());
        }
        
        return UserPrincipal.builder()
                .userId(user.getSocialId())
                .password(user.getPassword())
                .providerType(user.getProviderType())
                .roleType(user.getRoleType())
                .authorities(Collections.singletonList(simpleGrantedAuthority))
                .build();
    }

    // spring security -> userService(CustomOAuth2UserService)에서 사용자의 속성을 가져오기 위한 전처리.
    public static UserPrincipal create(User user, Map<String, Object> attributes) {
        UserPrincipal userPrincipal = create(user);
        userPrincipal.setAttributes(attributes);

        return userPrincipal;
    }


}
