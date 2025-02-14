package com.example.vannc.controller;


import com.example.vannc.dto.request.ConcertRequestDTO;
import com.example.vannc.dto.response.ConcertListResponseDTO;
import com.example.vannc.dto.response.ConcertResponseDTO;
import com.example.vannc.service.ConcertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ConcertController {
    @Autowired
    private ConcertService concertService;

    @GetMapping("/public/concert")
    public List<ConcertListResponseDTO> getConcerts(
            @RequestParam(required = false) String lastId,
            @RequestParam(defaultValue = "10") int size) {
        return concertService.getConcertsAfter(lastId, size);
    }

    @GetMapping("/public/concert/{id}")
    public ResponseEntity<ConcertResponseDTO> getConcertById(@PathVariable String id) {
        ConcertResponseDTO concert = concertService.getConcertById(id);
        return ResponseEntity.ok(concert);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/concert")
    public ResponseEntity<ConcertResponseDTO> createConcert(@RequestBody ConcertRequestDTO concertRequestDTO) {
        ConcertResponseDTO concert = concertService.createConcert(concertRequestDTO);
        return ResponseEntity.ok(concert);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/public/concert/{id}")
    public ResponseEntity<String> deleteConcert(@PathVariable String id) {
        if (concertService.deleteConcertById(id)) {
            return ResponseEntity.ok("Xóa concert dùng thành công!");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy concert!");
    }
}
