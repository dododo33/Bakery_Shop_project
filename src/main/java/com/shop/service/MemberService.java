package com.shop.service;

import com.shop.dto.MemberUpdateDto;
import com.shop.entity.Member;
import com.shop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ResponseBody;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {

    private final MemberRepository memberRepository;

    private final PasswordEncoder passwordEncoder;

    public Member saveMember(Member member){
        validateDuplicateMember(member);
        return memberRepository.save(member);
    }

    private void validateDuplicateMember(Member member){
        Member findMember = memberRepository.findByEmail(member.getEmail());
        if(findMember != null){
            throw new IllegalStateException("이미 가입된 회원입니다.");
        }
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Member member = memberRepository.findByEmail(email);

        if(member == null){
            throw new UsernameNotFoundException(email);
        }

        return User.builder()
                .username(member.getEmail())
                .password(member.getPassword())
                .roles(member.getRole().toString())
                .build();
    }


    @ResponseBody
    public boolean checkPassword(Member member, String checkPassword) {
        Member findMember = memberRepository.findByEmail(member.getEmail());
        if(findMember == null) {
            throw new IllegalStateException("없는 회원입니다.");
        }
        String realPassword = member.getPassword();

        boolean matches = passwordEncoder.matches(checkPassword, realPassword);
        System.out.println(matches);
        return matches;
    }


    public Long updateMember(MemberUpdateDto memberUpdateDto) {
        Member member = memberRepository.findByEmail(memberUpdateDto.getEmail());
        member.updatePassword(memberUpdateDto.getPassword());
        member.updateUsername(memberUpdateDto.getName());
        member.updateOriginalPassword(memberUpdateDto.getPassword());
        member.updateAddress(memberUpdateDto.getZipcode());

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodePw = encoder.encode(memberUpdateDto.getPassword());
        member.updatePassword(encodePw);

        memberRepository.save(member);

        return member.getId();


    }








}