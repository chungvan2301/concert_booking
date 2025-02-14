package com.example.vannc.controller;

import com.example.vannc.dto.request.RegisterRequestDTO;
import com.example.vannc.dto.request.UpdateUserDTO;
import com.example.vannc.dto.response.UserResponseDTO;
import com.example.vannc.entity.User;
import com.example.vannc.mapper.UserMapper;
import com.example.vannc.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    // 🟢 Lấy thông tin user theo ID
    @GetMapping("/information")
    public ResponseEntity<UserResponseDTO> getUser(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByEmail(userDetails.getUsername()); // Lấy user từ token
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(userMapper.toDTO(user));
    }


    // 🟢 Cập nhật user (có thể đổi mật khẩu)
    @PutMapping("/update")
    public ResponseEntity<User> updateUser(@RequestBody UpdateUserDTO userRequest) {
        User updatedUser = userService.updateUser(userRequest);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteUser(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByEmail(userDetails.getUsername());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (userService.deleteUserById(user.getId())) {
            return ResponseEntity.ok("Xóa người dùng thành công!");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy người dùng!");
    }

}
