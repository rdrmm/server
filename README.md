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

## Heartbeat Sensor (HBS)
Heartbeat Sensor, HBS for short, is a Spring microservice whose only purpose
in life is to receive heartbeat packets from agents then write these to redis.
HBS listens over http for a packet bearing a simple auth token in its header,
and having a body that resembles:
{
"agent_uuid": "UUID",
"hostname": "",
"cpu":"percent utilized",
"mem":"percent utilized",
"disk":"percent utilized each drive"
}
upon receiving the payload and authenticating the token the spring server will
commit the heartbeat payload to redis.

## Heartbeat Writer (HBW)
Heartbeat Writer, HBW for short, is a Spring microserve who reads the last
minute's heartbeats from redis and batch writes them to postgresql.

## Running the Services

To run the development environment, use Docker Compose:

```bash
docker-compose up --build
```

This will start all services: config-server on port 8888, redis on 6379, postgres on 5432, hbs on 8081, hbw on 8082.

To test HBS, send a POST request to `http://localhost:8081/api/heartbeat` with header `Authorization: Bearer valid-token` and JSON body as described.