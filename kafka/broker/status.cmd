@echo off
chcp 65001 >nul
rem Состояние кластера: контроллеры, брокеры, топик, группы консьюмеров.
set CONTAINER=kafka-broker
if not "%~1"=="" set CONTAINER=%~1
set BIN=/opt/kafka/bin

echo === Кворум контроллеров (лидер и голосующие) ===
docker exec %CONTAINER% %BIN%/kafka-metadata-quorum.sh --bootstrap-server localhost:9092 describe --status
echo.
echo === Реплики кворума (должно быть 3 узла) ===
docker exec %CONTAINER% %BIN%/kafka-metadata-quorum.sh --bootstrap-server localhost:9092 describe --replication
echo.
echo === Топик lab4-messages (лидеры партиций и реплики) ===
docker exec %CONTAINER% %BIN%/kafka-topics.sh --bootstrap-server localhost:9092 --describe --topic lab4-messages
echo.
echo === Группы консьюмеров ===
docker exec %CONTAINER% %BIN%/kafka-consumer-groups.sh --bootstrap-server localhost:9092 --describe --all-groups
pause
