package com.rapidstay.xap.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.filter.CharacterEncodingFilter;

@SpringBootApplication
@ComponentScan(basePackages = {
        "com.rapidstay.xap.admin",   // ✅ admin 모듈
        "com.rapidstay.xap.common"   // ✅ common 모듈
})
@EntityScan(basePackages = {
        "com.rapidstay.xap.common.entity",   // ✅ 공통 엔티티 스캔
        "com.rapidstay.xap.admin.entity"     // ✅ admin 엔티티 추가
})
@EnableJpaRepositories(basePackages = {
        "com.rapidstay.xap.common.repository",   // ✅ 공통 레포지토리
        "com.rapidstay.xap.admin.repository"     // ✅ admin 전용 레포지토리
})
public class AdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdminApplication.class, args);
    }

    /**
     * ✅ 한글 쿼리/본문 인코딩 고정 (검색 시 로그인 튕김 방지)
     * SecurityFilterChain 보다 먼저 실행되도록 order를 가장 앞에 둔다.
     */
    @Bean(name = "adminCharacterEncodingFilter")
    public FilterRegistrationBean<CharacterEncodingFilter> characterEncodingFilter() {
        CharacterEncodingFilter filter = new CharacterEncodingFilter();
        filter.setEncoding("UTF-8");
        filter.setForceEncoding(true);

        FilterRegistrationBean<CharacterEncodingFilter> reg = new FilterRegistrationBean<>(filter);
        reg.setOrder(-101); // 최우선 실행
        return reg;
    }
}
