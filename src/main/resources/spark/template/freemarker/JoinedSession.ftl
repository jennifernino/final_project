<#assign content>
	<h1>Joined Session</h1>
	<div id="members">
		<ul>
		<#list members>
			<#items as member>
				${member}
			</#items>
		</#list>
	</div>
	<p>Waiting for host to start the game ... </p>
</#assign>
<#include "main.ftl">