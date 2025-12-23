package es.udc.lbd.gema.lps.model.service.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import es.udc.lbd.gema.lps.model.domain.*;
import es.udc.lbd.gema.lps.model.util.jackson.CustomGeometryDeserializer;
import es.udc.lbd.gema.lps.model.util.jackson.CustomGeometrySerializer;
import org.locationtech.jts.geom.MultiPolygon;

public class ProvinceFullDTO {
  private Long id;
  private String name;

  @JsonSerialize(using = CustomGeometrySerializer.class)
  @JsonDeserialize(using = CustomGeometryDeserializer.class)
  private MultiPolygon extension;

  private Double population;
  private Double area;
  private TownDTO capital;

  public ProvinceFullDTO() {}

  public ProvinceFullDTO(Province province) {
    this.id = province.getId();
    this.name = province.getName();
    this.extension = province.getExtension();
    this.population = province.getPopulation();
    this.area = province.getArea();
    if (province.getCapital() != null) {
      this.capital = new TownDTO(province.getCapital());
    }
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public MultiPolygon getExtension() {
    return extension;
  }

  public void setExtension(MultiPolygon extension) {
    this.extension = extension;
  }

  public Double getPopulation() {
    return population;
  }

  public void setPopulation(Double population) {
    this.population = population;
  }

  public Double getArea() {
    return area;
  }

  public void setArea(Double area) {
    this.area = area;
  }

  public TownDTO getCapital() {
    return capital;
  }

  public void setCapital(TownDTO capital) {
    this.capital = capital;
  }

  public Province toProvince() {
    Province province = new Province();
    province.setId(this.getId());
    province.setName(this.getName());
    province.setExtension(this.getExtension());
    province.setPopulation(this.getPopulation());
    province.setArea(this.getArea());
    if (this.getCapital() != null) {
      province.setCapital(this.getCapital().toTown());
    }
    return province;
  }
}
