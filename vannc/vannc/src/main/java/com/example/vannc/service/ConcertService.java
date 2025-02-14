package com.example.vannc.service;


import com.example.vannc.dto.request.ConcertRequestDTO;
import com.example.vannc.dto.response.ConcertListResponseDTO;
import com.example.vannc.dto.response.ConcertResponseDTO;
import com.example.vannc.entity.Concert;
import com.example.vannc.exception.AppException;
import com.example.vannc.exception.ErrorCode;
import com.example.vannc.mapper.ConcertMapper;
import com.example.vannc.repository.ConcertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConcertService {
    private final ConcertMapper concertMapper;
    private final ConcertRepository concertRepository;

    public List<ConcertListResponseDTO> getConcertsAfter(String lastId, int size) {
        LocalDateTime nowPlus2Hours = LocalDateTime.now().plusHours(2);
        List<Concert> concerts;
        if (lastId == null || lastId.isEmpty()) {
            concerts = concertRepository.findAllByDateAfterOrderByIdAsc(nowPlus2Hours, PageRequest.of(0, size));
        } else {
            concerts = concertRepository.findByIdGreaterThanAndDateAfterOrderByIdAsc(lastId, nowPlus2Hours, PageRequest.of(0, size));
        }
        return concerts.stream().map(concertMapper::toListDTO).toList();
    }

    public ConcertResponseDTO createConcert(ConcertRequestDTO concertRequestDTO) {
        Concert concert = concertMapper.toEntity(concertRequestDTO);
        concertRepository.save(concert);
        return concertMapper.toDTO(concert);
    }

    public ConcertResponseDTO getConcertById(String id) {
        Concert concert = concertRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_KEY));
        return concertMapper.toDTO(concert);
    }

    public boolean deleteConcertById(String id) {
        if (concertRepository.existsById(id)) {
            concertRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
