package es.udc.lbd.gema.lps.web.rest;

import es.udc.lbd.gema.lps.model.service.TownService;
import es.udc.lbd.gema.lps.model.service.dto.TownDTO;
import es.udc.lbd.gema.lps.model.service.dto.TownFullDTO;
import es.udc.lbd.gema.lps.model.service.exceptions.NotFoundException;
import es.udc.lbd.gema.lps.model.service.exceptions.OperationNotAllowedException;
import es.udc.lbd.gema.lps.web.rest.custom.FeatureCollectionJSON;
import es.udc.lbd.gema.lps.web.rest.custom.ValidationErrorUtils;
import es.udc.lbd.gema.lps.web.rest.util.HeaderUtil;
import es.udc.lbd.gema.lps.web.rest.util.PaginationUtil;
import es.udc.lbd.gema.lps.web.rest.util.specification_utils.*;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(TownResource.TOWN_RESOURCE_URL)
public class TownResource {
  public static final String TOWN_RESOURCE_URL = "/api/entities/towns";

  private static final Logger logger = LoggerFactory.getLogger(TownResource.class);

  @Inject private TownService townService;

  @GetMapping("/without/municipality")
  public List<TownDTO> getAllWithoutMunicipality() {
    return townService.getAllWithoutMunicipality();
  }

  @GetMapping("/without/province")
  public List<TownDTO> getAllWithoutProvince() {
    return townService.getAllWithoutProvince();
  }

  @GetMapping("/without/region")
  public List<TownDTO> getAllWithoutRegion() {
    return townService.getAllWithoutRegion();
  }

  @GetMapping("/geom/location")
  public ResponseEntity<FeatureCollectionJSON> getLocation(
      @RequestParam(value = "xmin", required = false) Double xmin,
      @RequestParam(value = "xmax", required = false) Double xmax,
      @RequestParam(value = "ymin", required = false) Double ymin,
      @RequestParam(value = "ymax", required = false) Double ymax) {
    return new ResponseEntity<>(townService.getLocation(xmin, xmax, ymin, ymax), HttpStatus.OK);
  }

  /**
   * Get entities in pages<br>
   *
   * <p>If no <code>pageNum</code> is provided, then all results will be returned in one page
   *
   * @param pageable Contains all information about number page, items per page, order, ...
   * @param search Contains a text that will be searched in all the properties of the entity
   * @param filters Static filters to apply
   * @return Paginated entities
   */
  @GetMapping
  public ResponseEntity<Page<TownDTO>> getAll(
      @PageableDefault(page = 0, size = 100000, sort = "id") Pageable pageable,
      @RequestParam(value = "filters", required = false) List<String> filters,
      @RequestParam(value = "search", required = false) String search) {
    Page<TownDTO> page = townService.getAll(pageable, filters, search);
    HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, TOWN_RESOURCE_URL);
    return new ResponseEntity<>(page, headers, HttpStatus.OK);
  }

  @GetMapping("/{id}")
  public ResponseEntity<TownFullDTO> get(@PathVariable Long id) {
    try {
      return new ResponseEntity<>(townService.get(id), HttpStatus.OK);
    } catch (NotFoundException e) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  @PostMapping
  public ResponseEntity<?> create(@Valid @RequestBody TownFullDTO town, Errors errors)
      throws URISyntaxException {

    if (errors.hasErrors()) {
      return ResponseEntity.badRequest().body(ValidationErrorUtils.getValidationErrors(errors));
    }
    try {
      TownFullDTO result = townService.create(town);
      return ResponseEntity.created(
              new URI(String.format("%s/%s", TOWN_RESOURCE_URL, result.getId())))
          .body(result);
    } catch (OperationNotAllowedException e) {
      return ResponseEntity.badRequest()
          .headers(HeaderUtil.createError(e.getMessage(), null))
          .body(null);
    }
  }

  @PutMapping("/{id}")
  public ResponseEntity<?> update(
      @PathVariable Long id, @Valid @RequestBody TownFullDTO town, Errors errors) {
    if (errors.hasErrors()) {
      return ResponseEntity.badRequest().body(ValidationErrorUtils.getValidationErrors(errors));
    }
    try {
      TownFullDTO result = townService.update(id, town);
      return ResponseEntity.ok().body(result);
    } catch (OperationNotAllowedException e) {
      return ResponseEntity.badRequest()
          .headers(HeaderUtil.createError(e.getMessage(), null))
          .body(null);
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    try {
      townService.delete(id);
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .headers(HeaderUtil.createError("psql.exception", null))
          .build();
    }

    return ResponseEntity.ok().build();
  }
}
