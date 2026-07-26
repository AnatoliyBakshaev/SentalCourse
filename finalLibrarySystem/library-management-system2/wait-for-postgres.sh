#!/bin/sh
# Скрипт ожидает запуска PostgreSQL перед стартом приложения

set -e

host="$1"
shift
port="$1"
shift
cmd="$@"

echo "Ожидание PostgreSQL $host:$port..."

# Проверяем наличие netcat
if ! command -v nc >/dev/null 2>&1; then
    echo "nc (netcat) не найден, устанавливаем..."
    apk add --no-cache netcat-openbsd
fi

until nc -z "$host" "$port"; do
  sleep 1
done

echo "PostgreSQL готов! Запускаем приложение..."
exec $cmd