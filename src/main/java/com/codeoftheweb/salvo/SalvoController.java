package com.codeoftheweb.salvo;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api")
public class SalvoController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private GamePlayerRepository gamePlayerRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private GameRepository repo;


    @RequestMapping("/games")
    public Map<String, Object> getGames(Authentication authentication) {

        Map<String, Object> dto = new LinkedHashMap<>();

        if (isGuest(authentication)) {
            dto.put("player", "guest");
        } else {
            Player player = playerRepository.findByUserName(authentication.getName());
            dto.put("player", player.makePlayerDTO());
        }
        dto.put("games", gameRepository.findAll().stream().map(game -> game.makeGameDTO()).collect(Collectors.toList()));
        return dto;
    }

    @RequestMapping(path = "/games", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> createGame(Authentication authentication) {
        if(isGuest(authentication)){
            return new ResponseEntity<>(makeMap("error", "unauthorized"), HttpStatus.UNAUTHORIZED);
        }

        Player player = playerRepository.findByUserName(authentication.getName());



        Game newGame = new Game( LocalDateTime.now());
        gameRepository.save(newGame);

        GamePlayer newGamePlayer = new GamePlayer(LocalDateTime.now(), newGame, player);

        gamePlayerRepository.save(newGamePlayer);

        return new ResponseEntity<>(makeMap("id", newGamePlayer.getId()), HttpStatus.CREATED);
    }

    @RequestMapping(path = "/game/{gameId}/players", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> joinGame(Authentication authentication,@PathVariable Long gameId) {
        if (isGuest(authentication)) {
            return new ResponseEntity<>(makeMap("error", "U can´t enter in a game"), HttpStatus.UNAUTHORIZED);
        }


        Player player = playerRepository.findByUserName(authentication.getName());

        Optional<Game> game = gameRepository.findById(gameId);

        if(game.isEmpty()){
            return new ResponseEntity<>(makeMap("error", "Game no exist"), HttpStatus.CONFLICT);
        }

        if (game.get().getGamePlayers().size() > 1) {
            return new ResponseEntity<>(makeMap("error", "Game complete"), HttpStatus.CONFLICT);
        }


        GamePlayer newGamePlayer = new GamePlayer(LocalDateTime.now(), game.get(), player);

        gamePlayerRepository.save(newGamePlayer);





        return new ResponseEntity<>(makeMap("id", newGamePlayer.getId()), HttpStatus.CREATED);
    }


    @RequestMapping("/game_view/{gamePlayerId}")
    public Map<String, Object> findGamePlayer(@PathVariable Long gamePlayerId) {
        Optional<GamePlayer> gamePlayer = gamePlayerRepository.findById(gamePlayerId);
        return gamePlayer.get().makeGamePlayerByIdDTO();
    }

    @RequestMapping(value="/games/players/{gamePlayerId}/ships", method=RequestMethod.POST)
    public ResponseEntity<Map<String,Object>> addShip (Authentication authentication ,@PathVariable long gamePlayerId, @RequestBody Set<Ship> ships) {

        if (isGuest(authentication)) {
            return new ResponseEntity<>(makeMap("error", "U can´t enter in a game"), HttpStatus.UNAUTHORIZED);
        }


        Player player = playerRepository.findByUserName(authentication.getName());

        Optional<GamePlayer> gamePlayer = gamePlayerRepository.findById(gamePlayerId);

        if(gamePlayer.isEmpty()){
            return new ResponseEntity<>(makeMap("error", "Game no exist"), HttpStatus.CONFLICT);
        }

        if (gamePlayer.get().getPlayer().getId() != player.getId()) {
            return new ResponseEntity<>(makeMap("error", "not your game"), HttpStatus.CONFLICT);
        }

        if (gamePlayer.get().ships.size() > 0){

            return new ResponseEntity<>(makeMap("error", "U already have ships"), HttpStatus.CONFLICT);
        }
        gamePlayer.get().setShips(ships);

        gamePlayerRepository.save(gamePlayer.get());

        return new ResponseEntity<>(makeMap("success","ships created"),HttpStatus.CREATED);

    }

    @RequestMapping(value="/games/players/{gamePlayerId}/salvos", method=RequestMethod.POST)
    public ResponseEntity<Map<String,Object>> addSalvo (Authentication authentication ,@PathVariable long gamePlayerId, @RequestBody Salvo salvo) {

        if (isGuest(authentication)) {
            return new ResponseEntity<>(makeMap("error", "U can´t enter in a game"), HttpStatus.UNAUTHORIZED);
        }

        if (salvo.getSalvoShoots().size() > 5 ) {
            return new ResponseEntity<>(makeMap("error", "No puedes disparar mas de 5 piedras"), HttpStatus.CONFLICT);
        }

        Player player = playerRepository.findByUserName(authentication.getName());

        Optional<GamePlayer> gamePlayer = gamePlayerRepository.findById(gamePlayerId);

        if (gamePlayer.isEmpty()) {
            return new ResponseEntity<>(makeMap("error", "User are not playing"), HttpStatus.CONFLICT);
        }

        if (salvo.getSalvoShoots().size() <= 0){
            return new ResponseEntity<>(makeMap("error", "No shoot data"), HttpStatus.FORBIDDEN);
        }

        if (gamePlayer.get().getPlayer().getId() != player.getId()) {
            return new ResponseEntity<>(makeMap("error", "Is not your game"), HttpStatus.CONFLICT);
        }

        if (gamePlayer.get().salvoes.stream().anyMatch(item -> item.getTurn() >= salvo.getTurn())){
            return new ResponseEntity<>(makeMap("error", "no turn"), HttpStatus.FORBIDDEN);
        }

        gamePlayer.get().addSalvo(salvo);

        gamePlayerRepository.save(gamePlayer.get());

        return new ResponseEntity<>(makeMap("success","Salvo created"),HttpStatus.CREATED);

    }


    @RequestMapping(path = "/players", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> createUser(@RequestParam String name,@RequestParam Team team, @RequestParam String password) {
        if (name.isEmpty() || (password.isEmpty())) {
            return new ResponseEntity<>(makeMap("error", "No name"), HttpStatus.FORBIDDEN);
        }
        Player player = playerRepository.findByUserName(name);
        if (player != null) {
            return new ResponseEntity<>(makeMap("error", "Username already exists"), HttpStatus.CONFLICT);
        }
        Player newPlayer = new Player(name, team, passwordEncoder.encode(password));
        playerRepository.save(newPlayer);
        return new ResponseEntity<>(makeMap("done", "player "+ newPlayer.getUserName()+ " created with id "+newPlayer.getId()), HttpStatus.CREATED);
    }

    private Map<String, Object> makeMap(String key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return map;
    }

    private boolean isGuest(Authentication authentication) {
        return authentication == null || authentication instanceof AnonymousAuthenticationToken;
    }
}


