# Food Ordering & Delivery Management System

A Spring Boot microservices-based application for managing users, food items, orders, payments, and order notifications. The project demonstrates service-to-service communication, JWT authentication, centralized configuration, caching, and event-driven messaging.

## Features

- **User Management:** User registration, login, and JWT-based authentication.
- **Role-Based Access:** Separate permissions for customers and administrators.
- **Food Management:** Create, view, update, and delete food items.
- **Order Management:** Place orders, calculate totals using current food prices, and track order status.
- **Payment Service:** Simulated payment processing with payment records stored in MySQL.
- **Inter-Service Communication:** OpenFeign for communication between Order, Food, and Payment services.
- **Caching:** Redis-based caching for food data, with cache eviction on updates.
- **Event-Driven Notifications:** Apache Kafka publishes order-created events, consumed by the Notification Service.
- **Centralized Configuration:** Spring Cloud Config Server manages configuration for microservices.
- **API Gateway:** Spring Cloud Gateway provides a common entry point for user, food, and order APIs.

## Technology Stack

- **Language:** Java 21
- **Framework:** Spring Boot 4.1.1, Spring Security
- **Microservices:** Spring Cloud Config, Spring Cloud Gateway, OpenFeign
- **Databases:** MySQL, Redis
- **Messaging:** Apache Kafka
- **Authentication:** JWT
- **Tools:** Maven, Eclipse, Postman

## Architecture

The application consists of the following services:

| Service | Responsibility | Port |
|---|---|---:|
| Config Server | Centralized configuration | 8888 |
| API Gateway | Routes incoming API requests | 8080 |
| User Service | Registration, login, and users | 8081 |
| Food Service | Food management and caching | 8082 |
| Payment Service | Simulated payment processing | 8083 |
| Order Service | Order processing and orchestration | 8084 |
| Notification Service | Consumes order events from Kafka | Configured in `notification-service.properties` |

Each major business service uses its own MySQL database: `userdb`, `fooddb`, `paymentdb`, and `orderdb`.

### Order Processing Flow

1. A customer submits an order through the API Gateway.
2. Order Service retrieves food details through OpenFeign.
3. The order total is calculated and the order items are saved.
4. Order Service calls Payment Service to process the simulated payment.
5. The order is marked as `PLACED` after successful payment.
6. An `order-created` event is published to Kafka.
7. Notification Service consumes the event and prints the notification in its console.

## Prerequisites

Install or configure the following before running the application:

- Java 21
- MySQL 8 or compatible version
- Redis-compatible server, such as Redis or Memurai
- Apache Kafka
- Maven, or use the included Maven Wrapper
- Postman for API testing

## Setup and Run

### 1. Clone the repository

```bash
git clone https://github.com/skrish-g/Food-Ordering-Delivery-Management-System.git
cd Food-Ordering-Delivery-Management-System
```

### 2. Create the MySQL databases

Execute the following SQL in MySQL Workbench:

```sql
CREATE DATABASE userdb;
CREATE DATABASE fooddb;
CREATE DATABASE paymentdb;
CREATE DATABASE orderdb;
```

Ensure that the database credentials in `config-server/config-repo/` match your local MySQL configuration.

### 3. Start the supporting services

Start MySQL, your Redis-compatible server, and the Kafka broker.

Make Kafka available at `localhost:9092`. Create the notification topic if it does not already exist:

```bash
bin/windows/kafka-topics.bat --create --topic order-created --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1
```

Run this command from your Kafka installation directory on Windows.

### 4. Start the Spring Boot services

Run the following services from their respective project directories, using Eclipse or a terminal:

1. `config-server`
2. `user-service`
3. `food-service`
4. `payment-service`
5. `order-service`
6. `notification-service`
7. `api-gateway`

To run a service from its project directory using Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

Keep the services running in separate terminals. The Config Server must be available before the other services start.

## API Testing

Use Postman to test the APIs. For gateway-routed requests, use `http://localhost:8080` as the base URL.

| Method | Endpoint | Purpose |
|---|---|---|
| POST | `/users/register` | Register a user |
| POST | `/users/login` | Authenticate and obtain a JWT |
| GET | `/users/{id}` | Retrieve user details |
| GET | `/foods` | List food items |
| GET | `/foods/{id}` | Retrieve a food item |
| POST | `/foods` | Add a food item (Admin) |
| PUT | `/foods/{id}` | Update a food item (Admin) |
| DELETE | `/foods/{id}` | Delete a food item (Admin) |
| POST | `/orders` | Place an order |
| GET | `/orders/{id}` | Retrieve an order |
| POST | `http://localhost:8083/payments` | Test payment processing directly |

Protected endpoints require a valid JWT in Postman's **Authorization → Bearer Token** setting. Food creation, updates, and deletion require the appropriate administrator role.

### Example: Place an Order

**Request**

```http
POST http://localhost:8080/orders
Authorization: Bearer <CUSTOMER_JWT>
Content-Type: application/json
```

```json
{
  "customerId": 3,
  "restaurantId": 1,
  "items": [
    {
      "foodId": 1,
      "quantity": 2
    }
  ]
}
```

**Example response**

```json
{
  "customerId": 3,
  "orderId": 7,
  "restaurantId": 1,
  "status": "PLACED",
  "totalAmount": 600.0
}
```

The example assumes that food item `1` costs `300` and is available in the Food Service database.

## Project Structure

```text
Food-Ordering-Delivery-Management-System/
├── api-gateway/
├── config-server/
│   └── config-repo/
├── user-service/
├── food-service/
├── payment-service/
├── order-service/
├── notification-service/
├── .gitignore
├── README.md
└── IA-02-Questions-and-Answers.pdf
```

## Notes

- Payment processing is simulated; no external payment gateway is integrated.
- Notifications are printed to the Notification Service console rather than sent through email or SMS.
- Database credentials and JWT secrets in the local configuration are for development only and should be secured before production deployment.

## Author

**Krish Gupta**

GitHub: [@skrish-g](https://github.com/skrish-g)
