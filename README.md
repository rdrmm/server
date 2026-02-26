# server
Server components of rdRMM infrastructure with which agents communicate.

## Repository Structure and project infrastructure
This 'server' repository is used to organize multiple components each within
their own folder structure. The Docker-Compose file at the root of this repo
is provided to quickly spin up a development environment.

### Protocol
The protocol is kept simple by design:
Agent sends an HTTP POST to the heartbeat endpoint containing a json heartbeat
The server responds with the taskId and URL (or reference to) the next task.

### Repository Contents

## Eureka
Spring service discovery

## API Gateway
Spring API gateway with automatic route discovery

## Config Server
Config Server is a Spring Cloud Config Server that provides centralized
configuration for all microservices in the rdRMM infrastructure.

## Heartbeat Service
The Heartbeat Service is a Spring microservice that handles both receiving and
processing heartbeat data from agents.

### Heartbeat Service Functionality
1. **Receives heartbeat packets** from via POST requests to `/api/heartbeat`
   - Requires a bearer token in the Authorization header
   - Accepts JSON payload containing last task info and basic info/metrics:
      - UUID
      - hostname
      - CPU%
      - memory%
      - disk usage%
      - last task ID
      - last task logs
         - stdout
         - stderr
   - Temporarily stores heartbeats in Redis
   - Upon validating agent identity respond with pending task number and url

2. **Periodically writes heartbeats** from Redis to PostgreSQL
   - Runs every 60 seconds (how to deal with multiple instances?)
   - Transforms Redis data and persists to the heartbeats table
   - Cleans up processed entries from Redis

### Configuration
- Listens on port 8080
- Integrates with Spring Cloud Config Server for centralized configuration
- Uses Redis for temporary heartbeat storage
- Uses PostgreSQL for persistent storage

## Tasks Service
The Tasks Service is a Spring microservice that manages task assignments for
agents using Redis as a distributed cache. Tasks queue in postgresql using
an 'order' column, with other columns including the task id (uuid), the task
status (queued, scheduled, success, failure), url, and timestamps for created,
scheduled, started, and ended.
The UI is responsible for populating the postgresql database with tasks, and
the tasks microservice is responsible for scheduling tasks with lowest order
by placing them into redis and marking their status as scheduled.

### Functionality
The Tasks Service manages the association between agents and task URLs:vm
## API Endpoints
1. **Submit task assignments** via `POST /api/tasks`
   - Maps an agentUuid to a taskUrl
   - Stores bidirectional mappings for quick lookups
   
2. **Query task for agent** via `GET /api/tasks/{agentUuid}`
   - Returns the taskUrl assigned to a specific agent
   
3. **Query agents for task** via `GET /api/tasks/url-agents/{taskUrl}`
   - Returns all agentUuids assigned to a specific task URL
   
4. **List all task URLs** via `GET /api/tasks/urls/all`
   - Returns all task URLs currently in the system
   
5. **Delete task assignment** via `DELETE /api/tasks/{agentUuid}`
   - Removes the task assignment for an agent
   - Cleans up reverse mappings automatically

### Configuration
- Listens on port 8081
- Integrates with Spring Cloud Config Server for centralized configuration
- Uses Redis for all data storage (in-memory key-value pairs)

## UI
Web UI built with FastAPI and Jinja2 templates to display heartbeat data.
Provides a simple dashboard to view agent heartbeats.

## Running the Services

To run the development environment, use Docker Compose:

```bash
docker-compose up --build
```

This will start all services: config-server on port 8888, redis on 6379, postgres
on 5432, heartbeat on 8080, tasks on 8081, and ui on 8000.

To test the Heartbeat Service, send a POST request to
`http://localhost:8080/api/heartbeat`
with header
`Authorization: Bearer valid-token`
and JSON body as described above.

Example:
```bash
curl -X POST http://localhost:8080/api/heartbeat \
  -H "Authorization: Bearer valid-token" \
  -H "Content-Type: application/json" \
  -d '{"agentUuid":"test-uuid-1","hostname":"test-host","cpu":"5%","mem":"60%","disk":{"C:":"20%","D:":"10%"}}'
```

## Tasks Service Examples

To submit a task assignment:
```bash
curl -X POST http://localhost:8081/api/tasks \
  -H "Content-Type: application/json" \
  -d '{"agentUuid":"agent-1","taskUrl":"http://example.com/task/1"}'
```

To get the task URL for an agent:
```bash
curl http://localhost:8081/api/tasks/agent-1
```

To get all agents assigned to a task URL:
```bash
curl "http://localhost:8081/api/tasks/url-agents?taskUrl=http://example.com/task/1"
```

To list all task URLs:
```bash
curl http://localhost:8081/api/tasks/urls/all
```

To delete a task assignment:
```bash
curl -X DELETE http://localhost:8081/api/tasks/agent-1
```
