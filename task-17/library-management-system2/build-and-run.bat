@echo off
echo ==========================================
echo Библиотечная система - Docker сборка
echo ==========================================

docker-compose down
docker-compose up -d --build

echo.
echo Приложение запущено!
echo http://localhost:8080/library/
echo.
echo Логи: docker-compose logs -f app
echo Остановить: docker-compose down
pause