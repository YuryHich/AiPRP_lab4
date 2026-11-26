ЛАБОРАТОРНАЯ РАБОТА № 4
Веб-сервис TimeService и веб-сокет
==================================

Ветка kafka-dop: доп с Apache Kafka (1 продюсер, 3 брокера, 3 консьюмера).
Полный гайд: KAFKA_DOP_ГАЙД.md

Tomcat 9 лежит на диске D, порт 8079:
  D:\devTools\apache-tomcat-9.0.124

Проект:
  lab4\TimeService

ЧТО УЖЕ ПРОВЕРЕНО
-----------------
http://localhost:8079/TimeService/api/time
  {"time":"HH:mm:ss"}

http://localhost:8079/TimeService/api/translate?txt=кот
  {"word":"кот","translation":"cat"}

http://localhost:8079/TimeService/api/translate?txt=dog
  перевод «собака»

http://localhost:8079/TimeService/websocket/chat.xhtml
  time — текущее время
  кот / dog — перевод
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
