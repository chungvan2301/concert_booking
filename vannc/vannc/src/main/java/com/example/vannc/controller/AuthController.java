package com.example.vannc.controller;

import com.example.vannc.config.JwtService;
import com.example.vannc.dto.request.LoginRequestDTO;
import com.example.vannc.dto.request.RefreshTokenRequestDTO;
import com.example.vannc.dto.request.RegisterRequestDTO;
import com.example.vannc.dto.response.AuthResponseDTO;
import com.example.vannc.entity.User;
import com.example.vannc.repository.CustomUserDetails;
import com.example.vannc.service.TokenService;
import com.example.vannc.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final TokenService tokenService;

    public AuthController(UserService userService, AuthenticationManager authenticationManager,
                          JwtService jwtService, UserDetailsService userDetailsService, TokenService tokenService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.tokenService = tokenService;
    }

    // API đăng ký người dùng
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDTO request) {
        User newUser = userService.register(request);

        UserDetails userDetails = new CustomUserDetails(newUser);
        String accessToken = jwtService.generateAccessToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        tokenService.storeRefreshToken(newUser.getId(), refreshToken, 60 * 24); // TTL 24h

        return ResponseEntity.ok(new AuthResponseDTO(accessToken, refreshToken));
    }

    // API đăng nhập người dùng
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );

            UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getEmail());

            String accessToken = jwtService.generateAccessToken(userDetails);
            String refreshToken = jwtService.generateRefreshToken(userDetails);
            User user = userService.findByEmail(loginRequest.getEmail());
            tokenService.storeRefreshToken(user.getId(), refreshToken, 60 * 24); // TTL 24h

            return ResponseEntity.ok(new AuthResponseDTO(accessToken, refreshToken));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email hoặc mật khẩu không đúng!");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody RefreshTokenRequestDTO request) {
        String refreshToken = request.getRefreshToken();
        if (refreshToken == null || refreshToken.isEmpty()) {
            return ResponseEntity.badRequest().body("Refresh token không hợp lệ!");
        }
        try {
            // Lấy userId từ JWT
            User user = userService.findByEmail(jwtService.extractUsername(refreshToken));

            // Xóa refresh token khỏi Redis
            tokenService.deleteRefreshToken(user.getId());

            return ResponseEntity.ok("Đăng xuất thành công!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token không hợp lệ!");
        }
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody String refreshToken) {
        if (refreshToken == null || refreshToken.isEmpty()) {
            return ResponseEntity.badRequest().body("Refresh token không hợp lệ!");
        }
        try {
            // Lấy user từ refresh token
            String username = jwtService.extractUsername(refreshToken);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // Kiểm tra token hợp lệ
            if (!jwtService.isTokenValid(refreshToken, username)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token không hợp lệ!");
            }

            // Cấp lại access token mới
            String newAccessToken = jwtService.generateAccessToken(userDetails);

            return ResponseEntity.ok(new AuthResponseDTO(newAccessToken, refreshToken));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token không hợp lệ!");
        }
    }

}
