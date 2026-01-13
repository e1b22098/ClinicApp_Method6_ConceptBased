package com.clinic.booking.service;

import com.clinic.booking.mapper.BookingMapper;
import com.clinic.booking.mapper.BusinessDayMapper;
import com.clinic.booking.model.Booking;
import com.clinic.booking.model.BusinessDay;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

/**
 * 予約処理の中核となるビジネスロジックを提供するサービス
 */
@Service
public class BookingService {

    private final BookingMapper bookingMapper;
    private final BusinessDayMapper businessDayMapper;

    public BookingService(BookingMapper bookingMapper, BusinessDayMapper businessDayMapper) {
        this.bookingMapper = bookingMapper;
        this.businessDayMapper = businessDayMapper;
    }

    /**
     * 予約を作成する
     * 
     * @param booking 予約情報
     * @return 作成された予約
     * @throws IllegalArgumentException 営業日でない、予約受付停止中、時間枠が利用不可、既に予約が存在する場合
     */
    @Transactional
    public Booking createBooking(Booking booking) {
        // 営業日チェック
        Optional<BusinessDay> businessDayOpt = businessDayMapper.findByDate(booking.getBookingDate());
        if (businessDayOpt.isEmpty()) {
            throw new IllegalArgumentException("指定された日付は営業日ではありません");
        }

        BusinessDay businessDay = businessDayOpt.get();
        
        // 予約受付停止チェック
        if (!businessDay.getIsAccepting()) {
            throw new IllegalArgumentException("指定された日付は予約受付を停止しています");
        }

        // 時間枠チェック
        if (!isTimeSlotAvailable(businessDay.getTimeSlots(), booking.getBookingTime())) {
            throw new IllegalArgumentException("指定された時間枠は利用できません");
        }

        // 既存予約チェック（重複防止）
        Optional<Booking> existingBooking = bookingMapper.findByDateAndTime(
            booking.getBookingDate(), 
            booking.getBookingTime()
        );
        if (existingBooking.isPresent()) {
            throw new IllegalArgumentException("指定された日時は既に予約が入っています");
        }

        // 予約作成
        booking.setStatus("ACTIVE");
        bookingMapper.insert(booking);
        return booking;
    }

    /**
     * 予約を取得する
     * 
     * @param bookingId 予約ID
     * @return 予約情報
     */
    public Optional<Booking> getBooking(Integer bookingId) {
        return bookingMapper.findById(bookingId);
    }

    /**
     * 患者の予約一覧を取得する
     * 
     * @param patientId 患者ID
     * @return 予約一覧
     */
    public List<Booking> getBookingsByPatient(Integer patientId) {
        return bookingMapper.findByPatientId(patientId);
    }

    /**
     * 指定日付の予約一覧を取得する
     * 
     * @param bookingDate 予約日付
     * @return 予約一覧
     */
    public List<Booking> getBookingsByDate(LocalDate bookingDate) {
        return bookingMapper.findByDate(bookingDate);
    }

    /**
     * 予約をキャンセルする
     * 
     * @param bookingId 予約ID
     * @param patientId 患者ID（認可チェック用）
     * @throws IllegalArgumentException 予約が見つからない、または患者が一致しない場合
     */
    @Transactional
    public void cancelBooking(Integer bookingId, Integer patientId) {
        Optional<Booking> bookingOpt = bookingMapper.findById(bookingId);
        if (bookingOpt.isEmpty()) {
            throw new IllegalArgumentException("予約が見つかりません");
        }

        Booking booking = bookingOpt.get();
        if (!booking.getPatientId().equals(patientId)) {
            throw new IllegalArgumentException("この予約をキャンセルする権限がありません");
        }

        if (!"ACTIVE".equals(booking.getStatus())) {
            throw new IllegalArgumentException("既にキャンセル済みの予約です");
        }

        bookingMapper.updateStatus(bookingId, "CANCELLED");
    }

    /**
     * 時間枠が利用可能かチェックする
     * 
     * @param timeSlots 利用可能な時間枠（カンマ区切り文字列）
     * @param bookingTime 予約したい時間
     * @return 利用可能な場合true
     */
    private boolean isTimeSlotAvailable(String timeSlots, LocalTime bookingTime) {
        if (timeSlots == null || timeSlots.trim().isEmpty()) {
            return false;
        }
        
        String[] slots = timeSlots.split(",");
        String bookingTimeStr = bookingTime.toString();
        
        for (String slot : slots) {
            if (slot.trim().equals(bookingTimeStr)) {
                return true;
            }
        }
        return false;
    }
}
