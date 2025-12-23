package es.udc.lbd.gema.lps.model.service;

import es.udc.lbd.gema.lps.model.service.dto.TownDTO;
import es.udc.lbd.gema.lps.model.service.dto.TownFullDTO;
import es.udc.lbd.gema.lps.model.service.exceptions.NotFoundException;
import es.udc.lbd.gema.lps.model.service.exceptions.OperationNotAllowedException;
import es.udc.lbd.gema.lps.web.rest.custom.FeatureCollectionJSON;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TownService extends CachedGeomGetterService {

  List<TownDTO> getAllWithoutMunicipality();

  List<TownDTO> getAllWithoutProvince();

  List<TownDTO> getAllWithoutRegion();

  Page<TownDTO> getAll(Pageable pageable, List<String> filters, String search);

  TownFullDTO get(Long id) throws NotFoundException;

  TownFullDTO create(TownFullDTO town) throws OperationNotAllowedException;

  TownFullDTO update(Long id, TownFullDTO town) throws OperationNotAllowedException;

  void delete(Long id);

  FeatureCollectionJSON getLocation(Double xmin, Double xmax, Double ymin, Double ymax);
}
