# analytics_service  
This is backend part of the analytics service.  
You can see the frontend part here: https://github.com/gmvrpw/analytic-ui  

Всё писалось самостоятельно без использованния предложенного шаблона, поэтому чтобы совсем не заблудиться вот немного информации.  
Работали:  
&emsp;Боярников Евгений  
&emsp;Боярников Александр
  
Основные технологии:  
&emsp;Spring - REST API  
&emsp;Velocity - для работы с templates  
&emsp;PostgreSQL - БД для данных   
&emsp;MinIO - БД для скриптов  
&emsp;(Kafka + ClickHouse не реализовано, данные храняться в PostgreSQL D: )  
  
Структура взаимодействия сущностей:    
Пользователь  
&emsp;&emsp;|_ Юнит1  
&emsp;&emsp;|&emsp;&emsp;|_Контенер1  
&emsp;&emsp;|&emsp;&emsp;|&emsp;&emsp;|_Триггер1 (web-блок и event)  
&emsp;&emsp;|&emsp;&emsp;|&emsp;&emsp;|_Триггер2  
&emsp;&emsp;|&emsp;&emsp;|&emsp;&emsp;|_Триггер3  
&emsp;&emsp;|&emsp;&emsp;|_Контенер2  
&emsp;&emsp;|&emsp;&emsp;&emsp;&emsp;&nbsp;|_Триггер1  
&emsp;&emsp;|&emsp;&emsp;&emsp;&emsp;&nbsp;|_Триггер2  
&emsp;&emsp;|_Юнит1  
&emsp;&emsp;&emsp;&emsp;&nbsp;|_Контенер1  
&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&nbsp;&nbsp;|_Триггер1  
  
Что оно умеет: (Postman "документация" с примерами по api: https://documenter.getpostman.com/view/24365536/2s8YzZPKFp)  
&emsp;Добавлять/изменять/удалять/возвращать юниты/контейнеры с тригерами (PostgreSQL)  
&emsp;Фомировать/сохранять/изменять/отправлять js-файлы с скриптами (MinIO, Velocity)  
&emsp;Принимать/сохранять данные полученные от триггеров (PostgreSQL)  
&emsp;Формровать отчёты (в процессе разработки) (PostgreSQL)  
&emsp;Обогощать данные через внешний сервер (в процессе разработки)
