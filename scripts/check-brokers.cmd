@echo off
chcp 65001 >nul
rem Проверка, что порты брокеров доступны по сети с этого компьютера.
rem Использование: check-brokers.cmd 192.168.43.11 192.168.43.12 192.168.43.13
if "%~1"=="" (
    echo Использование: check-brokers.cmd IP_БРОКЕРА_1 IP_БРОКЕРА_2 IP_БРОКЕРА_3
    pause
    exit /b 1
)
for %%I in (%*) do (
    for %%P in (9092 9093) do (
        powershell -NoProfile -Command "$ok = (New-Object Net.Sockets.TcpClient).ConnectAsync('%%I', %%P).Wait(2000); if ($ok) { Write-Host '  OK   %%I:%%P' -ForegroundColor Green } else { Write-Host '  НЕТ  %%I:%%P' -ForegroundColor Red }"
    )
)
echo.
echo НЕТ = брокер не запущен, неверный IP, закрыт брандмауэр или включена изоляция клиентов в точке доступа.
pause
