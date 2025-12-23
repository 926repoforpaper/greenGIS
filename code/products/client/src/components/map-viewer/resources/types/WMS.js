import { constructRequestUrl, parseLayers } from "../utils/resources-utils";
import { createWMSLayer } from "@/components/map-viewer/common/map-layer-common";

function search(url, controller) {
  const requestUrl = constructRequestUrl(
    url,
    "WMS",
    "1.3.0",
    "GetCapabilities"
  );

  return fetch(requestUrl, { signal: controller.signal })
    .then((response) => response.text())
    .then((data) => {
      const xmlDoc = new DOMParser().parseFromString(data, "application/xml");

      return {
        layers: parseLayers(xmlDoc, "Layer[queryable='1']", "Name"),
      };
    });
}

function create(layer, layerParams) {
  return createWMSLayer(layer, layerParams);
}

export default {
  search,
  create,
};
