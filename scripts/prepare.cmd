@echo off
chcp 65001 >nul
rem Скачивает образ Kafka и собирает образ программы ЗАРАНЕЕ, пока есть быстрый интернет.
rem На занятии через раздачу с телефона качать ~1 ГБ будет долго.
cd /d "%~dp0.."
echo === docker pull apache/kafka:3.9.0 ===
docker pull apache/kafka:3.9.0 || goto :fail
echo.
echo === docker build программы (aiprp-lab4-timeservice) ===
docker build -t aiprp-lab4-timeservice TimeService || goto :fail
echo.
echo Всё скачано. Список образов:
docker images apache/kafka
docker images aiprp-lab4-timeservice
pause
exit /b 0
:fail
echo ОШИБКА: проверьте, что Docker Desktop запущен и есть интернет.
pause
exit /b 1
