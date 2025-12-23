package es.udc.lbd.gema.lps.model.service.dto;

import es.udc.lbd.gema.lps.model.domain.*;

public class ProvinceDTO {

  private Long id;
  private String name;
  private Double population;
  private Double area;
  private TownDTO capital;

  public ProvinceDTO() {}

  public ProvinceDTO(Province province) {
    this.id = province.getId();
    this.name = province.getName();
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
    province.setPopulation(this.getPopulation());
    province.setArea(this.getArea());
    if (this.getCapital() != null) {
      province.setCapital(this.getCapital().toTown());
    }
    return province;
  }
}
