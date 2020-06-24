<#assign content>
<div id="home-container" class="columns is-vcentered">
	<div id="col" class="column is-vcentered is-multiline">
		<h1 id="subtitle" class="is-robot column">Joined Session: ${code}</h1>
	  <h2>Host is <strong id="host-badge" class="is-robot">${host}</strong></h2>

		<p>Share code with friends to play “Guess the Song” together</p>

		<#if userCount gte 6>
			<p class="is-red is-robot">Max number of users per game reached.</p>
		</#if>

		<#if isHost>
		<form id='new-session-form' action='/guess-the-song/start-game' method='POST'>
			<div class="form-group side-by-side">
				<!-- <label for="game-code">Code: ${code}</label><br> -->
				<p id="copy-clip" class="control has-icons-left">
					<input class="input" type="text" id="game-code" name="game-code" value="${code}" readonly>
					<span class="icon is-small is-left">
						<i class="fas fa-clipboard"></i>
					</span>
				</p>
				<button type="button" class="button is-primary" id="copyCode" onclick="copy()"><i id="copy-icon" class="fas fa-copy"></i>Copy</button>
			</div>

			<input id='start-new-session' type="submit" class="button is-primary" value="Start Game">
		</form>
	  <#else>
		<div class="form-group side-by-side">
			<p id="copy-clip" class="control has-icons-left">
				<input class="input" type="text" id="game-code" name="game-code" value="${code}" readonly>
				<span class="icon is-small is-left">
					<i class="fas fa-clipboard"></i>
				</span>
			</p>
			<button type="button" class="button is-primary" id="copyCode" onclick="copy()"><i id="copy-icon" class="fas fa-copy"></i>Copy</button><br>
		</div>
		<p>Waiting for host to start the game <i class="fas fa-spinner fa-pulse"></i> </p>
	  </#if>
		<h2 class="is-robot">Users in the game:
		</h2>
		<div id="tag-box" class="tag-box tags">
			<#list members>
				<#items as member>
				 	<span class="person-tag tag is-info is-large"><i class="fas fa-user fa-1x member-icon"></i>${member}</span>
				</#items>
			</#list>

		</div>
	</div>
</div>




	<script>

		function copy() {
			var code = document.getElementById("game-code");
			var button = document.getElementById("copyCode");
			button.style.backgroundColor = "#9acd32";
			code.select();
			document.execCommand("copy");
			console.log("Copied the text: " + code.value);
			setTimeout(function(){
				button.style.backgroundColor = "#48C774";
				button.style.border = "none";
				code.selectionStart= 0;
				code.selectionEnd =0;
				code.blur();
			}, 400);
		}
	</script>
</#assign>
<#include "main.ftl">
