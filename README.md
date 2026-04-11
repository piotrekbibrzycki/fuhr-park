### fuhrpark

## description

FuhrPark is a fleet management backend built as a Spring Boot monolith for managing drivers, trucks, and trips.

The project focuses on domain rules such as trip assignment constraints, trip status transitions, soft delete, validation, pagination, and fleet statistics.

## architecture

FuhrPark is a Spring Boot application connected to a single PostgreSQL database. Its main domain areas are drivers, trucks, and trips, with business rules enforced directly in the service layer.

![architecture](docs/fuhrpark-architecture.svg)

## domain rules

The project extends basic CRUD by enforcing operational constraints inside the application.

A truck cannot be assigned to more than one active trip, a driver cannot be assigned to more than one active trip, trip status changes must follow the defined workflow, and inactive resources cannot be used in new assignments.


## trip lifecycle

Trips move through a simple state machine based on four statuses: `PLANNED`, `IN_PROGRESS`, `COMPLETED`, and `CANCELED`. Only selected transitions are allowed, which helps keep the workflow consistent and prevents invalid updates.

![trip-lifecycle](docs/fuhrpark-trip-lifecycle.svg)

## soft delete

Drivers and trucks are not physically removed from the database. Instead, they are marked as inactive and excluded from active operations. This preserves historical data while preventing inactive resources from being assigned or updated as if they were still available.


## reporting

FuhrPark includes a fleet statistics endpoint that aggregates operational data across drivers, trucks, and trips. The response covers active resources, trips grouped by status, completed distance, and simple ranking-style metrics such as the most active driver and truck.

## API documentation

After starting the application, Swagger UI is available at `http://localhost:8080/swagger-ui/index.html`.
