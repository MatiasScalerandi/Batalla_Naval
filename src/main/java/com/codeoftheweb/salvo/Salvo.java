package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Entity
public class Salvo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    private int turn;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="gamePlayer_id")
    private GamePlayer gamePlayer;

    @ElementCollection
    @Column(name="salvoShoots")
    private List<String> salvoShoots = new ArrayList<>();

    public Salvo() {
    }

    public Salvo(int turn, List<String> salvoShoots) {
        this.turn = turn;
        this.salvoShoots = salvoShoots;
        }

    public Map<String, Object> makeSalvoDTO() {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("turn", this.turn);
        dto.put("players", this.getGamePlayer().getPlayer().makePlayerDTO());
        dto.put("shoots", this.salvoShoots);
        dto.put("hits",  this.getHits());
        dto.put("sinks", this.getSinks());
        return dto;
    }

    public List<String> getHits(){
        List<String> hits = new ArrayList<>();
        this.gamePlayer.enemyGamePlayer().getShips().stream().forEach(ship -> {
            hits.addAll(ship.getShipLocation().stream().filter( shipLoc -> this.salvoShoots.indexOf(shipLoc) >= 0).collect(Collectors.toList()));
        });
        return hits;
    }

    public List<ShipType> getSinks(){
        List<ShipType> sinks = new ArrayList<>();

        this.gamePlayer.enemyGamePlayer().getShips().stream().forEach(ship -> {
            List<String> hits = new ArrayList<>();
            this.gamePlayer.getSalvoes().stream().forEach(salvo -> {
                if(salvo.turn <= this.turn)
                    hits.addAll(ship.getShipLocation().stream().filter( shipLoc -> salvo.getSalvoShoots().indexOf(shipLoc) >= 0).collect(Collectors.toList()));
            });
            if(hits.size() == ship.getShipLocation().size())
                sinks.add(ship.getShipType());
        });
        return sinks;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getTurn() {
        return turn;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }

    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }

    public List<String> getSalvoShoots() {
        return salvoShoots;
    }

    public void setSalvoShoots(List<String> salvoShoots) {
        this.salvoShoots = salvoShoots;
    }
}