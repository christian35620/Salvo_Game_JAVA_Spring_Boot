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

    private int salvoTurn;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="gamePlayerID")
    private GamePlayer gamePlayer;

    @ElementCollection
    @Column(name="location")
    private List<String> location=new ArrayList<>();

    //CONSTRUCTORS
    public Salvo(){}

    public Salvo(Integer salvoTurn, List<String> location, GamePlayer gamePlayer) {
        this.salvoTurn = salvoTurn;
        this.location = location;
        this.gamePlayer = gamePlayer;
    }


    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }

    public Integer getSalvoTurn() {
        return salvoTurn;
    }

    public List<String> getLocation() {
        return location;
    }

    public List<String> getHits(List<String> myShots, Set<Ship> opponentShips){

        List<String> allEnemyLocs = new ArrayList<>();

        opponentShips.forEach(ship -> allEnemyLocs.addAll(ship.getLocation()));

        return myShots
                .stream()
                .filter(shot -> allEnemyLocs
                        .stream()
                        .anyMatch(loc -> loc.equals(shot)))
                .collect(Collectors.toList());

    }



    public Map<String, Object> getDto() {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("turn", this.getSalvoTurn());
        dto.put("player",this.gamePlayer.getPlayer().getId());
        dto.put("location", this.getLocation());

        GamePlayer opponent = this.getGamePlayer().getOpponent();

        if(opponent != null){

            Set<Ship> enemyShips = opponent.getShips();

            dto.put("hits", this.getHits(this.getLocation(),enemyShips));

            Set<Salvo> mySalvoes = this.getGamePlayer()
                    .getSalvoes()
                    .stream()
                    .filter(salvo -> salvo.getSalvoTurn() <= this.getSalvoTurn())
                    .collect(Collectors.toSet());

            dto.put("sunk", this.gamePlayer.getSunkenShips(mySalvoes, enemyShips).stream().map(Ship::getDto));
        }



        return dto;

    }


    //**************************************************
}
