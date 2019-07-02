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
            <p>If you're reading this, you're looking at the default website for The Agony Forge Core.</p>
            <p>The Agony Forge Core is a project containing the code for the Agony Forge. You don't actually need to change anything in this project to make your own MUD. If you are a Java developer you can find all the code for the game in this project. If you would like to make significant changes to the way your game operates, or if you want to add new features or fix bugs this is the place to do it.</p>
        </div>
    </div>

    <#include "inc/links.inc.ftl">
</div>

<#include "inc/scripts.inc.ftl">
</body>
</html>
