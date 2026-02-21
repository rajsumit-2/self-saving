# Blackrock Challenge – Java 17 / Spring Boot

Production-grade APIs for automated retirement savings: expense parsing, transaction validation, q/p/k temporal constraints, NPS and Index returns, and performance metrics. Aligned with the challenge specification (timestamp format `yyyy-MM-dd HH:mm:ss`, colon-style endpoints).

## Requirements

- Java 17+
- Maven 3.6+
- Docker (optional)

## Build and run

```bash
# Run tests (must be in project root)
mvn test

# Run application
mvn spring-boot:run
```

Server listens on **http://localhost:5477** (or `PORT` env var).

## API (challenge spec)

Base path: `/blackrock/challenge/v1`

| Method | Path | Description |
|--------|------|-------------|
| GET | `/` | API overview and endpoint list |
| GET | `/health` | Liveness |
| POST | `/transactions:parse` | Parse expenses → transactions (ceiling, remanent) |
| POST | `/transactions:validator` | Validate by wage and max amount to invest |
| POST | `/transactions:filter` | Apply q/p/k periods; return valid/invalid |
| POST | `/returns:nps` | NPS returns (7.11%), tax benefit, inflation-adjusted |
| POST | `/returns:index` | Index fund returns (14.49%), inflation-adjusted |
| GET | `/performance` | Time (HH:mm:ss.SSS), memory (MB), thread count |

Transaction objects accept either `date` or `timestamp` for the datetime field. Timestamp format: `yyyy-MM-dd HH:mm:ss` (optional seconds supported as `yyyy-MM-dd HH:mm`).

## Docker

Per challenge: app runs on port **5477** inside the container; map with `-p 5477:5477`. Image name convention: `blk-hacking-ind-{name-lastname}`.

```bash
docker build -t blk-hacking-ind-<name-lastname> .
docker run -d -p 5477:5477 blk-hacking-ind-<name-lastname>
```

## Tests

Tests live under `src/test` (unit and integration). To run:

```bash
mvn test
```

Integration tests hit the colon-style paths (`transactions:parse`, `transactions:validator`, etc.).

## Project layout

- `src/main/java/com/blackrock/challenge/` – application, controllers, services, dto, util, config
- `src/test/java/` – unit and integration tests
