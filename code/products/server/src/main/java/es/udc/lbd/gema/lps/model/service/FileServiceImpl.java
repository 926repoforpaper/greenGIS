package es.udc.lbd.gema.lps.model.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.udc.lbd.gema.lps.web.rest.custom.FeatureCollectionJSON;
import jakarta.inject.Inject;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class FileServiceImpl implements FileService {

  private static final String DIRECTORY = "/src/main/resources/spatial/";

  private final Logger logger = LoggerFactory.getLogger(FileService.class);

  @Inject CachedGeomServiceFactory serviceFactory;

  @Override
  public void writeSpatialFiles() throws IOException {

    try {
      JSONParser parser = new JSONParser();
      JSONObject fileObj =
          (JSONObject) parser.parse(new FileReader("./src/main/resources/geoserver/layers.json"));

      JSONArray layers = (JSONArray) fileObj.get("layers");
      HashSet<String> createdLayers = new HashSet<String>();

      for (Object layerObject : layers) {
        JSONObject layer = (JSONObject) layerObject;
        Boolean cached = (Boolean) layer.get("cached");
        if ("geojson".equals((String) layer.get("layerType")) && cached != null && cached == true) {
          String name = ((String) layer.get("name")).split("-")[0];
          CachedGeomGetterService geomService = serviceFactory.getService(name);
          FeatureCollectionJSON json = geomService.getGeom(false, null);
          writeDataToFile(json, name + ".json");
        }
      }
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      return;
    }
  }

  private void writeDataToFile(FeatureCollectionJSON featureCollection, String fileName)
      throws IOException {
    String rootPath = System.getProperty("user.dir");
    File file = new File(rootPath + DIRECTORY + fileName);
    file.createNewFile();
    ObjectMapper mapper = new ObjectMapper();
    mapper.writeValue(file, featureCollection);
  }
}
