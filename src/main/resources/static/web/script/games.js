

var app = new Vue({
  el: '#app',
  data: {
    games: [],
    player: [],
    scores: [],
  }
});


$(function() {

	function getData(){

        fetch("/api/games",{
              method: "GET",

        })
        .then(function(response){
             return response.json()
        }).then(function(json){
            app.games = json.games;
            app.player = json.player;
            findPlayerScore();
        }).catch(function(error){
            console.log("error: "+error)
        })
	}
	getData();
})

function findPlayerScore(){
    app.games.forEach(function(game){
        game.gamePlayers.forEach(function(gamePlayer){
            if(!playerScoreExist(gamePlayer.player.id)){
                var playerScore = {
                    id : gamePlayer.player.id,
                    email : gamePlayer.player.email,
                    win : 0,
                    ties : 0,
                    lost : 0,
                    pts : 0
                }
                app.scores.push(playerScore);
            }
            makeScore(gamePlayer.score,gamePlayer.player.id);
        })
    });
}

function playerScoreExist(playerId){
    var exist = false;
    app.scores.forEach(function(score){
        if (score.id == playerId)
            exist = true;
    })
    return exist;
}

function makeScore(playerScore,playerId){
    app.scores.forEach(function(score){
        if(score.id == playerId){
            switch(playerScore){
                case 1.0:
                    score.win += 1;
                    score.pts += 1;
                    break;
                case 0.5:
                    score.ties += 1;
                    score.pts += 0.5;
                    break;
                 default:
                    score.lost += 1;
                    break;
            }
        }
    })
}

function makeStats() {
	app.forEach(function(dataTable){
	dataTable.gamePlayers.forEach(function(i) {
		statistics.forEach(function (j) {
			if (i.score > i.score) {
				if (i.playerId == j.playerId ) {
					j.win += 1.0
				} else if (i.playerId  == j.playerId ) {
					j.lost += 1.0
				}
			} else if (i.score < i.score) {
				if (i.playerId  == j.playerId ) {
					j.win += 1.0
				} else if (i.playerId  == j.playerId ) {
					j.lost += 1.0
				}
			} else {
				if (i.playerId  == j.playerId ) {
					j.ties += 0.5
				} else if (i.playerId  == j.playerId ) {
					j.ties += 0.5
				}
			}
		})

	})
})

}

function login(){
	var searchEmail = $("#email").val()
    var searchPass = $("#pwd").val()
if(searchEmail == "" || searchPass == "")
{console.log("write something asshole")
} else{

    $.post("/api/login", { userName: searchEmail , password: searchPass }).done(function() { console.log("logged in!"); window.location.reload() }).fail(function(){console.log("go away")})
   }
}

function logout(){
$.post("/api/logout").done(function() { console.log("logout!"); window.location.reload() })
}

function signIn(){
	var searchEmail = $("#email").val()
    var searchPass = $("#pwd").val()
    var team = $("#team").val()

 if(searchEmail == "" || searchPass == "")
 {console.log("write something asshole")
 } else{
 $.post("/api/players", { name: searchEmail ,team: team, password: searchPass}).done(function() { console.log("player created!"); login();}).fail(function(){console.log("error")})

  }

 }

 function toggleSignInForm(){
    $("#SignIn").toggle()
    $("#team").toggle()
 }

function newGame(){

$.post("/api/games").done(function(response) {window.location.href="game.html?gp="+response.id;}).fail(function(){console.log("error")})
}

function joinGame(gameId){


$.post("/api/game/"+gameId+"/players").done(function(response)  {window.location.href="game.html?gp="+response.id;}).fail(function(response){console.log(response)})


}




/*function sumaPuntos() {
	statistics.forEach(function (i) {
		i.pts = i.win * 1.0 + i.ties
	})
}


function findId (){
app.forEach(function(dataOfPlayer){
dataOfPlayer.gamePlayers.forEach(function(idOfPlayer){
if (idOfPlayer == player.id)
playerId.push(findId)
})
})

}*/