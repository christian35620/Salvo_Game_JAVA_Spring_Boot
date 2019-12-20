package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import java.util.*;

@Entity
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")

    //PROPERTIES
    private long id;
    private String userName;
    private String password;

    @OneToMany(mappedBy="player", fetch=FetchType.EAGER)
    private Set<GamePlayer> gamePlayers;

    @OneToMany(mappedBy="player", fetch=FetchType.EAGER)
    private Set<Score> scores;

    //CONSTRUCTORS
    public Player(){}

    public Player(String userName, String password) {
        this.userName = userName;
        this.password=password;
    }

    public long getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public Set<Score> getScores() {
        return scores;
    }

    public String getPassword() {
        return password;
    }

    public Score getScore (Game game){

        return scores.stream().filter(score -> score.getGame().getId() == game.getId()).findAny().orElse(null);
    }

    //*********************************************************************************

    //DTO
    public Map<String,Object> getDto(){
        Map<String,Object> dto=new HashMap<>();
        dto.put("playerID",this.getId());
        dto.put("playerEmail",this.getUserName());
        return dto;
    }

    public Map<String, Object> playerStatisticsDTO(){

        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", this.id);
        dto.put("email", this.getUserName());
        double total = this.getScores().stream().mapToDouble(Score::getScore).sum();
        double won = this.getScores().stream().filter(score -> score.getScore() == 1).count();
        double lost = this.getScores().stream().filter(score -> score.getScore() == 0).count();
        double tied = this.getScores().stream().filter(score -> score.getScore() == 0.5).count();
        dto.put("totalScore", total);
        dto.put("won", won);
        dto.put("lost", lost);
        dto.put("tied", tied);
        return dto;
    }


}
