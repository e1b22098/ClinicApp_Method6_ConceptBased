package com.clinic.booking.service;

import com.clinic.booking.mapper.BusinessDayMapper;
import com.clinic.booking.model.BusinessDay;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class BusinessDayService {

    private final BusinessDayMapper businessDayMapper;

    public BusinessDayService(BusinessDayMapper businessDayMapper) {
        this.businessDayMapper = businessDayMapper;
    }

    public List<BusinessDay> getAllBusinessDays() {
        return businessDayMapper.findAll();
    }

    public List<BusinessDay> getAcceptingDays() {
        return businessDayMapper.findAcceptingDays();
    }

    public Optional<BusinessDay> getBusinessDayByDate(LocalDate date) {
        return businessDayMapper.findByDate(date);
    }

    @Transactional
    public BusinessDay createBusinessDay(BusinessDay businessDay) {
        businessDayMapper.insert(businessDay);
        return businessDay;
    }

    @Transactional
    public void updateBusinessDay(BusinessDay businessDay) {
        businessDayMapper.update(businessDay);
    }

    @Transactional
    public void deleteBusinessDay(Integer businessDayId) {
        businessDayMapper.delete(businessDayId);
    }

    @Transactional
    public void updateAcceptingStatus(Integer businessDayId, Boolean isAccepting) {
        businessDayMapper.updateAcceptingStatus(businessDayId, isAccepting);
    }
}
