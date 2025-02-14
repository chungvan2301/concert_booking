package com.example.vannc.service;

import com.example.vannc.dto.request.BookingRequestDTO;
import com.example.vannc.dto.response.ApiResponse;
import com.example.vannc.entity.Booking;
import com.example.vannc.entity.Concert;
import com.example.vannc.exception.AppException;
import com.example.vannc.exception.ErrorCode;
import com.example.vannc.repository.BookingRepository;
import com.example.vannc.repository.ConcertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final ConcertRepository concertRepository;
    private final BookingRepository bookingRepository;
    private final BookingQueueService queueService;
    private final VNPayService vnPayService;

    public ResponseEntity<Map<String, String>> bookTicket(BookingRequestDTO request, String clientIp) {
        Concert concert = concertRepository.findById(request.getConcertId())
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));

        if (concert.getAvailableSeats() < request.getQuantity()) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Vé đã hết"));
        }

        // Tạo URL thanh toán VNPay
        String paymentUrl = vnPayService.createPaymentUrl(request, clientIp);
        System.out.println(paymentUrl);
        // Trích xuất txnRef từ URL
        String txnRef = vnPayService.extractTxnRef(paymentUrl);

        // Lưu booking vào Redis với txnRef làm key
        queueService.savePendingBooking(txnRef, request);

        return ResponseEntity.ok(Collections.singletonMap("paymentUrl", paymentUrl));
    }

    public List<Booking> getUserBookings(String userId) {
        return bookingRepository.findByUserId(userId);
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }
}
