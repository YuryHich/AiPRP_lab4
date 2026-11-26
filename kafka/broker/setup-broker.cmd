@echo off
chcp 65001 >nul
cd /d "%~dp0"
echo === Настройка брокера Kafka ===
echo.
set /p NODE_ID=Номер брокера (1, 2 или 3): 
set /p MY_IP=IP этого компьютера: 
set /p BROKER1_IP=IP брокера 1: 
set /p BROKER2_IP=IP брокера 2: 
set /p BROKER3_IP=IP брокера 3: 

> .env echo NODE_ID=%NODE_ID%
>>.env echo MY_IP=%MY_IP%
>>.env echo BROKER1_IP=%BROKER1_IP%
>>.env echo BROKER2_IP=%BROKER2_IP%
>>.env echo BROKER3_IP=%BROKER3_IP%

echo.
echo Записан файл %~dp0.env:
type .env
echo.
echo Запуск: docker compose up -d
pause
