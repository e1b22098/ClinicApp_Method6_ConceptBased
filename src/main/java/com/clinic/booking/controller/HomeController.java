package com.clinic.booking.controller;

import com.clinic.booking.service.PatientService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class HomeController {

    private final PatientService patientService;

    public HomeController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping("/")
    public String home() {
        return "index";
    }

    /**
     * 一時的なエンドポイント: パスワード更新ページを表示
     * 注意: 本番環境では削除してください
     */
    @GetMapping("/admin/update-all-passwords")
    public String updatePasswordsPage() {
        return "update-passwords";
    }

    /**
     * 一時的なエンドポイント: すべての患者のパスワードを "test1234" に更新
     * 注意: 本番環境では削除してください
     */
    @PostMapping("/admin/update-all-passwords")
    public String updateAllPasswords(@RequestParam(defaultValue = "test1234") String password, RedirectAttributes redirectAttributes) {
        try {
            int updatedCount = patientService.updateAllPasswords(password);
            redirectAttributes.addFlashAttribute("message", 
                updatedCount + "件の患者のパスワードを \"" + password + "\" に更新しました。");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "パスワードの更新に失敗しました: " + e.getMessage());
        }
        return "redirect:/admin/update-all-passwords";
    }
}
