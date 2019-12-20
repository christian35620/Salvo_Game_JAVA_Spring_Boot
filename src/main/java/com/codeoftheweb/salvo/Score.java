package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import java.util.*;

@Entity
public class Score {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    private double score;
    private Date finishDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="playerID")
    private Player player;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="gameID")
    private Game game;

    //CONSTRUCTOR
    public Score(double score, Date finishDate, Player player, Game game) {
        this.finishDate = finishDate;
        this.player = player;
        this.game = game;
        this.score=score;
    }

    public Score(){}

    public long getId() {
        return id;
    }

    public Player getPlayer() {
        return player;
    }

    public Game getGame() {
        return game;
    }

    public Date getFinishDate() {
        return finishDate;
    }

    public double getScore(){
            return this.score;
        }


    public Map<String, Object> scoreDTO(){

        Map<String, Object> dto = new LinkedHashMap<>();

        dto.put("score", this.score);
        return dto;
    }
}
