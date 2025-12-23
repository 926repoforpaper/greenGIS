import { constructRequestUrl, parseLayers } from "../utils/resources-utils";
import { createWCSLayer } from "@/components/map-viewer/common/map-layer-common";

function search(url, controller) {
  const requestUrl = constructRequestUrl(
    url,
    "WCS",
    "2.0.1",
    "GetCapabilities"
  );

  return fetch(requestUrl, { signal: controller.signal })
    .then((response) => response.text())
    .then((data) => {
      const xmlDoc = new DOMParser().parseFromString(data, "application/xml");

      return {
        layers: parseLayers(xmlDoc, "CoverageSummary", "CoverageId"),
      };
    });
}

async function create(layer, layerParams) {
  return await createWCSLayer(layer, layerParams);
}

export default {
  search,
  create,
};
