package com.artisan.example.springboot;

import lombok.AllArgsConstructor;
import org.apache.catalina.filters.CorsFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.servlet.http.HttpServletResponse;

@AllArgsConstructor
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final UserRepo userRepo;
    private final JwtTokenFilter jwtTokenFilter;


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // todo configure authentication manger
        auth.userDetailsService(username -> (UserDetails) userRepo.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(String.format("User: %s, not found", username))));
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // TODO configure web security

        // Enable CORS and disable CSRF
        http = http.cors().and().csrf().disable();

        // set session management to statless
        http = http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and();
        // set unauthorized requests exceptio nhandler
        http = http.exceptionHandling()
                .authenticationEntryPoint((request, response, ex)-> {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, ex.getMessage())
                }).and();

        // set permissions on endpoints
        http.authorizeRequests()
                .antMatchers("/api/public/**").permitAll()
                .antMatchers(HttpMethod.GET, "/api/author/**").permitAll()
                .antMatchers(HttpMethod.POST, "/api/author/search").permitAll()
                .antMatchers(HttpMethod.GET, "/api/book/**").permitAll()
                .antMatchers(HttpMethod.POST, "/api/book/search").permitAll()
                // Our private endpoints
                .anyRequest().authenticated();

        // Add Jwt token filter
        http.addFilterBefore(
                jwtTokenFilter,
                UsernamePasswordAuthenticationFilter.class
        );
    }

    // Used by spring security if CORS is enabled.
    @Bean
    public CorsFilter corsFilter(){
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**",config);
        return new CorsFilter(source);
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
