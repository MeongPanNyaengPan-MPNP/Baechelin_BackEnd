package com.mpnp.baechelin.login.oauth.info.impl;

import com.mpnp.baechelin.login.oauth.info.OAuth2UserInfo;

import java.util.Map;

// google 로그인에서 보내준 유저 정보를 가져오기 위한 클래스
public class GoogleOAuth2UserInfo extends OAuth2UserInfo {

    public GoogleOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getId() {
        return (String) attributes.get("sub");
    }

    @Override
    public String getName() {
        return (String) attributes.get("name");
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getImageUrl() {
        return (String) attributes.get("picture");
    }
}
