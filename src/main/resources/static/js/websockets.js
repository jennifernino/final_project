const GAME_TYPE = {
  CONNECTED: 0,
  NEWMEMBER: 1,
  LOCKED: 2,
  READY: 3,
  BEGIN: 4,
  SCOREBOARD: 5,
  ROUND: 6,
  EXITED: 7,
  HOSTLEFT: 8
};

// Note: [heroku/deployment]: currently default ("ws") is set up for localhost, to switch to
// heroku, change "ws://" to "wss://" below, and let REDIRECT_URI = REDIRECT_URI_HEROKU in Constants
var conn = new WebSocket("wss://" + window.location.host +
  "/websocket/cs32/sonic/skillz/users");

conn.onmessage = function(msg) {
  const data = JSON.parse(msg.data);
  switch (data.type) {
    case GAME_TYPE.CONNECTED:

      let userID = data.payload.id;
      document.cookie = "userID=" + userID.trim() + ";";
      break;
    case GAME_TYPE.NEWMEMBER:
      (function() {
        window.location.href = window.location.href;
      })();
      break;
    case GAME_TYPE.LOCKED:
      (function() {
        window.location.replace('/guess-the-song/start-game');
      })();
      break;
    case GAME_TYPE.READY:
      (function() {
        window.location.replace('/guess-the-song/game-ready');
      })();
      break;
    case GAME_TYPE.BEGIN:
      (function() {
        window.location.replace('/guess-the-song/welcome-page');
      })();
      break;
    case GAME_TYPE.SCOREBOARD:
      (function() {
        let result = data.payload.result;
        window.location.replace('/guess-the-song/scoreboard/' + result);
      })();
      break;
    case GAME_TYPE.ROUND:
      (function() {
        window.location.replace('/guess-the-song/game-round');
      })();
      break;
    case GAME_TYPE.EXITED:
      (function() {
        window.location.replace('/error/uls');
      })();
      break;
    case GAME_TYPE.HOSTLEFT:
      (function() {
        window.location.replace('/error/hls');
      })();
      break;
    default:
      console.log('Unknown message: ' + data.type);
      break;

  }
  console.log(msg);
};

setInterval(function() {
    conn.send(JSON.stringify({
      message: "HELLOOOOOOO!!! I AM ALIVE!"
    }));
  },
  20000);
conn.onclose = function(e) {

};
