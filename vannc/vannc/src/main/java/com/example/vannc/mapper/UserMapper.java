package com.example.vannc.mapper;

import com.example.vannc.dto.response.ConcertResponseDTO;
import com.example.vannc.dto.response.UserResponseDTO;
import com.example.vannc.entity.Concert;
import com.example.vannc.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserResponseDTO toDTO(User entity) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setName(entity.getName());
        dto.setEmail(entity.getEmail());
        dto.setDateOfBirth(entity.getDateOfBirth());
        dto.setGender(entity.getGender());
        return dto;
    }
}
