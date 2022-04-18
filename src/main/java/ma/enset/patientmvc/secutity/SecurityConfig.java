package ma.enset.patientmvc.secutity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity

public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired //injecter celui qu on a declarer le fichier app.prop
    private DataSource dataSource;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        PasswordEncoder passwordEncoder=passwordEncoder();

        auth.jdbcAuthentication().dataSource(dataSource).usersByUsernameQuery("select username as principal ,password as credentials ,active from users where username=?").
                authoritiesByUsernameQuery("select username as principal, role from users_roles where username=?  ").rolePrefix("ROLE_").
                passwordEncoder(passwordEncoder);







        //comment chercher les utilisateurs et les roles (base de donnees ou memory ...)
        //{noop} n utilise pas le mdp on codeur


       /* String code=passwordEncoder.encode("1234");
        System.out.println(code);
        auth.inMemoryAuthentication().withUser("user1").password(code).roles("USER");
        auth.inMemoryAuthentication().withUser("user2").password(code).roles("USER");
        auth.inMemoryAuthentication().withUser("admin").password(code).roles("USER","ADMIN");
    }*/
}
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //formulaire d authen //formulaire defaut http.formLogin() sinon on peut utiliser notre formulaire http.formLogin().loginPage("/login");
        http.formLogin();
        //les droits  d acces
        http.authorizeRequests().antMatchers("/").permitAll();

       /* http.authorizeRequests().antMatchers("/formPatient/**").hasRole("ADMIN");*/
        //tout ce qui lier a /admin doit etre auth en tant que admin
        http.authorizeRequests().antMatchers("/admin/**").hasRole("ADMIN");
        http.authorizeRequests().antMatchers("/user/**").hasRole("USER");
        //obligatoire
        http.authorizeRequests().anyRequest().authenticated();
        //gerer les exception
        http.exceptionHandling().accessDeniedPage("/403");

    }
    @Bean
    PasswordEncoder passwordEncoder(){

        return new BCryptPasswordEncoder();
    }
}