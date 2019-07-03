<#import "/spring.ftl" as spring>
<!DOCTYPE html>
<html lang="en">
<#assign styles = [ "/css/index.css" ]>
<#include "inc/header.inc.ftl">
<body>
<div class="container-fluid">
    <#assign nobuffer = true>
    <#include "inc/title.inc.ftl">

    <div class="row">
        <div class="col-10 offset-1">
            <p>This is the demo MUD for The Agony Forge.</p>
            <p>The Agony Forge is a free, <a href="https://github.com/scionaltera/agony-forge-demo">open source</a> web based MUD platform. Feel free to log in and look around. If you've ever thought about starting up your own MUD, this might just be a good way to get started!</p>
        </div>
    </div>

    <#include "inc/links.inc.ftl">
</div>

<#include "inc/scripts.inc.ftl">
</body>
</html>
