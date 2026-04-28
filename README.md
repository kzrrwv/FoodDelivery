# Food Delivery
    FoodDelivery — REST API для сервиса доставки еды. 
    Позволяет пользователям оформлять заказы, курьерам доставлять их, а администраторам управлять продуктами, категориями и заказами. 
    Проект демонстрирует базовую архитектуру backend-приложения с ролями пользователей и бизнес-логикой онлайн-доставки.
# Описание проекта
    Система FoodDelivery представляет собой полноценную платформу для онлайн-доставки еды, 
    которая автоматизирует процесс оформления заказов, управления ассортиментом товаров и взаимодействия между клиентами, курьерами и администраторами. 
    Приложение позволяет пользователям просматривать доступные продукты, формировать корзину, создавать заказы, выбирать способ оплаты и указывать адрес доставки. 
    После оформления заказа система передает его в обработку, а курьер получает возможность принять и доставить заказ клиенту.

    Backend часть проекта реализована в виде REST API на базе Spring Boot с использованием многослойной архитектуры, включающей уровни контроллеров, сервисов, репозиториев и сущностей.

    Frontend часть реализована и обеспечивает удобный пользовательский интерфейс для взаимодействия с системой.
# Роли пользователей
## USER(Клиент)
    - Регистрация и авторизация
    - Просмотр меню
    - Оформление заказов
    - Просмотр истории заказов
    - Оставление отзывов

## COURIER(Курьер)
    - Просмотр назначенных заказов
    - Изменение статуса доставки
    - История выполненных доставок

## ADMIN(Администратор)
    - Управление пользователями
    - Управление товарами и категориями
    - Управление заказами
    - Просмотр статистики
    - Модерация отзывов
# Технологии 
    - Java
    - Spring Boot
    - Spring Web
    - Spring Data JPA
    - Hibernate
    - REST API
    - Maven
    - JWT
# Структура проекта
    src/
        main/
            java/
                config/
                controller/
                domen/
                    dto/
                    model/
                        enums/
                    request/
                    responce/
                exception/
                repository/
                security/
                service/
                    impl/
                ProjectApplication
            resources/
                db.changelog/
                application.yaml
        test/
            java/
                serviceTest/
            
    
        
# Установка и запуск
### Требования
Перед запуском убедитесь, что у вас установлено:

    - Java 17+
    - Maven 3.6+
    - PostgreSQL (или другая настроенная база данных)
    - Docker (опционально, если запускаете через контейнеры)

### 1. Клонирование проекта
    git clone https://github.com/kzrrwv/food-delivery.git
    cd food-delivery

### 2. Настройка базы данных
    Создать базу данных PostgreSQL
    
    Открыть файл applicstion.yaml
    
    Указать свои настройки подключения
### 3. Сборка проекта
    mvn clean install
### 4. Запуск приложения
    mvn spring-boot:run
# Конфигурация
Проект использует файл 'application.yaml' для настройки основных компонентов приложения,
включая базу данных, Liquibase, JPA и параметры безопасности.

### Основная конфигурация приложения
    spring:
      application:
        name: project
### Настройки базы данных
    spring:
      datasource:
        url: jdbc:postgresql://localhost:5432/postgres
        username: postgres
        password: 12345
### JPA / Hibernate
    spring:
      jpa:
        hibernate:
          ddl-auto: validate

    Режим validate означает, что Hibernate проверяет соответствие сущностей и схемы базы данных, но не изменяет ее автоматически.
### Liquibase
    spring:
      liquibase:
        change-log: classpath:db/changelog/db.changelog-master.xml
          default-schema: public

    change-log - путь к основному файлу миграции
    default-schema - схема базы данных, используемая по умолчанию
### JWT
    token:
      signing:
        key: 53A73E5F1C4E0A2D3B5F2D784E6A1B423D6F247D1F6E5C3A596D635A75327855
    
    Для генерации и проверки JWT токенов используется секретный ключ.

# API
### Авторизация
    POST /auth/register — регистрация пользователя

    POST /auth/login — вход в систему
### Продукты
    GET /product — получить список товаров

    POST /product — добавить товар

    PUT /product/{id} — изменить товар

    DELETE /product/{id} — удалить товар
### Заказы
    GET /order — получить список заказов

    POST /order — добавить заказ

    PUT /order/{id} — изменить заказ

    DELETE /order/{id} — удалить заказ
### Отзывы
    GET /review — получить список отзывов

    POST /review — добавить отзыв

    PUT /review/{id} — изменить отзыв

    DELETE /review/{id} — удалить отзыв
### Пользователи
    GET /user — получить список пользователей

    POST /user — добавить пользователя

    PUT /user/{id} — изменить пользователя

    DELETE /user/{id} — удалить пользователя

# Тестирование
В проекте для тестов используется:

    JUnit 5
    Mockito
Реализованны тесты для базовых случаев сервисов.

# Docker
## Для запуска контейнера нужно:
### 1. Открыть Maven
### 2. Перейти в Lifecycle
### 3. Выполнить clean, затем validate
### 4. Запустить Docker
### 5. Перейти в docker-compose.yaml
### 6. Запустить project
    project:
      build: .
        container_name: foodDelivery
      ports:
        - '8080:8080'
      environment:
        - SPRING_DATASOURCE_URL=jdbc:postgresql://foodDeliveryPostgres:5432/postgres
        - SPRING_DATASOURCE_USERNAME=postgres
        - SPRING_DATASOURCE_PASSWORD=12345
      depends_on:
        - postgres
      networks:
        - food-network

# Контакты
    Email: kristinazar2007@gmail.com
    GitHub: https://github.com/kzrrwv
    Telegram: @kzrrwv
