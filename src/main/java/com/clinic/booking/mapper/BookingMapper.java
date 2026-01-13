package com.clinic.booking.mapper;

import com.clinic.booking.model.Booking;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Mapper
public interface BookingMapper {
    void insert(Booking booking);
    Optional<Booking> findById(@Param("bookingId") Integer bookingId);
    List<Booking> findByPatientId(@Param("patientId") Integer patientId);
    List<Booking> findByDate(@Param("bookingDate") LocalDate bookingDate);
    Optional<Booking> findByDateAndTime(@Param("bookingDate") LocalDate bookingDate, @Param("bookingTime") LocalTime bookingTime);
    void updateStatus(@Param("bookingId") Integer bookingId, @Param("status") String status);
    void delete(@Param("bookingId") Integer bookingId);
}
