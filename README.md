```markdown
# Wallet Service API

Сервис управления кошельками с автоматическим созданием и обработкой транзакций.

## Описание

Микросервис для управления кошельками с следующими возможностями:

- Автоматическое создание кошелька при первой транзакции
- Операции пополнения и снятия средств
- Проверка баланса
- Обработка ошибок
- Валидация транзакций

## Требования

- Docker

## Запуск локально

```bash
git clone <url-репозитория>
cd wallet-service
docker compose up --build -d
```

Сервис будет доступен по адресу: `http://localhost:8080`

## API Эндпоинты

### 1. Операции с кошельком

#### POST `/api/v1/wallet`

Обработка транзакции кошелька. Кошелёк создаётся автоматически, если не существует.

**Тело запроса:**

```json
{
    "walletId": "00000000-0000-0000-0000-000000000012",
    "operationType": "DEPOSIT",
    "amount": 100.50
}
```

**Типы операций:**

- `DEPOSIT` - Пополнение средств
- `WITHDRAW` - Снятие средств

**Успешный ответ (200 OK):**

```json
{
    "walletId": "00000000-0000-0000-0000-000000000012",
    "balance": 100.50
}
```

**Ответы с ошибками:**

- `400 Bad Request` - Неверный JSON формат или ошибка валидации
- `409 Conflict` - Недостаточно средств для снятия
- `500 Internal Server Error` - Ошибка сервера

### 2. Проверка баланса

#### GET `/api/v1/wallets/{walletId}`

Получение текущего баланса кошелька.

**Пример:**

```
GET http://localhost:8080/api/v1/wallets/00000000-0000-0000-0000-000000000012
```

**Успешный ответ (200 OK):**

```json
{
    "walletId": "00000000-0000-0000-0000-000000000012",
    "balance": 150.75
}
```

**Ответ с ошибкой:**

- `404 Not Found` - Кошелёк не найден

## Тестирование

### Запуск тестов

```bash
# Запуск всех тестов
./mvnw test

# Запуск конкретного тестового класса
./mvnw -Dtest=WalletServiceTest test
```

### Тестирование в Postman

#### Создание/Обновление кошелька (POST)

- Метод: `POST`
- URL: `http://localhost:8080/api/v1/wallet`
- Body: JSON с данными транзакции

#### Проверка баланса (GET)

- Метод: `GET`
- URL: `http://localhost:8080/api/v1/wallets/{walletId}`

### Примеры тестовых сценариев

#### Успешное пополнение

```json
// POST /api/v1/wallet
{
    "walletId": "00000000-0000-0000-0000-000000000012",
    "operationType": "DEPOSIT",
    "amount": 100.00
}
```

#### Успешное снятие

```json
// POST /api/v1/wallet
{
    "walletId": "00000000-0000-0000-0000-000000000012",
    "operationType": "WITHDRAW",
    "amount": 50.00
}
```

#### Ошибка при недостатке средств

```json
// POST /api/v1/wallet
{
    "walletId": "00000000-0000-0000-0000-000000000012",
    "operationType": "WITHDRAW",
    "amount": 1000.00
}
// Ответ: 409 Conflict - Insufficient funds
```

#### Неверный JSON формат

```json
// POST /api/v1/wallet
{
    "walletId": "00000000-0000-0000-0000-000000000012",
    "operationType": "DEPOSIT"
    "amount": 100.00  // Пропущена запятая
}
// Ответ: {"error":"Invalid JSON"}
```

## Структура проекта

```
src/
├── main/
│   ├── java/com/example/wallet/
│   │   ├── controller/
│   │   │   └── WalletController.java
│   │   ├── service/
│   │   │   └── WalletService.java
│   │   ├── model/
│   │   │   ├── Wallet.java
│   │   │   └── TransactionRequest.java
│   │   ├── repository/
│   │   │   └── WalletRepository.java
│   │   └── exception/
│   │       └── GlobalExceptionHandler.java
│   └── resources/
│       └── application.properties
└── test/
    └── java/com/example/wallet/
        └── WalletServiceTest.java
```
