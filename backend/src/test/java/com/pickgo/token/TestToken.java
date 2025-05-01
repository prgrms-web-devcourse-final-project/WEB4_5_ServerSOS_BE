package com.pickgo.token;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("test")
@Component
public class TestToken {
	@Value("${custom.jwt.admin_token}")
	public String adminToken;

	@Value("${custom.jwt.user_token}")
	public String userToken;

	@Value("${custom.jwt.expired_token}")
	public String expiredToken;
}
