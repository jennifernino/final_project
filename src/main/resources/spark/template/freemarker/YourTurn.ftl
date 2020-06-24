<#assign content>
<div id="home-container" class="columns is-vcentered">

	<div id="col" class="column is-vcentered is-multiline">
		<progress id="progress-bar" style="transform:rotate(180deg);" class="progress is-success" value="100" max="100">100%</progress>
		<h1 id="title" class="title">Guess the song</h1>

		<div id="timeradd"></div>
		<form id='new-session-form' action="/guess-the-song/guess-submitted" method="post">
			<div>
			  <label id="subtitle" class="is-green is-robot" for="songguess">Guess:</label><br>
				<p id="copy-clip" class="control has-icons-left">
					<input class="input" type="text" id="songguess" name="songguess"><br>
					<span class="icon is-small is-left">
						<i class="fas fa-question-circle"></i>
					</span>
				</p>
		  	<input id='submit-button' type="submit" class="button is-success" placeholder="Song guess..." value="Submit">
			</div>
		</form>
		<div id="audio-box">
		</div>


		<audio id="audio" autoplay hidden>
			<source src="${audioLink}" />
		</audio>
	</div>
</div>

	<!--<script src="https://sdk.scdn.co/spotify-player.js"></script>-->
	<script>
	let started = false;
	const TIME_LIM = 30;
	let timePassed = 0;
	let timeLeft = TIME_LIM;
	let timeInterval = null;
	let submitted = false;

	function countdown(){
		timerInterval = setInterval(() => {
			if (timePassed > 30) {
				timePassed = 30;
			} else {
				timePassed = timePassed + .0125;
			}

			timeLeft = TIME_LIM - timePassed;
			timeLeft = Math.floor(timeLeft)
			if (timeLeft < 0) {
				timeLeft = 0
			}
			if (timeLeft == 0 && !submitted) {
				// If it lags, we only want to call once
				document.forms[0].submit();
				submitted = true;
			}

			let progress = (30 - timePassed) / 30.0;
			let percent = (progress * 100);

			document.getElementById("progress-bar").setAttribute('value',percent)
			document.getElementById("progress-bar").innerHTML = percent + "%";
			document.getElementById("base-timer-label").innerHTML = formatTimeLeft(timeLeft);
		}, 12.5);
	}
	function formatTimeLeft(timeLeft) {
		let seconds = timeLeft;
		if (timeLeft < 10) {
			seconds = "0" + seconds;
		}
		seconds = "0:" + seconds;
		return seconds
	}

	document.getElementById("timeradd").innerHTML = ""+
	"<div class=\"base-timer\">" +
		"<svg class=\"base-timer__svg\" viewBox=\"100 100 300 300\" xmlns=\"http://www.w3.org/2000/svg\">" +
			"<g class=\"base-timer__circle\">" +
				"<circle class=\"base-timer__path-elapsed\" cx=\"150\" cy=\"150\" r=\"45\"/>" +
			"</g>" +
		"</svg>" +
		"<span id=\"base-timer-label\" class=\"base-timer__label\">" +
			 formatTimeLeft(timeLeft)
		+ "</span></div>";

	const playFromLink = () => {
		document.getElementById("audio").play();
		countdown();
		started = true;
		// document.getElementById("play-button").value = "Pause";
	}

	const getCode = () => {
	  const items = document.cookie.split(';');
	  for (cookie of items) {
	    const cleaned = cookie.trim();
			if (cleaned.startsWith("gameCode=")) {
				return cleaned.split("gameCode=")[1];
			}
	  }
	  return "";
	};

	(() => {
    if ((typeof(window.orientation) !== "undefined") || (navigator.userAgent.indexOf('IEMobile') !== -1)) {
			const playButton = document.createElement("input");
			playButton.setAttribute('type', 'button');
			playButton.classList.add('button');
			playButton.classList.add('is-primary');
			playButton.setAttribute('value', 'Play');
			playButton.id = "play-button";
			document.getElementById("col").appendChild(playButton)
			playButton.addEventListener('click', has_play_button)
		} else {
			playFromLink();
		}

	})();
	function has_play_button() {
		if (document.getElementById("play-button").value === "Pause") {
			document.getElementById("audio").pause();
			document.getElementById("play-button").value = "Play";
		} else {
			document.getElementById("audio").play();
			document.getElementById("play-button").value = "Pause";
		}
		if (!started) {
			countdown();
		}
	}


	// try { TODO: Get rid of try catch - does not get rid of 504
	// 	window.onSpotifyWebPlaybackSDKReady = () => {
	// 		// TODO: Is this the most secure way to do this?
	// 		const gameCode = getCode();
	// 		const url = "/get-token-player/" + gameCode;
	// 		console.log("Making call to get token and uri with gamecode:" + gameCode);
	// 		$.ajax({
	// 			url: url,
	// 			type: "GET",
	// 			contentType: "application/json; charset=utf-8",
	//       dataType: "json",
	// 			beforeSend: function(xhr) {
	//   			xhr.setRequestHeader("Accept", "application/json");
	//         xhr.setRequestHeader("Content-Type", "application/json");
	//       },
	// 			success: function(data) {
	// 				let played = false;
	// 				token = data.token;
	// 				song = data.uri;
	// 				console.log("Success!");
	// 				console.log("TOKEN: ", token);
	// 				console.log("SONG: ", song);
	//
	// 		  	const player = new Spotify.Player({
	// 	 				name: 'Web Playback SDK Quick Start Player',
	// 	 				getOAuthToken: cb => { cb(token); }
	// 	 			});
	// 				// Error handling
	// 				player.addListener('initialization_error', ({ message }) => {
	// 					console.error(message);
	// 					playFromLink();
	// 					// countdown();
	// 					played = true;
	// 					return;
	// 				});
	// 				player.addListener('authentication_error', ({ message }) => { console.error(message); });
	// 				player.addListener('account_error', ({ message }) => { console.error(message); });
	// 				player.addListener('playback_error', ({ message }) => { console.error(message); });
	// 				// Playback status updates
	// 				player.addListener('player_state_changed', state => { console.log(state); });
	// 				// Ready
	// 				player.addListener('ready', ({ device_id }) => {
	// 					console.log('Ready with Device ID', device_id);
	// 					// Play a track using our new device ID
	// 					if (!played) {
	// 						play(device_id);
	// 						countdown();
	// 					}
	//
	//
	// 				});
	// 				// Not Ready
	// 				player.addListener('not_ready', ({ device_id }) => {
	// 					console.log('Device ID has gone offline', device_id);
	// 				});
	// 				// Connect to the player!
	// 				player.connect();
	//
	// 				function play(device_id) {
	// 					// FIXME Song URIs go in data
	// 					data = "{\"uris\": " +"\[\"" + song + "\"\]" + "}";
	// 					console.log(data);
	// 					$.ajax({
	// 					 url: "https://api.spotify.com/v1/me/player/play?device_id=" + device_id,
	// 					 type: "PUT",
	// 					 data: data,
	// 					 beforeSend: function(xhr){xhr.setRequestHeader('Authorization', 'Bearer ' + token );},
	// 					 success: function(data) {
	// 						 console.log(data)
	// 					 }
	// 					});
	// 				}
	// 		 },
	// 		 error: function(data) {
	// 			 console.log("WOAH ERROR!!!");
	// 			 console.log(error);
	// 			 console.log("HEEYYY")
	// 		 }
	// 		});
	// 	};
	// } catch(error) {
	// 	console.log("Error with SpotifyWebPlaybackSDKPlayer");
	// }


</script>
</#assign>
<#include "main.ftl">