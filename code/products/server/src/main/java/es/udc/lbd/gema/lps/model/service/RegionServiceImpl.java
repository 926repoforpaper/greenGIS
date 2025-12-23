package es.udc.lbd.gema.lps.model.service;

import es.udc.lbd.gema.lps.model.domain.Region;
import es.udc.lbd.gema.lps.model.repository.RegionRepository;
import es.udc.lbd.gema.lps.model.service.dto.RegionDTO;
import es.udc.lbd.gema.lps.model.service.dto.RegionFullDTO;
import es.udc.lbd.gema.lps.model.service.exceptions.NotFoundException;
import es.udc.lbd.gema.lps.model.service.exceptions.OperationNotAllowedException;
import es.udc.lbd.gema.lps.model.service.util.GeoServerUtil;
import es.udc.lbd.gema.lps.web.rest.custom.FeatureCollectionJSON;
import es.udc.lbd.gema.lps.web.rest.custom.FeatureJSON;
import es.udc.lbd.gema.lps.web.rest.specifications.RegionSpecification;
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
public class RegionServiceImpl implements RegionService {

  @Inject private RegionRepository regionRepository;

  @Inject private GeoServerUtil gsUtil;

  public Page<RegionDTO> getAll(Pageable pageable, List<String> filters, String search) {
    Page<Region> page;
    if (search != null && !search.isEmpty()) {
      page = regionRepository.findAll(RegionSpecification.searchAll(search), pageable);
    } else {
      page =
          regionRepository.findAll(
              SpecificationUtil.getSpecificationFromFilters(filters, false), pageable);
    }
    return page.map(RegionDTO::new);
  }

  public FeatureCollectionJSON getExtension(Double xmin, Double xmax, Double ymin, Double ymax) {
    List<Region> list = regionRepository.getDataByBoundingBox(xmin, xmax, ymin, ymax);

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
    List<Region> list =
        regionRepository.findAll(SpecificationUtil.getSpecificationFromFilters(filters, false));

    List<FeatureJSON> ret =
        list.stream()
            .map(
                e -> {
                  FeatureJSON geoJSON = new FeatureJSON();
                  if (properties) {
                    geoJSON = new FeatureJSON(Region.class, e);
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

  public RegionFullDTO get(Long id) throws NotFoundException {
    Region region = findById(id);
    return new RegionFullDTO(region);
  }

  @Transactional(readOnly = false, rollbackFor = Exception.class)
  public RegionFullDTO create(RegionFullDTO regionDto) throws OperationNotAllowedException {
    if (regionDto.getId() != null) {
      throw new OperationNotAllowedException("region.error.id-exists");
    }
    Region regionEntity = regionDto.toRegion();
    Region regionSaved = regionRepository.save(regionEntity);
    return new RegionFullDTO(regionSaved);
  }

  @Transactional(readOnly = false, rollbackFor = Exception.class)
  public RegionFullDTO update(Long id, RegionFullDTO regionDto)
      throws OperationNotAllowedException {
    if (regionDto.getId() == null) {
      throw new OperationNotAllowedException("region.error.id-not-exists");
    }
    if (!id.equals(regionDto.getId())) {
      throw new OperationNotAllowedException("region.error.id-dont-match");
    }
    Region region =
        regionRepository
            .findById(id)
            .orElseThrow(() -> new OperationNotAllowedException("region.error.id-not-exists"));
    Region regionToUpdate = regionDto.toRegion();
    Region regionUpdated = regionRepository.save(regionToUpdate);
    return new RegionFullDTO(regionUpdated);
  }

  @Transactional(readOnly = false, rollbackFor = Exception.class)
  public void delete(Long id) {
    regionRepository.deleteById(id);
  }

  public void restartGeom() throws IllegalArgumentException {
    gsUtil.recalculateFeatureTypeBBox("t_region");
  }

  /** PRIVATE METHODS * */
  private Region findById(Long id) throws NotFoundException {
    return regionRepository
        .findById(id)
        .orElseThrow(() -> new NotFoundException("Cannot find Region with id " + id));
  }
}
