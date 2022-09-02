package com.agonyforge.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

import javax.inject.Inject;
import javax.sql.DataSource;

@EnableWebSecurity
@Configuration
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {
    private final UserDetailsManager userDetailsManager;

    @Inject
    public WebSecurityConfiguration(DataSource dataSource, AuthenticationManagerBuilder auth) throws Exception {
        super();

        this.userDetailsManager = auth
            .jdbcAuthentication()
            .dataSource(dataSource)
            .getUserDetailsService();
    }

    @Bean
    public SecurityContextLogoutHandler getSecurityContextLogoutHandler() {
        return new SecurityContextLogoutHandler();
    }

    @Bean
    public UserDetailsManager getUserDetailsManager() {
        return userDetailsManager;
    }

    @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
    public AuthenticationManager getAuthenticationManager() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf()
            .and()
            .logout()
            .logoutSuccessUrl("/")
            .and()
            .authorizeRequests(authz -> authz.antMatchers(
                "/*",
                "/mud/**",
                "/error",
                "/webjars/**",
                "/img/*",
                "/css/*",
                "/js/*"
            ).permitAll()
                .anyRequest()
                .authenticated())
            .oauth2Login(conf -> conf
                .loginPage("/")
                .defaultSuccessUrl("/"))
            .sessionManagement()
            .maximumSessions(2)
            .sessionRegistry(sessionRegistry());
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }
}
