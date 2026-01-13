package com.clinic.booking.mapper;

import com.clinic.booking.model.Admin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

@Mapper
public interface AdminMapper {
    Optional<Admin> findByAdminIdString(@Param("adminIdString") String adminIdString);
}
