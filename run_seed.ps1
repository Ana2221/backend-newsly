$mysql = "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe"
$seed  = (Get-Item "src\main\resources\seed.sql").FullName
$out   = "src\main\resources\seed_result.txt"
$database = if ($env:NEWSLY_DB_NAME) { $env:NEWSLY_DB_NAME } else { "newsly" }

Write-Output "Executando seed em $database..."

# Usa stdin redirection (mysql < arquivo) para evitar problemas com
# caminhos com espaços no --execute="source ..."
$result = Get-Content $seed -Raw | & $mysql -u root "-proot" $database --default-character-set=utf8mb4 2>&1

$exitCode = $LASTEXITCODE

# Filtra o warning de senha — não é erro
$errors = $result | Where-Object { $_ -match "ERROR" }

$result | Out-File $out -Encoding utf8

if ($exitCode -eq 0 -and -not $errors) {
    Write-Output "Seed executada com sucesso."
    "EXIT:0" | Out-File $out -Append -Encoding utf8
} else {
    Write-Output "Saída da seed:"
    $result | ForEach-Object { Write-Output $_ }
    "EXIT:$exitCode" | Out-File $out -Append -Encoding utf8
}
