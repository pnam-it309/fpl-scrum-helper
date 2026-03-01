package udpm.hn.server.infrastructure.security.config;
import java.util.Arrays;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import udpm.hn.server.infrastructure.constant.MappingConstants;
import udpm.hn.server.infrastructure.constant.Role;
import udpm.hn.server.infrastructure.exception.RestAuthenticationEntryPoint;
import udpm.hn.server.infrastructure.security.filter.TokenAuthenticationFilter;
import udpm.hn.server.infrastructure.security.oauth2.CustomOAuth2UserService;
import udpm.hn.server.infrastructure.security.oauth2.HttpCookieOAuth2AuthorizationRequestRepository;
import udpm.hn.server.infrastructure.security.oauth2.OAuth2AuthenticationFailureHandler;
import udpm.hn.server.infrastructure.security.oauth2.OAuth2AuthenticationSuccessHandler;
import udpm.hn.server.utils.Helper;

import java.util.Collections;
import java.util.List;

import static udpm.hn.server.utils.Helper.appendWildcard;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true,
        prePostEnabled = true
)
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;

    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

    private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;

    private final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

    @Value("${frontend.url}")
    private String allowedOrigin;

    @Bean
    public TokenAuthenticationFilter tokenAuthenticationFilter() {
        return new TokenAuthenticationFilter();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    public AuthenticationManager authenticationManager(
            PasswordEncoder passwordEncoder,
            UserDetailsService userDetailsService
    ) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder);
        provider.setUserDetailsService(userDetailsService);
        return new ProviderManager(provider);
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        
        log.info("CORS configuration loading with allowedOrigin: {}", allowedOrigin);
        
        if (allowedOrigin != null && !allowedOrigin.isEmpty()) {
            String[] origins = allowedOrigin.split("\\s*,\\s*");
            List<String> originList = Arrays.asList(origins);
            log.info("Setting allowed origins: {}", originList);
            config.setAllowedOriginPatterns(originList);
        } else {
            log.warn("No ALLOWED_ORIGIN found, defaulting to *");
            config.addAllowedOriginPattern("*");
        }
        
        config.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type", "*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        config.setAllowCredentials(true);
        config.setExposedHeaders(List.of("Authorization"));
        
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        log.info("Đã chạy vào filterchain");

        http.csrf(AbstractHttpConfigurer::disable);
        http.cors(c -> c.configurationSource(corsConfigurationSource()));
        http.sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.formLogin(AbstractHttpConfigurer::disable);
        http.httpBasic(AbstractHttpConfigurer::disable);
        http.exceptionHandling(e -> e.authenticationEntryPoint(new RestAuthenticationEntryPoint()));
        http.authorizeHttpRequests(auth -> auth.requestMatchers(
                        "/",
                        "/error",
                        "/favicon.ico",
                        "/*/*.png",
                        "/*/*.gif",
                        "/*/*.svg",
                        "/*/*.jpg",
                        "/*/*.html",
                        "/*/*.css",
                        "/*/*.js"
                )
                .permitAll());
                auth -> auth.requestMatchers(
                                "/ws/**",
                                "/auth/**",
                                Helper.appendWildcard(MappingConstants.API_AUTH_PREFIX),
                                "/oauth2/**"
                        )
                        .permitAll()
        );

        // API public
        http.authorizeHttpRequests(
                auth -> auth
                        .requestMatchers(Helper.appendWildcard(MappingConstants.API_ADMIN_STAFF+"/facility")).permitAll()
                        .requestMatchers(Helper.appendWildcard(MappingConstants.API_ADMIN_STAFF+"/department")).permitAll()
                        .requestMatchers(Helper.appendWildcard(MappingConstants.API_ADMIN_STAFF+"/major-department")).permitAll()
                        .requestMatchers(Helper.appendWildcard(MappingConstants.API_ADMIN_CATEGORY)).permitAll()
                        .requestMatchers(Helper.appendWildcard(MappingConstants.API_ADMIN_PROJECT)).permitAll()
                        .requestMatchers(Helper.appendWildcard(MappingConstants.API_PERMITALL_PREFIX)).permitAll()

        );
        // API ADMIN và Member
        http.authorizeHttpRequests(
                auth -> auth
// dược thêm

                        .requestMatchers(Helper.appendWildcard(MappingConstants.API_PROJECT_DETAILS_LABEL_PROJECT)).hasAnyAuthority(Role.QUAN_LY.name(),Role.THANH_VIEN.name())
                        .requestMatchers(Helper.appendWildcard(MappingConstants.API_MEMBER_TODO_VOTE)).hasAnyAuthority(Role.QUAN_LY.name(),Role.THANH_VIEN.name())
                        .requestMatchers(Helper.appendWildcard(MappingConstants.API_PROJECT_DETAILS_LABEL_TODO)).hasAnyAuthority(Role.QUAN_LY.name(),Role.THANH_VIEN.name())
                        .requestMatchers(Helper.appendWildcard(MappingConstants.API_PROJECT_DETAILS_MYPROJECT)).hasAnyAuthority(Role.QUAN_LY.name(),Role.THANH_VIEN.name())
                        .requestMatchers(Helper.appendWildcard(MappingConstants.API_PROJECT_DETAILS_TODO)).hasAnyAuthority(Role.QUAN_LY.name(),Role.THANH_VIEN.name())
                        .requestMatchers(Helper.appendWildcard(MappingConstants.API_PROJECT_DETAILS_TODO_VOTE)).hasAnyAuthority(Role.QUAN_LY.name(),Role.THANH_VIEN.name())
                        .requestMatchers(Helper.appendWildcard(MappingConstants.API_PROJECT_DETAILS_TODO_LIST)).hasAnyAuthority(Role.QUAN_LY.name(),Role.THANH_VIEN.name())
                        .requestMatchers(Helper.appendWildcard(MappingConstants.API_PROJECT_DETAILS_MEMBER_PROJECT)).hasAnyAuthority(Role.QUAN_LY.name(),Role.THANH_VIEN.name())

// vũ thêm
                        .requestMatchers(Helper.appendWildcard(MappingConstants.API_MANAGE_TODO)).hasAnyAuthority(Role.QUAN_LY.name(),Role.THANH_VIEN.name())
                        .requestMatchers(Helper.appendWildcard(MappingConstants.API_MANAGE_TODO_VOTE)).hasAnyAuthority(Role.QUAN_LY.name(),Role.THANH_VIEN.name())
//trường thêm
                        .requestMatchers(Helper.appendWildcard(MappingConstants.API_MANAGE_TODO_LIST)).hasAnyAuthority(Role.QUAN_LY.name(),Role.THANH_VIEN.name())
                        .requestMatchers(Helper.appendWildcard(MappingConstants.API_MANAGE_PHASE_PROJECT)).hasAnyAuthority(Role.QUAN_LY.name(),Role.THANH_VIEN.name())
                        .requestMatchers(Helper.appendWildcard(MappingConstants.API_MANAGE_PROJECT)).hasAnyAuthority(Role.QUAN_LY.name(),Role.THANH_VIEN.name())
                // duoc fix
//                        .requestMatchers(Helper.appendWildcard(MappingConstants.API_MANAGE_PROJECT_STUDENT)).hasAnyAuthority(Role.QUAN_LY.name(),Role.THANH_VIEN.name())
                        .requestMatchers(Helper.appendWildcard(MappingConstants.API_MANAGE_STAGE)).hasAnyAuthority(Role.QUAN_LY.name(),Role.THANH_VIEN.name())



        );
        // API  author
        http.authorizeHttpRequests(
                auth -> auth
                        .requestMatchers(Helper.appendWildcard(MappingConstants.API_ADMIN_PREFIX)).hasAnyAuthority(Role.ADMIN.name())
                        .requestMatchers(Helper.appendWildcard(MappingConstants.API_MANAGE_PREFIX)).permitAll()
                        .requestMatchers(Helper.appendWildcard(MappingConstants.API_MEMBER_PREFIX)).hasAnyAuthority(Role.THANH_VIEN.name())
        );

        // Chặn APIS ko định nghĩa
        http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        http.oauth2Login(
                oauth2 -> oauth2.authorizationEndpoint(a -> a.baseUri("/oauth2/authorize"))
                        .redirectionEndpoint(r -> r.baseUri("/oauth2/callback/**"))
                        .userInfoEndpoint(u -> u.userService(customOAuth2UserService))
                        .authorizationEndpoint(a -> a.authorizationRequestRepository(httpCookieOAuth2AuthorizationRequestRepository))
                        .successHandler(oAuth2AuthenticationSuccessHandler)
                        .failureHandler(oAuth2AuthenticationFailureHandler));
        http.addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

}