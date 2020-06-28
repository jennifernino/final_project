<#assign content>
<div id="home-container" class="columns is-vcentered">
	<div id="col" class="column is-vcentered">
		<h1 id="subtitle" class="is-robot">Playlist is ready</h1>
		<#if playOption>
			<img id="album" src="${imageUrl}">
			<p class="small-text">Playlist chosen: </p><strong id="host-badge" class="small-margin is-robot">${playlist}</strong>
		</#if>
    <#if isHost>
			<p class="section-header" >Choose a game difficulty:</p>
			<form action="/begin-round" method='POST'>
				<input type="text" id="selected" class="is-robot" name="selected" value="easy" readonly>
				<div id="row" class="columns is-vcentered">
					<div class="column is-vcentered">
						<input type="button" id="easy" name="easy" class="button is-primary is-rounded active" value="easy">
					</div>
					<div class="column is-vcentered">
						<input type="button" id="medium" name="medium" class="button is-primary is-rounded" value="medium">
					</div>
					<div class="column is-vcentered">
						<input type="button" id="hard" name="hard" class="button is-primary is-rounded" value="hard">
					</div>
				</div>
				<input type="submit" id="submit" class="button is-success is-rounded" value="Begin game!"><br>
			</form>
    <#else>
  		<p class="small-text">About to begin the game. Host is choosing the settings.</p>
    </#if>
	</div>
</div>

<script>
	let difficulty = "easy";
	document.getElementById(difficulty).style.backgroundColor = "#2E8B57";

	function changeOption(event) {
		console.log("herre");
		console.log(event.target.value);
		document.getElementById(difficulty).classList.remove("active");
		document.getElementById(difficulty).style.backgroundColor = "#00D1B2";

		switch (event.target.value) {
			case "hard":
				difficulty = "hard";
				break;
			case "medium":
				difficulty = "medium";
				break;
			default:
				difficulty = "easy";
				break;
		}
		document.getElementById(difficulty).classList.add("active");
		document.getElementById("selected").value = difficulty;
		document.getElementById(difficulty).style.backgroundColor = "#2E8B57";
	}

	document.getElementById("easy").addEventListener("click", changeOption);
	document.getElementById("medium").addEventListener("click", changeOption);
	document.getElementById("hard").addEventListener("click", changeOption);

</script>
</#assign>
<#include "main.ftl">