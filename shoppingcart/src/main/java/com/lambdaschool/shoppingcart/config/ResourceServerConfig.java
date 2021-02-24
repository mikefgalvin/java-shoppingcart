package com.lambdaschool.shoppingcart.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;

@Configuration
@EnableResourceServer
public class ResourceServerConfig
        extends ResourceServerConfigurerAdapter
{
    private static final String RESOURCE_ID = "resource_id";

    @Override
    public void configure(ResourceServerSecurityConfigurer resources)
    {
        resources.resourceId(RESOURCE_ID)
                .stateless(false);
    }

    @Override
    public void configure(HttpSecurity http)
            throws
            Exception
    {
        http.authorizeRequests()
                .antMatchers("/",
                        "/h2-console/**",
                        "/swagger-resources/**",
                        "/swagger-resource/**",
                        "/swagger-ui.html",
                        "/v2/api-docs",
                        "/webjars/**",
                        "/createnewuser")
                .permitAll()
                .antMatchers(HttpMethod.POST,
                        "/users/**")
                .hasAnyRole("ADMIN")
                .antMatchers(HttpMethod.DELETE,
                        "/users/**")
                .hasAnyRole("ADMIN")
                .antMatchers(HttpMethod.PUT,
                        "/users/**")
                .hasAnyRole("ADMIN")
                .antMatchers("/users/**",
                        "/useremails/**",
                        "/oauth/revoke-token",
                        "/logout")
                .authenticated()
                .antMatchers("/roles/**")
                .hasAnyRole("ADMIN")
                .anyRequest().denyAll() // deny any endpoint that is not explicitly given access rights
                .and()
                .exceptionHandling()
                .accessDeniedHandler(new OAuth2AccessDeniedHandler());

        // http.requiresChannel().anyRequest().requiresSecure(); required for https

        // disable the creation and use of Cross Site Request Forgery Tokens.
        // These tokens require coordination with the front end client that is beyond the scope of this class.
        // See https://www.yawintutor.com/how-to-enable-and-disable-csrf/ for more information
        http.csrf()
                .disable();

        // this disables all of the security response headers. This is necessary for access to the H2 Console.
        // Normally, Spring Security would include headers such as
        //     Cache-Control: no-cache, no-store, max-age=0, must-revalidate
        //     Pragma: no-cache
        //     Expires: 0
        //     X-Content-Type-Options: nosniff
        //     X-Frame-Options: DENY
        //     X-XSS-Protection: 1; mode=block
        http.headers()
                .frameOptions()
                .disable();

        // This application implements its own logout procedure so disable the one built into Spring Security
        http.logout()
                .disable();
    }
}
