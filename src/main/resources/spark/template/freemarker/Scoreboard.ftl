<#assign content>
<div id="home-container" class="columns">
  <!--left col -->
	<div id="col" class="column">
    <p id="result-header-${result}" class="is-robot">${currentPlayer} was <strong class=${result}>${result}</strong></p>
    <#if result=="incorrect">
    <p class="small-text">Their guess was <strong class=${result}>${guess}</strong></p>
    </#if>
    <p id="answer-header" class="is-robot">Answer</p>
    <div class="answer">
      <p class=""><strong class=" score-text is-green is-robot">Song name:</strong>${songName}</p>
      <p class=""><strong class="score-text is-green is-robot">Artist(s):</strong>${artist}</p>
    </div>
    <#if isNextTurn>
      <p>You're up next. You ready? </p>
      <form id='start-form' action='/get-next-round' method='POST'>
        <input id='start-submit' type="submit" class="button is-success" value="Let's go!">
      </form>
    </#if>
  </div>

  <!--right col -->
  <div id="col" class="column is-vcentered">
    <div id="stack">
        <img id="album" src="${imageUrl}">
        <audio controls>
          <source src="${audioLink}" />
        </audio>
    </div>
  </div>
</div>


  <div id="split">
    <div class="left">

    </div>
    <div class="right">

    </div>

  </div>


  <!-- scores here -->
  <script>
		setTimeout(() => {
			try {
				document.forms[0].submit();
			} catch(err) {
				console.log("Ruh roh");
			}
		}, 10000);
  </script>
</#assign>
<#include "main.ftl">