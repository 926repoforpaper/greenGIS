async function setupWaitForInitialLayersLoad(driver, center) {
  const script = `
    (function(center) {
        console.log('Injecting setupWaitForInitialLayersLoad...');
        window.setupWaitForInitialLayersLoad = function(center) {
            try {
                window.layersLoaded = false;
                const onLayersRendered = (event) => {
                    console.log('Layers rendered');
                    if (center != null) {
                        console.log('Pan to center:', center[0], center[1]);
                        event.detail.map._map.panTo(new L.latLng(center[0], center[1]));
                    }                    
                    const zoom = event.detail.map._map.getZoom();
                    window.mapDetails = {
                        zoom: zoom,
                        pixel_origin: event.detail.map._map.getPixelOrigin(),
                        bounds: event.detail.map._map.getBounds(),
                    };
                    window.document.removeEventListener(
                        "layers-rendered",
                        onLayersRendered
                    );
                    window.layersLoaded = true;
                };
                if (window.document.readyState === "complete") {
                    console.log('Document ready, adding listener.');
                    window.document.addEventListener("layers-rendered", onLayersRendered);
                }
                window.addEventListener("load", () => {
                    console.log('Window loaded, adding listener.');
                    window.document.addEventListener("layers-rendered", onLayersRendered);
                });
                window.document.addEventListener("layers-rendered", onLayersRendered);
            } catch (err) {
                console.error('Error in setupWaitForInitialLayersLoad:', err);
            }
        };
        // Llamamos a la función inmediatamente para configurarla
        window.setupWaitForInitialLayersLoad(center);
    })(${JSON.stringify(center)});`;

  await driver.sendDevToolsCommand("Page.addScriptToEvaluateOnNewDocument", {
    source: script,
  });
}

async function waitForInitialLayersLoad(driver) {
  return driver.wait(async () => {
    const layersLoaded = await driver.executeScript(() => {
      return window.layersLoaded || false;
    });
    return layersLoaded === true;
  });
}

async function setUpWaitForZoomRendered(driver) {
  return driver.executeScript(() => {
    window.zoomRenderedFired = false;
    const handler = (event) => {
      window.zoomRenderedFired = true;
      const zoom = event.detail.map._map.getZoom();
      window.mapDetails = {
        zoom: zoom,
        pixel_origin: event.detail.map._map.getPixelOrigin(),
        bounds: event.detail.map._map.getBounds(),
      };
      document.removeEventListener("zoom-rendered", handler);
    };
    document.addEventListener("zoom-rendered", handler);
  });
}

async function waitForZoomRendered(driver) {
  return driver.wait(async () => {
    const zoomRendered = await driver.executeScript(() => {
      return window.zoomRenderedFired || false;
    });
    return zoomRendered === true;
  });
}

async function setUpWaitForMoveRendered(driver) {
  return driver.executeScript(() => {
    window.moveRenderedFired = false;
    const handler = (event) => {
      window.moveRenderedFired = true;
      const zoom = event.detail.map._map.getZoom();
      window.mapDetails = {
        zoom: zoom,
        pixel_origin: event.detail.map._map.getPixelOrigin(),
        bounds: event.detail.map._map.getBounds(),
      };
      document.removeEventListener("move-rendered", handler);
    };
    document.addEventListener("move-rendered", handler);
  });
}

async function waitForMoveRendered(driver) {
  return driver.wait(async () => {
    const moveRendered = await driver.executeScript(() => {
      return window.moveRenderedFired || false;
    });
    return moveRendered === true;
  });
}

module.exports = {
  waitForZoomRendered,
  waitForMoveRendered,
  waitForInitialLayersLoad,
  waitForInitialLayersLoad,
  setUpWaitForMoveRendered,
  setUpWaitForZoomRendered,
  setupWaitForInitialLayersLoad,
};
