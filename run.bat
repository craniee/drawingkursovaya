@echo off
echo ========================================
echo   ЗАПУСК DRAWING KURSOVAYA
echo ========================================
echo.

:: Используем Java 21
set JAVA_HOME=C:\Program Files\Java\jdk-21
set PATH=%JAVA_HOME%\bin;%PATH%

echo Используется Java:
java --version
echo.

:: Путь к вашему JavaFX 25
set FX_PATH=C:\Users\javafx-sdk-25.0.1\lib

:: Проверяем JavaFX
if not exist "%FX_PATH%\javafx.base.jar" (
    echo  JavaFX не найден по пути: %FX_PATH%
    echo.
    echo Проверьте путь к JavaFX:
    echo У вас: C:\Users\javafx-sdk-25.0.1
    echo.
    dir "%FX_PATH%\*.jar" 2>nul || echo Папка не найдена
    pause
    exit /b 1
)

echo  JavaFX найден: %FX_PATH%
echo.

:: Сборка проекта (если нужно)
if not exist "build\libs\drawingkursovaya-1.0-SNAPSHOT.jar" (
    echo Сборка проекта...
    gradlew clean jar
)

echo Запуск приложения...
echo.

:: Запускаем с JavaFX 25
java --module-path "%FX_PATH%" ^
     --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.base,javafx.swing ^
     -jar "build\libs\drawingkursovaya-1.0-SNAPSHOT.jar"

echo.
echo ========================================
pause