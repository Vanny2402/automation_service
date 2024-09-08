package com.vanny.Automateapi.config.jwtconfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.vanny.Automateapi.service.jwtservice.JwtUserDetailsService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class WebsSecurityConfig {

    private final JwtAuthenticationentryPoint jwtAuthenticationEntryPoint;
    private final JwtRequestFilter jwtRequestFilter;
    private final JwtUserDetailsService jwtUserDetailsService;
    private final PasswordEncoder passwordEncoder; 
    @Autowired
    public WebsSecurityConfig(@Lazy JwtAuthenticationentryPoint jwtAuthenticationEntryPoint, 
                              @Lazy JwtRequestFilter jwtRequestFilter,
                              @Lazy JwtUserDetailsService jwtUserDetailsService,
                              PasswordEncoder passwordEncoder) { 
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtRequestFilter = jwtRequestFilter;
        this.jwtUserDetailsService = jwtUserDetailsService;
        this.passwordEncoder = passwordEncoder;  // Assign injected PasswordEncoder
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(jwtUserDetailsService).passwordEncoder(passwordEncoder);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//    	http
//        .authorizeHttpRequests(authz -> authz
//        	.requestMatchers("/","/index.html","app.js","styles.css","/authenticate","/image/**").permitAll()
//            .requestMatchers("/adbDevice").hasRole("ADMIN")
//            .anyRequest().authenticated()
//        )
//        .csrf().disable()
//        .exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint)
//        .and()
//        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
//        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
//        return http.build();
    	  http
          .authorizeHttpRequests(authz -> authz
              .requestMatchers("/", "/index.html", "/css/**", "/js/**", "/images/**").permitAll()  // Allow static resources
              .anyRequest()
              .authenticated()
          )
          .csrf(csrf -> csrf.disable())
          .formLogin(form -> form
              .defaultSuccessUrl("/generateToken", true) 
              .permitAll()
          )
          .logout(logout -> logout.permitAll())
          .exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint)
          .and()
          .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

      http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
      return http.build();
	}

    
}
