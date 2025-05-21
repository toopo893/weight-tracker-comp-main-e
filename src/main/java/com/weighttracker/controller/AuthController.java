package com.weighttracker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.weighttracker.entity.User;
import com.weighttracker.service.UserService;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;
    
    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }
    
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        return "register";
    }
    
    @PostMapping("/register")
    public String registerUser(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            @RequestParam("confirmPassword") String confirmPassword,
            @RequestParam("email") String email,
            RedirectAttributes redirectAttributes) {
        
        // パスワード確認のチェック
        if (!password.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "パスワードが一致しません");
            return "redirect:/register";
        }
        
        try {
            @SuppressWarnings("unused")
            User user = userService.registerUser(username, password, email);
            redirectAttributes.addFlashAttribute("success", "登録が完了しました。ログインしてください。");
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/register";
        }
    }
}