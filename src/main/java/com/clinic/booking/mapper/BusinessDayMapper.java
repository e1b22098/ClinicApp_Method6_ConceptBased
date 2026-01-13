package com.clinic.booking.mapper;

import com.clinic.booking.model.BusinessDay;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Mapper
public interface BusinessDayMapper {
    List<BusinessDay> findAll();
    List<BusinessDay> findAcceptingDays();
    Optional<BusinessDay> findByDate(@Param("businessDate") LocalDate businessDate);
    void insert(BusinessDay businessDay);
    void update(BusinessDay businessDay);
    void delete(@Param("businessDayId") Integer businessDayId);
    void updateAcceptingStatus(@Param("businessDayId") Integer businessDayId, @Param("isAccepting") Boolean isAccepting);
}
