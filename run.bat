@echo off
echo Запуск Drawing App...
echo.

REM Проверяем наличие gradlew.bat
if not exist "gradlew.bat" (
    echo Ошибка: gradlew.bat не найден. Запускайте из корня проекта.
    pause
    exit /b 1
)

REM Запускаем через Gradle — он сам соберёт и подключит JavaFX
call gradlew.bat run

pause