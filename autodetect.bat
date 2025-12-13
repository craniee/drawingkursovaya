@echo off
echo Поиск JavaFX на компьютере...
echo.

:: Используем Java 21
set JAVA_HOME=C:\Program Files\Java\jdk-21
set PATH=%JAVA_HOME%\bin;%PATH%

:: Ищем JavaFX в разных местах
set FX_PATH=

:: 1. Ваш текущий путь
if exist "C:\Users\javafx-sdk-25.0.1\lib\javafx.base.jar" (
    set FX_PATH=C:\Users\%USERNAME%\javafx-sdk-25.0.1\lib
    echo ✅ Найден JavaFX 25 в вашей папке
)

:: 2. Другие возможные пути
if "%FX_PATH%"=="" if exist "C:\javafx-sdk-21\lib\javafx.base.jar" (
    set FX_PATH=C:\javafx-sdk-21\lib
    echo ✅ Найден JavaFX 21
)

if "%FX_PATH%"=="" if exist "%USERPROFILE%\javafx-sdk\lib\javafx.base.jar" (
    set FX_PATH=%USERPROFILE%\javafx-sdk\lib
    echo ✅ Найден JavaFX
)

:: 3. Если не нашли
if "%FX_PATH%"=="" (
    echo ❌ JavaFX не найден!
    echo.
    echo У вас установлен: C:\Users\javafx-sdk-25.0.1
    echo Но я его не вижу. Проверьте:
    echo 1. Существует ли папка: C:\Users\javafx-sdk-25.0.1\lib
    echo 2. Есть ли в ней файлы: javafx.base.jar, javafx.controls.jar
    echo.
    pause
    exit /b 1
)

echo Путь к JavaFX: %FX_PATH%
echo.

:: Проверяем JAR
if not exist "build\libs\drawingkursovaya-1.0-SNAPSHOT.jar" (
    echo Собираю проект...
    gradlew clean jar
)

:: Запуск
echo Запускаю приложение...
java --module-path "%FX_PATH%" ^
     --add-modules javafx.controls,javafx.fxml ^
     -jar "build\libs\drawingkursovaya-1.0-SNAPSHOT.jar"

pause