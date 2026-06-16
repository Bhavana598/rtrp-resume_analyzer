package com.resume.controller;

import com.resume.service.ResumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class ResumeController {

    @Autowired
    private ResumeService resumeService;

    
    @PostMapping("/analyze")
    public String uploadResume(@RequestParam("file") MultipartFile file) {
        try {
            if (file == null || file.isEmpty()) {
                return "Please upload a valid resume file.";
            }

            String fileName = file.getOriginalFilename();

            if (fileName == null || !fileName.toLowerCase().endsWith(".pdf")) {
                return "Only PDF resumes are supported.";
            }

            return resumeService.processResume(file);

        } catch (Exception e) {
            return "Error uploading resume: " + e.getMessage();
        }
    }

    
   @PostMapping("/merge")
public String mergeResumes(
        @RequestParam("file1") MultipartFile file1,
        @RequestParam("file2") MultipartFile file2) {

    try {
        if (file1 == null || file2 == null ||
            file1.isEmpty() || file2.isEmpty()) {
            return "Please upload both resumes.";
        }

        return resumeService.mergeResumes(file1, file2);

    } catch (Exception e) {
        return "Error merging resumes: " + e.getMessage();
    }
}

    
    @PostMapping("/chat")
    public String chat(@RequestBody Map<String, String> body) {
        try {
            if (body == null || !body.containsKey("message")) {
                return "Message is required.";
            }

            return resumeService.chatWithAI(body.get("message"));

        } catch (Exception e) {
            return "Error in chat: " + e.getMessage();
        }
    }
}
