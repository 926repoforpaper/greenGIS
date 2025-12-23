import { createShapefileLayer } from "@/components/map-viewer/common/map-layer-common";

function search(url, controller, name) {
  return Promise.resolve({
    url: url,
    name: name || url.split("/").pop(),
  });
}

async function create(layer, layerParams) {
  return createShapefileLayer(layer, layerParams);
}

export default {
  search,
  create,
};
