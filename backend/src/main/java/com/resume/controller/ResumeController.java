package com.resume.controller;

import com.resume.service.ResumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@CrossOrigin(origins = "*") // allow frontend requests
public class ResumeController {  

    @Autowired
    private ResumeService resumeService;

    // =========================
    // 📄 RESUME UPLOAD & ANALYSIS
    // =========================
    @PostMapping("/analyze")
    public String uploadResume(@RequestParam("file") MultipartFile file) {

        try {
            if (file == null || file.isEmpty()) {
                return "Please upload a valid resume file.";
            }

            String fileName = file.getOriginalFilename();

            if (fileName == null) {
                return "Invalid file.";
            }

            fileName = fileName.toLowerCase();

            // ✅ Accept only PDF (recommended)
            if (!fileName.endsWith(".pdf")) {
                return "Only PDF resumes are supported.";
            }

            return resumeService.processResume(file);

        } catch (Exception e) {
            return "Error uploading resume: " + e.getMessage();
        }
    }

    @GetMapping("/merge")
     public String merge() {
    return resumeService.mergeResumes();
}

    // =========================
    // 💬 CHAT ENDPOINT
    // =========================
    @PostMapping("/chat")
    public String chat(@RequestBody Map<String, String> body) {

        try {
            if (body == null || !body.containsKey("message")) {
                return "Message is required.";
            }

            String message = body.get("message");

            return resumeService.chatWithAI(message);

        } catch (Exception e) {
            return "Error in chat: " + e.getMessage();
        }
    }
}