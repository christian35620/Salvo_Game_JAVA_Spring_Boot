var app = new Vue({  
    el: '#app',  
    data: {    
        datos: [],
        username:"",
        password:"",
        lastPage:"",
        gpId:"",
        loggedIn:0,
        currentUser:0,
        shotsLeft:5,
        message:"",
        letters:["A","B","C","D","E","F","G","H","I","J"],
        shipLocations:[],
        salvoRound:[]
    },
    created(){
        this.selectURL();
        this.validateUser();
    },
    methods: {
        playSound(sound) {
            
            if(sound) {
                var audio = new Audio(sound);
                audio.play();
              }
        
        },
        validateUser(){
            if (window.location.href=="http://localhost:8080/web/games.html") {
                this.getData(this.url);
            }  
            if (window.location.href=="http://localhost:8080/web/gridModificado.html") {
                
                // Recuperamos el historial almacenado en local 
                this.gpId = JSON.parse(sessionStorage.getItem('gameplayerId'));
                console.log(this.gpId)
                // Eliminamos el elemento "history" del almacenamiento local.
                // sessionStorage.removeItem('gameplayerId');
            }
            if (window.location.href=="http://localhost:8080/web/game.html") {
                var fill = 0;
                var update = setInterval(function() {
                    fill += 1;
                    $('#fillIcon').css('height', (fill+'%')); 
                    $('#fillIcon').css('margin-top', ((100 - fill)+'%')); 
                    if (fill === 100) {
                        clearInterval(update);        
                    }
                }, 100);
            }  

            


        },
        formAction(action){
            switch (action) {
                case 'login':
                    this.login();
                    break;
                case 'register':
                    this.register();
                    break;
                default:
                    break;
            }
        },
        gotoPage(direction){
            self=this;
            switch (direction) {
                case "fw":
                   
                    // Guardamos en local (sessionStorage) el gpId
                    sessionStorage.setItem('gameplayerId', JSON.stringify(getParameterByName("gp", window.location.href)));
                    window.location.href="http://localhost:8080/web/gridModificado.html";                    
                    
                    break;
                case "bk":
                    window.history.back()
                    // document.location.replace(self.lastPage);
                    break;
                default:
                    break;
            }
            
        },
        getParameterByName(name,url) {
            var match = RegExp('[?&]' + name + '=([^&]*)').exec(url);
            // var match = RegExp('[?&]' + name + '=([^&]*)'+"1");
            return match && decodeURIComponent(match[1].replace(/\+/g, ' '));
        },
        selectURL() {    
            this.url='http://localhost:8080/api/games';  /*usar para datos de localhost*/
        },
        getData(url) {
            var self=this;
            fetch(url, {
                method: 'GET'
            })
            .then(function(response) {
                if (response.ok) {
                    return response.json()
                  } else {
                    return Promise.reject('fetch promise rejected!')
                  }
            })
            .then(function(datos) {
                self.datos=datos; 
                console.log(datos);
                if (datos.currentPlayer!="N/A") {
                    self.currentUser=self.datos.currentPlayer.playerID;
                    self.username=self.datos.currentPlayer.playerEmail;
                    self.loggedIn=1;

                    self.datos.games.forEach(game => {
                        //Me fijo si el jugador actual esta en el juego
                        var gamePlayerSelected = 
                            game.gamePlayers.find(gamePlayer => gamePlayer.player.playerID == self.datos.currentPlayer.playerID);
                        game["gpId"] = gamePlayerSelected != undefined ? gamePlayerSelected.id : null;
                    });
                }
            })
            .catch(function(err) {
                console.error("el error sucedido es: "+err);
            });
            
        },
        loginData(url) {
            var self=this;
            fetch(url, {
                method: 'GET'
            })
            .then(function(response) {
                if (response.ok) {
                    return response.json()
                  } else {
                    return Promise.reject('fetch promise rejected!')
                  }
            })
            .then(function(datos) {
                self.datos=datos; 
                if (datos.currentPlayer!="N/A") {
                    self.currentUser=self.datos.currentPlayer.playerID;
                    self.username=self.datos.currentPlayer.playerEmail;
                    self.loggedIn=1;

                    self.datos.games.forEach(game => {
                        //Me fijo si el jugador actual esta en el juego 
                        var gamePlayerSelected = 
                            game.gamePlayers.find(gamePlayer => gamePlayer.player.playerID == self.datos.currentPlayer.playerID);
                        game["gpId"] = gamePlayerSelected != undefined ? gamePlayerSelected.id : null;
                    });
                }
            })
            .catch(function(err) {
                console.error("el error sucedido es: "+err);
            });
            
        },
        login() {
            var self=this;
            let formData = new FormData();
            formData.append('username', this.username);
            formData.append('password', this.password);
            
            const data = new URLSearchParams(formData);

            fetch("http://localhost:8080/api/login", {
                method: 'POST',
                credentials: 'same-origin',
                body: data
            })
            .then(function(response) {
                if (response.ok) {
                    console.log(response);
                } else {
                    return Promise.reject('fetch promise rejected!')
                }
            })
            .then(function() {
                document.querySelector("#loggedOut").classList.remove("show");
                self.loggedIn=1;
                self.loginData(self.url);
                // document.location.replace("http://localhost:8080/web/game.html");
            })
            .catch(function(err) {
                console.error("el error sucedido es: "+err);
            })
        },
        register() {
            var self=this;
            let formData = new FormData();
            formData.append('email', this.username);
            formData.append('password', this.password);
            
            const data = new URLSearchParams(formData);

            fetch("http://localhost:8080/api/players", {
                method: 'POST',
                credentials: 'same-origin',
                body: data
            })
            .then(function(response) {
                if (response.ok) {
                    console.log(response);
                } else {
                    return Promise.reject('fetch promise rejected!')
                }
            })
            .then(function() {
                self.loggedIn=1;
                self.login();
            })
            .catch(function(err) {
                console.error("el error sucedido es: "+err);
            });  
        },
        createGame() {
            var self=this;

            fetch("http://localhost:8080/api/games", {
                method: 'POST',
            })
            .then(function(response) {
                if (response.ok) {
                    return response.json();
                    
                } else {
                    return Promise.reject('fetch promise rejected!')
                }
            })
            .then(function(datos) {
                // self.loggedIn=1;
                // self.getData(self.url);
                // var gpId=JSON.parse(datos);
                console.log(datos)
                sessionStorage.setItem('gameplayerId', datos.gamePlayerId);
                window.location.href="http://localhost:8080/web/gridModificado.html"; 
            })
            .catch(function(err) {
                console.error("el error sucedido es: "+err);
            });  
        },
        joinGameButton(game){
            self=this;
            if (game.gamePlayers.length<2&&game.gamePlayers[0].player.playerEmail!=self.datos.currentPlayer.playerEmail&&(game.scores.length==0)&&self.loggedIn==1) {
                return true
            } else {
                return false
            }
            
        },
        joinGame(gameId) {
            var self=this;

            fetch("http://localhost:8080/api/games/"+gameId+"/players", {
                method: 'POST',
                credentials: 'same-origin',
                // body: data
            })
            .then(function(response) {
                if (response.ok) {
                    return response.json();
                } else {
                    return Promise.reject('fetch promise rejected!')
                }
            })
            .then(function(datos) {
                // self.loggedIn=1;
                // self.getData(self.url);
                console.log(datos);
                sessionStorage.setItem('gameplayerId', datos.gamePlayerId);
                window.location.href="http://localhost:8080/web/gridModificado.html";
            })
            .catch(function(err) {
                console.error("el error sucedido es: "+err);
            });  
        },
        traduce(tipo,alto,ancho,x,y){
            var letras=["A","B","C","D","E","F","G","H","I","J"];
            var locations=[];
            var caso=alto-ancho;

            if (caso<0) { //posicion de barco horizontal
                var letra=letras[y];
                    for (let index = 1+parseInt(x); index < 1+parseInt(x)+parseInt(ancho); index++) {
                        locations.push(letra+index)                       
                    }
            } else if(caso>0){ //posicion de barco vertical
                var numero=parseInt(x)+1;
                    for (let index = parseInt(y); index < parseInt(y)+parseInt(alto); index++) {
                        locations.push(letras[index]+numero)      
                    }
            }

            this.shipLocations.push({"type":tipo,"location":locations})

        },
        placeShips() {
            var self=this;
            document.querySelectorAll("div[data-gs-x]").forEach(ship => {
                this.traduce(ship.id, ship.dataset.gsHeight,ship.dataset.gsWidth,ship.dataset.gsX,ship.dataset.gsY)
            });


            fetch("http://localhost:8080/api/games/players/"+self.gpId+"/ships", {
                method: 'POST',
                credentials: 'same-origin',
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(self.shipLocations)
            })
            .then(function(response) {
                if (response.ok) {
                    console.log(response);
                } else {
                    return Promise.reject('fetch promise rejected!')
                }
            })
            .then(function() {
                self.loggedIn=1;
                var gpId = JSON.parse(sessionStorage.getItem('gameplayerId'));
                window.location.href="http://localhost:8080/web/game.html?gp="+gpId; 
                // window.history.back()
                // self.getData(self.url);
            })
            .catch(function(err) {
                console.error("el error sucedido es: "+err);
            });  
        },
        placeShot(id,sound){
            if (this.shotsLeft>0&&this.salvoRound.indexOf(id)==-1) {
                this.playSound(sound);
                document.querySelector("#salvoes "+"#"+id).classList.add("shot");
                this.salvoRound.push(id);
                this.shotsLeft-=1;
            } else if (this.shotsLeft<6&&this.salvoRound.indexOf(id)!=-1){
                document.querySelector("#salvoes "+"#"+id).classList.remove("shot");
                this.salvoRound.splice(this.salvoRound.indexOf(id),1);
                this.shotsLeft+=1;
            
            } else {
                alert("You have not shots left! \nTry deselecting shot locations to change your salvo round");
            }
            

            
            
            
        },
        placeSalvo(){
            var self=this;
            this.gpId=getParameterByName("gp", window.location.href)
            if (self.salvoRound.length==5) {
                fetch("http://localhost:8080/api/games/players/"+self.gpId+"/salvos", {
                method: 'POST',
                credentials: 'same-origin',
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(self.salvoRound)
                })
                .then(function(response) {
                    if (response.ok) {
                        console.log(response);
                    } else {
                        return Promise.reject('fetch promise rejected!')
                    }
                })
                .then(function() {
                    loadData(self.gpId);
                    document.querySelectorAll(".shot").forEach(shot => {
                        shot.classList.remove("shot")});
                    self.salvoRound.length=0;
                    self.shotsLeft=5;
                })
                .catch(function(err) {
                    console.error("el error sucedido es: "+err);
                }); 
            } else {
                console.log("cantidad incorrecta de salvos")
            }
        },
        logout() {
            var self=this;
            self.loggedIn=0;
            let formData = new FormData();
            formData.append('username', this.username);
            formData.append('password', this.password);
            
            const data = new URLSearchParams(formData);

            fetch("http://localhost:8080/api/logout", {
                method: 'POST',
                credentials: 'same-origin',
                body: data
            })
            .then(function(response) {
                if (response.ok) {
                    console.log(response);
                  } else {
                    return Promise.reject('fetch promise rejected!')
                  }
            })
            .then(function() {
                console.log("logged out");
                
                document.location.replace("http://localhost:8080/web/games.html");
            })
            .catch(function(err) {
                console.error("el error sucedido es: "+err);
            });
        }
    }
});