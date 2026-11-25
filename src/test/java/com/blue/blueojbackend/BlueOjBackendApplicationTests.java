package com.blue.blueojbackend;

import com.blue.blueojbackend.auth.IssuedToken;
import com.blue.blueojbackend.auth.JwtService;
import com.blue.blueojbackend.auth.LoginUser;
import com.blue.blueojbackend.auth.UserContext;
import com.blue.blueojbackend.exception.BusinessException;
import com.blue.blueojbackend.utils.PasswordUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.json.JsonMapper;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "springdoc.api-docs.enabled=true")
@ActiveProfiles("test")
@AutoConfigureMockMvc
class BlueOjBackendApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper jsonMapper;

    @Autowired
    private PasswordUtils passwordUtils;

    @Autowired
    private JwtService jwtService;

    @Test
    void contextLoads() {
    }

    @Test
    void openApiDocumentShouldBeAvailable() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(header().exists("X-Request-Id"));
    }

    @Test
    void longValueShouldBeSerializedAsString() throws Exception {
        String json = jsonMapper.writeValueAsString(Map.of("id", 9007199254740993L));

        assertThat(json).isEqualTo("{\"id\":\"9007199254740993\"}");
    }

    @Test
    void passwordShouldBeHashedAndMatched() {
        String rawPassword = "password123";
        String encodedPassword = passwordUtils.encode(rawPassword);

        assertThat(encodedPassword).isNotEqualTo(rawPassword);
        assertThat(passwordUtils.matches(rawPassword, encodedPassword)).isTrue();
        assertThat(passwordUtils.matches("wrongPassword", encodedPassword)).isFalse();
        assertThat(passwordUtils.isEncodingSupported("测".repeat(25))).isFalse();
    }

    @Test
    void jwtShouldBeSignedValidatedAndRejectedAfterTampering() {
        IssuedToken issuedToken = jwtService.issue(123L, "user", 7L);
        LoginUser loginUser = jwtService.parseAndValidate(issuedToken.value());

        assertThat(loginUser.userId()).isEqualTo(123L);
        assertThat(loginUser.userRole()).isEqualTo("user");
        assertThat(loginUser.authVersion()).isEqualTo(7L);
        assertThat(loginUser.expiresAt()).isEqualTo(issuedToken.expiresAt());

        String token = issuedToken.value();
        char replacement = token.charAt(token.length() - 1) == 'a' ? 'b' : 'a';
        String tamperedToken = token.substring(0, token.length() - 1) + replacement;
        assertThatThrownBy(() -> jwtService.parseAndValidate(tamperedToken))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void protectedEndpointShouldRejectMissingBearerToken() throws Exception {
        mockMvc.perform(get("/user/get/login"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(40100));

        mockMvc.perform(post("/user/logout")
                        .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(40100));
    }

    @Test
    void userContextShouldBeCleared() {
        LoginUser loginUser = new LoginUser(123L, "user", "token-id", 0L, java.time.Instant.now());
        UserContext.set(loginUser);
        assertThat(UserContext.getRequired()).isEqualTo(loginUser);

        UserContext.remove();
        assertThat(UserContext.get()).isNull();
    }

}
