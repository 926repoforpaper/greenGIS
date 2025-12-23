import { createGeoTIFFLayer } from "@/components/map-viewer/common/map-layer-common";
import JSZip from "jszip";

function search(url, controller, name, merge) {
  if (!Array.isArray(url)) url = url.split(",");

  return fetch(url[0], { signal: controller.signal }).then(async (response) => {
    const layerName = name || url[0].split("/").pop();
    const contentType = response.headers.get("Content-Type");

    let result = {
      name: layerName,
      resolution: 64,
      opacity: 1,
    };

    if (contentType.includes("zip")) {
      result.url = await _searchZip(response);
    } else if (contentType.includes("tif") && merge) {
      result.url = url;
    } else {
      result = url.map((str) => {
        return {
          name: str.split("/").pop(),
          url: [str],
          resolution: 64,
          opacity: 1,
        };
      });
    }

    return result;
  });
}

async function create(layer, layerParams) {
  return await createGeoTIFFLayer(layer, layerParams);
}

function _searchZip(data) {
  return data
    .blob()
    .then((blobData) => {
      const zip = new JSZip();
      return zip.loadAsync(blobData);
    })
    .then(async (zipData) => {
      const files = [];
      for (const [relativePath, zipEntry] of Object.entries(zipData.files)) {
        if (relativePath.endsWith(".tif")) {
          const fileData = await zipEntry.async("blob");
          files.push(fileData);
        }
      }
      return files;
    });
}

export default {
  search,
  create,
};
