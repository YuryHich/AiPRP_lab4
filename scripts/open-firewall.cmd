@echo off
chcp 65001 >nul
rem Открывает входящие порты 8079 (Tomcat), 9092 и 9093 (Kafka) и делает текущие сети "Частными".
rem Запускать ПРАВОЙ кнопкой -> "Запуск от имени администратора".

net session >nul 2>&1
if errorlevel 1 (
    echo Нужно запустить от имени администратора: правая кнопка по файлу -^> Запуск от имени администратора
    pause
    exit /b 1
)

netsh advfirewall firewall delete rule name="AiPRP lab4 Tomcat 8079" >nul 2>&1
netsh advfirewall firewall delete rule name="AiPRP lab4 Kafka 9092-9093" >nul 2>&1
netsh advfirewall firewall add rule name="AiPRP lab4 Tomcat 8079" dir=in action=allow protocol=TCP localport=8079 profile=any
netsh advfirewall firewall add rule name="AiPRP lab4 Kafka 9092-9093" dir=in action=allow protocol=TCP localport=9092-9093 profile=any

echo.
echo Сети в профиле "Общедоступная" переводятся в "Частная":
powershell -NoProfile -Command "Get-NetConnectionProfile | Where-Object NetworkCategory -eq 'Public' | ForEach-Object { Write-Host ' ' $_.InterfaceAlias $_.Name; Set-NetConnectionProfile -InterfaceIndex $_.InterfaceIndex -NetworkCategory Private }"
powershell -NoProfile -Command "Get-NetConnectionProfile | Format-Table InterfaceAlias, Name, NetworkCategory -AutoSize"

echo Готово. Удалить правила потом: netsh advfirewall firewall delete rule name="AiPRP lab4 Tomcat 8079"
echo                                 netsh advfirewall firewall delete rule name="AiPRP lab4 Kafka 9092-9093"
pause
