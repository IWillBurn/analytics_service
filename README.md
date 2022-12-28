# Аналитика  

## Участники:  
#### Бэкенд: Боярников Евгений Алексеевич  
#### Фронтенд: Боярников Александр Алексеевич  

## Ссылки
#### Это бэкенд часть приложения.  
#### [Фронтенд часть приложения](https://github.com/gmvrpw/analytic-ui)  
 
## Запуск приложения:  
### I. PostgreSQL:
#### 1. Запустить:    
```shell
docker run —name analytics -p 5432:5432 -e POSTGRES_USER=admin -e POSTGRES_PASSWORD=password -e POSTGRES_DB=analytics_db -d postgres:13.3  
```
(Доступ по localhost:5432)  
#### 2. Создать таблицы:    
```sql
CREATE TABLE owner  
(  
    user_id BIGSERIAL PRIMARY KEY,  
    user_name VARCHAR(200) NOT NULL  
);  
CREATE TABLE unit  
(  
    unit_id BIGSERIAL PRIMARY KEY,  
    user_id BIGSERIAL REFERENCES owner (user_id) NOT NULL,  
    unit_name VARCHAR(200) NOT NULL  
);  
CREATE TABLE container  
(  
    container_id BIGSERIAL PRIMARY KEY,  
    user_id BIGSERIAL REFERENCES owner (user_id) NOT NULL,  
    unit_id BIGSERIAL REFERENCES unit (unit_id) NOT NULL,  
    container_name VARCHAR(200) NOT NULL  
);  
CREATE TABLE trigger  
(  
    trigger_id BIGSERIAL PRIMARY KEY,  
    container_id BIGSERIAL REFERENCES container (container_id) NOT NULL,  
    unit_id BIGSERIAL REFERENCES unit (unit_id) NOT NULL,  
    user_id BIGSERIAL REFERENCES owner (user_id) NOT NULL,  
    web_name VARCHAR(200) NOT NULL,  
    web_id VARCHAR(200) NOT NULL,  
    web_class VARCHAR(200) NOT NULL,  
    event VARCHAR(200) NOT NULL  
);  
CREATE TABLE visitor  
(  
    ASID BIGSERIAL PRIMARY KEY,  
    unit_id BIGSERIAL REFERENCES unit (unit_id) NOT NULL,  
    MSISDN BIGINT,  
    first_name VARCHAR(200),  
    last_name VARCHAR(200),  
    patronymic VARCHAR(200)  
);  
CREATE TABLE data  
(  
    data_id BIGSERIAL PRIMARY KEY,  
    trigger_id BIGSERIAL REFERENCES trigger (trigger_id) NOT NULL,  
    container_id BIGSERIAL REFERENCES container (container_id) NOT NULL,  
    unit_id BIGSERIAL REFERENCES unit (unit_id) NOT NULL,  
    ASID BIGSERIAL REFERENCES visitor (ASID) NOT NULL,  
    event VARCHAR(200) NOT NULL,  
    date timestamp NOT NULL  
);   
```
### II. MinIO:  
#### 1. Запустить: 
```shell
docker run -p 9000:9000 -p 9090:9090 —name minio -v ~/minio/data:/data -e "MINIO_ROOT_USER=ROOTNAME" -e "MINIO_ROOT_PASSWORD=PASSWORD" quay.io/minio/minio server /data —console-address ":9090  
```
(Доступ по localhost:9000)  
#### 2. Создать bucket 'scripts'  
#### 3. Создать ключ доступа.  
#### 4. Ввести публичный и секретный ключи в MinioController    
### III. Java:  
#### 1. Запустить Java проект:
```shell
gradle run
```
(Доступ по localhost:8080/api)  

## Основные технологии:  
#### Spring - REST API  
#### Velocity - для работы с templates  
#### PostgreSQL - БД для данных   
#### MinIO - БД для скриптов  
  
## Что оно умеет ([Postman](https://documenter.getpostman.com/view/24365536/2s8YzZPKFp)):
#### Добавлять/изменять/удалять/возвращать юниты/контейнеры с тригерами (PostgreSQL)  
#### Фомировать/сохранять/изменять/отправлять js-файлы с скриптами (MinIO, Velocity)  
#### Принимать/сохранять данные полученные от триггеров (PostgreSQL)  
#### Обогощать данные через внешний сервер (PostgreSQL)  
#### Отвечать на основные типы запросов (PostgreSQL)  
