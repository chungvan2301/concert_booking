package com.example.vannc.controller;

import com.example.vannc.dto.request.BookingRequestDTO;
import com.example.vannc.dto.response.ApiResponse;
import com.example.vannc.entity.Booking;
import com.example.vannc.entity.User;
import com.example.vannc.service.BookingQueueService;
import com.example.vannc.service.BookingService;
import com.example.vannc.service.UserService;
import com.example.vannc.service.VNPayService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;
    private final UserService userService;

    @PostMapping("/booking")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Map<String, String>> bookTicket(@RequestBody BookingRequestDTO request, @AuthenticationPrincipal UserDetails userDetails, HttpServletRequest httpServletRequest) {
        User user = userService.findByEmail(userDetails.getUsername());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Gán userId vào request
        request.setUserId(user.getId());

        // Gọi BookingService để xử lý đặt vé
        return bookingService.bookTicket(request, httpServletRequest.getHeader("X-Forwarded-For"));
    }

    @GetMapping("/booking/user")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<Booking>> getUserBookings(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(bookingService.getUserBookings(user.getId()));
    }

    @GetMapping("/admin/booking")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Booking>> getAllBookings() {
        return ResponseEntity.ok(bookingService.getAllBookings());
    }
}
