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
            <p>${errorAttributes.status}: ${errorAttributes.error}</p>
        </div>
    </div>

    <#include "inc/links.inc.ftl">
</div>

<#include "inc/scripts.inc.ftl">
</body>
</html>
