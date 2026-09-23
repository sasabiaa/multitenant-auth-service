
# Quick Start Guide - Multitenant Auth Service

Step-by-step guide to cloning, running infrastructure dependencies, building, and testing the registration and login endpoints for the **multitenant-auth-service**.

---

## Prerequisites
Ensure you have the following installed on your machine:
* **Git**
* **Java SDK (JDK 17+)**
* **Apache Maven**
* **Docker & Docker Compose**
* **Postman / Bruno / cURL** (for API testing)

---

## Step-by-Step Setup

### 1. Clone the Repository
Clone the project repository to your local machine:
```
git clone https://github.com/sasabiaa/multitenant-auth-service.git)
```
2. Navigate to Directory
Change directory into your project root

3. Setup Environment Variable
Update the .env.example file, rename it to .env, and update the configuration variables as needed:

4. Start Infrastructure Services
Run required background services (PostgreSQL & Redis) using Docker Compose:

```
docker-compose up -d
```

5. Load Environment Variables & Build Project
Export variables from the .env file into your shell environment, then run the Maven clean install:
```
export $(cat .env | xargs) && mvn clean install
```

Testing API Endpoints
Use your preferred API client (e.g., Postman) to test the following authentication endpoints.

6. Register New User
HTTP Method: POST
URL: http://YOUR_HOST:YOUR_PORT/auth/register
Headers:
Content-Type: application/json
Request Body:
```
{
  "username": "CustomUsername",
  "userEmail": "CustomEmail",
  "password": "CustomPassword"
}
```

7. User Login
HTTP Method: POST
URL: http://YOUR_HOST:YOUR_PORT/auth/login
Headers:
Content-Type: application/json
Request Body:
```
{
  "emailOrUsername": "CustomEmailOrUsername",
  "password": "CustomPassword"
}
```
