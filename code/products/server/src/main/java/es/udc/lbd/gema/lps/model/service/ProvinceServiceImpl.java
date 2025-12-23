package es.udc.lbd.gema.lps.model.service;

import es.udc.lbd.gema.lps.model.domain.Province;
import es.udc.lbd.gema.lps.model.repository.ProvinceRepository;
import es.udc.lbd.gema.lps.model.service.dto.ProvinceDTO;
import es.udc.lbd.gema.lps.model.service.dto.ProvinceFullDTO;
import es.udc.lbd.gema.lps.model.service.exceptions.NotFoundException;
import es.udc.lbd.gema.lps.model.service.exceptions.OperationNotAllowedException;
import es.udc.lbd.gema.lps.model.service.util.GeoServerUtil;
import es.udc.lbd.gema.lps.web.rest.custom.FeatureCollectionJSON;
import es.udc.lbd.gema.lps.web.rest.custom.FeatureJSON;
import es.udc.lbd.gema.lps.web.rest.specifications.ProvinceSpecification;
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
public class ProvinceServiceImpl implements ProvinceService {

  @Inject private ProvinceRepository provinceRepository;

  @Inject private GeoServerUtil gsUtil;

  public Page<ProvinceDTO> getAll(Pageable pageable, List<String> filters, String search) {
    Page<Province> page;
    if (search != null && !search.isEmpty()) {
      page = provinceRepository.findAll(ProvinceSpecification.searchAll(search), pageable);
    } else {
      page =
          provinceRepository.findAll(
              SpecificationUtil.getSpecificationFromFilters(filters, false), pageable);
    }
    return page.map(ProvinceDTO::new);
  }

  public FeatureCollectionJSON getExtension(Double xmin, Double xmax, Double ymin, Double ymax) {
    List<Province> list = provinceRepository.getDataByBoundingBox(xmin, xmax, ymin, ymax);

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

  public FeatureCollectionJSON getGeom(Boolean properties, List<String> filters) {
    List<Province> list =
        provinceRepository.findAll(SpecificationUtil.getSpecificationFromFilters(filters, false));

    List<FeatureJSON> ret =
        list.stream()
            .map(
                e -> {
                  FeatureJSON geoJSON = new FeatureJSON();
                  if (properties) {
                    geoJSON = new FeatureJSON(Province.class, e);
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

  public ProvinceFullDTO get(Long id) throws NotFoundException {
    Province province = findById(id);
    return new ProvinceFullDTO(province);
  }

  @Transactional(readOnly = false, rollbackFor = Exception.class)
  public ProvinceFullDTO create(ProvinceFullDTO provinceDto) throws OperationNotAllowedException {
    if (provinceDto.getId() != null) {
      throw new OperationNotAllowedException("province.error.id-exists");
    }
    Province provinceEntity = provinceDto.toProvince();
    Province provinceSaved = provinceRepository.save(provinceEntity);
    return new ProvinceFullDTO(provinceSaved);
  }

  @Transactional(readOnly = false, rollbackFor = Exception.class)
  public ProvinceFullDTO update(Long id, ProvinceFullDTO provinceDto)
      throws OperationNotAllowedException {
    if (provinceDto.getId() == null) {
      throw new OperationNotAllowedException("province.error.id-not-exists");
    }
    if (!id.equals(provinceDto.getId())) {
      throw new OperationNotAllowedException("province.error.id-dont-match");
    }
    Province province =
        provinceRepository
            .findById(id)
            .orElseThrow(() -> new OperationNotAllowedException("province.error.id-not-exists"));
    Province provinceToUpdate = provinceDto.toProvince();
    Province provinceUpdated = provinceRepository.save(provinceToUpdate);
    return new ProvinceFullDTO(provinceUpdated);
  }

  @Transactional(readOnly = false, rollbackFor = Exception.class)
  public void delete(Long id) {
    provinceRepository.deleteById(id);
  }

  public void restartGeom() throws IllegalArgumentException {
    gsUtil.recalculateFeatureTypeBBox("t_province");
  }

  /** PRIVATE METHODS * */
  private Province findById(Long id) throws NotFoundException {
    return provinceRepository
        .findById(id)
        .orElseThrow(() -> new NotFoundException("Cannot find Province with id " + id));
  }
}
