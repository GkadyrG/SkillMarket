# DotaLink

DotaLink - учебный командный веб-проект на Java и Spring Boot для поиска игроков Dota 2, ведения игровых профилей и взаимодействия внутри сообщества.

## О проекте

Приложение позволяет:

- регистрироваться и входить в систему;
- редактировать профиль игрока;
- привязывать Dota/Steam-аккаунт;
- искать игроков по параметрам профиля;
- публиковать party posts и откликаться на них;
- оставлять отзывы и просматривать статистику.

## Основной функционал

### Аутентификация и доступ

- регистрация пользователей;
- вход и выход из системы;
- роли `ROLE_USER` и `ROLE_ADMIN`;
- защита маршрутов через Spring Security.

### Профили игроков

- просмотр собственного профиля;
- редактирование профиля;
- публичные страницы пользователей;
- выбор любимых героев;
- ранги, регионы и предпочтения по ролям.

### Dota account

- привязка Steam/Dota-аккаунта;
- обновление и удаление привязки;
- REST API для работы с Dota account;
- поддержка синхронизации с Steam Web API.

### Поиск и взаимодействие

- поиск игроков с фильтрацией и пагинацией;
- список пользователей для администратора;
- party posts и заявки;
- отзывы о пользователях;
- статистика и аналитика профиля.

### Обработка ошибок

- пользовательские страницы ошибок `403`, `404`, `500`;
- JSON-ответы об ошибках для API.

## Технологии

- Java 17
- Spring Boot 3
- Spring MVC
- Spring Security
- Spring Data JPA
- Hibernate
- PostgreSQL
- Flyway
- Thymeleaf
- Bootstrap 5
- jQuery
- Maven Wrapper
- Docker, Docker Compose

## Архитектура

Проект построен по классической многослойной схеме:

- `controller` - принимает HTTP-запросы и возвращает HTML или JSON;
- `service` - содержит бизнес-логику;
- `repository` - работает с базой данных;
- `model` - описывает сущности предметной области;
- `dto` - используется для форм, API и передачи данных между слоями.

Дополнительные модули:

- `security` - аутентификация, авторизация, роли;
- `common` - общие контроллеры, ошибки и исключения;
- `integration/dota` - интеграция со Steam API.

## Предметная модель

Основные сущности проекта:

- `User`
- `UserProfile`
- `DotaAccount`
- `Hero`
- `PartyPost`
- `PartyApplication`
- `Review`

Основные связи:

- `User` -> `UserProfile` (`OneToOne`)
- `User` -> `DotaAccount` (`OneToOne`)
- `UserProfile` <-> `Hero` (`ManyToMany`)
- `User` -> `PartyPost` (`OneToMany`)
- `User` -> `PartyApplication` (`OneToMany`)

## Ранги Dota

В проекте используются фиксированные ранги:

- Herald
- Guardian
- Crusader
- Archon
- Legend
- Ancient
- Divine
- Immortal

Ранги используются в профиле игрока, фильтрах поиска и party-функциональности.

## Интеграция со Steam API

Поддерживаются два режима работы:

- `stub` - без реального внешнего запроса;
- `steam api` - через `GetPlayerSummaries`.

Переменные окружения:

- `STEAM_API_ENABLED=false|true`
- `STEAM_API_KEY=...`
- `STEAM_API_BASE_URL=https://api.steampowered.com`

## Структура ресурсов

- `src/main/java` - Java-код приложения
- `src/main/resources/templates` - HTML-шаблоны Thymeleaf
- `src/main/resources/static` - CSS и JavaScript
- `src/main/resources/db/migration` - SQL-миграции Flyway
- `src/test` - тесты

## Запуск проекта

### Через Docker

```bash
docker compose up --build
```

Приложение будет доступно по адресу:

- `http://localhost:8080`

### Перезапуск с очисткой БД

```bash
docker compose down -v
docker compose up --build
```

### Локальный запуск

```bash
./mvnw spring-boot:run
```

Для Windows:

```bat
mvnw.cmd spring-boot:run
```

## Тестовые пользователи

- `admin / admin123` - администратор
- `demo / demo123` - обычный пользователь

## Основные маршруты

### Публичные и auth

- `/`
- `/login`
- `/register`

### Профиль и игроки

- `/profile/me`
- `/profile/edit`
- `/players`
- `/profiles/{username}`

### Dota account

- `/account/dota`
- `/api/account/dota`

### Администрирование

- `/admin/users`

### Party и взаимодействие

- `/party`
- `/posts`
- `/posts/{id}`

## REST API

### Dota account API

- `GET /api/account/dota`
- `POST /api/account/dota`
- `PUT /api/account/dota`
- `DELETE /api/account/dota`

### Party post API

- `GET /api/posts`
- `POST /api/posts`
- `PUT /api/posts/{id}`
- `DELETE /api/posts/{id}`
- `POST /api/posts/{id}/apply`

## База данных и миграции

Схема базы создается и поддерживается через Flyway.

Основные миграции:

- `V1__init_schema.sql` - начальная схема проекта;
- `V2__normalize_ranks.sql` - нормализация рангов;
- `V3__fix_party_applications_unique_constraint.sql` - доработка ограничений заявок;
- `V4__create_reviews.sql` - добавление отзывов.

## Интерфейс

Серверная часть строится на MVC с HTML-шаблонами Thymeleaf.

В проекте используются страницы для:

- входа и регистрации;
- просмотра и редактирования профиля;
- списка игроков;
- работы с Dota account;
- административной панели;
- просмотра party posts;
- отображения ошибок.
