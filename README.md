# Warehouse API

### Required Dependencies

- Docker

### Improvements

- Input validation for the routes
- Add Swagger file
- Centralized logging
- Metrics
- Authentication/Authorization
- Vault for password management
- Helmchart and deployment

### Stack

- Quarkus
- Ktormrun.sh
- PostgreSQL
- JWT for authentication / authorization

### Execute

```
- docker-compose up
- ./mvnw quarkus:dev
```