package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;


@Entity
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    @OneToMany(mappedBy="game", fetch=FetchType.EAGER)
    private List<GamePlayer> gamePlayers;

    @OneToMany(mappedBy="game", fetch=FetchType.EAGER)
    private Set<Score> scores;

    private Date creationDate;

    public Game() {
        this.creationDate = new Date();
    }

    public Game(Date date) {
        this.creationDate = date;
    }

    public List<Object> getScores(){
        List<Object> lista=new ArrayList<>();
      lista = this.scores.stream().map(Score::scoreDTO).collect(Collectors.toList());
        return lista;
    }

    public long getId() {
        return id;
    }

    public String getCreationDate() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd  HH:mm:ss");
        return format.format(this.creationDate);


    }

    public List<GamePlayer> getGamePlayers(){
        return this.gamePlayers;
    }

    public Map<String,Object>getDto(){
        Map<String,Object> map = new LinkedHashMap<>();
        map.put("id",this.getId());
        map.put("created",this.getCreationDate());
        map.put("scores", this.getScores());
        map.put("gamePlayers", this.gamePlayers.stream().map(gamePlayer -> gamePlayer.getDto()));



        return map;
    }


}



