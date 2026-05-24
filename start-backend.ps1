$env:SPRING_DATASOURCE_URL = 'jdbc:h2:file:./data/dineos;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH'
$env:SPRING_DATASOURCE_USERNAME = 'sa'
$env:SPRING_DATASOURCE_PASSWORD = ''
$env:SPRING_DATASOURCE_DRIVER_CLASS_NAME = 'org.h2.Driver'
$env:MENU_BASE_URL = 'http://localhost:3000'

$defaultPort = 8080
$altPort = 8081
$portBusy = Test-NetConnection -ComputerName '127.0.0.1' -Port $defaultPort -WarningAction SilentlyContinue | Select-Object -ExpandProperty TcpTestSucceeded

if ($portBusy) {
    Write-Host "Port $defaultPort is already in use. Starting backend on port $altPort instead."
    $env:SERVER_PORT = $altPort
} else {
    $env:SERVER_PORT = $defaultPort
}

& .\mvnw.cmd -DskipTests spring-boot:run
