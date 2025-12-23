import { convertGeoParquetToGeoJSON } from "@/components/map-viewer/resources/utils/parquetToGeoJSON";
import properties from "@/properties";
import { createExternalGeoJSONLayer } from "@/components/map-viewer/common/map-layer-common";
import { parseJSON } from "../utils/resources-utils";

const wasmData = _downloadWasmFile();

function search(url, controller, name) {
  const validUrl = new URL(url);

  return wasmData.then(async (wasmData) => {
    const data = await _readParquetFile(wasmData, validUrl);
    let parsedJSON = parseJSON(data.data);
    parsedJSON["name"] = name || validUrl.hostname;
    parsedJSON["url"] = validUrl.href;

    return parsedJSON;
  });
}

async function create(layer, layerParams) {
  layerParams["type"] = "GEOPARQUET";
  return createExternalGeoJSONLayer(layer, layerParams);
}

async function _downloadWasmFile() {
  const response = await fetch(`${properties.BASE_URL}gpq.wasm`);
  return await response.arrayBuffer();
}

async function _readParquetFile(wasmData, filePath) {
  try {
    const response = await fetch(filePath);
    const data = await response.arrayBuffer();
    return await convertGeoParquetToGeoJSON(wasmData, new Uint8Array(data));
  } catch (error) {
    console.error("Error reading the Parquet file:", error);
    throw error;
  }
}

export default {
  search,
  create,
};
