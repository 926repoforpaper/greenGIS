package es.udc.lbd.gema.lps.model.repository;

import es.udc.lbd.gema.lps.model.domain.Town;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TownRepository extends JpaRepository<Town, Long>, JpaSpecificationExecutor<Town> {

  Optional<Town> findById(Long pk);

  Page<Town> findByIdIn(List<Long> pk, Pageable pageable);

  @Query(
      value =
          "select * from t_town a where a.id not in (select coalesce(capital, -1) from t_municipality)",
      nativeQuery = true)
  List<Town> municipalityIsNull();

  @Query(
      value =
          "select * from t_town a where a.id not in (select coalesce(capital, -1) from t_province)",
      nativeQuery = true)
  List<Town> provinceIsNull();

  @Query(
      value =
          "select * from t_town a where a.id not in (select coalesce(capital, -1) from t_region)",
      nativeQuery = true)
  List<Town> regionIsNull();

  @Query(
      value =
          "select * from t_town a where ST_Intersects(a.location, ST_MakeEnvelope(:xmin, :ymin, :xmax, :ymax, 4326))",
      nativeQuery = true)
  List<Town> getDataByBoundingBox(
      @Param("xmin") Double xmin,
      @Param("xmax") Double xmax,
      @Param("ymin") Double ymin,
      @Param("ymax") Double ymax);
}
