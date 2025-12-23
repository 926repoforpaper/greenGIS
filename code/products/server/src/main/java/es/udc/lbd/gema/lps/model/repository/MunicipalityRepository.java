package es.udc.lbd.gema.lps.model.repository;

import es.udc.lbd.gema.lps.model.domain.Municipality;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MunicipalityRepository
    extends JpaRepository<Municipality, Long>, JpaSpecificationExecutor<Municipality> {

  Optional<Municipality> findById(Long pk);

  Page<Municipality> findByIdIn(List<Long> pk, Pageable pageable);

  @Query(
      value =
          "select * from t_municipality a where ST_Intersects(a.extension, ST_MakeEnvelope(:xmin, :ymin, :xmax, :ymax, 4326))",
      nativeQuery = true)
  List<Municipality> getDataByBoundingBox(
      @Param("xmin") Double xmin,
      @Param("xmax") Double xmax,
      @Param("ymin") Double ymin,
      @Param("ymax") Double ymax);

  @Query(
      value =
          "select cast(json_build_object('type', 'FeatureCollection', 'features', json_agg(cast(ST_AsGeoJSON(a.*) as json))) as text) from t_municipality a where ST_Intersects(a.extension, ST_MakeEnvelope(:xmin, :ymin, :xmax, :ymax, 4326))",
      nativeQuery = true)
  String getDataByBoundingBoxJsonDb(
      @Param("xmin") Double xmin,
      @Param("xmax") Double xmax,
      @Param("ymin") Double ymin,
      @Param("ymax") Double ymax);
}
