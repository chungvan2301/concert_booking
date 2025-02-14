package com.example.vannc.service;

import com.example.vannc.dto.request.BookingRequestDTO;
import com.example.vannc.entity.Booking;
import com.example.vannc.entity.Concert;
import com.example.vannc.entity.User;
import com.example.vannc.repository.BookingRepository;
import com.example.vannc.repository.ConcertRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class BookingWorker {
    private final BookingQueueService queueService;
    private final BookingRepository bookingRepository;
    private final ConcertRepository concertRepository;

    @Scheduled(fixedRate = 1000) // Mỗi giây xử lý booking
    public void processBookingQueue(String txnRef) {
        BookingRequestDTO request = queueService.getPendingBooking(txnRef);
        if (request == null) return;

        Concert concert = concertRepository.findById(request.getConcertId()).orElse(null);
        if (concert == null || concert.getAvailableSeats() < request.getQuantity()) {
            log.warn("Hết vé: " + request.getConcertId());
            return;
        }

        // Optimistic Locking: Kiểm tra trước khi cập nhật
        int updatedRows = concertRepository.updateAvailableSeats(request.getConcertId(), request.getQuantity());
        if (updatedRows == 0) {
            log.warn("Hết vé: " + request.getConcertId());
            return;
        }

        Booking booking = Booking.builder()
                .user(User.builder().id(request.getUserId()).build())
                .concert(concert)
                .quantity(request.getQuantity())
                .status(Booking.BookingStatus.CONFIRMED)
                .createdAt(LocalDateTime.now())
                .build();

        bookingRepository.save(booking);
        log.info("Đặt vé thành công: " + booking.getId());
    }
}
