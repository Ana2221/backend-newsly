#!/bin/bash

URL="https://backend-newsly.onrender.com"

echo "[$(date)] Enviando requisição para $URL"

curl -f -s -S "$URL" || echo "Erro ao acessar o backend"

echo "[$(date)] Requisição finalizada"
