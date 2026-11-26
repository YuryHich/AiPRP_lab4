@echo off
chcp 65001 >nul
rem Создаёт топик lab4-messages: 3 партиции, по 3 копии (реплики) каждой.
rem Запускать на ОДНОМ любом брокере, когда все три брокера уже запущены.
set CONTAINER=kafka-broker
if not "%~1"=="" set CONTAINER=%~1

docker exec %CONTAINER% /opt/kafka/bin/kafka-topics.sh --bootstrap-server localhost:9092 --create --if-not-exists --topic lab4-messages --partitions 3 --replication-factor 3
echo.
docker exec %CONTAINER% /opt/kafka/bin/kafka-topics.sh --bootstrap-server localhost:9092 --describe --topic lab4-messages
pause
