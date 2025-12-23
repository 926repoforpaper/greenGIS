package es.udc.lbd.gema.lps.model.service;

import es.udc.lbd.gema.lps.model.service.dto.ProvinceDTO;
import es.udc.lbd.gema.lps.model.service.dto.ProvinceFullDTO;
import es.udc.lbd.gema.lps.model.service.exceptions.NotFoundException;
import es.udc.lbd.gema.lps.model.service.exceptions.OperationNotAllowedException;
import es.udc.lbd.gema.lps.web.rest.custom.FeatureCollectionJSON;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProvinceService extends CachedGeomGetterService {

  Page<ProvinceDTO> getAll(Pageable pageable, List<String> filters, String search);

  ProvinceFullDTO get(Long id) throws NotFoundException;

  ProvinceFullDTO create(ProvinceFullDTO province) throws OperationNotAllowedException;

  ProvinceFullDTO update(Long id, ProvinceFullDTO province) throws OperationNotAllowedException;

  void delete(Long id);

  void restartGeom() throws IllegalArgumentException;

  FeatureCollectionJSON getExtension(Double xmin, Double xmax, Double ymin, Double ymax);
}
