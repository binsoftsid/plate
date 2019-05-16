package com.services;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
 
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true) 
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	JdbcTemplate jdbcTemplate;
	
	DataSource dataSource;
 
	@Autowired
	public void configAuthentication(AuthenticationManagerBuilder auth) throws Exception {
		dataSource = jdbcTemplate.getDataSource();
		auth.jdbcAuthentication().dataSource(dataSource)
				.usersByUsernameQuery("select username,password,enabled from users where username=?")
				.authoritiesByUsernameQuery("select username, role from roles where username=?")
				.dataSource(dataSource)
				.passwordEncoder(bCryptPasswordEncoder());
	}
 
	@Override
	protected void configure(HttpSecurity http) throws Exception {
			http.csrf().disable()
			.authorizeRequests()
			.antMatchers("/","/servisi","/pdf/**","/help","/kontakt").permitAll()
			.antMatchers("/admin","/admindata/**","/lozinke","/paramputni/**").hasRole("ADMIN")
			.anyRequest().authenticated()
			.and().formLogin().defaultSuccessUrl("/godina")
			.loginPage("/login").permitAll().and()
			//.logout().permitAll();

			// ovo radi
			.logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
			.logoutSuccessUrl("/").deleteCookies("JSESSIONID")
			.invalidateHttpSession(true);
		
			/*
			.logout().clearAuthentication(true)
	        .logoutSuccessUrl("/")
	        .deleteCookies("JSESSIONID")
	        .invalidateHttpSession(true);
	        */
			
			http.exceptionHandling().accessDeniedPage("/403");
			
	}
	@Override
	public void configure( WebSecurity web ) throws Exception
	{
	    web.ignoring().antMatchers( "/**/*.js", "/**/*.css","/**/*.jpg" );
	}	

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.inMemoryAuthentication().withUser("binsoft").password("bin123").roles("ADMIN");
	}
	
	//resenje kada password nije enkriptovan
	/*
	@Bean
	public static NoOpPasswordEncoder passwordEncoder() {
	 return (NoOpPasswordEncoder) NoOpPasswordEncoder.getInstance();
	}	
	*/
		
	//In 2.0.0. You need to set the password encoder like this:
	@Bean
	public PasswordEncoder passwordEncoder() {
		 return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		 return new BCryptPasswordEncoder();
	}

	
	
}