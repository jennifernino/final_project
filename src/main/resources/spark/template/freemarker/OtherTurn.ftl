<#assign content>
<div id="home-container" class="columns">
  <!--left col -->
	<div id="col" class="column">
		<p id="subtitle" class="is-robot"><strong class="is-green">${currPlayer}</strong> is taking their turn... </p>
	  <div class="right-instr">
	    <i id="large-icon" class="fas fa-cog fa-spin fa-5x"></i>
	  </div>
  </div>

  <!--right col -->
  <div id="col" class="column is-vcentered">
    <div id="stack">
        <img id="album" src="${imageUrl}">
        <audio id="audio" controls>
          <source src="${audioLink}" />
        </audio>
    </div>
  </div>
</div>
</#assign>
<script>
	// document.getElementById("audio").autoplay = true;
	document.getElementById("audio").load();
</script>
<#include "main.ftl">