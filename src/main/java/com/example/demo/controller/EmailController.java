package com.example.demo.controller;

import com.example.demo.domain.User;
import com.example.demo.service.EmailService;
import com.example.demo.service.UserService;
import com.example.demo.util.annontation.ApiMessage;
import com.example.demo.util.error.IdInvalidException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class EmailController {
    private EmailService emailService;
    private final UserService userService;
    public EmailController(EmailService emailService, UserService userService) {
        this.emailService = emailService;
        this.userService = userService;
    }
//    @Scheduled(cron = "*/60 * * * * *")
    @GetMapping("/email")
    @ApiMessage("Send simple email")
    public String sendSimpleEmail() {
        this.emailService.sendSubscribersEmailJobs();
        return "Simple email";
    }
    @PostMapping("/forget/pass")
    @ApiMessage("Forget a password")
    public ResponseEntity<String> forgetPass(@RequestParam("email") String email) {
        User user = this.userService.findByEmail(email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Email không tồn tại!");
        }
        this.emailService.sendCode(email);
        return ResponseEntity.ok().body("Mã khôi phục mật khẩu đã được gửi tới email của bạn.");
    }
}
