package com.example.vannc.service;

import com.example.vannc.dto.request.BookingRequestDTO;
import com.example.vannc.exception.AppException;
import com.example.vannc.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VNPayService {
    @Value("${vnpay.tmnCode}")
    private String tmnCode;
    @Value("${vnpay.secretKey}")
    private String secretKey;
    @Value("${vnpay.returnUrl}")
    private String returnUrl;
    @Value("${vnpay.url}")
    private String vnpayUrl;

    public String createPaymentUrl(BookingRequestDTO request, String clientIp) {
        long amount = request.getQuantity() * 500000; // Giá vé cố định 500,000 VND

        Map<String, String> params = new HashMap<>();
        params.put("vnp_Version", "2.1.0");
        params.put("vnp_Command", "pay");
        params.put("vnp_TmnCode", tmnCode);
        params.put("vnp_Amount", String.valueOf(amount * 100)); // VNPay yêu cầu x100
        params.put("vnp_CurrCode", "VND");
        params.put("vnp_TxnRef", String.valueOf(System.currentTimeMillis()));
        params.put("vnp_OrderInfo", URLEncoder.encode("thanh+toan+dat+ve+concert", StandardCharsets.UTF_8));
        params.put("vnp_Locale", "vn");
        params.put("vnp_ReturnUrl", URLEncoder.encode(returnUrl, StandardCharsets.UTF_8));
        params.put("vnp_CreateDate", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
        params.put("vnp_IpAddr", clientIp); // Địa chỉ IP của khách hàng
        params.put("vnp_OrderType", "other"); // Mã danh mục hàng hóa, xem danh mục của VNPAY
        params.put("vnp_ExpireDate", getExpireDate(15)); // Hết hạn sau 15 phút

        // Tạo chữ ký (signature)
        String hashData = params.entrySet().stream()
                .filter(entry -> !"vnp_SecureHash".equals(entry.getKey())) // Loại bỏ vnp_SecureHash
                .sorted(Map.Entry.comparingByKey()) // Sắp xếp theo key
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("&"));


        String signature = hmacSHA512(secretKey, hashData);
        params.put("vnp_SecureHash", signature);

        // Tạo URL redirect đến VNPay
        return vnpayUrl + "?" + params.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("&"));
    }

    public String extractTxnRef(String paymentUrl) {
        try {
            URI uri = new URI(paymentUrl);
            String query = uri.getQuery(); // Lấy phần query của URL
            Map<String, String> params = parseQuery(query);
            return params.get("vnp_TxnRef");
        } catch (Exception e) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
    }

    private Map<String, String> parseQuery(String query) {
        Map<String, String> map = new HashMap<>();
        if (query == null || query.isEmpty()) {
            return map;
        }

        for (String param : query.split("&")) {
            String[] pair = param.split("=", 2); // Giữ nguyên nếu có giá trị rỗng
            if (pair.length == 2) {
                map.put(pair[0], URLDecoder.decode(pair[1], StandardCharsets.UTF_8));
            } else {
                map.put(pair[0], ""); // Tránh lỗi nếu key không có value
            }
        }
        return map;
    }

    private String hmacSHA512(String key, String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA512");
            mac.init(new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512"));
            return Hex.encodeHexString(mac.doFinal(data.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new RuntimeException("Error generating HMACSHA512", e);
        }
    }

    private String getExpireDate(int minutes) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, minutes); // Thêm số phút hết hạn
        return new SimpleDateFormat("yyyyMMddHHmmss").format(calendar.getTime());
    }

}
