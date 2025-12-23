package es.udc.lbd.gema.lps.model.service;

import es.udc.lbd.gema.lps.web.rest.custom.FeatureCollectionJSON;
import java.util.List;

public interface CachedGeomGetterService {
  FeatureCollectionJSON getGeom(Boolean properties, List<String> filters);
}
