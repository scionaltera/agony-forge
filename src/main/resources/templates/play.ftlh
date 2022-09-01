<#import "/spring.ftl" as spring>
<#include "inc/security.inc.ftlh">
<!DOCTYPE html>
<html lang="en">
<#assign title="Play">
<#assign path="/play">
<#assign styles = [ "/css/color.css", "/css/play.css" ]>
<#include "inc/header.inc.ftlh">
<body>
<noscript>
    <span style="color: #ff0000">It seems your browser doesn't support Javascript! Websocket relies on
        Javascript being enabled. Please enable Javascript and reload this page!
    </span>
</noscript>
<@anonymous "ROLE_USER">
<div class="container-fluid">
    <a class="btn btn-primary" role="button" href="<@spring.url '/oauth2/authorization/cognito' />">Login</a>
</div>
</@anonymous>
<@secure "ROLE_USER">
<div>
    <div id="output-box">
        <ul id="output-list">
        </ul>
    </div>
    <div id="input-box">
        <form id="user-input-form">
            <input type="text" id="user-input" autocomplete="off" autofocus/>
        </form>
    </div>
</div>
</@secure>

<#include "inc/scripts.inc.ftlh">
<@secure "ROLE_USER">
<script type="text/javascript" src="https://cdn.jsdelivr.net/npm/sockjs-client@1.6.1/dist/sockjs.min.js"></script>
<script type="text/javascript" src="https://cdn.jsdelivr.net/npm/webstomp-client@1.2.6/dist/webstomp.min.js"></script>
<script type="text/javascript" src="<@spring.url '/js/client.js'/>"></script>
</@secure>
</body>
</html>
