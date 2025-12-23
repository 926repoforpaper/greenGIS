# Energy Measurement WebApp

This project consists of a web application that has a single codebase and four possible deployment configurations. These configurations are set up to be launched simultaneously or individually, depending on the needs of the production or development environment.

## Project Structure

The project has the following directory structure:

```
.
├── client/                # Source code for the client (Frontend)
├── deploy/                # Deployment files and configurations
│   ├── docker-compose.yml # Main Docker Compose configuration file
│   ├── nginx/             # Nginx configuration for routing and reverse proxy
│   ├── postgres/          # PostgreSQL service and configuration
│   ├── product-1/         # Configuration for product 1
│   ├── product-2/         # Configuration for product 2
│   ├── product-3/         # Configuration for product 3
│   ├── product-4/         # Configuration for product 4
│   ├── product-5/         # Configuration for product 5
└── server/                # Source code for the server (Backend)
```

## Deployment configuration

Before running this application, you need to define the client’s deployment URL in multiple configuration files.

For each product (product-x, representing product-1, product-2, etc.), replace 'client-url' with the actual deployment URL in the following files:

- /deploy/product-x/.env.production
- /deploy/product-x/.env

Updating these values ensures the application points to the correct client environment.

## Simultaneous or Individual Deployment

This project allows you to deploy all product configurations simultaneously, or deploy each product individually as needed.

### Simultaneous Deployment

To deploy all applications simultaneously, simply run the following command from the deploy directory:

```bash
docker-compose up
```
This command will start all the services defined in the docker-compose.yml file, including:
- PostgreSQL: A shared database for all products.
- Geoserver: A shared Geoserver service for handling geospatial data.
- The individual services for each product (product-1, product-2, product-3, product-4).

### Individual Deployment

If you only want to deploy a specific product, you can run the containers for that product individually. For example, to deploy product-1, use the following command:

```
docker-compose up -d --build p1-server p1-nginx postgres
```

## Products

### Product 1

The client for this product is configured to load cached GeoJSON layers both on the server-side, so there's no need to query the database every time the data is needed, and on the client-side.

To run this product:

```
docker-compose up -d --build p1-server p1-nginx postgres
```

### Product 2

The client for this product is configured to work with WMS layers retrieved from the deployed Geoserver instance.

To run this product: 

```
docker-compose up -d --build p2-server p2-nginx postgres geoserver
```

### Product 3

The client for this product is configured to load paginated GeoJSON layers based on the bounding box covered by the client's viewport. This means the server must be queried for the data visible to the user every time the user moves around the map.

To run this project: 

```
docker-compose up -d --build p3-server p3-nginx postgres
```

### Product 4

The client for this product is configured to load three WMS layers through Geoserver, and one cached GeoJSON layer, both on the server-side and the client-side.

To run this project:

```
docker-compose up -d --build p4-server p4-nginx postgres geoserver
```

### Product 5

The client for this product is configured to load GeoJSON layers based on the bounding box covered by the client's viewport, with GeoJSON string being generated from PostGIS instead creating the object in memory. This means the server must be queried for the data visible to the user every time the user moves around the map.

To run this project: 

```
docker-compose up -d --build p5-server p5-nginx postgres
```