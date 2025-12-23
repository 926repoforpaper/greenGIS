package es.udc.lbd.gema.lps.web.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import es.udc.lbd.gema.lps.model.service.MunicipalityService;
import es.udc.lbd.gema.lps.model.service.dto.MunicipalityDTO;
import es.udc.lbd.gema.lps.model.service.dto.MunicipalityFullDTO;
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
@RequestMapping(MunicipalityResource.MUNICIPALITY_RESOURCE_URL)
public class MunicipalityResource {
  public static final String MUNICIPALITY_RESOURCE_URL = "/api/entities/municipalitys";

  private static final Logger logger = LoggerFactory.getLogger(MunicipalityResource.class);

  @Inject private MunicipalityService municipalityService;

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
  public ResponseEntity<Page<MunicipalityDTO>> getAll(
      @PageableDefault(page = 0, size = 100000, sort = "id") Pageable pageable,
      @RequestParam(value = "filters", required = false) List<String> filters,
      @RequestParam(value = "search", required = false) String search) {
    Page<MunicipalityDTO> page = municipalityService.getAll(pageable, filters, search);
    HttpHeaders headers =
        PaginationUtil.generatePaginationHttpHeaders(page, MUNICIPALITY_RESOURCE_URL);
    return new ResponseEntity<>(page, headers, HttpStatus.OK);
  }

  @GetMapping("/geom/extension")
  public ResponseEntity<FeatureCollectionJSON> getExtension(
      @RequestParam(value = "xmin", required = false) Double xmin,
      @RequestParam(value = "xmax", required = false) Double xmax,
      @RequestParam(value = "ymin", required = false) Double ymin,
      @RequestParam(value = "ymax", required = false) Double ymax) {
    return new ResponseEntity<>(
        municipalityService.getExtension(xmin, xmax, ymin, ymax), HttpStatus.OK);
  }

  @GetMapping("/geom/extensionJsonDb")
  public ResponseEntity<String> getExtensionJsonDb(
      @RequestParam(value = "xmin", required = false) Double xmin,
      @RequestParam(value = "xmax", required = false) Double xmax,
      @RequestParam(value = "ymin", required = false) Double ymin,
      @RequestParam(value = "ymax", required = false) Double ymax)
      throws JsonProcessingException {
    return new ResponseEntity<>(
        municipalityService.getExtensionJsonDb(xmin, xmax, ymin, ymax), HttpStatus.OK);
  }

  @GetMapping("/{id}")
  public ResponseEntity<MunicipalityFullDTO> get(@PathVariable Long id) {
    try {
      return new ResponseEntity<>(municipalityService.get(id), HttpStatus.OK);
    } catch (NotFoundException e) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  @PostMapping
  public ResponseEntity<?> create(
      @Valid @RequestBody MunicipalityFullDTO municipality, Errors errors)
      throws URISyntaxException {

    if (errors.hasErrors()) {
      return ResponseEntity.badRequest().body(ValidationErrorUtils.getValidationErrors(errors));
    }
    try {
      MunicipalityFullDTO result = municipalityService.create(municipality);
      return ResponseEntity.created(
              new URI(String.format("%s/%s", MUNICIPALITY_RESOURCE_URL, result.getId())))
          .body(result);
    } catch (OperationNotAllowedException e) {
      return ResponseEntity.badRequest()
          .headers(HeaderUtil.createError(e.getMessage(), null))
          .body(null);
    }
  }

  @PutMapping("/{id}")
  public ResponseEntity<?> update(
      @PathVariable Long id, @Valid @RequestBody MunicipalityFullDTO municipality, Errors errors) {
    if (errors.hasErrors()) {
      return ResponseEntity.badRequest().body(ValidationErrorUtils.getValidationErrors(errors));
    }
    try {
      MunicipalityFullDTO result = municipalityService.update(id, municipality);
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
      municipalityService.delete(id);
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .headers(HeaderUtil.createError("psql.exception", null))
          .build();
    }

    return ResponseEntity.ok().build();
  }

  @PutMapping("/geom/restart")
  public ResponseEntity<?> updateGeom() {
    try {
      municipalityService.restartGeom();
      return ResponseEntity.ok().build();
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
  }
}
