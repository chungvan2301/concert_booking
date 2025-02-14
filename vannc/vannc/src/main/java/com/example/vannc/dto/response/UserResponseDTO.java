package com.example.vannc.dto.response;

import com.example.vannc.entity.User;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserResponseDTO {
    private String email;
    private String name;
    private LocalDate dateOfBirth;
    private User.Gender gender;
}
