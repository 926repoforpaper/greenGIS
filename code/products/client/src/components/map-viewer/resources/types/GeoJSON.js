import { createExternalGeoJSONLayer } from "@/components/map-viewer/common/map-layer-common";
import { parseJSON } from "../utils/resources-utils";

function search(url, controller, name) {
  const validUrl = new URL(url);

  return fetch(validUrl, { signal: controller.signal })
    .then((response) => response.text())
    .then((data) => {
      const dataset = validUrl.pathname
        .split("/")
        .filter((str) => str.includes(".geojson"))[0];

      const layerName = name || dataset || validUrl.host;
      const parsedData = parseJSON(data);

      return {
        type: parsedData.type,
        name: layerName,
        url: url,
        features: parsedData.features,
      };
    });
}

async function create(layer, layerParams) {
  return createExternalGeoJSONLayer(layer, layerParams);
}

export default {
  search,
  create,
};
