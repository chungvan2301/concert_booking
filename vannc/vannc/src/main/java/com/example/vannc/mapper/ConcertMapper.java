package com.example.vannc.mapper;

import com.example.vannc.dto.request.ConcertRequestDTO;
import com.example.vannc.dto.response.ConcertListResponseDTO;
import com.example.vannc.dto.response.ConcertResponseDTO;
import com.example.vannc.entity.Concert;
import org.springframework.stereotype.Component;

@Component
public class ConcertMapper {
    public Concert toEntity(ConcertRequestDTO dto) {
        return Concert.builder()
                .name(dto.getName())
                .location(dto.getLocation())
                .date(dto.getDate())
                .totalSeats(dto.getTotalSeats())
                .availableSeats(dto.getTotalSeats()) // Khi mới tạo, availableSeats = totalSeats
                .image(dto.getImage())
                .locationCity(dto.getLocationCity())
                .price(dto.getPrice())
                .category(dto.getCategory())
                .build();
    }

    public ConcertResponseDTO toDTO(Concert entity) {
        ConcertResponseDTO dto = new ConcertResponseDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setLocation(entity.getLocation());
        dto.setDate(entity.getDate());
        dto.setTotalSeats(entity.getTotalSeats());
        dto.setAvailableSeats(entity.getAvailableSeats());
        dto.setImage(entity.getImage());
        dto.setLocationCity(entity.getLocationCity());
        dto.setPrice(entity.getPrice());
        return dto;
    }

    public ConcertListResponseDTO toListDTO(Concert entity) {
        ConcertListResponseDTO dto = new ConcertListResponseDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDate(entity.getDate());
        dto.setAvailableSeats(entity.getAvailableSeats());
        dto.setImage(entity.getImage());
        dto.setLocationCity(entity.getLocationCity());
        dto.setPrice(entity.getPrice());
        return dto;
    }
}
