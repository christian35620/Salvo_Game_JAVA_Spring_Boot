package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import java.util.*;

@Entity
public class Ship {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    private String type;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="gamePlayerID")
    private GamePlayer gamePlayer;

    @ElementCollection
    @Column(name="location")
    private List<String> location=new ArrayList<>();

    //CONSTRUCTORS
    public Ship(){}

    public Ship(String type, List<String> location, GamePlayer gamePlayer) {
        this.type = type;
        this.location = location;
        this.gamePlayer = gamePlayer;
    }

    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }

    public String getType() {
        return type;
    }

    public List<String> getLocation() {
        return location;
    }


    public Map<String, Object> getDto() {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("type", this.getType());
        dto.put("location", this.getLocation());
        return dto;
    }

}
