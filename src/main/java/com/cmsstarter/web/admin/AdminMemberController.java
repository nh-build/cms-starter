package com.cmsstarter.web.admin;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.cmsstarter.domain.member.MemberRepository;

import lombok.RequiredArgsConstructor;

/** 회원 관리 (MVP: 읽기 전용 목록) */
@Controller
@RequiredArgsConstructor
public class AdminMemberController {

    private final MemberRepository members;

    @GetMapping("/admin/members")
    public String list(@RequestParam(defaultValue = "0") int page, Model model) {
        model.addAttribute("active", "members");
        model.addAttribute("page", members.findAll(PageRequest.of(Math.max(page, 0), 20, Sort.by("id").descending())));
        return "admin/members";
    }
}
