package com.sparta.hh99_actualproject.config;

import com.sparta.hh99_actualproject.jwt.JwtAccessDeniedHandler;
import com.sparta.hh99_actualproject.jwt.JwtAuthenticationEntryPoint;
import com.sparta.hh99_actualproject.jwt.JwtSecurityConfig;
import com.sparta.hh99_actualproject.jwt.TokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.net.ssl.HttpsURLConnection;

@EnableWebSecurity //스프링 시큐리티를 사용하기 위함
@EnableGlobalMethodSecurity(prePostEnabled = true) //@PreAuthorize 어노테이션을 사용하기 위함
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final TokenProvider tokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;


    public SecurityConfig(
            TokenProvider tokenProvider,
            JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
            JwtAccessDeniedHandler jwtAccessDeniedHandler
    ) {
        this.tokenProvider = tokenProvider;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring()
                .antMatchers(
                        "/h2-console/**"
                        , "/favicon.ico"
                )
                //Swagger 권한설정X
                .antMatchers("/v2/api-docs",
                        "/swagger-resources/**",
                        "/swagger-ui.html",
                        "/webjars/**",
                        "/swagger/**");

    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {

        httpSecurity
                // token을 사용하는 방식이기 때문에 csrf를 disable합니다.
                .csrf().disable()

                // cors 설정
                .cors()
                .and()

                //Exception을 핸들링할때 사용할 클래스들을 추가
                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler)

                //h2-console 을 사용하기 위해서 Header 옵션 변경
                .and()
                .headers()
                .frameOptions()
                .sameOrigin()

                // 세션을 사용하지 않기 때문에 STATELESS로 설정
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()
                .authorizeRequests()
                .antMatchers("/api/test/**").permitAll() //Test용 메서드는 권한설정X
                .antMatchers("/user/signup").permitAll() //회원가입 메서드 권한설정X
                .antMatchers("/user/memberIdCheck").permitAll() //중복체크 메서드 권한설정X
                .antMatchers("/user/nicknameCheck").permitAll() //중복체크 메서드 권한설정X
                .antMatchers("/user/login").permitAll() //로그인 메서드 권한설정X
                .antMatchers("/user/kakao/callback").permitAll() //소셜 로그인 메서드 권한설정X

                .antMatchers("/swagger-ui.html").permitAll() //스웨거 권한설정 X
                .antMatchers("/webjars/**").permitAll() //스웨거 권한설정 X
                .antMatchers("/**").permitAll() //스웨거 권한설정 X
                .anyRequest().authenticated() // 나머지 API는 권한 설정

                //JWTFilter 를 addFilterBefore 로 등록했던 JwtSecurityConfig 클래스도 적용
                .and()
                .apply(new JwtSecurityConfig(tokenProvider));
    }

}