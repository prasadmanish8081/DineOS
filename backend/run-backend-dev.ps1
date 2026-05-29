$env:SPRING_DATASOURCE_URL = 'jdbc:h2:file:./data/dineos;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH'
$env:SPRING_DATASOURCE_USERNAME = 'sa'
$env:SPRING_DATASOURCE_PASSWORD = ''
$env:SPRING_DATASOURCE_DRIVER_CLASS_NAME = 'org.h2.Driver'
$env:MENU_BASE_URL = 'http://localhost:3000'

Set-Location $PSScriptRoot
& .\mvnw.cmd -DskipTests spring-boot:run
