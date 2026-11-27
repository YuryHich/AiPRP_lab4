ЛАБОРАТОРНАЯ РАБОТА № 4
Веб-сервис TimeService и веб-сокет, вариант 1 (калькулятор)
===========================================================

Tomcat 9 лежит на диске D, порт 8079:
  D:\devTools\apache-tomcat-9.0.124

Проект:
  lab4\TimeService

ЧТО УЖЕ ПРОВЕРЕНО
-----------------
http://localhost:8079/TimeService/api/time
  {"time":"HH:mm:ss"}

http://localhost:8079/TimeService/calc.html
  поле ввода: любое бесскобочное выражение, не только 2+4*7

http://localhost:8079/TimeService/api/calc?expr=2+4*7
  тот же сервис напрямую, пример из задания
  {"expression": "2+4*7", "result": 30}

http://localhost:8079/TimeService/api/calc?expr=1/0
  {"expression": "1/0", "error": "деление на ноль"}

http://localhost:8079/TimeService/websocket/chat.xhtml
  time — текущее время
  2+4*7 — результат вычисления
  любой другой текст — реплика в чат

ЕСЛИ TOMCAT НЕ ЗАПУЩЕН
----------------------
1. Скопировать lab4\TimeService\target\TimeService.war
   в D:\devTools\apache-tomcat-9.0.124\webapps
2. Из папки bin запустить startup.bat
   JAVA_HOME уже прописан в bin\setenv.bat
3. Остановить: shutdown.bat

NETBEANS
--------
File -> Open Project -> lab4\TimeService
Сервер: Tools -> Servers -> Add Server
  Apache Tomcat
  папка D:\devTools\apache-tomcat-9.0.124
Это Tomcat 9 (javax), не Tomcat 10.

Собрать заново:
  D:\devTools\netbeans\java\maven\bin\mvn.cmd -f lab4\TimeService\pom.xml package

ВЕТКИ РЕПОЗИТОРИЯ
-----------------
main      — вариант 1 (калькулятор)
variant2  — вариант 2 (перевод)
