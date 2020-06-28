<#assign content>
<div id="home-container" class="columns is-vcentered">
	<div id="col" class="column is-vcentered">
		<h1 id="subtitle" class="bottom-margin is-robot">Welcome to guess the song!</h1>
		<#if playOption>
			<img id="album" src="${imageUrl}">
			<p class="small-text">Playlist chosen: </p><strong id="host-badge" class="small-margin is-robot">${playlist}</strong>
		</#if>
	  <p class="small-text">The host has decided to start the game as: </p><strong id="host-badge" class="small-margin is-robot">${level}</strong>
	  <p class="small-text">Up first, we have <strong class="is-green">${firstPlayer}</strong>, are you ready? </p>

	  <#if isNextPlayer>
	  	<form id='start-form' action='/get-next-round' method='POST'>
	  	  <input id='submit' type="submit" class="button is-success is-rounded active" value="Let's go!">
	  	</form>
	  </#if>
	</div>
</div>
</#assign>
<#include "main.ftl">