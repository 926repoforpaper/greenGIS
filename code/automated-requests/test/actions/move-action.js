const { By, until } = require("selenium-webdriver");
const {
  setUpWaitForMoveRendered,
  waitForMoveRendered,
} = require("../utils/event-listeners");
const {
  setUpWaitForSvgRendered,
  waitForSvgRendered,
} = require("../utils/await-rendering");
const { dispatchMouseEvent } = require("../utils/mouse-events");
const { sleep } = require("../utils/utils");

async function execute(driver, config = {}, logToCsv = console.log) {
  try {
    for (let i = 0; i < (config.repetitions || 1); i++) {
      await setUpWaitForMoveRendered(driver);
      const map = await driver.wait(until.elementLocated(By.id("map")));

      const rect = await map.getRect();
      const centerY = rect.y + rect.height / 2;
      const centerX = rect.x + rect.width / 2;

      // x == 1 movimiento hacia derecha
      const initX =
        config.x == null || config.x == 0
          ? centerX
          : config.x > 0
          ? rect.x + Math.abs(rect.width * config.x) - 1
          : rect.x + 1;

      const finalX =
        config.x == null || config.x == 0
          ? centerX
          : config.x > 0
          ? rect.x + 1
          : rect.x + Math.abs(rect.width * config.x) - 1;

      // y == 1 movimiento hacia arriba
      const initY =
        config.y == null || config.y == 0
          ? centerY
          : config.y > 0
          ? rect.y + 1
          : rect.y + Math.abs(rect.height * config.y) - 1;

      const finalY =
        config.y == null || config.y == 0
          ? centerY
          : config.y > 0
          ? rect.y + Math.abs(rect.height * config.y) - 1
          : rect.y + 1;

      logToCsv(`move-action - finalX: ${finalX} - finalY: ${finalY}`);
      await setUpWaitForSvgRendered(driver);

      // Mock del movimiento para disparar el evento movestart
      await dispatchMouseEvent(driver, map, "mousedown", initX, initY);
      await dispatchMouseEvent(driver, map, "mousemove", initX + 5, initY + 5);
      await dispatchMouseEvent(driver, map, "mouseup", finalX, finalY);

      await sleep(500);

      // Movimiento real
      await dispatchMouseEvent(driver, map, "mousedown", initX, initY);
      await dispatchMouseEvent(driver, map, "mousemove", finalX, finalY);
      await dispatchMouseEvent(driver, map, "mouseup", finalX, finalY);

      await waitForMoveRendered(driver);

      // Leaflet no actualiza elementos canvas tras mover el mapa
      await waitForSvgRendered(driver);

      await sleep(config.sleep || 500);
    }
  } catch (err) {
    console.error(err);
  }
}

module.exports = {
  execute,
};
