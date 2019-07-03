<head>
    <meta charset="utf-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <#if title??>
    <title>${title} - The Agony Forge</title>
    <#else>
    <title>The Agony Forge</title>
    </#if>
    <meta name="description" content="The Agony Forge is a web based MUD."/>
    <meta name="viewport" content="width=device-width"/>
    <link rel="canonical" href="https://agonyforge.com${path!}"/>
    <link rel="stylesheet" type="text/css"
          href="<@spring.url 'https://fonts.googleapis.com/css?family=Inconsolata'/>">
    <link rel="stylesheet" type="text/css" href="<@spring.url '/webjars/bootstrap/css/bootstrap.min.css'/>"/>
    <link rel="stylesheet" type="text/css" href="<@spring.url '/webjars/font-awesome/css/all.min-jsf.css'/>"/>
    <#if styles??>
    <#list styles as style>
    <link rel="stylesheet" type="text/css" href="<@spring.url '${style}'/>"/>
    </#list>
    </#if>
</head>
