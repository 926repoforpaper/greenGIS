package es.udc.lbd.gema.lps.model.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import es.udc.lbd.gema.lps.model.service.dto.MunicipalityDTO;
import es.udc.lbd.gema.lps.model.service.dto.MunicipalityFullDTO;
import es.udc.lbd.gema.lps.model.service.exceptions.NotFoundException;
import es.udc.lbd.gema.lps.model.service.exceptions.OperationNotAllowedException;
import es.udc.lbd.gema.lps.web.rest.custom.FeatureCollectionJSON;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MunicipalityService extends CachedGeomGetterService {

  Page<MunicipalityDTO> getAll(Pageable pageable, List<String> filters, String search);

  MunicipalityFullDTO get(Long id) throws NotFoundException;

  MunicipalityFullDTO create(MunicipalityFullDTO municipality) throws OperationNotAllowedException;

  MunicipalityFullDTO update(Long id, MunicipalityFullDTO municipality)
      throws OperationNotAllowedException;

  void delete(Long id);

  void restartGeom() throws IllegalArgumentException;

  FeatureCollectionJSON getExtension(Double xmin, Double xmax, Double ymin, Double ymax);

  String getExtensionJsonDb(Double xmin, Double xmax, Double ymin, Double ymax)
      throws JsonProcessingException;
}
