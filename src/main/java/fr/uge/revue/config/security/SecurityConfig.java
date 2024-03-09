package fr.uge.revue.config.security;

import fr.uge.revue.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.Objects;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final UserService userService;
    private final BCryptPasswordEncoder passwordEncoder;

    public SecurityConfig(UserService userService, BCryptPasswordEncoder passwordEncoder) {
        Objects.requireNonNull(userService);
        Objects.requireNonNull(passwordEncoder);
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        Objects.requireNonNull(http);
        http
                .authenticationProvider(daoAuthenticationProvider())
                .csrf().disable()
                .authorizeRequests()
                    .antMatchers("/users/addFriend/{userId}").authenticated()
                    .antMatchers("/reviews/{reviewId}/like").authenticated()
                    .antMatchers("/reviews/{reviewId}/dislike").authenticated()
                    .antMatchers("/comments/{commentId}/like").authenticated()
                    .antMatchers("/comments/{commentId}/dislike").authenticated()
                    .antMatchers("/responses/{responseId}/like").authenticated()
                    .antMatchers("/responses/{responseId}/dislike").authenticated()
                    .antMatchers("/createReview").authenticated()
                    .antMatchers("/deleteProfile").authenticated()
                    .antMatchers("/profile").authenticated()
                    .antMatchers("/reviews/{reviewId}/notifications/activate", "/reviews/{reviewId}/notifications/deactivate").authenticated()
                    .antMatchers("/notifications/{notificationId}/markAsRead", "/notifications/{notificationId}/markAsRead/redirect").authenticated()
                    .antMatchers("/deleteResponse").authenticated()
                    .antMatchers("/deleteComment").authenticated()
                    .antMatchers("/deleteReview").authenticated()
                    .antMatchers("/reviews/{reviewId}/response").authenticated()
                    .antMatchers("/reviews/{reviewId}/comment").authenticated()
                    .antMatchers("/users/{userId}/updateUsername").authenticated()
                    .antMatchers("/users/{userId}/updateEmail").authenticated()
                    .antMatchers("/users/{userId}/updatePassword").authenticated()
                .and().formLogin().loginPage("/login")
                .and().logout().logoutSuccessUrl("/")
                .and().authorizeRequests()
                    .antMatchers("/api/v1/createReview").authenticated()
                    .antMatchers("/api/v1/notifications").authenticated()
                    .antMatchers("/api/v1/reviews/{reviewId}/comment").authenticated()
                    .antMatchers("/api/v1/reviews/{reviewId}/notifications/state").authenticated()
                    .antMatchers("/api/v1/reviews/{reviewId}/notifications/activate", "/api/v1/reviews/{reviewId}/notifications/deactivate").authenticated()
                    .antMatchers("/api/v1/reviews/{reviewId}/like").authenticated()
                    .antMatchers("/api/v1/reviews/{reviewId}/dislike").authenticated()
                .and().authorizeRequests().anyRequest().permitAll()
                .and().httpBasic();
        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring().requestMatchers(new AntPathRequestMatcher("/h2-console/**"));
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        Objects.requireNonNull(authenticationConfiguration);
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder);
        provider.setUserDetailsService(userService);
        return provider;
    }
}
