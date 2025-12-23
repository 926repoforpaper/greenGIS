function constructRequestUrl(url, service, version, request) {
  try {
    return new URL(
      `${url}?service=${service}&version=${version}&request=${request}`
    );
  } catch (error) {
    this.$notify({
      text: this.$t("addNewLayer.invalidUrl"),
      type: "error",
    });
  }
}

function parseLayers(xmlDoc, layerSelector, nameSelector) {
  const layers = [];
  const layerElements = xmlDoc.querySelectorAll(layerSelector);
  layerElements.forEach((layer) => {
    const layerName = layer.querySelector(nameSelector)?.textContent;
    const layerTitle = layer.querySelector("Title")?.textContent;
    if (layerName) {
      layers.push({ layerName, layerTitle: layerTitle || layerName });
    }
  });
  return layers;
}

function parseJSON(text) {
  try {
    const data = JSON.parse(text);
    return data;
  } catch (error) {
    return null;
  }
}

export { constructRequestUrl, parseLayers, parseJSON };
