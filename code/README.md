# Code Repository for GreenGIS Project

This repository contains the source code and supporting tooling for the **GreenGIS** project, which is composed of two main components: an automated testing suite and a multi-product web application. Each component is designed to be modular, reproducible, and deployable using containerized environments.

---

## Index

- [Repository Structure](#repository-structure)
- [automated-requests](#automated-requests)
- [products](#products)
  - [Main Structure](#main-structure)
  - [Deployment with Docker Compose](#deployment-with-docker-compose)
  - [Available Products](#available-products)
  - [Pre-deployment Configuration](#pre-deployment-configuration)
- [Additional Notes](#additional-notes)
- [Authors](#authors)

---

## Repository Structure

The project is organized into the following top-level directories:

- **automated-requests**: End-to-end automated tests implemented with Selenium, targeting the Leaflet-based map viewer.
- **products**: A full web application consisting of a Vue.js frontend and a Java backend, supporting multiple product variants and deployment configurations.

---

## automated-requests

This module contains automated end-to-end tests that simulate user interactions with the map viewer. The tests are designed to validate map behavior, layer loading, and general application stability from a user perspective.

### Features

- Selenium-based browser automation.
- Can be executed locally or inside a Docker container.
- Uses Node.js for dependency management and test execution.

### Requirements

- **Node.js** (the version is managed via `nvm`; use `nvm use` to select the correct version).
- **Docker** (optional, recommended for isolated and reproducible executions).

### Local Execution

To run the automated tests locally:

```bash
cd automated-requests
nvm use
npm install
npm test
```

### Docker Execution

To build and run the tests inside a Docker container:

```bash
cd automated-requests
docker build -t selenium-tests .
docker run --rm selenium-tests
```

To execute the tests using local files instead of the files baked into the container image:

```bash
docker run --rm -v .:/program -w /program selenium-tests
```

> **Note:** The first Docker build may take longer than usual due to the installation of Google Chrome and related dependencies.

---

## products

This module contains the main web application. A single shared codebase is used to support multiple product configurations, each with different data sources, map layers, and deployment characteristics.

### Main Structure

More information about the web application can be found in the `products/README.md` file. The main components of the web application are:

- `client/`: Vue.js frontend application.
- `server/`: Java backend built with Gradle.
- `deploy/`: Docker Compose files and per-product configuration, including services such as Nginx, PostgreSQL, and GeoServer.

### Deployment with Docker Compose

All services can be started together from the `products/deploy` directory:

```bash
cd products/deploy
docker-compose up
```

To build and start services for a specific product only (example: `product-1`):

```bash
docker-compose up -d --build p1-server p1-nginx postgres
```

### Available Products

The application supports multiple product variants, each demonstrating a different approach to geospatial data delivery:

- **product-1**: Cached GeoJSON layers on both client and server.
- **product-2**: WMS layers served from GeoServer.
- **product-3**: GeoJSON layers paginated by bounding box.
- **product-4**: Hybrid approach combining WMS (GeoServer) and cached GeoJSON.
- **product-5**: GeoJSON dynamically generated from PostGIS based on bounding box queries.

### Pre-deployment Configuration

For each product, client deployment URLs must be configured before running the application. Update the following files accordingly:

- `deploy/product-x/.env`
- `deploy/product-x/.env.production`

Replace the placeholder value (`client-url`) with the appropriate deployment URL for the client application.

---

## Additional Notes

- Refer to the individual README files inside `automated-requests/` and `products/` for more detailed, component-specific documentation.
- Depending on the selected Docker Compose configuration, services such as PostgreSQL and GeoServer may be shared across multiple products.
- Docker is the recommended execution environment to ensure consistency across development, testing, and deployment.

---

## Authors

- Victor Lamas <victor.lamas@udc.es>
- Miguel Luaces <miguel.luaces@udc.es>
- Erick Morales Pombo <erick.morales.pombo@udc.es>
