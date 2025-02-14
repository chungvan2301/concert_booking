# Concert Booking Backend API

## Overview

The **Concert Booking Backend API** is a robust and scalable backend system built with **Spring Boot**, designed to handle concert ticket bookings.

## Tech Stack

- **Spring Boot** – Backend framework
- **PostgreSQL** – Relational database
- **Redis** – In-memory caching for improved performance
- **Redis Queue** – Asynchronous task processing
- **VNPay** – Payment gateway integration
- **JWT (JSON Web Token)** – Secure user authentication
- **Docker** – Containerized deployment

## Features

### 1. **User Authentication & Authorization**

- Secure registration and login
- JWT-based authentication
- Role-based access control (User, Admin)

### 2. **Concert & Ticket Management**

- Create, update, and delete concert events (Admin only)
- Browse available concerts
- Ticket booking

### 3. **Payment Integration**

- Secure payment processing with **VNPay**
- Order tracking and status updates

### 4. **Performance Optimization**

- **Redis Queue** for handling booking service
- - **Redis caching** for frequently accessed data (update soon)

## Installation & Setup

### Prerequisites

Ensure you have the following installed:

- **Java 23**
- **Maven**
- **PostgreSQL**
- **Redis**

### Clone the Repository

```sh
git clone https://github.com/chungvan2301/concert_booking.git
cd concert_booking/backend
```

### Configure the Application

Update the **application.properties** file with your database and Redis settings.

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/concert_booking
spring.datasource.username=your_db_user
spring.datasource.password=your_db_password

spring.redis.host=localhost
spring.redis.port=6379
```



