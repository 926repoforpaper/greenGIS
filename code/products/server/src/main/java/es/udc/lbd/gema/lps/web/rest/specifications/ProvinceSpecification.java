package es.udc.lbd.gema.lps.web.rest.specifications;

import es.udc.lbd.gema.lps.model.domain.Province;
import es.udc.lbd.gema.lps.web.rest.util.specification_utils.SpecificationUtil;
import jakarta.persistence.criteria.*;
import jakarta.persistence.criteria.Path;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;

public class ProvinceSpecification {
  public static Specification<Province> searchAll(String search) {
    return new Specification<Province>() {
      @Override
      public Predicate toPredicate(
          Root<Province> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();
        String stringToFind = ("%" + search + "%").toLowerCase();
        Path path = SpecificationUtil.getPath(root, null);
        predicates.add(
            criteriaBuilder.like(
                criteriaBuilder.lower(path.get("id").as(String.class)), stringToFind));
        predicates.add(
            criteriaBuilder.like(
                criteriaBuilder.lower(path.get("name").as(String.class)), stringToFind));
        predicates.add(
            criteriaBuilder.like(
                criteriaBuilder.lower(path.get("population").as(String.class)), stringToFind));
        predicates.add(
            criteriaBuilder.like(
                criteriaBuilder.lower(path.get("area").as(String.class)), stringToFind));
        predicates.add(
            criteriaBuilder.like(
                criteriaBuilder.lower(
                    root.join("capital", JoinType.LEFT).get("id").as(String.class)),
                stringToFind));
        return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
      }
    };
  }
}
