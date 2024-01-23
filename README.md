$ Bakery Shop project [베이커리 쇼핑몰]

## 🧐 배경

- 스프링 부트와 Jpa, Security 등의 기술스택들을 이용해 개인 포트폴리오를 완성했지만, 관련 지식을 더 숙지할 필요성을 느껴서
  학습 목적으로 시작하게 됐습니다.

## 프로젝트 개발 환경
1.  운영체제 : Windows 10
2.  통합개발환경(IDE) : intellij
3.  JDK 버전 : JDK 11
4.  스프링 부트 버전 : 2.5.2
6.  빌드 툴 : Maven
7.  관리 툴 : GitHub

#### 주요 프레임워크 / 라이브러리
- Java 11
- SpringBoot 2.7.1
- SpringBoot Security
- Spring Data JPA
- QueryDsl

#### DataBase 
- MySql

#### 프론트엔드
- Html/Css
- Thymeleaf
- Bootstrap5

#### API
- OAuth2.0 API, Java Mail

#  📜프로젝트 구현기능

-   **회원 (Member)**
      -   회원가입 / 이메일 인증 / 임시비밀번호 전송 / 비밀번호 찾기 / 로그인 , 로그아웃 / 구글 로그인 / 유저(User), 관리자(Admin) / 내 정보

-   **상품 (Item)**
      -  상품 등록 / 상품 수정 / 상품 삭제 / 상품 리스트(메인페이지 , 카테고리 , 상품검색 ) / 상품 상세 페이지 

-   **장바구니 (Cart)**
    -   장바구니 담기 / 장바구니 조회 / 장바구니 삭제 / 장바구니 수량 가격

-  **주문 (Order)**  
    - 상품 주문 / 주문 내역 조회 / 판매 목록(Admin) / 주문취소

## 핵심 키워드
- 스프링부트, 스프링 시큐리티, JPA를 사용하여 웹 애플리케이션 기획
- 백엔드 개발의 전과정 운영 경험
- MVC 프레임워크 기반으로 백엔드 서버를 구축
- RestFur API를 설계하고 구현하는 경험
- 사용자 인증 및 권한 부여를 구현하고 애플리케이션의 보안 측면을 고려하는 경험

### 소셜 로그인 (google)
- Spring Security
- OAuth2 인증 방식 사용

`WebSecurityConfigurerAdapter` 가 Deprecated 됨에 따라 `SecurityFilterChain` 을 빈으로 등록하는 방식으로 변경  

다음의 로직들로 구현함
1. DefaultOAuth2UserService 를 상속해서 만든 커스텀 OAuth2UserService
2. UserDetails 메서드에 실제 OAuth2User 구현
3. 위의 내용들을 반영하는 SecurityConfig 설정
4. MemberService 에서 loadUserByUsername 메서드를 구현하여 회원을 찾도록 함.
5. MemberController 에서 UserDetail  
    Form 로그인이면 UserDetails,
    OAuth2 로그인이면 OAuth2User 타입으로 반환

https://github.com/dododo33/Bakery_Shop_project/assets/101379925/ffeb6800-7347-4cb6-b116-24986fc8615b   

### 회원가입 시 이메일 인증 기능 구현
- spring-boot-starter-mail 라이브러리 사용
- application.properties 에 구글 smtp 설정 추가
- mail controller와 mail service 설계
- 마주한 에러와 해결과정
    - `401 error`  

      : 다음과 같이 http.authorizeRequests()에 /mail/과 관련된 경로를 모두 permitAll() 해줌으로써 권한문제 해결
    ```java
    http.authorizeRequests()                        
                    .mvcMatchers("/css/**", "/js/**", "/img/**").permitAll()   
                    .mvcMatchers("/", "/members/**", "/item/**", "/images/**", "/mail/**").permitAll()
    ```
    
    - `403 error` 에러 발생  
    
     : csrf를 disable 함으로써 문제 해결
    ```java
    http.authorizeRequests()                       
                    .mvcMatchers("/css/**", "/js/**", "/img/**").permitAll()  
                    .mvcMatchers("/", "/members/**", "/item/**", "/images/**", "/mail/**").permitAll()
                    .mvcMatchers("/admin/**").hasRole("ADMIN")    
                    .anyRequest().authenticated()
                    .and()
                    .csrf().ignoringAntMatchers("/mail/**") // csrf disable 설정 
            ;
    ```

(이메일 전송 인증)

![1](https://github.com/dododo33/Bakery_Shop_project/assets/101379925/9f3a7e63-a35c-4236-b4d4-090b0e59779e)

![2](https://github.com/dododo33/Bakery_Shop_project/assets/101379925/9ede49ee-0039-4e26-8a62-e2d9cd0c6329)

![3](https://github.com/dododo33/Bakery_Shop_project/assets/101379925/e53be39a-c027-4122-b9c9-1c928564921d)

![4](https://github.com/dododo33/Bakery_Shop_project/assets/101379925/fd877072-ca6f-4bb6-a121-d60fc06c1256)

![5](https://github.com/dododo33/Bakery_Shop_project/assets/101379925/2814e06d-9331-48c2-84e9-51af62793504)





## 문제해결
- 401 error
     : csrf 예외처리 설정을 통해 해결    
## ERD 
  







