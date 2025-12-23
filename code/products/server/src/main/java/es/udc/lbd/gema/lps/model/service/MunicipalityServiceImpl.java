package es.udc.lbd.gema.lps.model.service;

import es.udc.lbd.gema.lps.model.domain.Municipality;
import es.udc.lbd.gema.lps.model.repository.MunicipalityRepository;
import es.udc.lbd.gema.lps.model.service.dto.MunicipalityDTO;
import es.udc.lbd.gema.lps.model.service.dto.MunicipalityFullDTO;
import es.udc.lbd.gema.lps.model.service.exceptions.NotFoundException;
import es.udc.lbd.gema.lps.model.service.exceptions.OperationNotAllowedException;
import es.udc.lbd.gema.lps.model.service.util.GeoServerUtil;
import es.udc.lbd.gema.lps.web.rest.custom.FeatureCollectionJSON;
import es.udc.lbd.gema.lps.web.rest.custom.FeatureJSON;
import es.udc.lbd.gema.lps.web.rest.specifications.MunicipalitySpecification;
import es.udc.lbd.gema.lps.web.rest.util.specification_utils.*;
import jakarta.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true, rollbackFor = Exception.class)
public class MunicipalityServiceImpl implements MunicipalityService {

  @Inject private MunicipalityRepository municipalityRepository;

  @Inject private GeoServerUtil gsUtil;

  public Page<MunicipalityDTO> getAll(Pageable pageable, List<String> filters, String search) {
    Page<Municipality> page;
    if (search != null && !search.isEmpty()) {
      page = municipalityRepository.findAll(MunicipalitySpecification.searchAll(search), pageable);
    } else {
      page =
          municipalityRepository.findAll(
              SpecificationUtil.getSpecificationFromFilters(filters, false), pageable);
    }
    return page.map(MunicipalityDTO::new);
  }

  public FeatureCollectionJSON getExtension(Double xmin, Double xmax, Double ymin, Double ymax) {
    List<Municipality> list = municipalityRepository.getDataByBoundingBox(xmin, xmax, ymin, ymax);

    List<FeatureJSON> ret =
        list.stream()
            .map(
                e -> {
                  FeatureJSON geoJSON = new FeatureJSON();
                  geoJSON.setProperties(new HashMap());
                  geoJSON.setId(e.getId());
                  geoJSON.getProperties().put("displayString", "" + e.getId() + "");
                  geoJSON.setGeometry(e.getExtension());
                  return geoJSON;
                })
            .filter(e -> e.getGeometry() != null)
            .collect(Collectors.toList());
    return new FeatureCollectionJSON(ret);
  }

  public String getExtensionJsonDb(Double xmin, Double xmax, Double ymin, Double ymax) {
    return municipalityRepository.getDataByBoundingBoxJsonDb(xmin, xmax, ymin, ymax);
  }

  public FeatureCollectionJSON getGeom(Boolean properties, List<String> filters) {
    List<Municipality> list =
        municipalityRepository.findAll(
            SpecificationUtil.getSpecificationFromFilters(filters, false));

    List<FeatureJSON> ret =
        list.stream()
            .map(
                e -> {
                  FeatureJSON geoJSON = new FeatureJSON();
                  if (properties) {
                    geoJSON = new FeatureJSON(Municipality.class, e);
                  } else {
                    geoJSON.setProperties(new HashMap());
                  }
                  geoJSON.setId(e.getId());
                  geoJSON.getProperties().put("displayString", "" + e.getId() + "");
                  geoJSON.setGeometry(e.getExtension());
                  return geoJSON;
                })
            .filter(e -> e.getGeometry() != null)
            .collect(Collectors.toList());
    return new FeatureCollectionJSON(ret);
  }

  public MunicipalityFullDTO get(Long id) throws NotFoundException {
    Municipality municipality = findById(id);
    return new MunicipalityFullDTO(municipality);
  }

  @Transactional(readOnly = false, rollbackFor = Exception.class)
  public MunicipalityFullDTO create(MunicipalityFullDTO municipalityDto)
      throws OperationNotAllowedException {
    if (municipalityDto.getId() != null) {
      throw new OperationNotAllowedException("municipality.error.id-exists");
    }
    Municipality municipalityEntity = municipalityDto.toMunicipality();
    Municipality municipalitySaved = municipalityRepository.save(municipalityEntity);
    return new MunicipalityFullDTO(municipalitySaved);
  }

  @Transactional(readOnly = false, rollbackFor = Exception.class)
  public MunicipalityFullDTO update(Long id, MunicipalityFullDTO municipalityDto)
      throws OperationNotAllowedException {
    if (municipalityDto.getId() == null) {
      throw new OperationNotAllowedException("municipality.error.id-not-exists");
    }
    if (!id.equals(municipalityDto.getId())) {
      throw new OperationNotAllowedException("municipality.error.id-dont-match");
    }
    Municipality municipality =
        municipalityRepository
            .findById(id)
            .orElseThrow(
                () -> new OperationNotAllowedException("municipality.error.id-not-exists"));
    Municipality municipalityToUpdate = municipalityDto.toMunicipality();
    Municipality municipalityUpdated = municipalityRepository.save(municipalityToUpdate);
    return new MunicipalityFullDTO(municipalityUpdated);
  }

  @Transactional(readOnly = false, rollbackFor = Exception.class)
  public void delete(Long id) {
    municipalityRepository.deleteById(id);
  }

  public void restartGeom() throws IllegalArgumentException {
    gsUtil.recalculateFeatureTypeBBox("t_municipality");
  }

  /** PRIVATE METHODS * */
  private Municipality findById(Long id) throws NotFoundException {
    return municipalityRepository
        .findById(id)
        .orElseThrow(() -> new NotFoundException("Cannot find Municipality with id " + id));
  }
}
