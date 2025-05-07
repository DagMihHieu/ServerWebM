// package com.lowquality.serverwebm.controller;

// import com.lowquality.serverwebm.dto.LoginResponse;
// import com.lowquality.serverwebm.service.AuthenticationService;
// import lombok.RequiredArgsConstructor;
// import org.springframework.http.ResponseEntity;
// import org.springframework.security.core.annotation.AuthenticationPrincipal;
// import org.springframework.security.oauth2.core.user.OAuth2User;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;

// @RestController
// @RequestMapping("/api/auth/oauth2")
// @RequiredArgsConstructor
// public class OAuth2Controller {

//     private final AuthenticationService authenticationService;

//     @GetMapping("/callback/google")
//     public ResponseEntity<LoginResponse> googleCallback(@AuthenticationPrincipal OAuth2User oauth2User) {
//         String email = oauth2User.getAttribute("email");
//         String name = oauth2User.getAttribute("name");
        
//         // Handle OAuth2 login
//         return ResponseEntity.ok(authenticationService.handleOAuth2Login(email, name));
//     }
// } 