package es.udc.lbd.gema.lps.model.service;

import es.udc.lbd.gema.lps.model.service.dto.RegionDTO;
import es.udc.lbd.gema.lps.model.service.dto.RegionFullDTO;
import es.udc.lbd.gema.lps.model.service.exceptions.NotFoundException;
import es.udc.lbd.gema.lps.model.service.exceptions.OperationNotAllowedException;
import es.udc.lbd.gema.lps.web.rest.custom.FeatureCollectionJSON;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RegionService extends CachedGeomGetterService {

  Page<RegionDTO> getAll(Pageable pageable, List<String> filters, String search);

  RegionFullDTO get(Long id) throws NotFoundException;

  RegionFullDTO create(RegionFullDTO region) throws OperationNotAllowedException;

  RegionFullDTO update(Long id, RegionFullDTO region) throws OperationNotAllowedException;

  void delete(Long id);

  void restartGeom() throws IllegalArgumentException;

  FeatureCollectionJSON getExtension(Double xmin, Double xmax, Double ymin, Double ymax);
}
