@echo off
chcp 65001 >nul
cd /d "%~dp0"
echo === Настройка программы (продюсер или консьюмер) ===
echo.
set /p ROLE=Роль (producer или consumer): 
set /p NODE_NAME=Имя узла (producer, consumer1, consumer2, consumer3): 
set /p BROKER1_IP=IP брокера 1: 
set /p BROKER2_IP=IP брокера 2: 
set /p BROKER3_IP=IP брокера 3: 

> .env echo ROLE=%ROLE%
>>.env echo NODE_NAME=%NODE_NAME%
>>.env echo BROKER1_IP=%BROKER1_IP%
>>.env echo BROKER2_IP=%BROKER2_IP%
>>.env echo BROKER3_IP=%BROKER3_IP%
>>.env echo KAFKA_GROUP=

echo.
echo Записан файл %~dp0.env:
type .env
echo.
echo Запуск: docker compose up -d --build
pause
