package com.gca.automation.component;

import com.gca.automation.dto.AuthCredentials;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@Scope("cucumber-glue")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TestContext {
    private AuthCredentials credentials;
    private String jwtToken;
    private ResponseEntity<?> lastResponse;
}
