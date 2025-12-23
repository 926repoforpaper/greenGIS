/**
 * Common functions to create and modify layers
 */
import properties from "@/properties";
import RepositoryFactory from "@/repositories/RepositoryFactory";
import { getStyle } from "@/components/map-viewer/common/map-styles-common";
import {
  GeoPackageLayer,
  GeoJSONLayer,
  WMSLayer,
  WCSLayer,
  GeoTIFFLayer,
  ShapeFileLayer,
} from "@lbdudc/map-viewer";
import layers from "../config-files/layers.json";

/**
 * Creates a WCS Layer.
 */
async function createWCSLayer(json, layerParams, layerInMap = {}) {
  const options = json.options;

  const availableStyles = _getAvailableStyles(json);
  const defaultStyle = _getDefaultStyle(json, availableStyles, layerInMap);

  var wcsLayer = new WCSLayer(
    {
      id: json.name,
      label: layerParams.label,
      baseLayer: false,
      selected: layerInMap.selected || layerInMap.selected == null,
      url: json.url || properties.GEOSERVER_URL + "/wcs",
      params: options,
      added: layerParams.added,
    },
    availableStyles,
    defaultStyle
  );

  await wcsLayer.createLayerFromGeoraster();
  return wcsLayer;
}

/**
 * Creates a GeoPackage Layer.
 */
async function createGeoPackageLayer(json, layerParams, layerInMap = {}) {
  const availableStyles = _getAvailableStyles(json);
  const defaultStyle = _getDefaultStyle(json, availableStyles, layerInMap);

  return await GeoPackageLayer.create(
    json.data,
    {
      id: json.name || layerParams.label,
      label: layerParams.label,
      baseLayer: false,
      selected: layerInMap.selected || layerInMap.selected == null, // if no value is given, it is shown in map
      url: json.url,
      added: layerParams.added,
    },
    availableStyles,
    defaultStyle
  );
}

/**
 * Creates a GeoTIFF Layer.
 */
async function createGeoTIFFLayer(json, layerParams, layerInMap = {}) {
  const options = json.options;

  const availableStyles = _getAvailableStyles(json);
  const defaultStyle = _getDefaultStyle(json, availableStyles, layerInMap);

  const geoTIFFLayer = new GeoTIFFLayer(
    {
      id: json.name,
      label: layerParams.label,
      baseLayer: false,
      selected: layerInMap.selected || layerInMap.selected == null, // if no value is given, it is shown in map
      url: json.url,
      params: options,
      added: layerParams.added,
      resolution: json.resolution,
      opacity: json.opacity,
    },
    availableStyles,
    defaultStyle
  );

  await geoTIFFLayer.createGeoRasterLayer();
  return geoTIFFLayer;
}

/**
 * Creates a Shapefile Layer.
 */
function createShapefileLayer(json, layerParams, layerInMap = {}) {
  const availableStyles = _getAvailableStyles(json);
  const defaultStyle = _getDefaultStyle(json, availableStyles, layerInMap);

  return new ShapeFileLayer(
    {
      id: json.name,
      label: layerParams.label,
      baseLayer: false,
      selected: layerInMap.selected || layerInMap.selected == null, // if no value is given, it is shown in map
      url: json.url,
      added: layerParams.added,
    },
    availableStyles,
    defaultStyle
  );
}

/**
 * Generates a unique layer ID for an existing map.
 */
function getUniqueLayerId(map, layerName, count = 1) {
  const uniqueId = `${layerName}.${count}`;
  if (map.getLayer(uniqueId)) {
    return getUniqueLayerId(map, layerName, count + 1);
  } else {
    return uniqueId;
  }
}

/**
 * Creates a WMS Layer.
 */
function createWMSLayer(json, layerParams, layerInMap = {}) {
  const options = json.options;

  const availableStyles = _getAvailableStyles(json);
  const defaultStyle = _getDefaultStyle(json, availableStyles, layerInMap);

  return new WMSLayer(
    {
      id: json.name,
      label: layerParams.label,
      baseLayer: false,
      selected: layerInMap.selected || layerInMap.selected == null, // if no value is given, it is shown in map
      url: json.url || properties.GEOSERVER_URL + "/wms",
      params: options,
      added: layerParams.added,
    },
    availableStyles,
    defaultStyle
  );
}

function createExternalGeoJSONLayer(json, layerParams, layerInMap = {}) {
  const availableStyles = _getAvailableStyles(json);
  const defaultStyle = _getDefaultStyle(json, availableStyles, layerInMap);

  return new GeoJSONLayer(
    json,
    {
      id: json.name || layerParams.label,
      label: layerParams.label,
      baseLayer: false,
      selected: layerInMap.selected || layerInMap.selected == null, // if no value is given, it is shown in map
      url: json.url,
      added: layerParams.added,
      type: layerParams.type,
    },
    availableStyles,
    defaultStyle
  );
}

/**
 * Creates a GeoJSON Layer.
 */
function createGeoJSONLayer(json, layerParams, layerInMap = {}, map) {
  const repository = RepositoryFactory.get(_getRepositoryNameFromJSON(json));

  const options = _getBBoxPagination(layerParams.bounds);
  const popup = layerParams.hasOwnProperty("popupFn")
    ? layerParams.popupFn(layerInMap.form || json.form)
    : null;
  const availableStyles = _getAvailableStyles(json);
  const defaultStyle = _getDefaultStyle(json, availableStyles, layerInMap);
  const isCached =
    layers.layers.find((el) => el.name === json.name).cached || false;
  const fileName = isCached
    ? _getEntityNameFromJSON(json)
    : _getPropertyNameFromJSON(json);

  const dataWrapper = () => {
    return new Promise(async (resolve) => {
      const data = await repository.getGeom(fileName, isCached, options);
      map.getLeafletMap().fire("layerchange", { name: json.name });
      resolve(data);
    });
  };

  return new GeoJSONLayer(
    dataWrapper,
    {
      id: json.name,
      label: layerParams.label,
      baseLayer: false,
      selected: layerInMap.selected || layerInMap.selected == null, // if no value is given, it is shown in map
      url: json.url,
      popup: popup,
      added: layerParams.added,
      type: layerParams.type,
    },
    availableStyles,
    defaultStyle
  );
}

function _getAvailableStyles(json) {
  return json.availableStyles?.map((availableStyleName) =>
    getStyle(availableStyleName)
  );
}

function updateLayer(map, bbox, popupFn) {
  const layersToShow = layers.layers.filter(
    (layer) => layer.layerType === "geojson" && map.getLayer(layer.name)
  );

  const options = _getBBoxPagination(bbox);

  layersToShow.forEach((json) => {
    const repository = RepositoryFactory.get(_getRepositoryNameFromJSON(json));
    repository
      .getGeom(_getPropertyNameFromJSON(json), false, options)
      .then((data) => {
        if (data.features.length !== 0) {
          let layer = map.getLayer(json.name);
          layer
            .getLayer()
            .then((layr) => {
              _updateLayerData(layr, data);
              map.getLeafletMap().fire("layerchange", { name: json.name });
            })
            .finally(() => {
              layer.bindPopup(popupFn(json.form));
            });
        } else {
          map.getLeafletMap().fire("layerchange", { name: json.name });
        }
      });
  });
}

function _updateLayerData(layer, data) {
  if (Object.keys(layer._layers).length === 0) {
    layer.addData(data);
  } else {
    const dataObj = {};
    data.features.forEach((item) => (dataObj[item.id] = item));

    const newFeatures = Object.values(dataObj);
    layer.addData(newFeatures);

    layer.eachLayer((subLayer) => {
      const found = dataObj[subLayer.feature.id];
      if (found) {
        delete dataObj[subLayer.feature.id];
      } else {
        layer.removeLayer(subLayer);
      }
    });
  }
}

function _getDefaultStyle(json, availableStyles, layerInMap) {
  return layerInMap.style != null
    ? availableStyles.find((style) => style.id === layerInMap.style)
    : json.defaultStyle != null
    ? availableStyles.find((style) => style.id === json.defaultStyle)
    : null;
}

function _getEntityNameFromJSON(json) {
  const prop = json.entityName != null ? "entityName" : "name";
  const nameParts = json[prop].split("-");
  if (nameParts.length > 2) {
    return (
      nameParts[0] +
      "-" +
      nameParts[1].charAt(0).toUpperCase() +
      nameParts[1].slice(1)
    );
  }
  return nameParts[0];
}

function _getRepositoryNameFromJSON(json) {
  let entityName = _getEntityNameFromJSON(json);
  let repositorySuffix = "EntityRepository";
  // Check if component entity
  if (entityName.indexOf("-") != -1) {
    repositorySuffix = "Repository";
    entityName = entityName.replace("-", "");
  }
  return (
    entityName.charAt(0).toUpperCase() + entityName.slice(1) + repositorySuffix
  );
}

function _getPropertyNameFromJSON(json) {
  // returns last substring after a '-'
  const prop = json.entityName != null ? "entityName" : "name";
  const nameParts = json[prop].split("-");
  return nameParts[nameParts.length - 1];
}

function _getBBoxPagination(bounds) {
  const augmentedBBox = _incrementBBox(
    bounds.getWest(),
    bounds.getEast(),
    bounds.getSouth(),
    bounds.getNorth()
  );

  const options = { params: {} };
  options.params.xmin = bounds.getWest();
  options.params.xmax = bounds.getEast();
  options.params.ymin = bounds.getSouth();
  options.params.ymax = bounds.getNorth();

  return options;
}

function _incrementBBox(xmin, xmax, ymin, ymax) {
  let incrementNS = (ymax - ymin) * 0.3;
  let incrementEW = (xmax - xmin) * 0.3;

  let augmentedXmin = xmin - incrementEW;
  let augmentedXmax = xmax + incrementEW;
  let augmentedYmin = ymin - incrementNS;
  let augmentedYmax = ymax + incrementNS;

  return {
    xmin: augmentedXmin,
    xmax: augmentedXmax,
    ymin: augmentedYmin,
    ymax: augmentedYmax,
  };
}

export {
  createWMSLayer,
  createGeoJSONLayer,
  createGeoPackageLayer,
  createWCSLayer,
  createGeoTIFFLayer,
  createShapefileLayer,
  getUniqueLayerId,
  createExternalGeoJSONLayer,
  updateLayer,
};
