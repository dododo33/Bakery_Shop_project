package com.shop.entity;

import com.shop.constant.Role;
import com.shop.dto.MemberFormDto;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;

import javax.persistence.*;
import java.util.Collection;

@Builder
@Entity
@Table(name="member")
@Getter @Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Member extends BaseTimeEntity implements UserDetails{      // 회원 정보 저장 엔티티

    @Id
    @Column(name="member_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @Column(unique = true)              // 회원은 이메일을 통해 유일하게 구분하므로 unique 속성 지정
    private String email;

    private String originalpassword;

    private String password;

    private String picture;

    private String address;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String provider;    // oauth2를 이용한 경우 어떤 플랫폼을 이용하는지
    private String providerId;  // oauth2를 이용할 경우 아이디값




    public static Member createMember(MemberFormDto memberFormDto, PasswordEncoder passwordEncoder){
        Member member = new Member();
        member.setName(memberFormDto.getName());
        member.setEmail(memberFormDto.getEmail());
        member.setAddress(memberFormDto.getAddress());
        member.setPicture(member.getPicture());
        String password = passwordEncoder.encode(memberFormDto.getPassword()); // 스프링 시큐리티 설정 클래스에 등록한 BCryptPasswordEncoder Bean을 파라미터로 넘겨서 비밀번호 암호화
        member.setPassword(password);
        member.setRole(Role.USER);
        return member;
    }

    @Builder(builderClassName = "UserDetailRegister", builderMethodName = "userDetailRegister")
    public Member(String name, String password, String email, Role role) {
        this.name = name;
        this.password = password;
        this.email = email;
        this.role = role;
    }

    @Builder(builderClassName = "OAuth2Register", builderMethodName = "oauth2Register")
    public Member(String name, String password, String email, Role role, String provider, String providerId) {
        this.name = name;
        this.password = password;
        this.email = email;
        this.role = role;
        this.provider = provider;
        this.providerId = providerId;
    }


    public Member update(String name, String picture) {
        this.name = name;
        this.picture = picture;

        return this;
    }





    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getUsername() {
        return this.name = name;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void updateOriginalPassword(String originalpassword) {
        this.originalpassword = originalpassword;
    }

    public void updateUsername(String name) {
        this.name = name;
    }
    public void updateAddress(String address) {
        this.address = address;
    }


}
