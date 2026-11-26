@echo off
chcp 65001 >nul
echo === IPv4-адреса этого компьютера ===
echo Нужен адрес адаптера Wi-Fi (Беспроводная сеть), обычно вида 192.168.x.x или 172.20.10.x
echo.
powershell -NoProfile -Command "Get-NetIPAddress -AddressFamily IPv4 | Where-Object { $_.IPAddress -notlike '127.*' -and $_.IPAddress -notlike '169.254.*' } | Sort-Object InterfaceAlias | Format-Table InterfaceAlias, IPAddress -AutoSize"
echo Адаптеры vEthernet (WSL, Docker) и VPN - НЕ те, их адреса не подходят.
pause
