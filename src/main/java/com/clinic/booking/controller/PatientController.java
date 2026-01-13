package com.clinic.booking.controller;

import com.clinic.booking.model.Booking;
import com.clinic.booking.model.BusinessDay;
import com.clinic.booking.model.Patient;
import com.clinic.booking.service.BookingService;
import com.clinic.booking.service.BusinessDayService;
import com.clinic.booking.service.PatientService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Controller
@RequestMapping("/patient")
public class PatientController {

    private final PatientService patientService;
    private final BusinessDayService businessDayService;
    private final BookingService bookingService;

    public PatientController(PatientService patientService, BusinessDayService businessDayService, BookingService bookingService) {
        this.patientService = patientService;
        this.businessDayService = businessDayService;
        this.bookingService = bookingService;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "patient/login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("patient", new Patient());
        return "patient/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute Patient patient, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "patient/register";
        }

        if (patientService.findByPhoneNumber(patient.getPhoneNumber()).isPresent()) {
            result.rejectValue("phoneNumber", "error.phoneNumber", "この電話番号は既に登録されています");
            return "patient/register";
        }

        try {
            Patient registeredPatient = patientService.register(patient);
            String patientNumber = registeredPatient.getPatientNumber();
            if (patientNumber == null || patientNumber.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "診察券番号の生成に失敗しました。管理者にお問い合わせください。");
                return "redirect:/patient/register";
            }
            redirectAttributes.addFlashAttribute("message", 
                "登録が完了しました。診察券番号: " + patientNumber + " でログインしてください。");
            return "redirect:/patient/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "登録中にエラーが発生しました: " + e.getMessage());
            return "redirect:/patient/register";
        }
    }

    @GetMapping("/reset-password")
    public String resetPasswordPage() {
        return "patient/reset-password";
    }

    @PostMapping("/reset-password")
    public String requestResetPassword(@RequestParam String patientNumber, RedirectAttributes redirectAttributes) {
        try {
            String token = patientService.generateResetToken(patientNumber);
            redirectAttributes.addFlashAttribute("message", "パスワードリセット用のリンクを送信しました。");
            redirectAttributes.addFlashAttribute("resetToken", token);
            return "redirect:/patient/reset-password/" + token;
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/patient/reset-password";
        }
    }

    @GetMapping("/reset-password/{token}")
    public String resetPasswordForm(@PathVariable String token, Model model) {
        model.addAttribute("token", token);
        return "patient/reset-password-form";
    }

    @PostMapping("/reset-password/{token}")
    public String resetPassword(@PathVariable String token, @RequestParam String newPassword, RedirectAttributes redirectAttributes) {
        try {
            patientService.resetPassword(token, newPassword);
            redirectAttributes.addFlashAttribute("message", "パスワードをリセットしました。ログインしてください。");
            return "redirect:/patient/login";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/patient/reset-password/" + token;
        }
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        String patientNumber = authentication.getName();
        Patient patient = patientService.findByPatientNumber(patientNumber)
            .orElseThrow(() -> new RuntimeException("患者が見つかりません"));
        
        List<Booking> bookings = bookingService.getBookingsByPatient(patient.getPatientId());
        model.addAttribute("patient", patient);
        model.addAttribute("bookings", bookings);
        return "patient/dashboard";
    }

    @GetMapping("/booking/create")
    public String createBookingPage(Model model) {
        List<BusinessDay> businessDays = businessDayService.getAcceptingDays();
        model.addAttribute("businessDays", businessDays);
        model.addAttribute("booking", new Booking());
        return "patient/booking-create";
    }

    @PostMapping("/booking/create")
    public String createBooking(Authentication authentication, 
                               @RequestParam LocalDate bookingDate,
                               @RequestParam LocalTime bookingTime,
                               RedirectAttributes redirectAttributes) {
        String patientNumber = authentication.getName();
        Patient patient = patientService.findByPatientNumber(patientNumber)
            .orElseThrow(() -> new RuntimeException("患者が見つかりません"));

        Booking booking = new Booking();
        booking.setPatientId(patient.getPatientId());
        booking.setBookingDate(bookingDate);
        booking.setBookingTime(bookingTime);

        try {
            bookingService.createBooking(booking);
            redirectAttributes.addFlashAttribute("message", "予約が完了しました。");
            return "redirect:/patient/dashboard";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/patient/booking/create";
        }
    }

    @GetMapping("/booking/{bookingId}")
    public String viewBooking(@PathVariable Integer bookingId, Authentication authentication, Model model) {
        String patientNumber = authentication.getName();
        Patient patient = patientService.findByPatientNumber(patientNumber)
            .orElseThrow(() -> new RuntimeException("患者が見つかりません"));

        Booking booking = bookingService.getBooking(bookingId)
            .orElseThrow(() -> new RuntimeException("予約が見つかりません"));

        if (!booking.getPatientId().equals(patient.getPatientId())) {
            return "redirect:/patient/dashboard";
        }

        model.addAttribute("booking", booking);
        return "patient/booking-detail";
    }

    @PostMapping("/booking/{bookingId}/cancel")
    public String cancelBooking(@PathVariable Integer bookingId, Authentication authentication, RedirectAttributes redirectAttributes) {
        String patientNumber = authentication.getName();
        Patient patient = patientService.findByPatientNumber(patientNumber)
            .orElseThrow(() -> new RuntimeException("患者が見つかりません"));

        try {
            bookingService.cancelBooking(bookingId, patient.getPatientId());
            redirectAttributes.addFlashAttribute("message", "予約をキャンセルしました。");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/patient/dashboard";
    }
}
