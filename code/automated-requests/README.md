# Automated requests

This project automates interactions with a web map viewer developed in Leaflet using Selenium and Node.js.

## Getting started

To execute the tests, you need Node.js installed on your machine. Use the following commands to set up the environment and run the tests:

```
nvm use
npm install
npm test
```

Alternatively, you can also execute the tests in a Docker container to ensure compatibility and eliminate setup on your local environment. See the Docker instructions below.

## Docker Execution

To run the tests in Docker:

```bash
docker build -t selenium-tests .
docker run --rm selenium-tests
```

To run the tests using the host files instead of the container files:

```bash
docker run --rm -v .:/program -w /program selenium-tests
```

Be aware that the first time you build the Docker image, the process may take a while. This is because it involves downloading and installing Chrome within the Docker container, which can be time-consuming. However, subsequent builds will be significantly faster.

## Actions

Actions represent the core test interactions and are modular scripts that define specific behaviors within the web map viewer. Each action is stored in the [/actions](/test/actions/) directory and follows the naming convention 'action-name'-action.js. 

#### Action Definition

Each action should export a function `execute(webDriver, config = {})`. This function is used to perform a specific task, such as clicking, dragging, or interacting with map elements. 


#### Using Actions

The [actions-factory.js](/test/actions/actions-factory.js) simplifies action management. It allows you to dynamically load and execute actions in your test scripts:

```js
const ActionsFactory = require("./actions/actions-factory");

const action = ActionsFactory.get('action-name');
await action.execute(driver, {});
```

## Test Sepecification

The default test ([default_test](/test/default_test.js)) reads its configuration from [testSpec.json](./testSpec.json). This JSON file defines the steps and parameters for the end-to-end tests. The following structure is required:

- URL: The URL of the web application where the tests will be executed.
- headless: A flag indicating whether to run the browser in headless mode.
- actions: An array of actions to be performed. Each object in the array should have two attributes:
    - name: The name of the action, matching the file in the [/actions](/test/actions/) directory.
    - config: A configuration object passed as the second parameter to the execute function.

The actions are executed sequentially, in the order they are defined, within the following loop:

```js
for (const action of spec.actions) {
    const actionF = ActionsFactory.get(action.name);
    await actionF.execute(driver, action.config);
}
```

## Implemented actions

Below is a list of currently implemented actions, along with a brief description of their purpose and configuration options:

#### Move

The **Move** action simulates dragging the map within the viewer, allowing for controlled horizontal and vertical panning. The movement parameters are configurable and determine both the direction and the magnitude of the drag. 

**Configuration parameters**:

- **x**: Controls the horizontal movement. 
    - Positive values (up to 1) move the map to the right.
    - Negative values (down to -1) move the map to the left.
    - 0 or null results in no horizontal movement.
    - The magnitude of x determines the proportion of the map's width (client's viewport) covered during the movement.

- **y**: Controls the vertical movement.
    - Positive values (up to 1) move the map upward.
    - Negative values (down to -1) move the map downward.
    - 0 or null results in no vertical movement.
    - The magnitude of y determines the proportion of the map's height covered during the movement.

- **repetitions**: Number of times the movement should be executed consecutively. Defaults to 1.

- **sleep**: Time in milliseconds to wait after each repetition. Defaults to 0.


### ZoomIn and ZoomOut

The **ZoomIn** and **ZoomOut** actions simulate clicking the respective zoom buttons on the map viewer.

**Configuration parameters**:

- **repetitions**: Number of times the movement should be executed consecutively. Defaults to 1.

- **sleep**: Time in milliseconds to wait after each repetition. Defaults to 0.

### TrackLineString

The **TrackLineString** action simulates following a series of geographic coordinates (a route defined as in [reoute.json](./routes/route.json)) on the map.

**Configuration parameters**:

- **repetitions**: Number of times the movement should be executed consecutively. Defaults to 1.

- **sleep**: Time in milliseconds to wait after each movement. Defaults to 0.

- **filePath**: Path to the file containing the route.