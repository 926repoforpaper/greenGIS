package es.udc.lbd.gema.lps.model.service;

import es.udc.lbd.gema.lps.model.domain.Town;
import es.udc.lbd.gema.lps.model.repository.TownRepository;
import es.udc.lbd.gema.lps.model.service.dto.TownDTO;
import es.udc.lbd.gema.lps.model.service.dto.TownFullDTO;
import es.udc.lbd.gema.lps.model.service.exceptions.NotFoundException;
import es.udc.lbd.gema.lps.model.service.exceptions.OperationNotAllowedException;
import es.udc.lbd.gema.lps.web.rest.custom.FeatureCollectionJSON;
import es.udc.lbd.gema.lps.web.rest.custom.FeatureJSON;
import es.udc.lbd.gema.lps.web.rest.specifications.TownSpecification;
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
public class TownServiceImpl implements TownService {

  @Inject private TownRepository townRepository;

  public List<TownDTO> getAllWithoutMunicipality() {
    return townRepository.municipalityIsNull().stream()
        .map(TownDTO::new)
        .collect(Collectors.toList());
  }

  public List<TownDTO> getAllWithoutProvince() {
    return townRepository.provinceIsNull().stream().map(TownDTO::new).collect(Collectors.toList());
  }

  public List<TownDTO> getAllWithoutRegion() {
    return townRepository.regionIsNull().stream().map(TownDTO::new).collect(Collectors.toList());
  }

  public FeatureCollectionJSON getLocation(Double xmin, Double xmax, Double ymin, Double ymax) {
    List<Town> list = townRepository.getDataByBoundingBox(xmin, xmax, ymin, ymax);

    List<FeatureJSON> ret =
        list.stream()
            .map(
                e -> {
                  FeatureJSON geoJSON = new FeatureJSON();
                  geoJSON.setProperties(new HashMap());
                  geoJSON.setId(e.getId());
                  geoJSON.getProperties().put("displayString", "" + e.getId() + "");
                  geoJSON.setGeometry(e.getLocation());
                  return geoJSON;
                })
            .filter(e -> e.getGeometry() != null)
            .collect(Collectors.toList());
    return new FeatureCollectionJSON(ret);
  }

  public Page<TownDTO> getAll(Pageable pageable, List<String> filters, String search) {
    Page<Town> page;
    if (search != null && !search.isEmpty()) {
      page = townRepository.findAll(TownSpecification.searchAll(search), pageable);
    } else {
      page =
          townRepository.findAll(
              SpecificationUtil.getSpecificationFromFilters(filters, false), pageable);
    }
    return page.map(TownDTO::new);
  }

  public FeatureCollectionJSON getGeom(Boolean properties, List<String> filters) {
    List<Town> list =
        townRepository.findAll(SpecificationUtil.getSpecificationFromFilters(filters, false));

    List<FeatureJSON> ret =
        list.stream()
            .map(
                e -> {
                  FeatureJSON geoJSON = new FeatureJSON();
                  if (properties) {
                    geoJSON = new FeatureJSON(Town.class, e);
                  } else {
                    geoJSON.setProperties(new HashMap());
                  }
                  geoJSON.setId(e.getId());
                  geoJSON.getProperties().put("displayString", "" + e.getId() + "");
                  geoJSON.setGeometry(e.getLocation());
                  return geoJSON;
                })
            .filter(e -> e.getGeometry() != null)
            .collect(Collectors.toList());
    return new FeatureCollectionJSON(ret);
  }

  public TownFullDTO get(Long id) throws NotFoundException {
    Town town = findById(id);
    return new TownFullDTO(town);
  }

  @Transactional(readOnly = false, rollbackFor = Exception.class)
  public TownFullDTO create(TownFullDTO townDto) throws OperationNotAllowedException {
    if (townDto.getId() != null) {
      throw new OperationNotAllowedException("town.error.id-exists");
    }
    Town townEntity = townDto.toTown();
    Town townSaved = townRepository.save(townEntity);
    return new TownFullDTO(townSaved);
  }

  @Transactional(readOnly = false, rollbackFor = Exception.class)
  public TownFullDTO update(Long id, TownFullDTO townDto) throws OperationNotAllowedException {
    if (townDto.getId() == null) {
      throw new OperationNotAllowedException("town.error.id-not-exists");
    }
    if (!id.equals(townDto.getId())) {
      throw new OperationNotAllowedException("town.error.id-dont-match");
    }
    Town town =
        townRepository
            .findById(id)
            .orElseThrow(() -> new OperationNotAllowedException("town.error.id-not-exists"));
    Town townToUpdate = townDto.toTown();
    Town townUpdated = townRepository.save(townToUpdate);
    return new TownFullDTO(townUpdated);
  }

  @Transactional(readOnly = false, rollbackFor = Exception.class)
  public void delete(Long id) {
    townRepository.deleteById(id);
  }

  /** PRIVATE METHODS * */
  private Town findById(Long id) throws NotFoundException {
    return townRepository
        .findById(id)
        .orElseThrow(() -> new NotFoundException("Cannot find Town with id " + id));
  }
}
