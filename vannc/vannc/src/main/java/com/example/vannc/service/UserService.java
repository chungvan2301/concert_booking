package com.example.vannc.service;

import com.example.vannc.config.JwtService;
import com.example.vannc.dto.request.RegisterRequestDTO;
import com.example.vannc.dto.request.UpdateUserDTO;
import com.example.vannc.dto.response.AuthResponseDTO;
import com.example.vannc.dto.response.UserResponseDTO;
import com.example.vannc.entity.User;
import com.example.vannc.exception.AppException;
import com.example.vannc.exception.ErrorCode;
import com.example.vannc.mapper.UserMapper;
import com.example.vannc.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private JwtService jwtService;

    @Transactional
    public User register(RegisterRequestDTO request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setName(request.getName());
        user.setDateOfBirth(request.getDateOfBirth());
        user.setGender(request.getGender());
        user.setRole(User.Role.USER);  // Mặc định là user

        return userRepository.save(user);
    }

    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<UserResponseDTO> getUserById(String id) {
        return userRepository.findById(id).map(userMapper::toDTO);
    }

    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    @Transactional
    public User updateUser(UpdateUserDTO updateUserDTO) {
        User user = userRepository.findById(updateUserDTO.getId()).orElseThrow(() -> new AppException(ErrorCode.USER_EXISTED));
        user.setName(updateUserDTO.getName());
        user.setDateOfBirth(updateUserDTO.getDateOfBirth());
        user.setGender(updateUserDTO.getGender());

        if (updateUserDTO.getPassword() != null && !updateUserDTO.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(updateUserDTO.getPassword()));
        }

        return userRepository.save(user);
    }

    public boolean deleteUserById(String id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
    }
}
