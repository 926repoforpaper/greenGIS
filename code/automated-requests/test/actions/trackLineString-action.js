const { By, until } = require("selenium-webdriver");
const { dispatchMouseEvent } = require("../utils/mouse-events");
const fs = require("fs");
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
 * Verifica si un punto (lon, lat) está dentro de los límites actuales del mapa.
 */
async function puntoDentroDeLimites(driver, lon, lat) {
  return driver.executeScript(
    (lon, lat) => {
      const point = window.L.latLng(lat, lon);
      return window.mapDetails.bounds.contains(point);
    },
    lon,
    lat
  );
}

async function execute(driver, config = {}, logToCsv = console.log) {
  try {
    const routePoints = JSON.parse(fs.readFileSync(config.filePath)).route;

    const map = await driver.wait(until.elementLocated(By.id("map")));
    const rect = await map.getRect();
    const centerX = rect.width / 2;
    const centerY = rect.height / 2;

    for (let i = 0; i < routePoints.length; i++) {
      logToCsv(`trackLineString-action - point: ${i} of ${routePoints.length}`);
      const [targetLon, targetLat] = routePoints[i];

      // Verificar si el punto objetivo (targetLon, targetLat) está en el viewport
      let pointVisible = await puntoDentroDeLimites(
        driver,
        targetLon,
        targetLat
      );

      if (pointVisible) {
        continue;
      }

      // Mueve el viewport hacia el punto actual de la ruta hasta que esté visible
      while (!pointVisible) {
        // Obtener los detalles actuales del mapa
        const mapDetails = await getMapDetails(driver);
        const northEast = mapDetails.bounds._northEast;
        const southWest = mapDetails.bounds._southWest;

        // Calcular el centro actual del viewport en latitud y longitud
        const currentLon = (northEast.lng + southWest.lng) / 2;
        const currentLat = (northEast.lat + southWest.lat) / 2;

        // Calcular la diferencia de coordenadas entre el centro actual y el punto objetivo
        const deltaLon = targetLon - currentLon;
        const deltaLat = targetLat - currentLat;

        // Normalizar los deltas para calcular el movimiento en la dirección deseada
        const magnitude = Math.sqrt(deltaLon * deltaLon + deltaLat * deltaLat);
        const moveX = ((deltaLon / magnitude) * -rect.width) / 2; // Desplazamiento horizontal
        const moveY = ((deltaLat / magnitude) * rect.height) / 2; // Desplazamiento vertical

        logToCsv(`trackLineString-action - moveX: ${moveX} - moveY: ${moveY}`);
      
        await setUpWaitForMoveRendered(driver);
        await setUpWaitForSvgRendered(driver);

        await dispatchMouseEvent(driver, map, "mousedown", centerX, centerY);
        await dispatchMouseEvent(
          driver,
          map,
          "mousemove",
          centerX + 5,
          centerY + 5
        );
        await dispatchMouseEvent(
          driver,
          map,
          "mouseup",
          centerX + 5,
          centerY + 5
        );
        await sleep(500);

        // Simular un "click and drag" en la dirección deseada
        await dispatchMouseEvent(driver, map, "mousedown", centerX, centerY);
        await dispatchMouseEvent(
          driver,
          map,
          "mousemove",
          centerX + moveX,
          centerY + moveY
        );
        await dispatchMouseEvent(
          driver,
          map,
          "mouseup",
          centerX + moveX,
          centerY + moveY
        );

        await waitForMoveRendered(driver);
        await waitForSvgRendered(driver);

        // Recalcular si el punto objetivo ya está visible
        pointVisible = await puntoDentroDeLimites(driver, targetLon, targetLat);
        await sleep(config.sleep || 500);
      }
    }
  } catch (err) {
    console.error(err);
  }
}

module.exports = {
  execute,
};
