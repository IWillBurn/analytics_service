# Аналитика  

Участники:  
&emsp;Бэкенд: Боярников Евгений Алексеевич  
&emsp;Фронтенд: Боярников Александр Алексеевич  

Это бэкенд часть приложения. 
Фронтенд часть приложения: https://github.com/gmvrpw/analytic-ui  
 
Запуск приложения:  
I. PostgreSQL:
1. Запустить:    
docker run —name analytics -p 5432:5432 -e POSTGRES_USER=admin -e POSTGRES_PASSWORD=password -e POSTGRES_DB=analytics_db -d postgres:13.3  
(Доступ по localhost:5432)  
2. Создать таблицы:    
CREATE TABLE owner  
(  
&emsp;user_id BIGSERIAL PRIMARY KEY,  
&emsp;user_name VARCHAR(200) NOT NULL  
);  
CREATE TABLE unit  
(  
&emsp;unit_id BIGSERIAL PRIMARY KEY,  
&emsp;user_id BIGSERIAL REFERENCES owner (user_id) NOT NULL,  
&emsp;unit_name VARCHAR(200) NOT NULL  
);  
CREATE TABLE container  
(  
&emsp;container_id BIGSERIAL PRIMARY KEY,  
&emsp;user_id BIGSERIAL REFERENCES owner (user_id) NOT NULL,  
&emsp;unit_id BIGSERIAL REFERENCES unit (unit_id) NOT NULL,  
&emsp;container_name VARCHAR(200) NOT NULL  
);  
CREATE TABLE trigger  
(  
&emsp;trigger_id BIGSERIAL PRIMARY KEY,  
&emsp;container_id BIGSERIAL REFERENCES container (container_id) NOT NULL,  
&emsp;unit_id BIGSERIAL REFERENCES unit (unit_id) NOT NULL,  
&emsp;user_id BIGSERIAL REFERENCES owner (user_id) NOT NULL,  
&emsp;web_name VARCHAR(200) NOT NULL,  
&emsp;web_id VARCHAR(200) NOT NULL,  
&emsp;web_class VARCHAR(200) NOT NULL,  
&emsp;event VARCHAR(200) NOT NULL  
);  
CREATE TABLE visitor  
(  
&emsp;ASID BIGSERIAL PRIMARY KEY,  
&emsp;unit_id BIGSERIAL REFERENCES unit (unit_id) NOT NULL,  
&emsp;MSISDN BIGINT,  
&emsp;first_name VARCHAR(200),  
&emsp;last_name VARCHAR(200),  
&emsp;patronymic VARCHAR(200)  
);  
CREATE TABLE data  
(  
&emsp;data_id BIGSERIAL PRIMARY KEY,  
&emsp;trigger_id BIGSERIAL REFERENCES trigger (trigger_id) NOT NULL,  
&emsp;container_id BIGSERIAL REFERENCES container (container_id) NOT NULL,  
&emsp;unit_id BIGSERIAL REFERENCES unit (unit_id) NOT NULL,  
&emsp;ASID BIGSERIAL REFERENCES visitor (ASID) NOT NULL,  
&emsp;event VARCHAR(200) NOT NULL,  
&emsp;date timestamp NOT NULL  
);   

II. MinIO:  
1. Запустить:  
docker run -p 9000:9000 -p 9090:9090 —name minio -v ~/minio/data:/data -e "MINIO_ROOT_USER=ROOTNAME" -e "MINIO_ROOT_PASSWORD=PASSWORD" quay.io/minio/minio server /data —console-address ":9090  
(Доступ по localhost:9000)  
2. Создать bucket 'scripts'  
3. Создать ключ доступа.  
4. Ввести публичный и секретный ключи в MinioController  


Основные технологии:  
&emsp;Spring - REST API  
&emsp;Velocity - для работы с templates  
&emsp;PostgreSQL - БД для данных   
&emsp;MinIO - БД для скриптов  
  
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
  
Посетитель (ASID, MSISDN, first_name, last_name, patronymic)
  
Что оно умеет: (Postman "документация" с примерами по api: https://documenter.getpostman.com/view/24365536/2s8YzZPKFp)  
&emsp;Добавлять/изменять/удалять/возвращать юниты/контейнеры с тригерами (PostgreSQL)  
&emsp;Фомировать/сохранять/изменять/отправлять js-файлы с скриптами (MinIO, Velocity)  
&emsp;Принимать/сохранять данные полученные от триггеров (PostgreSQL)  
&emsp;Обогощать данные через внешний сервер (PostgreSQL)  
&emsp;Отвечать на основные типы запросов (PostgreSQL)  
