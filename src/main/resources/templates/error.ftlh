<#import "/spring.ftl" as spring>
<!DOCTYPE html>
<html lang="en">
<#assign styles = [ "/css/index.css" ]>
<#include "inc/header.inc.ftlh">
<body>
<div class="container-fluid">
    <#assign nobuffer = true>
    <#include "inc/title.inc.ftlh">

    <div class="row">
        <div class="col-10 offset-1">
            <p>${errorAttributes.status}: ${errorAttributes.error}</p>
        </div>
    </div>

    <#include "inc/links.inc.ftlh">
</div>

<#include "inc/scripts.inc.ftlh">
</body>
</html>
