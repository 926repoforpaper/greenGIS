package es.udc.lbd.gema.lps.model.repository;

import es.udc.lbd.gema.lps.model.domain.Region;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RegionRepository
    extends JpaRepository<Region, Long>, JpaSpecificationExecutor<Region> {

  Optional<Region> findById(Long pk);

  Page<Region> findByIdIn(List<Long> pk, Pageable pageable);

  @Query(
      value =
          "select * from t_region a where ST_Intersects(a.extension, ST_MakeEnvelope(:xmin, :ymin, :xmax, :ymax, 4326))",
      nativeQuery = true)
  List<Region> getDataByBoundingBox(
      @Param("xmin") Double xmin,
      @Param("xmax") Double xmax,
      @Param("ymin") Double ymin,
      @Param("ymax") Double ymax);
}
