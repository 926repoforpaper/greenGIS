const { By, until } = require("selenium-webdriver");
const { dispatchMouseEvent } = require("../utils/mouse-events");
const {
  setUpWaitForMoveRendered,
  waitForMoveRendered,
} = require("../utils/event-listeners");
const { sleep, getMapDetails } = require("../utils/utils");
const {
  setUpWaitForSvgRendered,
  waitForSvgRendered,
} = require("../utils/await-rendering");

/**
 * Verifica si un punto (lon, lat) está en el centro del mapa.
 */
async function puntoEnElCentroDelMapa(driver, lon, lat) {
  return driver.executeScript(
    (lon, lat) => {
      const center = window.mapDetails.bounds.getCenter();
      const point = window.L.latLng(lat, lon);
      const tolerance = 0.1;
      return Math.abs(center.lat - point.lat) < tolerance && Math.abs(center.lng - point.lng) < tolerance;
    },
    lon,
    lat
  );
}

async function execute(driver, config = {}, logToCsv = console.log) {
  try {
    targetLon = config.center[1];
    targetLat = config.center[0];
    const map = await driver.wait(until.elementLocated(By.id("map")));
    const rect = await map.getRect();
    const centerX = rect.width / 2;
    const centerY = rect.height / 2;

    // Verificar si el punto objetivo (targetLon, targetLat) está en el viewport
    let pointEnCentro = await puntoEnElCentroDelMapa(
      driver,
      targetLon,
      targetLat
    );
    if (pointEnCentro) {
      return;
    }

    let mapDetails = await getMapDetails(driver);
    let northEast = mapDetails.bounds._northEast;
    let southWest = mapDetails.bounds._southWest;

    let currentLon = (northEast.lng + southWest.lng) / 2;
    let currentLat = (northEast.lat + southWest.lat) / 2;

    let scaleX = rect.width / (northEast.lng - southWest.lng);
    let scaleY = rect.height / (northEast.lat - southWest.lat);

    let deltaLon = targetLon - currentLon;
    let deltaLat = targetLat - currentLat;

    let moveX = deltaLon * scaleX;
    let moveY = -deltaLat * scaleY;
    logToCsv(`moveTo-action - moveX: ${moveX} - moveY: ${moveY}`);
    
    await setUpWaitForMoveRendered(driver);
    await setUpWaitForSvgRendered(driver);

    await dispatchMouseEvent(driver, map, "mousedown", centerX, centerY);
    await dispatchMouseEvent(driver, map, "mousemove", centerX + 5, centerY + 5);
    await dispatchMouseEvent(driver, map, "mouseup", centerX + 5, centerY + 5);

    await sleep(500);
    
    await dispatchMouseEvent(driver, map, "mousedown", centerX, centerY);
    await dispatchMouseEvent(driver, map, "mousemove", centerX + moveX, centerY + moveY);
    await dispatchMouseEvent(driver, map, "mouseup", centerX + moveX, centerY + moveY);
    
    await waitForMoveRendered(driver);
    await waitForSvgRendered(driver);

    await sleep(config.sleep || 500);

    mapDetails = await getMapDetails(driver);
    northEast = mapDetails.bounds._northEast;
    southWest = mapDetails.bounds._southWest;
    currentLon = (northEast.lng + southWest.lng) / 2;
    currentLat = (northEast.lat + southWest.lat) / 2;

  } catch (err) {
    console.error(err);
  }
}

module.exports = {
  execute,
};
