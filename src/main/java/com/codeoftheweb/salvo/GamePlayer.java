package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

@Entity
public class GamePlayer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    private LocalDateTime joinDate;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="player_id")
    private Player player;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="game_id")
    private Game game;


    @OneToMany(mappedBy="gamePlayer", fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    Set<Ship> ships = new HashSet<>();

    @OneToMany(mappedBy="gamePlayer", fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    Set<Salvo> salvoes = new HashSet<>();



    public GamePlayer() { }

    public GamePlayer (LocalDateTime joinDate, Game game, Player player){
        this.joinDate = joinDate;
        this.game = game;
        this.player = player;

    }

    public GamePlayer (LocalDateTime joinDate, Game game, Player player, Set<Ship> ships, Set<Salvo> salvoes){
        this.joinDate = joinDate;
        this.game = game;
        this.player = player;
        ships.forEach(this::addShip);
        salvoes.forEach(this::addSalvo);
    }

    public void addSalvo(Salvo salvo) {
        salvo.setGamePlayer(this);
        salvoes.add(salvo);
    }
    public void addShip(Ship ship) {
        ship.setGamePlayer(this);
        ships.add(ship);
    }

    public Map<String, Object> makeGamePlayerDTO() {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", this.getId());
        dto.put("player", this.player.makePlayerDTO() );
        if (this.getScore() != null)
            dto.put("score", this.getScore().getPoints());
        else
            dto.put("score", this.getScore());
        return dto;
    }

    public Map<String, Object> makeGamePlayerByIdDTO() {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", this.game.getId());
        dto.put("gamePlayers", this.game.getGamePlayers().stream().map(GamePlayer::makeGamePlayerDTO));
        dto.put("ship", this.ships.stream().map(Ship::makeShipDTO) );
        dto.put("salvoes", this.game.getGamePlayers().stream().flatMap(gp -> gp.salvoes.stream().map(Salvo::makeSalvoDTO)));
        dto.put("gameState",gameState());
        return dto;
    }

    public GamePlayer enemyGamePlayer(){
        return this.game.getGamePlayers().stream().filter(g -> g.getId() !=  this.id).findFirst().orElse(null);
    }

    private GameState gameState(){
        GameState gameState = GameState.WAIT;
        Salvo lastTurn = this.getLastTurn(this.salvoes);
        GamePlayer enemy = this.enemyGamePlayer();
        if(lastTurn!=null && enemy!=null){
            Salvo enemyLastTurn = this.getLastTurn(enemy.salvoes);
            if(enemyLastTurn != null && enemyLastTurn.getTurn() == lastTurn.getTurn()) {
                if ((lastTurn.getSinks().size() == enemy.getShips().size()) && (enemyLastTurn.getSinks().size() != this.ships.size()))
                    gameState = GameState.WIN;
                else if((lastTurn.getSinks().size() == enemy.getShips().size()) && (enemyLastTurn.getSinks().size() == this.ships.size())){
                    gameState = GameState.TIE;
                }
                else if((lastTurn.getSinks().size() != enemy.getShips().size()) && (enemyLastTurn.getSinks().size() == this.ships.size())){
                    gameState = GameState.LOSE;
                }else {
                    gameState = GameState.PLAY;
                }
            }
            else{
                if(enemyLastTurn != null && (lastTurn.getTurn() <= enemyLastTurn.getTurn())){
                    gameState = GameState.PLAY;
                }else if(enemyLastTurn != null && (lastTurn.getTurn() > enemyLastTurn.getTurn())){
                    gameState = GameState.WAIT;
                }
            }
        }else if(this.ships.size() <= 0){
            gameState = GameState.PLACE_SHIP;
        }else if(enemy!=null){
            gameState = GameState.PLAY;
        }


        return gameState;
    }

    private Salvo getLastTurn(Set<Salvo> salvoes){
        return salvoes.stream().sorted(Comparator.comparing(Salvo::getTurn).reversed()).findFirst().orElse(null);
    }


    public Score getScore(){
        return this.player.getGameScore(this.game);
    }



    public LocalDateTime getJoinDate() {
        return joinDate;
    }

    public void setjoinDate(LocalDateTime joinDate) {
        this.joinDate = joinDate ;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Set<Ship> getShips() {
        return ships;
    }

    public void setShips(Set<Ship> ships) {
        ships.forEach(this::addShip);
    }

    public Set<Salvo> getSalvoes() {
        return salvoes;
    }

    public void setSalvoes(Set<Salvo> salvoes) {
        this.salvoes = salvoes;
    }
}
