@echo off
echo Сборка Drawing Kursovaya...
echo.
gradlew clean jar
if errorlevel 1 (
    echo  Ошибка сборки!
    pause
    exit /b 1
)
echo  Сборка завершена!
echo JAR файл: build\libs\drawingkursovaya-1.0-SNAPSHOT.jar
pause