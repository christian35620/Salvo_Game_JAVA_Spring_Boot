package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;


@Entity
public class GamePlayer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    private Date joinDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="playerID")
    private Player player;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="gameID")
    private Game game;

    @OneToMany(mappedBy="gamePlayer", fetch=FetchType.EAGER)
    //private List<Ship> ships;
    private Set<Ship> ships=new HashSet<>();

    @OneToMany(mappedBy="gamePlayer", fetch=FetchType.EAGER)
    private Set<Salvo> salvoes=new HashSet<>();


    //CONSTRUCTOR
    public GamePlayer(Player player, Game game) {
        this.joinDate = new Date();
        this.player = player;
        this.game = game;
    }

    public GamePlayer(){}

    public long getId() {
        return id;
    }

    public Player getPlayer() {
        return player;
    }

    public Set<Ship> getShips(){
        return ships;
    }

    public Set<Salvo> getSalvoes(){return salvoes;}

    public Score getScore (){
        return this.player.getScore(this.game);
    }

    public Game getGame() {
        return game;
    }

    public void addShip(Ship ship){
        ship.setGamePlayer(this);
        ships.add(ship);
    }

    public void addSalvo(Salvo salvo){
        salvo.setGamePlayer(this);
        salvoes.add(salvo);
    }


    public GamePlayer getOpponent(){
        return this.getGame().getGamePlayers()
                .stream().filter(gp -> gp.getId() != this.getId())
                .findFirst()
                .orElse(null);
    }

    public List<Ship> getSunkenShips(Set<Salvo> mySalvoes, Set<Ship> opponentShips){

        List<String> allShots = new ArrayList<>();

        mySalvoes.forEach(salvo -> allShots.addAll(salvo.getLocation()));

        return opponentShips
                .stream()
                .filter(ship -> allShots.containsAll(ship.getLocation()))
                .collect(Collectors.toList());
    }

    public Map<String,Object>getDto(){
        Map<String,Object> map = new LinkedHashMap<>();
        map.put("id",this.getId());
        map.put("joinDate",this.joinDate);
        map.put("player",this.player.getDto());
        if(this.getScore() != null){
            map.put("score", this.getScore().getScore());
        }else{

            map.put("score", "N/A");
        }

        return map;
    }
}
