@echo off
chcp 65001 > nul
echo =============================================
echo   УСТАНОВКА БАЗЫ ДАННЫХ BOOKSTORE (PostgreSQL)
echo =============================================
echo.

set DB_HOST=localhost
set DB_PORT=5432
set DB_USER=postgres
set DB_PASSWORD=postgres
set DB_NAME=bookstore

echo Введите хост [%DB_HOST%]:
set /p input_host=
if not "%input_host%"=="" set DB_HOST=%input_host%

echo Введите порт [%DB_PORT%]:
set /p input_port=
if not "%input_port%"=="" set DB_PORT=%input_port%

echo Введите имя пользователя [%DB_USER%]:
set /p input_user=
if not "%input_user%"=="" set DB_USER=%input_user%

echo Введите пароль пользователя %DB_USER%:
set /p DB_PASSWORD=

echo.
echo [1/3] Создание базы данных и таблиц...
set PGPASSWORD=%DB_PASSWORD%
psql -h %DB_HOST% -p %DB_PORT% -U %DB_USER% -f ddl\create_tables.sql
if errorlevel 1 (
    echo ОШИБКА при создании таблиц!
    pause
    exit /b 1
)
echo Готово!

echo.
echo [2/3] Заполнение тестовыми данными...
psql -h %DB_HOST% -p %DB_PORT% -U %DB_USER% -d %DB_NAME% -f dml\insert_test_data.sql
if errorlevel 1 (
    echo ОШИБКА при заполнении данных!
    pause
    exit /b 1
)
echo Готово!

echo.
echo [3/3] Проверка данных...
psql -h %DB_HOST% -p %DB_PORT% -U %DB_USER% -d %DB_NAME% -c "SELECT COUNT(*) AS books_count FROM books; SELECT COUNT(*) AS orders_count FROM orders; SELECT COUNT(*) AS requests_count FROM requests;"

echo.
echo =============================================
echo   БАЗА ДАННЫХ УСПЕШНО УСТАНОВЛЕНА!
echo =============================================
pause