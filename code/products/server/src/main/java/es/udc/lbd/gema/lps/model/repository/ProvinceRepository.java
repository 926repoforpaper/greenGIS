package es.udc.lbd.gema.lps.model.repository;

import es.udc.lbd.gema.lps.model.domain.Province;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProvinceRepository
    extends JpaRepository<Province, Long>, JpaSpecificationExecutor<Province> {

  Optional<Province> findById(Long pk);

  Page<Province> findByIdIn(List<Long> pk, Pageable pageable);

  @Query(
      value =
          "select * from t_province a where ST_Intersects(a.extension, ST_MakeEnvelope(:xmin, :ymin, :xmax, :ymax, 4326))",
      nativeQuery = true)
  List<Province> getDataByBoundingBox(
      @Param("xmin") Double xmin,
      @Param("xmax") Double xmax,
      @Param("ymin") Double ymin,
      @Param("ymax") Double ymax);
}
