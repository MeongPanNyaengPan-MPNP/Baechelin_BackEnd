package com.mpnp.baechelin.login.oauth.service;

import com.mpnp.baechelin.login.oauth.entity.ProviderType;
import com.mpnp.baechelin.login.oauth.entity.RoleType;
import com.mpnp.baechelin.login.oauth.entity.UserPrincipal;
import com.mpnp.baechelin.login.oauth.exception.OAuthProviderMissMatchException;
import com.mpnp.baechelin.exception.ErrorCode;
import com.mpnp.baechelin.login.oauth.info.OAuth2UserInfo;
import com.mpnp.baechelin.login.oauth.info.OAuth2UserInfoFactory;
import com.mpnp.baechelin.user.domain.User;
import com.mpnp.baechelin.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/* naver의 oauth2 인증을 통해서 불러온 유저 정보를 처리하기 위한 custom 클래스
 * 소셜 api에서 가져온 유저의 정보를 db에 저장하기 위해 구현
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // DefaultOAuth2UserService 의 loadUser 클래스를 사용하기 위해 super 키워드 사용. this 키워드는 내 클래스나 필드에 사용.
        OAuth2User user = super.loadUser(userRequest);

        try {
            // 업데이트되거나 저장된 유저 정보 리턴
            return this.process(userRequest, user);

        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            ex.printStackTrace(); // 모든 에러 정보 출력
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }


    // 유저 정보를 DB에 저장하거나 업데이트하는 메소드
    private OAuth2User process(OAuth2UserRequest userRequest, OAuth2User user) {
        // 요청한 유저 정보에 들어있는 provider type을 가져온다.
        ProviderType providerType = ProviderType.valueOf(userRequest.getClientRegistration().getRegistrationId().toUpperCase());

        // 유저 정보를 가져온다.
        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(providerType, user.getAttributes());

        // DB에 저장된 유저 정보를 가져온다.
        User savedUser = userRepository.findByEmail(userInfo.getEmail());

        if (savedUser != null) {
            // DB에 유저 정보가 있을 때
            if (providerType != savedUser.getProviderType()) {
                throw new OAuthProviderMissMatchException(ErrorCode.ALREADY_LOGIN_ACCOUNT.getCode() + "&provider_type=" + savedUser.getProviderType());
            }
            updateUser(savedUser, userInfo);
        } else {
            // DB에 유저 정보가 없을 때
            savedUser = createUser(userInfo, providerType);
        }

        return UserPrincipal.create(savedUser, user.getAttributes());
    }

    private User createUser(OAuth2UserInfo userInfo, ProviderType providerType) {
        User user = User.builder()
                .socialId(userInfo.getId())
                .name(userInfo.getName())
                .email(userInfo.getEmail())
                .emailVerifiedYn("Y")
                .profileImageUrl(userInfo.getImageUrl())
                .providerType(providerType)
                .roleType(RoleType.USER)
                .build();

        return userRepository.saveAndFlush(user);
    }

    private void updateUser(User user, OAuth2UserInfo userInfo) {
        // DB에 있는 user name과 소셜에서 보내준 user name이 다를 시 DB 업데이트
        // 즉, 소셜에서 이름을 바꿨으면 업데이트
        if (userInfo.getName() != null && !user.getName().equals(userInfo.getName())) {
            user.setName(userInfo.getName());
        }

        // 위와 동일
        if (userInfo.getImageUrl() != null && !user.getProfileImageUrl().equals(userInfo.getImageUrl())) {
            user.setProfileImageUrl(userInfo.getImageUrl());
        }

    }

}
