package com.hrsystem.demo.security;

import org.apache.tomcat.util.http.parser.HttpParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;
@Configuration
public class SecurityConfig {

    //@Bean
    //public PasswordEncoder encoder() {
    //    return NoOpPasswordEncoder.getInstance();
    //}

    @Bean
    public UserDetailsManager userDetailsManager (DataSource dataSource) throws Exception {
        JdbcUserDetailsManager jdbcUserDetailsManager = new JdbcUserDetailsManager(dataSource);
        jdbcUserDetailsManager.setUsersByUsernameQuery("select username,password,enabled from users where username = ?");
        jdbcUserDetailsManager.setAuthoritiesByUsernameQuery("select username,authority from authorities where username = ?");


        return jdbcUserDetailsManager;
    }

    @Bean
    public SecurityFilterChain filterChain (HttpSecurity httpSecurity) throws Exception
    {
        httpSecurity.authorizeHttpRequests(configurer ->
                configurer.requestMatchers(HttpMethod.POST,"/api/leaves/add").hasAnyRole("EMPLOYEE","MANAGER","ADMIN")
                        .requestMatchers(HttpMethod.PUT,"/api/employees/profile").hasAnyRole("EMPLOYEE","MANAGER","ADMIN")
                        .requestMatchers(HttpMethod.PUT,"/api/leaves/update/{id}").hasAnyRole("MANAGER","ADMIN")
                        .requestMatchers(HttpMethod.DELETE,"/api/employees/delete/{username}").hasRole("MANAGER")
                        .requestMatchers(HttpMethod.GET,"/api/employees/profile/{username}").hasAnyRole("MANAGER","ADMIN")
                        .requestMatchers(HttpMethod.GET,"/api/leaves/{username}/all").hasRole("MANAGER")
                        .requestMatchers(HttpMethod.POST,"/api/employees/add").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST,"/api/employees/{username}/addSupervisor/{supervisor}").hasRole("ADMIN")
                        .anyRequest().authenticated());


        httpSecurity.httpBasic();
        httpSecurity.csrf().disable();


         return httpSecurity.build();

    }
}


