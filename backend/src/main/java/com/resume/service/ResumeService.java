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


   
    public String processResume(MultipartFile file) {
        try {

            if (!"application/pdf".equals(file.getContentType())) {
                return "Please upload a valid PDF file.";
            }

            String text = ResumeParser.extractText(file);

            if (text == null || text.trim().isEmpty()) {
                return "Could not extract text from PDF.";
            }

           
            lastResumeText = text;
            resumes.add(text);

            if (resumes.size() > 3) {
                resumes.remove(0);
            }

           
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


    public String chatWithAI(String message) {

        try {
            if (message == null || message.trim().isEmpty()) {
                return "Please enter a message.";
            }

            chatHistory.add("User: " + message);

           
            if (chatHistory.size() > 6) {
                chatHistory.remove(0);
            }

            StringBuilder prompt = new StringBuilder();

            prompt.append("You are a career assistant.\n")
                  .append("Always answer using the resume provided.\n\n");

           
            if (lastResumeText != null && !lastResumeText.isEmpty()) {
                prompt.append("RESUME:\n")
                      .append(lastResumeText)
                      .append("\n\n");
            }

           
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


   
    public String mergeResumes(MultipartFile file1, MultipartFile file2) {

    try {
        String text1 = ResumeParser.extractText(file1);
        String text2 = ResumeParser.extractText(file2);

        if (text1 == null || text2 == null ||
            text1.isEmpty() || text2.isEmpty()) {
            return "Error reading resumes.";
        }

        String prompt =
            "You are an expert resume builder.\n\n" +

            "I have TWO resumes of the SAME person:\n" +
            "1. OLD RESUME\n" +
            "2. UPDATED RESUME\n\n" +

            "Your task:\n" +
            "- Compare both resumes carefully\n" +
            "- Combine them into ONE FINAL resume\n" +
            "- Remove duplicates\n" +
            "- Keep latest and best information\n" +
            "- Improve wording professionally\n\n" +

            "OLD RESUME:\n" + text1 + "\n\n" +
            "UPDATED RESUME:\n" + text2 + "\n\n" +

            "FINAL OUTPUT FORMAT:\n" +
            "Name\nContact\nSummary\nEducation\nSkills\nProjects\nProfiles\nAchievements\nCertifications\n";
  
        return openAIService.analyzeResume(prompt);

    } catch (Exception e) {
        return "Error merging resumes: " + e.getMessage();
    }
}
}
