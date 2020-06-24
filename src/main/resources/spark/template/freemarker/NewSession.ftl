<#assign content>
<div id="home-container" class="columns is-vcentered">
	<div id="col" class="column is-vcentered is-multiline">
		<h1 id="title" class="title">New Session</h1>
		<!-- TODO: Replace with form, change redirect to just post/get -->
		<form id='new-session-form' action='/guess-the-song/host-waiting-room' method='POST'>
		  <div class="form-group control">
				<label class="inputlabel form-label" for="nickname">Nickname:</label>
				<p class="inputbox control has-icons-left">
			    <input class="input" type="text" id="nickname" name="nickname" maxlength="30" placeholder="e.g PokemonMaster">
			    <span class="icon is-small is-left">
			      <i class="fas fa-user-astronaut"></i>
			    </span>
			  </p>
		  </div>
			<div class="form-group control">
				<label class="inputlabel form-label" for="numSongs">Number of songs per player:</label>
				<div class="inputbox control has-icons-left">
					<div id="dropSelect" class="select">
						<select id="numSongs" name="numSongs">
							<option value="3">3</option>
							<option value="4">4</option>
							<option value="5">5</option>
							<option value="6">6</option>
							<option value="7">7</option>
							<option value="8">8</option>
						</select>
					</div>
					<div class="icon is-small is-left">
			      <i class="fas fa-music"></i>
			    </div>
				</div>
			</div>
		  <input id='submit-new-session' type="submit" class="button is-primary" value="Create New Session" disabled>
		</form>
		<!-- TODO -->

	</div>
</div>

<script>
	let nickname = document.getElementById("nickname");
	nickname.addEventListener("input", function(event) {
		if (nickname.value.trim().length > 2) {
			document.getElementById("submit-new-session").disabled = false;
		} else {
			document.getElementById("submit-new-session").disabled = true;
		}
	});
</script>
</#assign>
<#include "main.ftl">