var gpQuery = paramObj(location.href).gp
var miJugador;
var contrincante;
var data;
var shootsFired = 0;

var optionsForStaticGrid = {
        //grilla de 10 x 10
        width: 10,
        height: 10,
        //separacion entre elementos (les llaman widgets)
        verticalMargin: 0,
        //altura de las celdas
        cellHeight: 40,
        //desabilitando el resize de los widgets
        disableResize: true,
        //widgets flotantes
		float: true,
        //removeTimeout: 100,
        //permite que el widget ocupe mas de una columna
        disableOneColumnMode: true,
        //false permite mover, true impide
        staticGrid: true,
        //activa animaciones (cuando se suelta el elemento se ve más suave la caida)
        animate: true
    }

    var optionsMove = {
            //grilla de 10 x 10
            width: 10,
            height: 10,
            //separacion entre elementos (les llaman widgets)
            verticalMargin: 0,
            //altura de las celdas
            cellHeight: 40,
            //desabilitando el resize de los widgets
            disableResize: true,
            //widgets flotantes
    		float: true,
            //removeTimeout: 100,
            //permite que el widget ocupe mas de una columna
            disableOneColumnMode: true,
            //false permite mover, true impide
            staticGrid: false,
            //activa animaciones (cuando se suelta el elemento se ve más suave la caida)
            animate: true
        }

function listOfShips(){

var shipData = [];

$(".ships").each(function(index, ship){
    var obj = new Object;
    obj.shipType = ship.id;

    var x = +(ship.dataset.gsX)+1;
    var y = +(ship.dataset.gsY);
    var w = ship.dataset.gsWidth;
    var h = ship.dataset.gsHeight;
    var cellsArray = []

    if (w > h){
        for(i=0; i < w; i++){
                    cellsArray.push(changeNumber(y)+(x+i))
                }
    } else{
        for(i=0; i < h; i++){
                    cellsArray.push(changeNumber(y+i)+x)
                }
    }

obj.shipLocation = cellsArray;


shipData.push(obj)

})


$.post({
url: "/api/games/players/"+gpQuery+"/ships",
data: JSON.stringify(shipData),
contentType: "application/json"
}).done(function(response){
            console.log(response);
            window.location.reload();

            }).fail(function(response){
            console.log(response)

            })
}

function apuntar(e){

        if (!$("#"+e.target.id).hasClass("piedrazo")  && !$("#"+e.target.id).hasClass("tiraPiedra")){
            if(shootsFired < 5){
                $("#"+e.target.id).addClass("tiraPiedra")
                shootsFired++;
            }
            else{
                alert("No puedes tirar mas de 5 piedras")
            }
        } else if($("#"+e.target.id).hasClass("tiraPiedra")){
            $("#"+e.target.id).removeClass("tiraPiedra")
            shootsFired--;
        }

}

function listOfSalvo(){
var salvoData = new Object;
var array = []
salvoData.turn = getTurn()



$(".tiraPiedra").each(function(index, salvo){

array.push(salvo.id)

})

salvoData.salvoShoots = array

$.post({
url: "/api/games/players/"+gpQuery+"/salvos",
data: JSON.stringify(salvoData),
contentType: "application/json"
}).done(function(response){
            console.log(response);
            window.location.reload();

            }).fail(function(response){
            console.log(response)

            })
}
function getTurn(){
var turn = 0
data.salvoes.forEach(function(salvo){
if (salvo.players.id == miJugador.id){
turn ++
}
})

if(turn  == 0  ){
    return 1
} else{
    return turn + 1
}

}

$(function() {

	function getData(){

        fetch("/api/game_view/"+gpQuery,{
              method: "GET",

              }).then(function(response){
                     return response.json()
              }).then(function(json){
              data = json;
              getPlayers();
             if (data.ship.length <= 0){
                crearGrilla(optionsMove)
             }
             else{
                crearGrilla(optionsForStaticGrid)
             }
             salvoesShoots();
             findHitsAndSinks();
             enableButtons();
             if(data.gameState != 'PLACE_SHIP')
                setInterval(function(){location.reload()}, 10000);
        }).catch(function(){
            console.log("error")
        })
	}




	getData();
})

function enableButtons(){
    $("#newShip").hide();
    $("#shoot").hide();
    switch(data.gameState){
        case 'PLACE_SHIP':
            $("#newShip").show();
            app.gameState = 'Posicione su Barra!!';
        break;
        case 'WAIT':
            app.gameState = 'Aguantiiiaaaaaaaa!!';
        break;
        case 'PLAY':
            app.gameState = 'Dale gato, reventale la ventana!!';
            $("#shoot").show();
        break;
        case 'WIN':
            app.gameState = 'Saluda al campeon papa!!';
        break;
        case 'LOSE':
            app.gameState = 'Ponganle ganas boludos!!';
        break;
        case 'TIE':
            app.gameState = 'Nos vemos en la vuelta, safaron!!';
        break;
    }
}

function crearGrilla (options) {

    //se inicializa el grid con las opciones
    $('.grid-stack').gridstack(options);

    grid = $('#grid').data('gridstack');


    //verificando si un area se encuentra libre
    //no está libre, false
    console.log(grid.isAreaEmpty(1, 8, 3, 1));
    //está libre, true
    console.log(grid.isAreaEmpty(1, 7, 3, 1));




    function shipView (array) {
    array.forEach(function(ship){
        var x = +(ship.location[0].slice(1))-1
        var y = changeLetter(ship.location[0].slice(0,1) )
        var h;
        var w;
        if (ship.location[0].slice(0,1) == ship.location[1].slice(0,1)){
                                h = 1
                                w = ship.location.length
        } else {
                                h = ship.location.length
                                w = 1
        }
        if(w > h){
        grid.addWidget($('<div id="'+ship.type+'"><div class="grid-stack-item-content '+ ship.type+'_H"></div><div/>'),
                        x, y, w, h);
        } else{
        grid.addWidget($('<div id="'+ship.type+'"><div class="grid-stack-item-content '+ ship.type+'_V"></div><div/>'),
                                x, y, w, h);
        }




    })

    }

    if(data.ship.length != 0){
       shipView(data.ship)
    } else{
        if(miJugador.team == "SAN_LORENZO"){

            grid.addWidget($('<div id="BARRA_BRAVA_SL" class="ships"><div class="grid-stack-item-content BARRA_BRAVA_SL_H"></div><div/>'),
                                            0, 0, 5, 1);
            grid.addWidget($('<div id="MICRO_SL" class="ships"><div class="grid-stack-item-content MICRO_SL_H"></div><div/>'),
                                            5, 4, 4, 1);
            grid.addWidget($('<div id="AUTO1_SL" class="ships"><div class="grid-stack-item-content AUTO1_SL_V"></div><div/>'),
                                            0, 0, 1, 3);
            grid.addWidget($('<div id="AUTO2_SL" class="ships"><div class="grid-stack-item-content AUTO2_SL_H"></div><div/>'),
                                            0, 0, 3, 1);
            grid.addWidget($('<div id="GORDO_SL" class="ships"><div class="grid-stack-item-content GORDO_SL_V"></div><div/>'),
                                            0, 0, 1, 2);

            $("#AUTO1_SL").click(function(){
                 if($(this).children().hasClass("AUTO1_SL_H")){
                     grid.resize($(this),1,3);
                     $(this).children().removeClass("AUTO1_SL_H");
                     $(this).children().addClass("AUTO1_SL_V");
                 }else{
                     grid.resize($(this),3,1);
                     $(this).children().addClass("AUTO1_SL_H");
                     $(this).children().removeClass("AUTO1_SL_V");
                 }
             });

             $("#GORDO_SL").click(function(){
                 if($(this).children().hasClass("GORDO_SL_H")){
                     grid.resize($(this),1,2);
                     $(this).children().removeClass("GORDO_SL_H");
                     $(this).children().addClass("GORDO_SL_V");
                 }else{
                     grid.resize($(this),2,1);
                     $(this).children().addClass("GORDO_SL_H");
                     $(this).children().removeClass("GORDO_SL_V");
                 }
             });

              $("#AUTO2_SL").click(function(){
                     if($(this).children().hasClass("AUTO2_SL_H")){
                         grid.resize($(this),1,3);
                         $(this).children().removeClass("AUTO2_SL_H");
                         $(this).children().addClass("AUTO2_SL_V");
                     }else{
                         grid.resize($(this),3,1);
                         $(this).children().addClass("AUTO2_SL_H");
                         $(this).children().removeClass("AUTO2_SL_V");
                     }
                 });

                 $("#BARRA_BRAVA_SL").click(function(){
                     if($(this).children().hasClass("BARRA_BRAVA_SL_H")){
                         grid.resize($(this),1,5);
                         $(this).children().removeClass("BARRA_BRAVA_SL_H");
                         $(this).children().addClass("BARRA_BRAVA_SL_V");
                     }else{
                         grid.resize($(this),5,1);
                         $(this).children().addClass("BARRA_BRAVA_SL_H");
                         $(this).children().removeClass("BARRA_BRAVA_SL_V");
                     }
                 });

                   $("#MICRO_SL").click(function(){
                         if($(this).children().hasClass("MICRO_SL_H")){
                             grid.resize($(this),1,4);
                             $(this).children().removeClass("MICRO_SL_H");
                             $(this).children().addClass("MICRO_SL_V");
                         }else{
                             grid.resize($(this),4,1);
                             $(this).children().addClass("MICRO_SL_H");
                             $(this).children().removeClass("MICRO_SL_V");
                         }
                     });
        }else {
                 grid.addWidget($('<div id="BARRA_BRAVA_HU" class="ships"><div class="grid-stack-item-content BARRA_BRAVA_HU_H"></div><div/>'),
                                                 0, 0, 5, 1);
                 grid.addWidget($('<div id="MICRO_HU" class="ships"><div class="grid-stack-item-content MICRO_HU_H"></div><div/>'),
                                                 5, 4, 4, 1);
                 grid.addWidget($('<div id="AUTO1_HU" class="ships"><div class="grid-stack-item-content AUTO1_HU_V"></div><div/>'),
                                                 0, 0, 1, 3);
                 grid.addWidget($('<div id="AUTO2_HU" class="ships"><div class="grid-stack-item-content AUTO2_HU_H"></div><div/>'),
                                                 0, 0, 3, 1);
                 grid.addWidget($('<div id="GORDO_HU" class="ships"><div class="grid-stack-item-content GORDO_HU_V"></div><div/>'),
                                                 0, 0, 1, 2);

                  $("#AUTO1_HU").click(function(){
                       if($(this).children().hasClass("AUTO1_HU_H")){
                           grid.resize($(this),1,3);
                           $(this).children().removeClass("AUTO1_HU_H");
                           $(this).children().addClass("AUTO1_HU_V");
                       }else{
                           grid.resize($(this),3,1);
                           $(this).children().addClass("AUTO1_HU_H");
                           $(this).children().removeClass("AUTO1_HU_V");
                       }
                   });

                   $("#GORDO_HU").click(function(){
                       if($(this).children().hasClass("GORDO_HU_H")){
                           grid.resize($(this),1,2);
                           $(this).children().removeClass("GORDO_HU_H");
                           $(this).children().addClass("GORDO_HU_V");
                       }else{
                           grid.resize($(this),2,1);
                           $(this).children().addClass("GORDO_HU_H");
                           $(this).children().removeClass("GORDO_HU_V");
                       }
                   });

                    $("#AUTO2_HU").click(function(){
                       if($(this).children().hasClass("AUTO2_HU_H")){
                           grid.resize($(this),1,3);
                           $(this).children().removeClass("AUTO2_HU_H");
                           $(this).children().addClass("AUTO2_HU_V");
                       }else{
                           grid.resize($(this),3,1);
                           $(this).children().addClass("AUTO2_HU_H");
                           $(this).children().removeClass("AUTO2_HU_V");
                       }
                   });

                   $("#BARRA_BRAVA_HU").click(function(){
                       if($(this).children().hasClass("BARRA_BRAVA_HU_H")){
                           grid.resize($(this),1,5);
                           $(this).children().removeClass("BARRA_BRAVA_HU_H");
                           $(this).children().addClass("BARRA_BRAVA_HU_V");
                       }else{
                           grid.resize($(this),5,1);
                           $(this).children().addClass("BARRA_BRAVA_HU_H");
                           $(this).children().removeClass("BARRA_BRAVA_HU_V");
                       }
                   });

                 $("#MICRO_HU").click(function(){
                       if($(this).children().hasClass("MICRO_HU_H")){
                           grid.resize($(this),1,4);
                           $(this).children().removeClass("MICRO_HU_H");
                           $(this).children().addClass("MICRO_HU_V");
                       }else{
                           grid.resize($(this),4,1);
                           $(this).children().addClass("MICRO_HU_H");
                           $(this).children().removeClass("MICRO_HU_V");
                       }
                   });
             }

    }


    //todas las funciones se encuentran en la documentación
    //https://github.com/gridstack/gridstack.js/tree/develop/doc
};



function paramObj(search) {
               var obj = {};
               var reg = /(?:[?&]([^?&#=]+)(?:=([^&#]*))?)(?:#.*)?/g;

               search.replace(reg, function(match, param, val) {
                 obj[decodeURIComponent(param)] = val === undefined ? "" : decodeURIComponent(val);
               });

               return obj;
             }

var app = new Vue({
	el: '#app',
	data: {
        arrayLetter : ["A", "B", "C", "D", "E", "F", "G", "H", "I", "J"],
        gameHistory: [],
        gameState:null,
	}
});

function salvoesShoots(){
    data.salvoes.forEach(function(salvoGrilla){
        if (miJugador.id == salvoGrilla.players.id ){
            salvoGrilla.shoots.forEach(function(shot){
                if(salvoGrilla.hits.indexOf(shot) >= 0){
                    $("#"+shot).addClass("vidrioShoot");
                }
                else
                    $("#"+shot).addClass("piedrazo");
            })
        } else {
            salvoGrilla.shoots.forEach(function(shot){
                var x = +(shot.slice(1))-1;
                var y = changeLetter(shot.slice(0,1));
                data.ship.forEach(function(ship){
                if (ship.location.indexOf(shot) > -1){
                $("#grid").append("<div style='top:"+y*40+"px; left:"+x*40+"px' class='vidrio'><div>")
                }
                })

            })
        }
    })
/*
salvoGrilla.shoots.forEach(function(shot){

 data.ship.forEach(function(shipLocation){
  if (shipLocation.location == shot)
   $("#"+shot).addClass("piedrazo")
   })

})*/
}

function getPlayers(){


data.gamePlayers.forEach(function(gamePlayer){
   if (gamePlayer.id == gpQuery){
   miJugador = gamePlayer.player
   }
   else {

    contrincante = gamePlayer.player



   }

})

if(contrincante != null){
$("#duelo").html("<h1>"+miJugador.email+" vs. "+contrincante.email+"</h1>")
} else{
$("#duelo").html("<h1>"+miJugador.email+" waiting for an opponent</h1>")

}

}




function changeLetter (letra){
    switch(letra) {
     case "A":
       // code block
       return 0
       break;
     case "B":
       // code block
       return 1
       break;

        case "C":
              // code block
              return 2
              break;
            case "D":
              // code block
              return 3
              break;

               case "E":
                     // code block
                     return 4
                     break;
                   case "F":
                     // code block
                     return 5
                     break;

                      case "G":
                            // code block
                            return 6
                            break;
                          case "H":
                            // code block
                            return 7
                            break;
                             case "I":
                                   // code block
                                   return 8
                                   break;
                                 case "J":
                                   // code block
                                   return 9
                                   break;

   }
   }

function changeNumber (number){
        switch(number) {
         case 0:
           // code block
           return "A"
           break;
         case 1:
           // code block
           return "B"
           break;

            case 2:
                  // code block
                  return "C"
                  break;
                case 3:
                  // code block
                  return "D"
                  break;

                   case 4:
                         // code block
                         return "E"
                         break;
                       case 5:
                         // code block
                         return "F"
                         break;

                          case 6:
                                // code block
                                return "G"
                                break;
                              case 7:
                                // code block
                                return "H"
                                break;
                                 case 8:
                                       // code block
                                       return "I"
                                       break;
                                     case 9:
                                       // code block
                                       return "J"
                                       break;

       }
       }

function findHitsAndSinks(){
    var histories = [];
    data.salvoes.forEach(function(salvo,index){
        var turnHistory = {};
        if(miJugador.id == salvo.players.id){
            turnHistory.turn = salvo.turn,
            turnHistory.opponent = {
                                    hits: '-',
                                    left: '-',
                                }
            turnHistory.player = {
                hits: salvo.hits.join(',') + ' || ' + salvo.sinks.join(','),
                left: 5 - salvo.sinks.length
            }
            data.salvoes.forEach(function(salvoOpponent,indexOpponent){
                if(contrincante.id == salvoOpponent.players.id && salvoOpponent.turn == turnHistory.turn){
                    turnHistory.opponent = {
                        hits: salvoOpponent.hits.join(',')+ ' || ' + salvoOpponent.sinks.join(','),
                        left: 5 - salvoOpponent.sinks.length
                    }
                }
            });
            histories.push(turnHistory);
        }
    });
    app.gameHistory = histories;
}

/* */