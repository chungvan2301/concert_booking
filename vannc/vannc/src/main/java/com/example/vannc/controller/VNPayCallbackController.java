package com.example.vannc.controller;

import com.example.vannc.dto.request.BookingRequestDTO;
import com.example.vannc.entity.Booking;
import com.example.vannc.entity.Concert;
import com.example.vannc.entity.User;
import com.example.vannc.repository.BookingRepository;
import com.example.vannc.repository.ConcertRepository;
import com.example.vannc.service.BookingQueueService;
import com.example.vannc.service.BookingWorker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
@Slf4j
public class VNPayCallbackController {
    private final BookingQueueService queueService;
    private final BookingRepository bookingRepository;
    private final ConcertRepository concertRepository;
    private final BookingWorker bookingWorker;

    @GetMapping("/vnpay-return")
    public ResponseEntity<String> handlePaymentCallback(@RequestParam Map<String, String> params) {
        String txnRef = params.get("vnp_TxnRef");
        String vnpResponseCode = params.get("vnp_ResponseCode");

        if ("00".equals(vnpResponseCode)) { // Thanh toán thành công, chuyển booking sang hàng đợi xử lý
            bookingWorker.processBookingQueue(txnRef);
            return ResponseEntity.ok("Thanh toán thành công!");
        }

        return ResponseEntity.badRequest().body("Payment failed.");
    }
}
