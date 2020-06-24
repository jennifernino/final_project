<#assign content>
<div id="home-container" class="columns is-vcentered">
	<div id="col" class="column is-vcentered">
	<h1 id="subtitle" class="is-robot column"> Guess the Song </h1>
	<div class="instructions">
		<h3 class="section-header">Instructions</h3>
		<p class="small-text">Welcome to Guess The Song!</p>
		<ul>
			<li class="small-text">All players need to have a Spotify account to play this game. Any account type is fine, free or premium. We will be using Spotify data to give you customized songs. </li>
			<li class="small-text">At the start of the game, the host chooses the number of songs per player and level of difficulty of the game. We will generate a customized playlist based on your favorite songs and artists.</li>
			<li class="small-text">During their turn, a player has thirty seconds to guess the name of the song playing. You get only one guess, so make it count! No worries, small typos are okay! You also do not need to worry about items that would generally be included in parentheses in the title.</li>
			<li class="small-text">A player gets a point if they guess the right song and no points if they get it wrong. Waiting for the timer to time out won't work either; if a player runs out of time, they won't get a point. The person with the most points by the end wins the game.</li>
			<li class="small-text">If a player exits the game (clicks on the "Home" icon) while the game is running, the game will prompt the user to confirm exit and the game will be closed; everyone in that game session will be redirected to the home page.</li>
			<li class="small-text">Max number of players per session is currently capped at 6.</li>
		</ul>
		<p class="small-text">Have fun, and build those sonic skillzzz!</p>
	</div>
	<div class="home-button">
		<a id="resize-button" class="game-button button is-success is-medium is-rounded" href="/guess-the-song/new-session">New Session</a>
		<a id="resize-button" class="game-button button is-success is-medium is-rounded" href="/guess-the-song/join-session">Join Session</a>
	</div>
</div>
</#assign>
<#include "main.ftl">