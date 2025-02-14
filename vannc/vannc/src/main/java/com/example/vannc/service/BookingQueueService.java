package com.example.vannc.service;

import com.example.vannc.dto.request.BookingRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class BookingQueueService {
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String QUEUE_NAME = "bookingQueue";
    private static final String PENDING_QUEUE = "pendingBookingQueue";
    private static final String PROCESSING_QUEUE = "processingBookingQueue";

    public void savePendingBooking(String txnRef, BookingRequestDTO bookingRequest) {
        redisTemplate.opsForHash().put(QUEUE_NAME, txnRef, bookingRequest);
    }

    // Lấy booking theo txnRef
    public BookingRequestDTO getPendingBooking(String txnRef) {
        return (BookingRequestDTO) redisTemplate.opsForHash().get(QUEUE_NAME, txnRef);
    }

    // Chuyển đơn sang hàng đợi đã thanh toán
    public void moveToProcessingQueue(String txnRef) {
        BookingRequestDTO request = getPendingBooking(txnRef);
        if (request != null) {
            redisTemplate.opsForHash().put(PROCESSING_QUEUE, txnRef, request);
            redisTemplate.opsForHash().delete(PENDING_QUEUE, txnRef);
        }
    }

    // Lấy đơn đã thanh toán để xử lý trong BookingWorker
    public BookingRequestDTO popBookingToProcess() {
        Map<Object, Object> queue = redisTemplate.opsForHash().entries(PROCESSING_QUEUE);
        if (queue.isEmpty()) return null;

        Map.Entry<Object, Object> entry = queue.entrySet().iterator().next();
        String txnRef = (String) entry.getKey();
        BookingRequestDTO request = (BookingRequestDTO) entry.getValue();

        redisTemplate.opsForHash().delete(PROCESSING_QUEUE, txnRef);
        return request;
    }

    // Xóa booking khỏi queue sau khi đã xử lý xong
    public void removePendingBooking(String txnRef) {
        redisTemplate.opsForHash().delete(QUEUE_NAME, txnRef);
    }
}
