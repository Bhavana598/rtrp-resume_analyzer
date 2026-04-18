package com.resume.service;

import java.util.ArrayList;
import java.util.List;

import com.resume.util.ResumeParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ResumeService {

    @Autowired
    private OpenAIService openAIService;

    private String lastResumeText = "";
    private List<String> resumes = new ArrayList<>();
    private List<String> chatHistory = new ArrayList<>();


    // =========================
    // 📄 RESUME ANALYSIS
    // =========================
    public String processResume(MultipartFile file) {
        try {

            if (!"application/pdf".equals(file.getContentType())) {
                return "Please upload a valid PDF file.";
            }

            String text = ResumeParser.extractText(file);

            if (text == null || text.trim().isEmpty()) {
                return "Could not extract text from PDF.";
            }

            // ✅ Save resume
            lastResumeText = text;
            resumes.add(text);

            if (resumes.size() > 3) {
                resumes.remove(0);
            }

            // 🔥 Reset chat
            chatHistory.clear();

            String prompt = "You are an HR expert.\n\n"
                    + "Analyze this resume and provide:\n"
                    + "1. Skills\n"
                    + "2. Missing Skills\n"
                    + "3. Suggestions\n"
                    + "4. Job Roles\n"
                    + "5. Score out of 10\n\n"
                    + "Resume:\n" + text;

            return openAIService.analyzeResume(prompt);

        } catch (Exception e) {
            return "Error processing resume: " + e.getMessage();
        }
    }


    // =========================
    // 💬 CHAT WITH AI
    // =========================
    public String chatWithAI(String message) {

        try {
            if (message == null || message.trim().isEmpty()) {
                return "Please enter a message.";
            }

            chatHistory.add("User: " + message);

            // 🔥 Limit history (important)
            if (chatHistory.size() > 6) {
                chatHistory.remove(0);
            }

            StringBuilder prompt = new StringBuilder();

            prompt.append("You are a career assistant.\n")
                  .append("Always answer using the resume provided.\n\n");

            // ✅ Attach resume
            if (lastResumeText != null && !lastResumeText.isEmpty()) {
                prompt.append("RESUME:\n")
                      .append(lastResumeText)
                      .append("\n\n");
            }

            // ✅ Attach chat history
            prompt.append("CONVERSATION:\n");

            for (String msg : chatHistory) {
                prompt.append(msg).append("\n");
            }

            prompt.append("\nNow answer the latest question clearly:\n");

            String response = openAIService.analyzeResume(prompt.toString());

            chatHistory.add("Assistant: " + response);

            return response;

        } catch (Exception e) {
            return "Error processing chat: " + e.getMessage();
        }
    }


    // =========================
    // ⚖ COMPARE RESUMES
    // =========================
    public String compareResumes() {

        if (resumes.size() < 2) {
            return "Upload at least 2 resumes.";
        }

        StringBuilder prompt = new StringBuilder();

        prompt.append("Compare these resumes and provide:\n")
              .append("1. Strengths of each\n")
              .append("2. Weaknesses\n")
              .append("3. Best candidate\n")
              .append("4. Ranking\n\n");

        for (int i = 0; i < resumes.size(); i++) {
            prompt.append("Resume ").append(i + 1).append(":\n")
                  .append(resumes.get(i))
                  .append("\n\n");
        }

        return openAIService.analyzeResume(prompt.toString());
    }


    // =========================
    // 🧠 MERGE RESUMES
    // =========================
    public String mergeResumes() {

    if (resumes.size() < 2) {
        return "Upload at least 2 resumes.";
    }

    String oldResume = resumes.get(0);
    String newResume = resumes.get(1);

    String prompt = 
        "You are an expert resume builder.\n\n" +

        "I have TWO resumes of the SAME person:\n" +
        "1. OLD RESUME\n" +
        "2. UPDATED RESUME\n\n" +

        "Your task:\n" +
        "1. Identify improvements in the new resume\n" +
        "2. Combine BOTH resumes\n" +
        "3. KEEP all useful details from both\n" +
        "4. REMOVE duplicates\n" +
        "5. Create ONE FINAL PROFESSIONAL RESUME\n" +
        "6. Make it ATS-friendly, clean, and well structured\n\n" +

        "OLD RESUME:\n" + oldResume + "\n\n" +
        "UPDATED RESUME:\n" + newResume + "\n\n" +

        "FINAL OUTPUT FORMAT:\n" +
        "Name\nContact\nSummary\nEducation\nSkills\nProjects\nProfiles\nAchievements\nCertifications\n\n" +

        "IMPORTANT:\n" +
        "- Do NOT say resumes are identical\n" +
        "- Actually compare content carefully\n" +
        "- Include NEW skills, links, certifications\n" +
        "- Improve wording professionally\n";

    return openAIService.analyzeResume(prompt);
}
}