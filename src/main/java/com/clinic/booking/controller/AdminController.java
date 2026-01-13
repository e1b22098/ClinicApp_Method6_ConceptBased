package com.clinic.booking.controller;

import com.clinic.booking.model.Booking;
import com.clinic.booking.model.BusinessDay;
import com.clinic.booking.service.AdminService;
import com.clinic.booking.service.BookingService;
import com.clinic.booking.service.BusinessDayService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final BookingService bookingService;
    private final BusinessDayService businessDayService;
    private final AdminService adminService;

    public AdminController(BookingService bookingService, BusinessDayService businessDayService, AdminService adminService) {
        this.bookingService = bookingService;
        this.businessDayService = businessDayService;
        this.adminService = adminService;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "admin/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password, RedirectAttributes redirectAttributes) {
        if (adminService.authenticate(username, password)) {
            Authentication auth = new UsernamePasswordAuthenticationToken(
                username,
                password,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))
            );
            SecurityContextHolder.getContext().setAuthentication(auth);
            return "redirect:/admin/dashboard";
        } else {
            redirectAttributes.addFlashAttribute("error", "ログインに失敗しました");
            return "redirect:/admin/login";
        }
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        List<BusinessDay> businessDays = businessDayService.getAllBusinessDays();
        model.addAttribute("businessDays", businessDays);
        return "admin/dashboard";
    }

    @GetMapping("/bookings")
    public String bookingsList(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date, Model model) {
        if (date == null) {
            date = LocalDate.now();
        }
        List<Booking> bookings = bookingService.getBookingsByDate(date);
        model.addAttribute("bookings", bookings);
        model.addAttribute("selectedDate", date);
        return "admin/bookings";
    }

    @GetMapping("/business-days")
    public String businessDaysList(Model model) {
        List<BusinessDay> businessDays = businessDayService.getAllBusinessDays();
        model.addAttribute("businessDays", businessDays);
        return "admin/business-days";
    }

    @GetMapping("/business-days/create")
    public String createBusinessDayPage(Model model) {
        model.addAttribute("businessDay", new BusinessDay());
        return "admin/business-day-form";
    }

    @PostMapping("/business-days/create")
    public String createBusinessDay(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate businessDate,
                                   @RequestParam String timeSlots,
                                   @RequestParam(defaultValue = "true") Boolean isAccepting,
                                   RedirectAttributes redirectAttributes) {
        BusinessDay businessDay = new BusinessDay();
        businessDay.setBusinessDate(businessDate);
        businessDay.setTimeSlots(timeSlots);
        businessDay.setIsAccepting(isAccepting);
        
        businessDayService.createBusinessDay(businessDay);
        redirectAttributes.addFlashAttribute("message", "営業日を登録しました。");
        return "redirect:/admin/business-days";
    }

    @PostMapping("/business-days/{businessDayId}/delete")
    public String deleteBusinessDay(@PathVariable Integer businessDayId, RedirectAttributes redirectAttributes) {
        businessDayService.deleteBusinessDay(businessDayId);
        redirectAttributes.addFlashAttribute("message", "営業日を削除しました。");
        return "redirect:/admin/business-days";
    }

    @PostMapping("/business-days/{businessDayId}/toggle-accepting")
    public String toggleAcceptingStatus(@PathVariable Integer businessDayId, RedirectAttributes redirectAttributes) {
        List<BusinessDay> allDays = businessDayService.getAllBusinessDays();
        BusinessDay businessDay = allDays.stream()
            .filter(day -> day.getBusinessDayId().equals(businessDayId))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("営業日が見つかりません"));
        
        Boolean newStatus = !businessDay.getIsAccepting();
        businessDayService.updateAcceptingStatus(businessDayId, newStatus);
        redirectAttributes.addFlashAttribute("message", 
            newStatus ? "予約受付を再開しました。" : "予約受付を停止しました。");
        return "redirect:/admin/business-days";
    }
}
