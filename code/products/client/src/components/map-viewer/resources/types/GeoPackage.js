import { createGeoPackageLayer } from "@/components/map-viewer/common/map-layer-common";

function search(url, controller, name) {
  const validUrl = new URL(url);

  return fetch(validUrl, { signal: controller.signal })
    .then((response) => response.arrayBuffer())
    .then((data) => {
      const dataset = validUrl.pathname
        .split("/")
        .filter((str) => str.includes(".gpkg"))[0];

      const layerName = dataset.split(".gpkg")[0] || name;

      return {
        name: layerName,
        url: url,
        data: new Uint8Array(data),
      };
    });
}

async function create(layer, layerParams) {
  return createGeoPackageLayer(layer, layerParams);
}

export default {
  search,
  create,
};
