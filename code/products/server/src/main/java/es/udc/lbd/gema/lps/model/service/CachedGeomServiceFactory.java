package es.udc.lbd.gema.lps.model.service;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class CachedGeomServiceFactory {
  @Inject RegionService regionService;
  @Inject MunicipalityService municipalityService;
  @Inject ProvinceService provinceService;
  @Inject TownService townService;

  private final Map<String, CachedGeomGetterService> servicesMap = new HashMap<>();

  @PostConstruct
  public void init() {
    servicesMap.put("region", regionService);
    servicesMap.put("town", townService);
    servicesMap.put("municipality", municipalityService);
    servicesMap.put("province", provinceService);
  }

  public CachedGeomGetterService getService(String serviceName) {
    CachedGeomGetterService service = servicesMap.get(serviceName.toLowerCase());

    if (service == null) {
      throw new IllegalArgumentException("No service found for name: " + serviceName);
    }

    return service;
  }
}
