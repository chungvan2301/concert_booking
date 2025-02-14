package com.example.vannc.repository;

import com.example.vannc.entity.Concert;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ConcertRepository extends JpaRepository<Concert,String> {
    List<Concert> findAllByDateAfterOrderByIdAsc(LocalDateTime date, Pageable pageable);

    List<Concert> findByIdGreaterThanAndDateAfterOrderByIdAsc(String lastId, LocalDateTime date, Pageable pageable);


    @Modifying
    @Transactional
    @Query("UPDATE Concert c SET c.availableSeats = c.availableSeats - :quantity WHERE c.id = :id AND c.availableSeats >= :quantity")
    int updateAvailableSeats(@Param("id") String id, @Param("quantity") int quantity);
}
