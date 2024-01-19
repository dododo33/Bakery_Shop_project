package com.shop.controller;


import com.shop.config.auto.PrincipalDetails;
import com.shop.dto.MailDto;
import com.shop.dto.MemberFormDto;
import com.shop.dto.MemberUpdateDto;
import com.shop.repository.MemberRepository;
import com.shop.service.MailService;
import com.shop.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import com.shop.entity.Member;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.validation.BindingResult;

import javax.validation.Valid;
import java.security.Principal;
import java.util.Map;

import static com.shop.constant.Role.*;
import static org.hibernate.tool.schema.SchemaToolingLogging.LOGGER;

@RequestMapping("/members")
@Controller
@RequiredArgsConstructor
public class MemberController {

    @Autowired
    private MemberRepository memberRepository;
    private final MemberService memberService;

    private final MailService mailService;
    private final PasswordEncoder passwordEncoder;


    @GetMapping(value = "/new")
    public String memberForm(Model model){
        model.addAttribute("memberFormDto", new MemberFormDto());
        return "member/memberForm";
    }

    @PostMapping(value = "/new")
    public String newMember(@Valid MemberFormDto memberFormDto, BindingResult bindingResult, Model model){

        if(bindingResult.hasErrors()){
            return "member/memberForm";
        }

        try {
            Member member = Member.createMember(memberFormDto, passwordEncoder);
            memberService.saveMember(member);
        } catch (IllegalStateException e){
            model.addAttribute("errorMessage", e.getMessage());
            return "member/memberForm";
        }

        return "redirect:/";
    }

    @GetMapping(value = "/login")
    public String loginMember(){
        return "member/memberLoginForm";
    }

    @GetMapping(value = "/login/error")
    public String loginError(Model model){
        model.addAttribute("loginErrorMsg", "아이디 또는 비밀번호를 확인해주세요");
        return "member/memberLoginForm";
    }

    @GetMapping("/form/loginInfo")
    @ResponseBody
    public String formLoginInfo(Authentication authentication, @AuthenticationPrincipal PrincipalDetails principalDetails){

        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        String member = principal.getUsername();
        System.out.println(member);

        String user1 = principalDetails.getUsername();
        System.out.println(user1);

        return member.toString();
    }


    @GetMapping("/oauth/loginInfo")
    @ResponseBody
    public String oauthLoginInfo(Authentication authentication, @AuthenticationPrincipal OAuth2User oAuth2UserPrincipal){
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oAuth2User.getAttributes();
        System.out.println(attributes);
        // PrincipalOauth2UserService의 getAttributes내용과 같음

        Map<String, Object> attributes1 = oAuth2UserPrincipal.getAttributes();
        // attributes == attributes1

        return attributes.toString();     //세션에 담긴 user가져올 수 있음
    }


    @GetMapping("/loginInfo")
    @ResponseBody
    public String loginInfo(Authentication authentication, @AuthenticationPrincipal PrincipalDetails principalDetails){
        String result = "";

        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        if(principal.getName() == null) {
            result = result + "Form 로그인 : " + principal;
        }else{
            result = result + "OAuth2 로그인 : " + principal;
        }
        return result;
    }

    @GetMapping(value = "/findMember")
    public String findMember(Model model) {
        return "/member/findMemberForm";
    }

    @Transactional
    @PostMapping("/sendEmail")
    public String sendEmail(@RequestParam("memberEmail") String memberEmail){
        MailDto dto = mailService.createMailAndChangePassword(memberEmail);
        mailService.mailSend(dto);

        return "/member/memberLoginForm";
    }

    @RequestMapping(value = "/findId", method = RequestMethod.POST)
    @ResponseBody
    public String findId(@RequestParam("memberEmail") String memberEmail) {
        String email = String.valueOf(memberRepository.findByEmail(memberEmail));
        System.out.println("회원 이메일 = " + email);

        if(email == null) {
            return null;
        }else {
            return email;
        }
    }

    @GetMapping("myInfo")
    public String memberInfo(Principal principal, ModelMap modelMap, Member member) {
        String loginId = principal.getName();
        Member memberId = memberRepository.findByEmail(loginId);
        modelMap.addAttribute("member", memberId);

        if (memberId.getRole() == USER ) {
            System.out.println("USER LOGIN");
            return "member/myInfo";
        }
        if (memberId.getRole() == ADMIN) {
            System.out.println("ADMIN LOGIN");
            return "member/myInfo";
        }
        if (memberId.getRole() == SOCIAL) {
            System.out.println("SOCIAL LOGIN");
            return "member/oauthMemberMyInfo";
        }
        return "null";
    }




    @GetMapping("checkPwdForm")
    public String checkPwdView() {
        return "member/passwordCheckForm";
    }


    @GetMapping("/checkPwd")
    @ResponseBody
    public boolean checkPassword(Principal principal, Member member, @RequestParam String checkPassword, Model model) {

        String loginId = principal.getName();
        Member memberId = memberRepository.findByEmail(loginId);
        System.out.println(memberId.getPassword());

        return memberService.checkPassword(memberId, checkPassword);
    }

    @GetMapping(value = "/updateForm")
    public String updateMemberForm(Principal principal, Model model) {
        String loginId = principal.getName();
        Member memberId = memberRepository.findByEmail(loginId);
        model.addAttribute("member", memberId);

        return "member/memberUpdateForm";
    }

    @PostMapping(value = "/updateForm")
    public String updateMember(@Valid MemberUpdateDto memberUpdateDto, Model model) {
        model.addAttribute("member", memberUpdateDto);
        memberService.updateMember(memberUpdateDto);

        return "redirect:/members/myInfo";
    }





}