package com.cmsstarter.web.shop;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.cmsstarter.domain.member.MemberService;
import com.cmsstarter.domain.member.SignupForm;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final MemberService members;

    @GetMapping("/login")
    public String login() {
        return "shop/login";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("form", new SignupForm());
        return "shop/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("form") SignupForm form, BindingResult binding) {
        if (binding.hasErrors()) {
            return "shop/register";
        }
        try {
            members.register(form);
        } catch (IllegalArgumentException e) {
            binding.rejectValue("email", "duplicate", e.getMessage());
            return "shop/register";
        }
        return "redirect:/login?registered";
    }
}
