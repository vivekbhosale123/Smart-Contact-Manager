package org.vdb.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration

public class myConfig {

	@Bean
	public UserDetailsService getUserDetailsService() {
		return new UserDetailsServiceImpl();
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
		daoAuthenticationProvider.setUserDetailsService(this.getUserDetailsService());
		daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());

		return daoAuthenticationProvider;
	}

	// securityFilterChain method

	@SuppressWarnings("deprecation")
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authenticationProvider(authenticationProvider()) // Register the custom AuthenticationProvider
                .authorizeRequests(
                        authorize -> authorize.requestMatchers("/admin/**").hasRole("ADMIN").requestMatchers("/user/**")
                                .hasRole("USER").requestMatchers("/**").permitAll().anyRequest().authenticated())
                .formLogin(login -> login
                        .loginPage("/signin")
                        .loginProcessingUrl("/dologin")  
                        .defaultSuccessUrl("/user/index")
//                        .failureUrl("/failed")
                		).csrf(csrf -> csrf.disable()); // Only disable CSRF if necessary for testing

		return http.build();
	}
}
