# JEE-Counsellor Backend

A Spring Boot application providing backend services for the JEE Counsellor platform.

---

## 🛠️ Prerequisites

Make sure you have installed:
- **Java 21** or later (`java -version`)
- **Docker & Docker Compose** (recommended for database setup) or a local **PostgreSQL** instance

---

## ⚙️ Configuration

Application configuration is located at `src/main/resources/application.yml`.

### Environment Variables
You can customize application settings using the following environment variables (or copy `.env.example` to `.env`):

| Variable | Default Value | Description |
| :--- | :--- | :--- |
| `PORT` | `8080` | Application HTTP port |
| `SPRING_PROFILES_ACTIVE` | `dev` | Active Spring profile (`dev`, `prod`) |
| `DB_HOST` | `localhost` | PostgreSQL host |
| `DB_PORT` | `5432` | PostgreSQL port |
| `DB_NAME` | `jee_counsellor` | Database name |
| `DB_USER` | `postgres` | Database user |
| `DB_PASSWORD` | `postgres` | Database password |
| `DDL_AUTO` | `update` | Hibernate DDL strategy (`update`, `validate`, `create-drop`) |
| `SHOW_SQL` | `true` | Show SQL statements in logs |

---

## 🚀 Getting Started

### 1. Start the PostgreSQL Database
If you have Docker installed, start the database with a single command:

```bash
docker compose up -d
```

To verify the container is running:
```bash
docker ps
```

### 2. Run the Application

Using the included Maven wrapper:

```bash
# Make sure the wrapper is executable (on Linux/macOS)
chmod +x mvnw

# Run the Spring Boot application
./mvnw spring-boot:run
```

*(On Windows, run `mvnw.cmd spring-boot:run`)*

---

## 📡 Verification & Endpoints

Once the application starts up, you can test if it's alive:

```bash
curl http://localhost:8080/health-check
```

Expected Response:
```text
Application-alive
```

---

## 📦 Building for Production

To package the application into an executable JAR:

```bash
./mvnw clean package -DskipTests
```

Run the built JAR:

```bash
java -jar target/jee-counsellor-0.0.1-SNAPSHOT.jar
```

---

## 🛑 Stopping the Database

To stop the PostgreSQL container:

```bash
docker compose down
```
