package com.leetcode.webapp.controller;

import com.leetcode.webapp.model.GeminiAIResponse;
import com.leetcode.webapp.model.LeetCodeQuestion;
import com.leetcode.webapp.service.LeetCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/leetcode")
public class LeetCodeController {

    private final LeetCodeService leetCodeService;

    @Autowired
    public LeetCodeController(LeetCodeService leetCodeService) {
        this.leetCodeService = leetCodeService;
    }

    @GetMapping({"/", "/home"})  // Handles both "/" and "/home"
    public String home(Model model) {
        // You can add any necessary data to the model here
        // For example, a welcome message or a summary of the site
        model.addAttribute("message", "Welcome to the LeetCode Web App!");
        return "home"; // Name of the Thymeleaf template
    }


    @GetMapping("/questions")
    public String getAllQuestions(Model model) {
        List<LeetCodeQuestion> questions = leetCodeService.getAllQuestions();
        model.addAttribute("questions", questions);
        return "questionList"; // Name of the Thymeleaf template
    }

    @GetMapping("/questions/filter")
    public String getFilteredQuestions(
            @RequestParam(required = false) String difficulty,
            @RequestParam(required = false) String company,
            Model model) {

        List<LeetCodeQuestion> questions = leetCodeService.getQuestions(difficulty, company);
        model.addAttribute("questions", questions);
        return "questionList";
    }


    @GetMapping("/questions/{id}")
    public String getQuestionDetail(@PathVariable Long id, Model model) {
        LeetCodeQuestion question = leetCodeService.getQuestionById(id);
        if (question == null) {
            return "error"; // Or handle the error appropriately
        }
        model.addAttribute("question", question);
        return "questionDetail";
    }

    @GetMapping("/questions/{id}/evaluate")
    public String showEvaluationForm(@PathVariable Long id, Model model) {
        LeetCodeQuestion question = leetCodeService.getQuestionById(id);
        if (question == null) {
            return "error";
        }
        model.addAttribute("question", question);
        model.addAttribute("code", ""); // Empty code for the form
        return "codeEvaluationForm";
    }

    @PostMapping("/questions/{id}/evaluate")
    public String evaluateCode(
            @PathVariable Long id,
            @RequestParam String code,
            Model model) {

        LeetCodeQuestion question = leetCodeService.getQuestionById(id);
        if (question == null) {
            return "error";
        }

        GeminiAIResponse geminiResponse = leetCodeService.evaluateCode(code, question);
        model.addAttribute("question", question);
        model.addAttribute("code", code); // Return the submitted code
        model.addAttribute("geminiResponse", geminiResponse); // Pass the response
        return "evaluationResult";
    }
}