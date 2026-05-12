### fuhrpark

## description

FuhrPark is a fleet management backend built with Java 21 and Spring Boot.

The system manages trucks, drivers, trips, fleet statistics and latest truck locations received through a raw TCP tracking module. It combines a standard REST API with a small networking extension.

## architecture

FuhrPark is a Spring Boot application connected to PostgreSQL. The main application exposes REST endpoints for fleet management, while a separate TCP receiver listens for truck location updates on port `9090`.

The REST API runs on port `8080`. The TCP tracking receiver accepts newline-delimited JSON messages and updates the latest known location of an active truck.

![architecture](docs/fuhrpark-architecture.svg)

## domain model

The main domain areas are:

- `Truck` - fleet vehicle with license plate, brand, capacity, and active status
- `Driver` - driver assigned to trips
- `Trip` - planned or completed transport route
- `TruckLocation` - latest known location received from the TCP tracking module

Business rules are enforced in the service layer. A truck or driver cannot be assigned to more than one active trip, inactive resources cannot be used for new assignments, and trip status changes must follow the defined lifecycle.

![domain-model](docs/fuhrpark-domain-model.svg)

## trip lifecycle

Trips move through a controlled state machine:

- `PLANNED`
- `IN_PROGRESS`
- `COMPLETED`
- `CANCELED`

Only selected transitions are allowed. This prevents invalid updates such as completing a trip that was never started or modifying a canceled trip as if it were still active.

![trip-lifecycle](docs/fuhrpark-trip-lifecycle.svg)

## TCP truck tracking

The app includes a raw TCP tracking receiver for latest truck location updates.

The application listens on port `9090` and accepts newline-delimited JSON messages from truck devices or simulators.

Example message:

```json
{"truckId":"f05c1aee-6e6f-4e46-a855-581fdb284880","lat":52.2297,"lon":21.0122,"speed":72.5,"timestamp":"2026-05-12T10:30:00Z"}
```

Each message is validated before saving:

- truck ID must exist,
- truck must be active,
- latitude must be between `-90` and `90`,
- longitude must be between `-180` and `180`,
- speed cannot be negative,
- timestamp cannot be far in the future.

The system stores only the latest location per truck. 
Latest truck location can be read through REST:

```http
GET /trucks/{id}/location
```

![tcp-tracking](docs/fuhrpark-tcp-tracking.svg)

## REST API

FuhrPark exposes REST endpoints for managing fleet resources and reading operational data.

Main API areas:

- trucks
- drivers
- trips
- fleet statistics
- latest truck location

Swagger UI is available after startup:

```text
http://localhost:8080/swagger-ui/index.html
```

![rest-api](docs/fuhrpark-rest-api.svg)

## trip filtering

The `/trips` endpoint supports optional filtering by trip status, driver, and truck. Results are returned with pagination.

Example:

```http
GET /trips?status=PLANNED&driverId={driverId}&truckId={truckId}&page=0&size=10
```

## reporting

FuhrPark includes a fleet statistics endpoint that aggregates operational data across drivers, trucks, and trips.

The response includes:

- active trucks
- active drivers
- trips grouped by status
- completed distance
- most active driver
- most used truck


## soft delete

Drivers and trucks are not physically removed from the database. Instead, they are marked as inactive and excluded from active operations.

This preserves historical trip data while preventing inactive resources from being assigned to new work.

## docker

Docker Compose starts the complete local runtime environment:

- Spring Boot application,
- PostgreSQL database,
- REST API port `8080`,
- TCP tracking port `9090`.

Run the system:

```bash
docker compose up -d --build
```

Available services:

```text
REST API:      http://localhost:8080
Swagger UI:    http://localhost:8080/swagger-ui/index.html
TCP tracking:  localhost:9090
PostgreSQL:    localhost:5432
```

Stop the system:

```bash
docker compose down -v
```

![docker-runtime](docs/fuhrpark-docker-runtime.svg)

## kubernetes

The `k8s/` directory contains a minimal Kubernetes deployment.

The containerized application can be described in a cluster-style environment with:

- `Deployment`
- `Service`
- `ConfigMap`
- `Secret`

The service exposes both application ports:

- HTTP REST API on container port `8080`
- TCP tracking receiver on container port `9090`

This is a local/cloud-readiness example, not a production Kubernetes setup.

![kubernetes](docs/fuhrpark-kubernetes.svg)

## CI pipeline

GitHub Actions validates the project on every push and pull request.

The pipeline:

- sets up Java 21
- runs Maven tests
- builds the Spring Boot jar
- builds the Docker image

![ci-pipeline](docs/fuhrpark-ci-pipeline.svg)

## testing

The project includes unit and integration tests.

Test coverage focuses on:

- domain rules
- service-layer validation
- trip lifecycle behavior
- latest truck location updates
- REST endpoint behavior
- application context startup

Run tests locally:

```bash
./mvnw test
```

On Windows:

```powershell
.\mvnw.cmd test
```

## tech stack

[![My Stack](https://skillicons.dev/icons?i=java,spring,postgres,docker,kubernetes,githubactions,maven&theme=light)](https://skillicons.dev)

