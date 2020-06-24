<#assign content>
<div id="home-container">
	<h1 id="title">Join a Session</h1>

	<form id='new-session-form' action="/guess-the-song/waiting-room" method="POST">
	  <label class="form-label" for="gameCode">Session code:</label><br>
		<p class="control has-icons-left">
			<input class="input" type="text" id="gameCode" name="gameCode" maxlength="30" placeholder="e.g 000001"><br>
			<span class="icon is-small is-left">
				<i class="fas fa-qrcode"></i>
			</span>
		</p>
		<label class="form-label" for="nickname">Nickname:</label><br>
		<p class="control has-icons-left">
			<input class="input" type="text" id="nickname" name="nickname" maxlength="30" placeholder="e.g PokemonMaster"><br>
			<span class="icon is-small is-left">
				<i class="fas fa-user-astronaut"></i>
			</span>
		</p>



	  <input type="submit" id="submit-join-session" class="button is-primary" value="Submit" disabled>
	</form>
	<p class="is-red is-robot">${error}</p>
</div>

	<script>
		let nickname = document.getElementById("nickname");
		let code = document.getElementById("gameCode");

		code.addEventListener("input", function(event) {
			if (nickname.value.trim().length > 2 && code.value.trim().length > 0) {
				document.getElementById("submit-join-session").disabled = false;
			} else {
				document.getElementById("submit-join-session").disabled = true;
			}
		});

		nickname.addEventListener("input", function(event) {
			if (nickname.value.trim().length > 2 && code.value.trim().length > 0) {
				document.getElementById("submit-join-session").disabled = false;
			} else {
				document.getElementById("submit-join-session").disabled = true;
			}
		});

	</script>
</#assign>
<#include "main.ftl">