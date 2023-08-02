package com.tadashop.nnt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.web.cors.CorsConfiguration;

import lombok.RequiredArgsConstructor;

import static com.tadashop.nnt.utils.constant.Role.ADMIN;
import static com.tadashop.nnt.utils.constant.Role.USER;
import static com.tadashop.nnt.utils.Permission.ADMIN_CREATE;
import static com.tadashop.nnt.utils.Permission.ADMIN_DELETE;
import static com.tadashop.nnt.utils.Permission.ADMIN_READ;
import static com.tadashop.nnt.utils.Permission.ADMIN_UPDATE;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.HttpMethod.PATCH;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfiguration {

	private final JwtAuthenticationFilter jwtAuthFilter;
	private final AuthenticationProvider authenticationProvider;
	private final LogoutHandler logoutHandler;
	@Bean
	  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		
		  http.cors().configurationSource(request -> new CorsConfiguration().applyPermitDefaultValues());
		  http
		  	.csrf().disable()
	    	.authorizeHttpRequests()
	    		.requestMatchers(
	    				"/api/**",  
		    			"/v2/api-docs",
		                "/v3/api-docs",
		                "/v3/api-docs/**",
		                "/swagger-resources",
		                "/swagger-resources/**",
		                "/configuration/ui",
		                "/configuration/security",
		                "/swagger-ui/**",
		                "/webjars/**",
		                "/swagger-ui.html")
	    		.permitAll()
	    		
		    	.requestMatchers("/api/admin/**").hasRole(ADMIN.name())
		        .requestMatchers(GET, "/api/admin/**").hasAuthority(ADMIN_READ.name())
		        .requestMatchers(POST, "/api/admin/**").hasAuthority(ADMIN_CREATE.name())
		        .requestMatchers(PUT, "/api/admin/**").hasAuthority(ADMIN_UPDATE.name())
		        .requestMatchers(PATCH, "/api/admin/**").hasAuthority(ADMIN_UPDATE.name())
		        .requestMatchers(DELETE, "/api/admin/**").hasAuthority(ADMIN_DELETE.name())
		    	
		        .requestMatchers("/api/user/**").hasRole(USER.name())
		    	.anyRequest()
		    		.authenticated()
	    	.and()
	    		.sessionManagement()
	    		.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
	    	.and()
	    		.authenticationProvider(authenticationProvider)
	    		.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
		  	.logout()
		        .logoutUrl("/api/auth/logout")
		        .addLogoutHandler(logoutHandler)
		        .logoutSuccessHandler((request, response, authentication) -> SecurityContextHolder.clearContext());
	    	
	    	
	    return http.build();
	  }
}