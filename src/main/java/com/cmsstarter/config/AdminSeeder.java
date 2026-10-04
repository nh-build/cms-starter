package com.cmsstarter.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.cmsstarter.domain.member.Member;
import com.cmsstarter.domain.member.MemberRepository;
import com.cmsstarter.domain.member.Role;

import lombok.RequiredArgsConstructor;

/** ADMIN_PASSWORD 가 설정돼 있고 해당 이메일이 없을 때만 관리자 계정을 만든다. */
@Component
@RequiredArgsConstructor
public class AdminSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminSeeder.class);

    private final AppProperties props;
    private final MemberRepository members;
    private final PasswordEncoder encoder;

    @Override
    public void run(ApplicationArguments args) {
        AppProperties.Admin admin = props.getAdmin();
        if (admin.getPassword() == null || admin.getPassword().isBlank()) {
            log.warn("ADMIN_PASSWORD 가 비어 있어 관리자 계정을 만들지 않습니다. .env 를 확인하세요.");
            return;
        }
        String email = admin.getEmail().trim().toLowerCase();
        if (members.existsByEmail(email)) {
            return;
        }
        Member m = new Member();
        m.setEmail(email);
        m.setPasswordHash(encoder.encode(admin.getPassword()));
        m.setName(admin.getName());
        m.setRole(Role.ADMIN);
        members.save(m);
        log.info("관리자 계정을 생성했습니다: {}", email);
    }
}
