const { By, until } = require("selenium-webdriver");
const {
  waitForZoomRendered,
  setUpWaitForZoomRendered,
} = require("../utils/event-listeners");
const {
  setUpWaitForSvgRendered,
  waitForSvgRendered,
  waitForCanvasRender,
} = require("../utils/await-rendering");
const { sleep } = require("../utils/utils");

async function execute(driver, config = {}, logToCsv = console.log) {
  try {
    for (let i = 0; i < (config.repetitions || 1); i++) {
      await setUpWaitForZoomRendered(driver);
      await setUpWaitForSvgRendered(driver);

      let zoomOutBtn = await driver.wait(
        until.elementLocated(By.css("a.leaflet-control-zoom-out"))
      );
      await driver.wait(until.elementIsEnabled(zoomOutBtn));

      await zoomOutBtn.click();
      logToCsv(`zoomOut-action`);

      await waitForZoomRendered(driver);
      await waitForSvgRendered(driver);
      await waitForCanvasRender(driver);

      await sleep(config.sleep || 500);
    }
  } catch (err) {
    console.error(err);
  }
}

module.exports = {
  execute,
};
