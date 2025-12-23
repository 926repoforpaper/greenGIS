const fs = require("fs");
const { getChromeWebDriver, monitorLogs } = require("./utils/chrome-webdriver");
const { sleep, createLogger } = require("./utils/utils");
const {
  setupWaitForInitialLayersLoad,
  waitForInitialLayersLoad,
  setUpWaitForMoveRendered,
  waitForMoveRendered,
} = require("./utils/event-listeners");
const {
  setUpWaitForSvgRendered,
  waitForSvgRendered,
} = require("./utils/await-rendering");
const ActionsFactory = require("./actions/actions-factory");

describe("Leaflet Map", function () {
  let driver;
  const args = require("minimist")(process.argv.slice(2));
  const specFile = args.specFile || "./testSpec_1_reallyclose.json";
  let spec = JSON.parse(fs.readFileSync(specFile, "utf8"));
  this.timeout(3000000);
  const logToCsv = createLogger();

  before(async function () {
    logToCsv("Starting driver");
    driver = await getChromeWebDriver(spec.headless);
    logToCsv("Driver started");
    // Uncomment the following line to monitor browser logs
    // monitorLogs(driver);
    await driver.manage().deleteAllCookies();
    logToCsv("Cookies deleted");
  });

  after(async () => await driver.quit());

  it("Interactuar con mapa", async function () {
    try {
      logToCsv("We are going to test " + spec.URL);
      logToCsv("Setup wait for layers to load");
      await setupWaitForInitialLayersLoad(driver, spec.center);
      logToCsv("Getting URL");      
      await driver.get(spec.URL);
      if (spec.center != null) {
        logToCsv("Setup wait for move rendered");
        await setUpWaitForMoveRendered(driver);
        logToCsv("Setup wait for SVG rendered");
        await setUpWaitForSvgRendered(driver);
      }
      logToCsv("Waiting for initial layers load");
      await waitForInitialLayersLoad(driver);

      if (spec.center != null) {
        logToCsv("Waiting for move rendered");
        await waitForMoveRendered(driver);
        logToCsv("Waiting for SVG rendered");
        await waitForSvgRendered(driver);
        await sleep(500);
      }
      logToCsv("Executing actions");
      for (const action of spec.actions) {
        const actionF = ActionsFactory.get(action.name);
        logToCsv("Executing: " + action.name);
        await actionF.execute(driver, action.config, logToCsv);

        await sleep(500);
      }
      logToCsv("Waiting at the end");
      if (!spec.headless) {
        await sleep(20000);
      }
    } catch (err) {
      console.error(`[${new Date().toISOString()}]`, err);
    }
  });
});
