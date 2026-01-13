package com.clinic.booking.service;

import com.clinic.booking.mapper.BookingMapper;
import com.clinic.booking.mapper.BusinessDayMapper;
import com.clinic.booking.model.Booking;
import com.clinic.booking.model.BusinessDay;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * BookingServiceの単体テスト
 * JUnitおよびMockitoを用いて、正常系および異常系ユースケースを網羅
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("BookingService単体テスト")
class BookingServiceTest {

    @Mock
    private BookingMapper bookingMapper;

    @Mock
    private BusinessDayMapper businessDayMapper;

    @InjectMocks
    private BookingService bookingService;

    private Booking testBooking;
    private BusinessDay testBusinessDay;
    private LocalDate testDate;
    private LocalTime testTime;

    @BeforeEach
    void setUp() {
        testDate = LocalDate.of(2024, 1, 15);
        testTime = LocalTime.of(10, 0);
        
        testBooking = new Booking();
        testBooking.setBookingId(1);
        testBooking.setPatientId(1);
        testBooking.setBookingDate(testDate);
        testBooking.setBookingTime(testTime);
        testBooking.setStatus("ACTIVE");

        testBusinessDay = new BusinessDay();
        testBusinessDay.setBusinessDayId(1);
        testBusinessDay.setBusinessDate(testDate);
        testBusinessDay.setIsAccepting(true);
        testBusinessDay.setTimeSlots("09:00,10:00,11:00,14:00,15:00,16:00");
    }

    @Test
    @DisplayName("正常系: 予約作成が成功する")
    void testCreateBooking_Success() {
        // Given
        when(businessDayMapper.findByDate(testDate)).thenReturn(Optional.of(testBusinessDay));
        when(bookingMapper.findByDateAndTime(testDate, testTime)).thenReturn(Optional.empty());
        doAnswer(invocation -> {
            Booking booking = invocation.getArgument(0);
            booking.setBookingId(1);
            return null;
        }).when(bookingMapper).insert(any(Booking.class));

        Booking newBooking = new Booking();
        newBooking.setPatientId(1);
        newBooking.setBookingDate(testDate);
        newBooking.setBookingTime(testTime);

        // When
        Booking result = bookingService.createBooking(newBooking);

        // Then
        assertNotNull(result);
        assertEquals("ACTIVE", result.getStatus());
        verify(businessDayMapper, times(1)).findByDate(testDate);
        verify(bookingMapper, times(1)).findByDateAndTime(testDate, testTime);
        verify(bookingMapper, times(1)).insert(any(Booking.class));
    }

    @Test
    @DisplayName("異常系: 営業日でない日付で予約作成を試みた場合、例外が発生する")
    void testCreateBooking_NotBusinessDay() {
        // Given
        when(businessDayMapper.findByDate(testDate)).thenReturn(Optional.empty());

        Booking newBooking = new Booking();
        newBooking.setPatientId(1);
        newBooking.setBookingDate(testDate);
        newBooking.setBookingTime(testTime);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            bookingService.createBooking(newBooking);
        });

        assertEquals("指定された日付は営業日ではありません", exception.getMessage());
        verify(businessDayMapper, times(1)).findByDate(testDate);
        verify(bookingMapper, never()).insert(any(Booking.class));
    }

    @Test
    @DisplayName("異常系: 予約受付停止中の日付で予約作成を試みた場合、例外が発生する")
    void testCreateBooking_NotAccepting() {
        // Given
        testBusinessDay.setIsAccepting(false);
        when(businessDayMapper.findByDate(testDate)).thenReturn(Optional.of(testBusinessDay));

        Booking newBooking = new Booking();
        newBooking.setPatientId(1);
        newBooking.setBookingDate(testDate);
        newBooking.setBookingTime(testTime);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            bookingService.createBooking(newBooking);
        });

        assertEquals("指定された日付は予約受付を停止しています", exception.getMessage());
        verify(businessDayMapper, times(1)).findByDate(testDate);
        verify(bookingMapper, never()).insert(any(Booking.class));
    }

    @Test
    @DisplayName("異常系: 利用不可な時間枠で予約作成を試みた場合、例外が発生する")
    void testCreateBooking_InvalidTimeSlot() {
        // Given
        LocalTime invalidTime = LocalTime.of(13, 0); // 時間枠に含まれていない時間
        when(businessDayMapper.findByDate(testDate)).thenReturn(Optional.of(testBusinessDay));

        Booking newBooking = new Booking();
        newBooking.setPatientId(1);
        newBooking.setBookingDate(testDate);
        newBooking.setBookingTime(invalidTime);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            bookingService.createBooking(newBooking);
        });

        assertEquals("指定された時間枠は利用できません", exception.getMessage());
        verify(businessDayMapper, times(1)).findByDate(testDate);
        verify(bookingMapper, never()).insert(any(Booking.class));
    }

    @Test
    @DisplayName("異常系: 既に予約が存在する日時で予約作成を試みた場合、例外が発生する")
    void testCreateBooking_DuplicateBooking() {
        // Given
        when(businessDayMapper.findByDate(testDate)).thenReturn(Optional.of(testBusinessDay));
        when(bookingMapper.findByDateAndTime(testDate, testTime)).thenReturn(Optional.of(testBooking));

        Booking newBooking = new Booking();
        newBooking.setPatientId(1);
        newBooking.setBookingDate(testDate);
        newBooking.setBookingTime(testTime);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            bookingService.createBooking(newBooking);
        });

        assertEquals("指定された日時は既に予約が入っています", exception.getMessage());
        verify(businessDayMapper, times(1)).findByDate(testDate);
        verify(bookingMapper, times(1)).findByDateAndTime(testDate, testTime);
        verify(bookingMapper, never()).insert(any(Booking.class));
    }

    @Test
    @DisplayName("正常系: 予約IDで予約を取得できる")
    void testGetBooking_Success() {
        // Given
        when(bookingMapper.findById(1)).thenReturn(Optional.of(testBooking));

        // When
        Optional<Booking> result = bookingService.getBooking(1);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testBooking.getBookingId(), result.get().getBookingId());
        verify(bookingMapper, times(1)).findById(1);
    }

    @Test
    @DisplayName("正常系: 存在しない予約IDで取得を試みた場合、空のOptionalが返る")
    void testGetBooking_NotFound() {
        // Given
        when(bookingMapper.findById(999)).thenReturn(Optional.empty());

        // When
        Optional<Booking> result = bookingService.getBooking(999);

        // Then
        assertTrue(result.isEmpty());
        verify(bookingMapper, times(1)).findById(999);
    }

    @Test
    @DisplayName("正常系: 患者IDで予約一覧を取得できる")
    void testGetBookingsByPatient_Success() {
        // Given
        List<Booking> bookings = List.of(testBooking);
        when(bookingMapper.findByPatientId(1)).thenReturn(bookings);

        // When
        List<Booking> result = bookingService.getBookingsByPatient(1);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(bookingMapper, times(1)).findByPatientId(1);
    }

    @Test
    @DisplayName("正常系: 日付で予約一覧を取得できる")
    void testGetBookingsByDate_Success() {
        // Given
        List<Booking> bookings = List.of(testBooking);
        when(bookingMapper.findByDate(testDate)).thenReturn(bookings);

        // When
        List<Booking> result = bookingService.getBookingsByDate(testDate);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(bookingMapper, times(1)).findByDate(testDate);
    }

    @Test
    @DisplayName("正常系: 予約キャンセルが成功する")
    void testCancelBooking_Success() {
        // Given
        Integer bookingId = 1;
        Integer patientId = 1;
        when(bookingMapper.findById(bookingId)).thenReturn(Optional.of(testBooking));

        // When
        bookingService.cancelBooking(bookingId, patientId);

        // Then
        verify(bookingMapper, times(1)).findById(bookingId);
        verify(bookingMapper, times(1)).updateStatus(bookingId, "CANCELLED");
    }

    @Test
    @DisplayName("異常系: 存在しない予約IDでキャンセルを試みた場合、例外が発生する")
    void testCancelBooking_NotFound() {
        // Given
        Integer bookingId = 999;
        Integer patientId = 1;
        when(bookingMapper.findById(bookingId)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            bookingService.cancelBooking(bookingId, patientId);
        });

        assertEquals("予約が見つかりません", exception.getMessage());
        verify(bookingMapper, times(1)).findById(bookingId);
        verify(bookingMapper, never()).updateStatus(anyInt(), anyString());
    }

    @Test
    @DisplayName("異常系: 他の患者の予約をキャンセルしようとした場合、例外が発生する")
    void testCancelBooking_Unauthorized() {
        // Given
        Integer bookingId = 1;
        Integer differentPatientId = 2; // 異なる患者ID
        when(bookingMapper.findById(bookingId)).thenReturn(Optional.of(testBooking));

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            bookingService.cancelBooking(bookingId, differentPatientId);
        });

        assertEquals("この予約をキャンセルする権限がありません", exception.getMessage());
        verify(bookingMapper, times(1)).findById(bookingId);
        verify(bookingMapper, never()).updateStatus(anyInt(), anyString());
    }

    @Test
    @DisplayName("異常系: 既にキャンセル済みの予約をキャンセルしようとした場合、例外が発生する")
    void testCancelBooking_AlreadyCancelled() {
        // Given
        Integer bookingId = 1;
        Integer patientId = 1;
        testBooking.setStatus("CANCELLED");
        when(bookingMapper.findById(bookingId)).thenReturn(Optional.of(testBooking));

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            bookingService.cancelBooking(bookingId, patientId);
        });

        assertEquals("既にキャンセル済みの予約です", exception.getMessage());
        verify(bookingMapper, times(1)).findById(bookingId);
        verify(bookingMapper, never()).updateStatus(anyInt(), anyString());
    }
}
