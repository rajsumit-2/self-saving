# Blackrock Challenge – Java 17 / Spring Boot

Production-grade APIs for automated retirement savings: expense parsing, transaction validation, q/p/k temporal constraints, NPS and Index returns, and performance metrics. Aligned with the challenge specification (timestamp format `yyyy-MM-dd HH:mm:ss`, colon-style endpoints).

## Requirements

- Java 17+
- Maven 3.6+ (or use Maven Wrapper: `./mvnw`)
- Docker (optional - for local builds only; CI/CD uses GitHub Actions)

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


### GitHub Actions CI/CD

Docker images are automatically built and pushed to GitHub Container Registry (GHCR) using GitHub Actions.

**Workflow triggers:**
- Push to `main`/`master` branch → Builds and pushes image
- Pull requests → Builds image (no push) for testing
- Manual trigger → Available from Actions tab

**Image location:**
- Registry: `ghcr.io`
- Image: `ghcr.io/rajsumit-2/self-saving:latest`
- Tags: Automatically tagged with branch name, commit SHA, and `latest` for main branch

**Pull and run the built image:**
```bash
docker pull ghcr.io/rajsumit-2/self-saving:latest
docker run -p 5477:5477 ghcr.io/rajsumit-2/self-saving:latest
```

**Workflow features:**
- ✅ Multi-platform support
- ✅ Docker layer caching for faster builds
- ✅ Automatic tagging
- ✅ No manual Docker Desktop required

## Tests

Tests live under `src/test` (unit and integration). To run:

```bash
mvn test
```

Integration tests hit the colon-style paths (`transactions:parse`, `transactions:validator`, etc.).

## Project layout

- `src/main/java/com/blackrock/challenge/` – application, controllers, services, dto, util, config
- `src/test/java/` – unit and integration tests
- `.github/workflows/` – GitHub Actions CI/CD workflows


# cURL examples

Base URL: **http://localhost:5477** (use `-H "X-API-Key: YOUR_KEY"` if `API_KEY` is set).

---

## Discovery & health

```bash
# API overview and endpoint list
curl -s http://localhost:5477/

# Liveness
curl -s http://localhost:5477/health
```

---

## 1. Parse expenses → transactions

```bash
curl -s -X POST http://localhost:5477/blackrock/challenge/v1/transactions:parse \
  -H "Content-Type: application/json" \
  -d '{
    "expenses": [
      { "date": "2023-10-12 20:15:00", "amount": 250 },
      { "date": "2023-02-28 15:49:00", "amount": 375 },
      { "date": "2023-07-01 21:59:00", "amount": 620 },
      { "date": "2023-12-17 08:09:00", "amount": 480 }
    ]
  }'
```

With `timestamp` instead of `date`:

```bash
curl -s -X POST http://localhost:5477/blackrock/challenge/v1/transactions:parse \
  -H "Content-Type: application/json" \
  -d '{"expenses":[{"timestamp":"2023-10-12 20:15:00","amount":250}]}'
```

---

## 2. Validate transactions

```bash
curl -s -X POST http://localhost:5477/blackrock/challenge/v1/transactions:validator \
  -H "Content-Type: application/json" \
  -d '{
    "wage": 50000,
    "maxAmountToInvest": 200000,
    "transactions": [
      { "date": "2023-01-01 10:00:00", "amount": 150, "ceiling": 200, "remanent": 50 },
      { "date": "2023-01-02 10:00:00", "amount": 80, "ceiling": 100, "remanent": 20 }
    ]
  }'
```

Without `maxAmountToInvest` (defaults to min(10% of annual income, 200000)):

```bash
curl -s -X POST http://localhost:5477/blackrock/challenge/v1/transactions:validator \
  -H "Content-Type: application/json" \
  -d '{"wage":50000,"transactions":[{"date":"2023-01-01 10:00:00","amount":100,"ceiling":200,"remanent":100}]}'
```

---

## 3. Filter by q / p / k periods

```bash
curl -s -X POST http://localhost:5477/blackrock/challenge/v1/transactions:filter \
  -H "Content-Type: application/json" \
  -d '{
    "q": [
      { "fixed": 0, "start": "2023-07-01 00:00:00", "end": "2023-07-31 23:59:59" }
    ],
    "p": [
      { "extra": 25, "start": "2023-10-01 08:00:00", "end": "2023-12-31 19:59:59" }
    ],
    "k": [
      { "start": "2023-03-01 00:00:00", "end": "2023-11-30 23:59:59" },
      { "start": "2023-01-01 00:00:00", "end": "2023-12-31 23:59:59" }
    ],
    "transactions": [
      { "date": "2023-10-12 20:15:00", "amount": 250, "ceiling": 300, "remanent": 50 },
      { "date": "2023-02-28 15:49:00", "amount": 375, "ceiling": 400, "remanent": 25 },
      { "date": "2023-07-01 21:59:00", "amount": 620, "ceiling": 700, "remanent": 80 },
      { "date": "2023-12-17 08:09:00", "amount": 480, "ceiling": 500, "remanent": 20 }
    ]
  }'
```

Minimal (no q/p/k):

```bash
curl -s -X POST http://localhost:5477/blackrock/challenge/v1/transactions:filter \
  -H "Content-Type: application/json" \
  -d '{"q":[],"p":[],"k":[],"transactions":[]}'
```

---

## 4. NPS returns

```bash
curl -s -X POST http://localhost:5477/blackrock/challenge/v1/returns:nps \
  -H "Content-Type: application/json" \
  -d '{
    "age": 29,
    "wage": 50000,
    "inflation": 0.055,
    "q": [{ "fixed": 0, "start": "2023-07-01 00:00:00", "end": "2023-07-31 23:59:59" }],
    "p": [{ "extra": 25, "start": "2023-10-01 08:00:00", "end": "2023-12-31 19:59:59" }],
    "k": [
      { "start": "2023-03-01 00:00:00", "end": "2023-11-30 23:59:59" },
      { "start": "2023-01-01 00:00:00", "end": "2023-12-31 23:59:59" }
    ],
    "transactions": [
      { "date": "2023-10-12 20:15:00", "amount": 250, "ceiling": 300, "remanent": 75 },
      { "date": "2023-02-28 15:49:00", "amount": 375, "ceiling": 400, "remanent": 25 },
      { "date": "2023-07-01 21:59:00", "amount": 620, "ceiling": 700, "remanent": 0 },
      { "date": "2023-12-17 08:09:00", "amount": 480, "ceiling": 500, "remanent": 45 }
    ]
  }'
```

---

## 5. Index fund returns

```bash
curl -s -X POST http://localhost:5477/blackrock/challenge/v1/returns:index \
  -H "Content-Type: application/json" \
  -d '{
    "age": 29,
    "inflation": 0.055,
    "q": [],
    "p": [],
    "k": [{ "start": "2023-01-01 00:00:00", "end": "2023-12-31 23:59:59" }],
    "transactions": [
      { "date": "2023-06-01 12:00:00", "amount": 100, "ceiling": 200, "remanent": 100 }
    ]
  }'
```

---

## 6. Performance

```bash
curl -s http://localhost:5477/blackrock/challenge/v1/performance
```

---

## Optional: API key

When the server is started with `API_KEY` set, send it on every API request (except `/` and `/health`):

```bash
export API_KEY=your-secret-key

curl -s -X POST http://localhost:5477/blackrock/challenge/v1/transactions:parse \
  -H "Content-Type: application/json" \
  -H "X-API-Key: $API_KEY" \
  -d '{"expenses":[{"date":"2023-01-01 12:00:00","amount":100}]}'
```

Wrong or missing key returns **401** and `{"error":"Missing or invalid X-API-Key"}`.