package com.codeoftheweb.salvo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@SpringBootApplication
public class SalvoApplication {

	@Autowired
	PasswordEncoder passwordEncoder;

	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class, args);
	}


	@Bean
	public CommandLineRunner initData(PlayerRepository playerRepository, GameRepository gameRepository, GamePlayerRepository gamePlayerRepository, ShipRepository shipRepository, SalvoRepository salvoRepository, ScoreRepository scoreRepository) {
		return (args) -> {
			// save a couple of customers
			Player player1 = new Player("j.bauer@ctu.gov", Team.SAN_LORENZO, passwordEncoder.encode("24"));
			playerRepository.save(player1);
			Player player2 = new Player("c.obrian@ctu.gov", Team.HURACAN, passwordEncoder.encode("42"));
			playerRepository.save(player2);
			playerRepository.save(new Player("kim_bauer@gmail.com", Team.SAN_LORENZO, passwordEncoder.encode("kb")));
			playerRepository.save(new Player("t.almeida@ctu.gov", Team.SAN_LORENZO, passwordEncoder.encode("mole")));
			Game game1 = new Game (LocalDateTime.now());
			gameRepository.save (game1);
			Game game2 = new Game (LocalDateTime.now().plusHours(1));
			gameRepository.save (game2);
			Game game3 = new Game (LocalDateTime.now().plusHours(2));
			gameRepository.save (game3);


			Set<Ship> shipSet1 = new HashSet<>();
			shipSet1.add(new Ship(ShipType.BARRA_BRAVA_SL, new ArrayList<>(Arrays.asList("E5","E6","E7","E8"))));
			shipSet1.add(new Ship(ShipType.MICRO_SL, new ArrayList<>(Arrays.asList("J2","J3","J4","J5","J6"))));
			shipSet1.add(new Ship(ShipType.AUTO1_SL, new ArrayList<>(Arrays.asList("H1","H2","H3"))));
			shipSet1.add(new Ship(ShipType.AUTO2_SL, new ArrayList<>(Arrays.asList("A2","B2","C2"))));
			shipSet1.add(new Ship(ShipType.GORDO_SL, new ArrayList<>(Arrays.asList("F5","F6"))));


			Set<Salvo> salvoSet1 = new HashSet<>();
			salvoSet1.add(new Salvo(1,new ArrayList<>(Arrays.asList("J2","J3","J4","J5","J6"))));
			salvoSet1.add(new Salvo(2,new ArrayList<>(Arrays.asList("E5","E6","E7","E8"))));
			salvoSet1.add(new Salvo(3,new ArrayList<>(Arrays.asList("H1","H2","H3","A2","B2"))));
			//salvoSet1.add(new Salvo(4,new ArrayList<>(Arrays.asList("C2","F5","F6"))));

			Set<Ship> shipSet2 = new HashSet<>();
			shipSet2.add(new Ship(ShipType.BARRA_BRAVA_HU, new ArrayList<>(Arrays.asList("E5","E6","E7","E8"))));
			shipSet2.add(new Ship(ShipType.MICRO_HU, new ArrayList<>(Arrays.asList("J2","J3","J4","J5","J6"))));
			shipSet2.add(new Ship(ShipType.AUTO1_HU, new ArrayList<>(Arrays.asList("H1","H2","H3"))));
			shipSet2.add(new Ship(ShipType.AUTO2_HU, new ArrayList<>(Arrays.asList("A2","B2","C2"))));
			shipSet2.add(new Ship(ShipType.GORDO_HU, new ArrayList<>(Arrays.asList("F5","F6"))));

			Set<Salvo> salvoSet2 = new HashSet<>();
			salvoSet2.add(new Salvo(1,new ArrayList<>(Arrays.asList("J2","J3","J4","J5","J6"))));
			salvoSet2.add(new Salvo(2,new ArrayList<>(Arrays.asList("E5","E6","E7","E8"))));
			salvoSet2.add(new Salvo(3,new ArrayList<>(Arrays.asList("H1","H2","H3","A2","B2"))));
			//salvoSet2.add(new Salvo(4,new ArrayList<>(Arrays.asList("C2","F5","F6"))));

			gamePlayerRepository.save(new GamePlayer(LocalDateTime.now(), game1, player1, shipSet1, salvoSet1));
			gamePlayerRepository.save(new GamePlayer(LocalDateTime.now(), game1, player2, shipSet2, salvoSet2));

		};
	}



}