# DotaLink

Учебный командный проект на Spring Boot (MVC) для игроков Dota 2.

## Что реализовано (модуль участника №1)

- Регистрация, логин, логаут
- Spring Security + роли `ROLE_USER` / `ROLE_ADMIN`
- Профиль пользователя (приватный и публичный)
- Редактирование профиля с валидацией и PRG
- Привязка Dota аккаунта (полный CRUD)
- REST API для Dota аккаунта
- AJAX: проверка username/email, CRUD Dota аккаунта
- Поиск игроков + пагинация
- Админ-страница пользователей + пагинация
- Кастомные страницы ошибок `403/404/500` + JSON ошибки для API

## Стек

- Java 17
- Spring Boot 3, Spring MVC, Spring Security
- Spring Data JPA, Hibernate
- PostgreSQL, Flyway
- Thymeleaf, Bootstrap 5, jQuery
- Maven Wrapper
- Docker, Docker Compose

## Сущности

- `User`
- `UserProfile`
- `DotaAccount`
- `Hero`
- `PartyPost` (каркас)
- `PartyApplication` (каркас)

Связи:

- `User` -> `UserProfile` (OneToOne)
- `User` -> `DotaAccount` (OneToOne)
- `UserProfile` <-> `Hero` (ManyToMany)

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

На форме профиля и в фильтре игроков — выпадающий список.

## Интеграция Steam API

Есть 2 режима:

- по умолчанию: `stub` (без реального запроса)
- с ключом: реальный запрос к Steam Web API (`GetPlayerSummaries`)

Переменные окружения:

- `STEAM_API_ENABLED=false|true`
- `STEAM_API_KEY=...`
- `STEAM_API_BASE_URL=https://api.steampowered.com`

На странице `/account/dota` есть индикатор: `Steam sync: enabled/disabled`.

## Быстрый запуск через Docker

```bash
docker compose up --build
```

Открыть:

- `http://localhost:8080`

### Чистый перезапуск БД

```bash
docker compose down -v
docker compose up --build
```

## Локальный запуск (без Docker)

```bash
./mvnw spring-boot:run
```

Windows:

```bat
mvnw.cmd spring-boot:run
```

## Тестовые пользователи

- `admin / admin123` (админ)
- `demo / demo123` (обычный пользователь)

## Основные URL

- `/`
- `/register`
- `/login`
- `/profile/me`
- `/profile/edit`
- `/account/dota`
- `/players`
- `/players/{username}`
- `/admin/users`
- `/api/account/dota`

## REST API (DotaAccount)

- `GET /api/account/dota`
- `POST /api/account/dota`
- `PUT /api/account/dota`
- `DELETE /api/account/dota`

## Быстрая проверка

1. Запусти `docker compose up --build`
2. Открой `/register`, создай пользователя
3. Войди через `/login`
4. Проверь `/profile/me` и `/profile/edit`
5. Проверь CRUD на `/account/dota` (форма + AJAX)
6. Под `demo` открой `/admin/users` (должен быть 403)
7. Под `admin` открой `/admin/users` (должна открыться таблица)
8. Проверь `/players` (фильтры и пагинация)
