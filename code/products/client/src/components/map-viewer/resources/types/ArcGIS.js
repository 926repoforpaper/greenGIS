import { createExternalGeoJSONLayer } from "@/components/map-viewer/common/map-layer-common";

function search(url, controller) {
  return fetch(`${url}/rest/services?f=json`, { signal: controller.signal })
    .then((response) => response.json())
    .then((data) => {
      const featureServers = data.services
        .filter((service) => service.type === "FeatureServer")
        .map((serviceData) => {
          return {
            serviceName: serviceData.name,
            serviceUrl: serviceData.url,
          };
        });

      return {
        services: featureServers,
      };
    });
}

function create(layer, layerParams) {
  return fetch(`${layer.url}/query?f=pgeojson&where=1=1&outFields=*`)
    .then((response) => response.json())
    .then((data) => {
      layer.features = data.features;
      layerParams["type"] = "ARCGIS";
      return createExternalGeoJSONLayer(layer, layerParams);
    });
}

function getLayers(url) {
  return fetch(`${url}?f=json`)
    .then((response) => response.json())
    .then((data) =>
      data.layers.map((layer) => {
        return {
          layerName: layer.name,
          layerTitle: layer.name,
          layerUrl: `${url}/${layer.id}`,
        };
      })
    )
    .catch(() => {});
}

export default {
  search,
  create,
  getLayers,
};
