// Funcion usada para esperar por el re-renderizado de las capas geojson
/**
 * Configura un observador para detectar cambios en elementos <svg>.
 */
async function setUpWaitForSvgRendered(driver) {
  return driver.executeScript(() => {
    window.svgRenderedFired = false;

    if (window.svgObservers && Array.isArray(window.svgObservers)) {
      window.svgObservers.forEach((observer) => observer.disconnect());
    }
    window.svgObservers = [];

    const svgElements = document.querySelectorAll("svg");
    const validSvgElements = Array.from(svgElements).filter((svg) =>
      svg.querySelector("g")
    );

    if (validSvgElements.length === 0) {
      window.svgRenderedFired = true;
      return;
    }

    let pendingObservations = validSvgElements.length;

    validSvgElements.forEach((svgElement) => {
      const observer = new MutationObserver(() => {
        pendingObservations--;
        observer.disconnect();

        if (pendingObservations === 0) {
          window.svgRenderedFired = true;
        }
      });

      const config = {
        attributes: true,
        childList: true,
        subtree: true,
      };

      observer.observe(svgElement, config);
      window.svgObservers.push(observer); // Agregar el observador a la lista
    });
  });
}

async function waitForSvgRendered(driver) {
  return driver.wait(async () => {
    const svgRendered = await driver.executeScript(() => {
      return window.svgRenderedFired || false;
    });
    return svgRendered === true;
  });
}

// Funcion usada para esperar por el re-renderizado de las capas geoserver
/**
 * La funcion solo detecta cambios al producirse un cambio de zoom.
 * Cuando se producen desplazamientos por el mapa no siempre se dan cambios a nivel de
 * hml.
 */
async function waitForCanvasRender(driver) {
  await driver.executeAsyncScript((callback) => {
    const tileContainers = document.querySelectorAll(".leaflet-tile-container");

    // Filtra los tileContainers para verificar si su padre tiene el atributo 'style' con opacidad
    const filteredTileContainers = Array.from(tileContainers).filter(
      (container) => {
        const parent = container.parentElement;
        return (
          parent &&
          parent.getAttribute("style") &&
          parent.getAttribute("style").includes("opacity")
        );
      }
    );

    if (filteredTileContainers.length === 0) {
      callback(); // Si no hay contenedores válidos, resuelve
    }

    let observersCount = 0;
    const observerCallback = (mutationsList, observer) => {
      observersCount--;

      if (observersCount === 0) {
        observer.disconnect();
        callback();
      }
    };

    filteredTileContainers.forEach((container) => {
      const observer = new MutationObserver(observerCallback);
      observersCount++;
      const config = { attributes: true, childList: true, subtree: true };
      observer.observe(container, config);
    });
  });
}

module.exports = {
  waitForCanvasRender,
  setUpWaitForSvgRendered,
  waitForSvgRendered,
};
