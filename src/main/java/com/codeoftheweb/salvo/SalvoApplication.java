package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;




import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;

@SpringBootApplication
public class SalvoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class, args);
	}

	@Bean
	public CommandLineRunner initData(PlayerRepository playerRepository, GameRepository gameRepository, GamePlayerRepository gamePlayerRepository, ShipRepository shipRepository, SalvoRepository salvoRepository, ScoreRepository scoreRepository) {
		return (args) -> {
			// save a couple of customers
			Player player1= new Player("j.bauer@ctu.gov", passwordEncoder().encode("24"));
			Player player2= new Player("c.obrian@ctu.gov",  passwordEncoder().encode("42"));
			Player player3= new Player("kim_bauer@gmail.com",  passwordEncoder().encode("kb"));
			Player player4= new Player("t.almeida@ctu.gov",  passwordEncoder().encode("mole"));

			playerRepository.save(player1);
			playerRepository.save(player2);
			playerRepository.save(player3);
			playerRepository.save(player4);

			Date date1=new Date();
			Date date2=Date.from(date1.toInstant().plusSeconds(3600));
			Date date3=Date.from(date1.toInstant().plusSeconds(3600*2));
			Date date4=Date.from(date1.toInstant().plusSeconds(3600*3));

			Game game1= new Game(date1);
			Game game2= new Game(date2);
			Game game3= new Game(date3);
			Game game4= new Game(date4);
			gameRepository.save(game1);
			gameRepository.save(game2);
			gameRepository.save(game3);
			gameRepository.save(game4);

			GamePlayer gamePlayer1 = new GamePlayer (player1, game1);
			GamePlayer gamePlayer2 = new GamePlayer (player2, game1);
			GamePlayer gamePlayer3 = new GamePlayer (player1, game2);
			GamePlayer gamePlayer4 = new GamePlayer (player2, game2);
			GamePlayer gamePlayer5 = new GamePlayer (player2, game3);
			GamePlayer gamePlayer6 = new GamePlayer (player4, game3);
			GamePlayer gamePlayer7 = new GamePlayer (player1, game4);

			gamePlayerRepository.save(gamePlayer1);
			gamePlayerRepository.save(gamePlayer2);
			gamePlayerRepository.save(gamePlayer3);
			gamePlayerRepository.save(gamePlayer4);
			gamePlayerRepository.save(gamePlayer5);
			gamePlayerRepository.save(gamePlayer6);
			gamePlayerRepository.save(gamePlayer7);


			Ship ship1 = new Ship("submarine", Arrays.asList("E1", "F1", "G1"),gamePlayer1);
			Ship ship2 = new Ship("destroyer", Arrays.asList("H2", "H3", "H4"),gamePlayer1);
			Ship ship3 = new Ship("patrol_boat", Arrays.asList("B4", "B5"),gamePlayer1);
			Ship ship4 = new Ship("destroyer", Arrays.asList("B5", "C5", "D5"),gamePlayer2);
			Ship ship5 = new Ship("patrol_boat", Arrays.asList("F1", "F2"),gamePlayer2);
			Ship ship6 = new Ship("destroyer", Arrays.asList("B5", "C5", "D5"),gamePlayer3);
			Ship ship7 = new Ship("patrol_boat", Arrays.asList("C6", "C7"),gamePlayer3);
			Ship ship8 = new Ship("submarine", Arrays.asList("A2", "A3", "A4"),gamePlayer4);
			Ship ship9 = new Ship("patrol_boat", Arrays.asList("G6", "H6"),gamePlayer4);
			Ship ship10 = new Ship("destroyer", Arrays.asList("B5", "C5", "D5"),gamePlayer5);
			Ship ship11 = new Ship("patrol_boat", Arrays.asList("C6", "C7"),gamePlayer5);
			Ship ship12 = new Ship("submarine", Arrays.asList("A2", "A3", "A4"),gamePlayer6);
			Ship ship13 = new Ship("patrol_boat", Arrays.asList("G6", "H6"),gamePlayer6);

			shipRepository.save(ship1);
			shipRepository.save(ship2);
			shipRepository.save(ship3);
			shipRepository.save(ship4);
			shipRepository.save(ship5);
			shipRepository.save(ship6);
			shipRepository.save(ship7);
			shipRepository.save(ship8);
			shipRepository.save(ship9);
			shipRepository.save(ship10);
			shipRepository.save(ship11);
			shipRepository.save(ship12);
			shipRepository.save(ship13);

			Salvo salvo1 = new Salvo(1, Arrays.asList("B5", "C5", "F1"),gamePlayer1);
			Salvo salvo2 = new Salvo(1, Arrays.asList("B4", "B5", "B6"),gamePlayer2);
			Salvo salvo3 = new Salvo(2, Arrays.asList("F2", "D5"),gamePlayer1);
			Salvo salvo4 = new Salvo(2, Arrays.asList("E1", "H3", "A2"),gamePlayer2);
			Salvo salvo5 = new Salvo(1, Arrays.asList("A2", "A4", "G6"),gamePlayer3);
			Salvo salvo6 = new Salvo(1, Arrays.asList("B5", "D5", "C7"),gamePlayer4);
			Salvo salvo7 = new Salvo(2, Arrays.asList("A3", "H6"),gamePlayer3);
			Salvo salvo8 = new Salvo(2, Arrays.asList("C5", "C6"),gamePlayer4);
			Salvo salvo9 = new Salvo(1, Arrays.asList("G6", "H6", "A4"),gamePlayer5);
			Salvo salvo10 = new Salvo(1, Arrays.asList("H1", "H2", "H3"),gamePlayer6);
			Salvo salvo11 = new Salvo(2, Arrays.asList("A2", "A3", "D8"),gamePlayer5);
			Salvo salvo12 = new Salvo(2, Arrays.asList("E1", "F2", "G3"),gamePlayer6);

			salvoRepository.save(salvo1);
			salvoRepository.save(salvo2);
			salvoRepository.save(salvo3);
			salvoRepository.save(salvo4);
			salvoRepository.save(salvo5);
			salvoRepository.save(salvo6);
			salvoRepository.save(salvo7);
			salvoRepository.save(salvo8);
			salvoRepository.save(salvo9);
			salvoRepository.save(salvo10);
			salvoRepository.save(salvo11);
			salvoRepository.save(salvo12);

			Date finishDate1=Date.from(date3.toInstant().plusSeconds(2*3600));
			Date finishDate2=Date.from(finishDate1.toInstant().plusSeconds(3600));
			Date finishDate3=Date.from(finishDate1.toInstant().plusSeconds(2*3600));

			Score score1 = new Score(1, finishDate1, player1, game1);
			Score score2 = new Score(0, finishDate1, player2, game1);
			Score score3 = new Score(0.5, finishDate2, player1, game2);
			Score score4 = new Score(0.5, finishDate2, player2, game2);
			//Score score5 = new Score(0, finishDate3, player2, game3);
			//Score score6 = new Score(1, finishDate3, player4, game3);

			scoreRepository.save(score1);
			scoreRepository.save(score2);
			scoreRepository.save(score3);
			scoreRepository.save(score4);
			//scoreRepository.save(score5);
			//scoreRepository.save(score6);

		};
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

}



@Configuration
class WebSecurityConfiguration extends GlobalAuthenticationConfigurerAdapter {
	/*@Bean
	ServletRegistrationBean h2servletRegistration(){
		ServletRegistrationBean registrationBean = new ServletRegistrationBean( new WebServlet());
		registrationBean.addUrlMappings("/console/*");
		return registrationBean;
	}*/

	@Autowired
	PlayerRepository playerRepository;

	@Override
	public void init(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(username ->  {
			Player player = playerRepository.findByUserName(username);
			if (player != null) {
				return new User(player.getUserName(), player.getPassword(),
						AuthorityUtils.createAuthorityList("USER"));
			} else {
				throw new UsernameNotFoundException("Unknown user: " + username);
			}
		});
	}
}

@Configuration
@EnableWebSecurity
class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
				.antMatchers("/web/games.html","/web/scripts/**","/web/styles/**","/web/images/**","/api/games", "/api/login", "/api/players").permitAll()
				//.antMatchers("/web/**").permitAll()
				.antMatchers("/admin/**").hasAuthority("ADMIN")
				.antMatchers("/api/**","/web/**").hasAuthority("USER")
				//.antMatchers("/").permitAll()
				.anyRequest().denyAll()
				.and()
				.authorizeRequests().antMatchers("/h2-console/**").permitAll();
				//.formLogin();

		http.csrf().disable();
		http.headers().frameOptions().disable();


		http.formLogin()
				.usernameParameter("username")
				.passwordParameter("password")
				.loginPage("/api/login");

		http.logout().logoutUrl("/api/logout");

		// turn off checking for CSRF tokens
		http.csrf().disable();

		// if user is not authenticated, just send an authentication failure response
		http.exceptionHandling().authenticationEntryPoint((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

		// if login is successful, just clear the flags asking for authentication
		http.formLogin().successHandler((req, res, auth) -> clearAuthenticationAttributes(req));

		// if login fails, just send an authentication failure response
		http.formLogin().failureHandler((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

		// if logout is successful, just send a success response
		http.logout().logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler());

	}

	private void clearAuthenticationAttributes(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
		}
	}
}