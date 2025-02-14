package com.example.vannc.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConcertResponseDTO {
    private String id;
    private String name;
    private String location;
    private LocalDateTime date;
    private int totalSeats;
    private int availableSeats;
    private String image;
    private String locationCity;
    private BigDecimal price;
}
