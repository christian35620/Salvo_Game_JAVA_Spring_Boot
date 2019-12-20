$(function() {
    loadData();
});

function getParameterByName(name) {
    var match = RegExp('[?&]' + name + '=([^&]*)').exec(window.location.search);
    // var match = RegExp('[?&]' + name + '=([^&]*)'+"1");
    return match && decodeURIComponent(match[1].replace(/\+/g, ' '));
};

function loadData(gamePlayer){
    var gamePlayer=getParameterByName('gp');
    $.get('/api/game_view/'+gamePlayer)
        .done(function(data) {
            console.log(data)
            let playerInfo;
            let shipLocations=[];
            if(data.gamePlayers[0].id == gamePlayer){
                try {
                    playerInfo = [data.gamePlayers[0].player.playerEmail,data.gamePlayers[1].player.playerEmail];
                    playerIDs=[data.gamePlayers[0].player.playerID,data.gamePlayers[1].player.playerID];
                } catch (error) {
                    playerInfo = [data.gamePlayers[0].player.playerEmail,""];
                    playerIDs=[data.gamePlayers[0].player.playerID,""];
                }
                
            }else{
                try {
                    playerInfo = [data.gamePlayers[1].player.playerEmail,data.gamePlayers[0].player.playerEmail];
                    playerIDs=[data.gamePlayers[1].player.playerID,data.gamePlayers[0].player.playerID];
                } catch (error) {
                    playerInfo = ["",data.gamePlayers[0].player.playerEmail];
                    playerIDs=["",data.gamePlayers[0].player.playerID];
                }
                
            }
            $('#playerInfo').text(playerInfo[0] + '(you) vs ' + playerInfo[1]);

            data.ships.forEach(function(shipPiece){
                shipLocations.push(...shipPiece.location);
            });

            shipLocations.forEach(shipLocation => {
                $('#ships'+' '+'#'+shipLocation).addClass('ship-piece');
            });
           

            data.salvoes.forEach(function(salvo){
                if (salvo.player==playerIDs[1]) {
                    salvo.location.forEach(function(salvoLocation){
                        if (shipLocations.indexOf(salvoLocation)!=-1) {
                            $('#ships'+' '+'#'+salvoLocation).addClass('ship-piece-hited');
                            $('#ships'+' '+'#'+salvoLocation).text(salvo.turn);
                        }
                    })
                }

                if (salvo.player==playerIDs[0]) {
                    salvo.location.forEach(salvoLocation => {
                        $('#salvoes'+' '+'#'+salvoLocation).addClass('salvo');
                        $('#salvoes'+' '+'#'+salvoLocation).text(salvo.turn);
                    });
                    salvo.hits.forEach(salvoHit => {
                        $('#salvoes'+' '+'#'+salvoHit).removeClass('salvo');
                        $('#salvoes'+' '+'#'+salvoHit).addClass('ship-piece-hited');
                        $('#salvoes'+' '+'#'+salvoHit).text(salvo.turn);
                    });
                    salvo.sunk.forEach(sunk => {
                        switch (sunk.type) {
                            case "carrier":
                                document.getElementById("carrier_e").innerHTML="<span>SUNK</span>"
                                break;
                            case "battleship":
                                document.getElementById("battleship_e").innerHTML="<span>SUNK</span>"
                                break;
                            case "destroyer":
                                document.getElementById("destroyer_e").innerHTML="<span>SUNK</span>"
                                break;
                            case "submarine":
                                document.getElementById("submarine_e").innerHTML="<span>SUNK</span>"
                                break;
                            case "patrol_boat":
                                document.getElementById("patrol_boat_e").innerHTML="<span>SUNK</span>"
                                break;
                            default:
                                break;
                        }
                    });
                }


                
                
            });
        })
        .fail(function( jqXHR, textStatus ) {
          alert( "Failed: " + textStatus );
        });
};