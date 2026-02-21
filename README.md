# server
Server components of rdRMM infrastructure with which agents communicate with.

## Repository Structure and project infrastructure
This 'server' repository is used to organize multiple components each within
their own folder structure. The Docker-Compose file at the root of this repo
is provided to quickly spin up a development environment.

### Repository Contents

## Config Server
Config Server is a Spring Cloud Config Server that provides centralized configuration
for all microservices in the rdRMM infrastructure.

## Heartbeat Service
The Heartbeat Service is a unified Spring microservice that handles both receiving and processing heartbeat data from agents. It combines the functionality of the previous Heartbeat Sensor (HBS) and Heartbeat Writer (HBW) into a single application.

### Functionality
The Heartbeat Service:
1. **Receives heartbeat packets** from agents over HTTP POST requests to `/api/heartbeat`
   - Requires a bearer token in the Authorization header
   - Accepts JSON payloads with agent metrics (UUID, hostname, CPU, memory, disk usage)
   - Temporarily stores heartbeats in Redis

2. **Periodically writes heartbeats** from Redis to PostgreSQL
   - Runs every 60 seconds
   - Transforms Redis data and persists to the heartbeats table
   - Cleans up processed entries from Redis

### Configuration
- Listens on port 8080
- Integrates with Spring Cloud Config Server for centralized configuration
- Uses Redis for temporary heartbeat storage
- Uses PostgreSQL for persistent storage

## UI
Web UI built with FastAPI and Jinja2 templates to display heartbeat data.
Provides a simple dashboard to view agent heartbeats.

## Running the Services

To run the development environment, use Docker Compose:

```bash
docker-compose up --build
```

This will start all services: config-server on port 8888, redis on 6379, postgres on 5432, heartbeat on 8080, and ui on 8000.

To test the Heartbeat Service, send a POST request to `http://localhost:8080/api/heartbeat` with header `Authorization: Bearer valid-token` and JSON body as described above.

Example:
```bash
curl -X POST http://localhost:8080/api/heartbeat \
  -H "Authorization: Bearer valid-token" \
  -H "Content-Type: application/json" \
  -d '{"agentUuid":"test-uuid-1","hostname":"test-host","cpu":"5%","mem":"60%","disk":{"C:":"20%","D:":"10%"}}'
```
