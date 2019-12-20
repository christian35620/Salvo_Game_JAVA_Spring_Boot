package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static jdk.nashorn.internal.objects.NativeDebug.map;

@RestController
@RequestMapping("/api")
public class SalvoController {
    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private GamePlayerRepository gamePlayerRepository;

    @Autowired
    private ShipRepository shipRepository;

    @Autowired
    private SalvoRepository salvoRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @RequestMapping(path = "/players", method = RequestMethod.POST)
    public ResponseEntity<Object> register(
            @RequestParam String email, @RequestParam String password) {

        if (email.isEmpty() || password.isEmpty()) {
            return new ResponseEntity<>("Missing data", HttpStatus.FORBIDDEN);
        }

        if (playerRepository.findByUserName(email) !=  null) {
            return new ResponseEntity<>("Name already in use", HttpStatus.FORBIDDEN);
        }

        playerRepository.save(new Player(email, passwordEncoder.encode(password)));
        Player newPlayer=playerRepository.findByUserName(email);

        return new ResponseEntity<>("New Player Created: "+ newPlayer.getDto(),HttpStatus.CREATED);

    }

    private boolean isGuest(Authentication authentication) {
        return authentication == null || authentication instanceof AnonymousAuthenticationToken;
    }

    @RequestMapping("/games")
    public Map<String, Object> getGames(Authentication authentication) {

        Map<String,Object> dto = new HashMap<>();

        if(isGuest(authentication)){

            dto.put("currentPlayer", "N/A");
        }else{

            dto.put("currentPlayer", playerRepository.findByUserName(authentication.getName()).getDto());
        }

        dto.put("games", gameRepository
                .findAll() //games
                .stream()
                .map(Game::getDto)
                .collect(Collectors.toList()));
        dto.put("leaderboard", playerRepository
                .findAll() //games
                .stream()
                .map(Player::playerStatisticsDTO)
                .collect(Collectors.toList()));
        return dto;
  }

    @RequestMapping("/game_view/{gamePlayerID}")
    public ResponseEntity<Object> gameViewDTO(@PathVariable long gamePlayerID, Authentication authentication){

        GamePlayer gamePlayer = gamePlayerRepository.findById(gamePlayerID).orElseThrow(() -> new EntityNotFoundException(""+gamePlayerID));
        Player player= playerRepository.findByUserName(authentication.getName());
        if (gamePlayer.getPlayer().getId()==player.getId()){
            Map<String, Object> map = gamePlayer.getGame().getDto();

            List<Map<String, Object>> shipsDto = gamePlayer.getShips().stream().map(ship -> ship.getDto()).collect(toList());
            List<Map<String, Object>> salvoesDto=gamePlayer.getGame().getGamePlayers().stream().flatMap(gameplayer->gameplayer.getSalvoes().stream().map(salvo -> salvo.getDto())).collect(toList());

            map.put("ships", shipsDto);
            map.put("salvoes", salvoesDto);
            map.put("statistics", gamePlayer.getPlayer().playerStatisticsDTO());
            return new ResponseEntity<>(map,HttpStatus.OK);
        }else{
            return new ResponseEntity<>("unauthorized:  this is not your game", HttpStatus.UNAUTHORIZED);
        }
    }


    @RequestMapping(path = "/games", method = RequestMethod.POST)
    public ResponseEntity<Object> createGames(Authentication authentication){


        if(isGuest(authentication)){

            return new ResponseEntity<>("Miss player", HttpStatus.FORBIDDEN);
        }else{



            Player player = playerRepository.findByUserName((authentication.getName()));
            Game newGame = new Game(new Date());
            gameRepository.save(newGame);
            GamePlayer newGamePlayer = new GamePlayer(player, newGame);
            gamePlayerRepository.save(newGamePlayer);

            Map<String,Object> map = new HashMap<>();
            map.put("gamePlayerId",newGamePlayer.getId());

            return new ResponseEntity<>(map, HttpStatus.CREATED);


        }

    }

    @RequestMapping(path = "/games/{gameId}/players", method = RequestMethod.POST)
    public ResponseEntity<Object> joinGame(@PathVariable long gameId, Authentication authentication){

        Player player = playerRepository.findByUserName((authentication.getName()));
        Game game = gameRepository.findById((Long) gameId).orElse(null);
        GamePlayer opponent = game.getGamePlayers().stream().findFirst().orElse(null);

        if(isGuest(authentication)){
            Map<String, Object> mensaje=new HashMap<>();
            mensaje.put("error", "Miss player");
            return new ResponseEntity<Object>(mensaje, HttpStatus.FORBIDDEN);
        }else if(game == null) {

            return new ResponseEntity<>("No such game", HttpStatus.FORBIDDEN);
        }else if(player.getUserName() == opponent.getPlayer().getUserName()) {

            return new ResponseEntity<>(    "You are already in this game", HttpStatus.FORBIDDEN);
        }else if(game.getGamePlayers().size() > 1){

            return new ResponseEntity<>("a lot players", HttpStatus.FORBIDDEN);
        }else{

            GamePlayer newGamePlayer = new GamePlayer(player, game);
            gamePlayerRepository.save(newGamePlayer);

            Map<String,Object> map = new HashMap<>();
            map.put("gamePlayerId",newGamePlayer.getId());
            return new ResponseEntity<>(map, HttpStatus.CREATED);
        }

    }

    @RequestMapping(path = "/games/players/{gamePlayerId}/ships", method = RequestMethod.POST)
    public ResponseEntity<Object> addShips(@PathVariable long gamePlayerId, @RequestBody List<Ship> ships, Authentication authentication){

        Player player = playerRepository.findByUserName((authentication.getName()));
        GamePlayer gamePlayer = gamePlayerRepository.findById(gamePlayerId).orElse(null);

        if(isGuest(authentication)){
            Map<String, Object> mensaje=new HashMap<>();
            mensaje.put("error", "Miss player");
            return new ResponseEntity<Object>(mensaje, HttpStatus.FORBIDDEN);
        }else if(gamePlayer == null) {

            return new ResponseEntity<>("No such gamePlayer", HttpStatus.FORBIDDEN);
        }else if(!player.getUserName().equals(gamePlayer.getPlayer().getUserName())) {

            return new ResponseEntity<>("Your username does not match this game player", HttpStatus.FORBIDDEN);
        }else if(gamePlayer.getShips().size() > 0){

            return new ResponseEntity<>("error: Ships already placed", HttpStatus.NOT_FOUND);
        }else if(ships.size() != 5){

            return new ResponseEntity<>("error: more/less ships", HttpStatus.FORBIDDEN);
        }else{

            for (Ship ship : ships) {
                ship.setGamePlayer(gamePlayer);
                shipRepository.save(ship);
            }
            return new ResponseEntity<>("success: ships placed", HttpStatus.CREATED);
        }

    }

    @RequestMapping(path = "/games/players/{gamePlayerId}/salvos", method = RequestMethod.POST)
    public ResponseEntity<Object> addSalvos(@PathVariable long gamePlayerId, @RequestBody List<String> shots, Authentication authentication){

        Player player = playerRepository.findByUserName((authentication.getName()));
        GamePlayer gamePlayer = gamePlayerRepository.findById(gamePlayerId).orElse(null);

        if(isGuest(authentication)){
            Map<String, Object> mensaje=new HashMap<>();
            mensaje.put("error", "Miss player");
            return new ResponseEntity<Object>(mensaje, HttpStatus.FORBIDDEN);
        }else if(gamePlayer == null) {

            return new ResponseEntity<>("No such gamePlayer", HttpStatus.FORBIDDEN);
        }else if(!player.getUserName().equals(gamePlayer.getPlayer().getUserName())) {

            return new ResponseEntity<>("Your username does not match this game player", HttpStatus.FORBIDDEN);
        }else if(shots.size() != 5){

            return new ResponseEntity<>("error: A Salvo needs 5 shots", HttpStatus.FORBIDDEN);
        }else{
            int turn = gamePlayer.getSalvoes().size() + 1;
            Salvo salvo = new Salvo(turn, shots, gamePlayer);
            salvoRepository.save(salvo);


            return new ResponseEntity<>("success: salvo fired", HttpStatus.CREATED);
        }

    }

}