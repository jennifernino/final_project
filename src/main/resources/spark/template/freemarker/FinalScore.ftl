<#assign content>

<div id="home-container" class="columns">
  <!--left col -->
	<div id="col" class="column">
      <div id="stack-left" class="left-final-score">
    <h1 id="left-title" class="is-robot">Good game!</h1>
		<p class="is-robot">WINNER(S)</p>

		<#list winners>
			<#items as winner>
  			<div class="winner">
          <span class="icon">
            <i class="fa fa-fw fa-trophy"></i>
          </span>
  				<p class="final-text is-robot"> ${winner[0]}</p>
  				<p class="final-text-point">${winner[1]}</p>
  			</div>
			</#items>
		</#list>
    		<form id='start-form' action='/' method='GET'>
    			<input id='start-submit' type="submit" class="bottom-button button is-success" value="Go Home!">
    		</form>
    </div>
  </div>

  <!--right col -->
  <div class="column is-vcentered">
      <div id="stack-scores">
      <#list others>
        <#items as other>
          <div class="user-score">
            <span class="icon">
              <i class="fa fa-fw fa-star"></i>
            </span>
            <p class="not-winner is-robot">${other[0]}</p>
            <p class="not-winner-text">${other[1]}</p>
          </div>
        </#items>
      </#list>

      </div>
  </div>
</div>




	<div class="right">

	</div>
</div>

</#assign>
<#include "main.ftl">