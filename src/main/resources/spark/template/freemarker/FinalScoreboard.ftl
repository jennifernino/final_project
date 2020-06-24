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
    <p>The last turn was taken. Are you ready to see the final results?</p>
    <form id='start-form' action='/guess-the-song/final-score' method='GET'>
      <input id='start-submit' type="submit" class="button is-success" value="Final score!">
    </form>
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

  <!-- scores here -->

  <script>
    document.getElementById("album").onerror = function failedImage() {
      document.getElementById("album").src = "images/no_photo.jpg";
    }
  </script>
</#assign>
<#include "main.ftl">