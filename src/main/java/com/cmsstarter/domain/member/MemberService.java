package com.cmsstarter.domain.member;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {

    private final MemberRepository members;
    private final PasswordEncoder encoder;

    @Transactional
    public Member register(SignupForm form) {
        String email = form.getEmail().trim().toLowerCase();
        if (members.existsByEmail(email)) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }
        Member m = new Member();
        m.setEmail(email);
        m.setPasswordHash(encoder.encode(form.getPassword()));
        m.setName(form.getName().trim());
        m.setPhone(form.getPhone());
        m.setRole(Role.USER);
        return members.save(m);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member m = members.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new UsernameNotFoundException(email));
        return User.withUsername(m.getEmail())
                .password(m.getPasswordHash())
                .roles(m.getRole().name())
                .build();
    }
}
