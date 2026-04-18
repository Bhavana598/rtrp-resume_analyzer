package com.resume.service;

import java.util.*;

public class LLMService {

    static List<String> knownSkills = Arrays.asList(
            "java","python","c","c++","javascript","html","css",
            "react","spring","mysql","mongodb","node","aws",
            "machine learning","data science","android"
    );

    static List<String> suggestions = Arrays.asList(
            "Build more real-world projects",
            "Improve GitHub portfolio",
            "Practice Data Structures & Algorithms",
            "Add internship experience",
            "Improve system design knowledge",
            "Contribute to open source",
            "Earn cloud certifications"
    );

    static List<String> jobRoles = Arrays.asList(
            "Software Developer",
            "Backend Engineer",
            "Frontend Developer",
            "Full Stack Developer",
            "Data Analyst",
            "Machine Learning Engineer"
    );

    public static String analyze(String resumeText) {

        resumeText = resumeText.toLowerCase();
        Random rand = new Random();

        List<String> foundSkills = new ArrayList<>();

        for(String skill : knownSkills){
            if(resumeText.contains(skill)){
                foundSkills.add(skill);
            }
        }

        
        List<String> missingSkills = new ArrayList<>(knownSkills);
        missingSkills.removeAll(foundSkills);
        Collections.shuffle(missingSkills);

        
        Collections.shuffle(suggestions);
        Collections.shuffle(jobRoles);

        StringBuilder result = new StringBuilder();

        result.append("Skills Identified:\n");
        if(foundSkills.isEmpty()){
            result.append("- Basic Programming Knowledge\n");
        }else{
            for(String s: foundSkills)
                result.append("- ").append(s).append("\n");
        }

        result.append("\nMissing Skills:\n");
        for(int i=0;i<3;i++)
            result.append("- ").append(missingSkills.get(i)).append("\n");

        result.append("\nSuggestions:\n");
        for(int i=0;i<3;i++)
            result.append("- ").append(suggestions.get(i)).append("\n");

        result.append("\nRecommended Job Roles:\n");
        for(int i=0;i<2;i++)
            result.append("- ").append(jobRoles.get(i)).append("\n");

        return result.toString();
    }
}