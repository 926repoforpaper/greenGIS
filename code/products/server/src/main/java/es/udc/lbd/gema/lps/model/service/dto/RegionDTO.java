package es.udc.lbd.gema.lps.model.service.dto;

import es.udc.lbd.gema.lps.model.domain.*;

public class RegionDTO {

  private Long id;
  private String name;
  private Double population;
  private Double area;
  private TownDTO capital;

  public RegionDTO() {}

  public RegionDTO(Region region) {
    this.id = region.getId();
    this.name = region.getName();
    this.population = region.getPopulation();
    this.area = region.getArea();
    if (region.getCapital() != null) {
      this.capital = new TownDTO(region.getCapital());
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

  public Region toRegion() {
    Region region = new Region();
    region.setId(this.getId());
    region.setName(this.getName());
    region.setPopulation(this.getPopulation());
    region.setArea(this.getArea());
    if (this.getCapital() != null) {
      region.setCapital(this.getCapital().toTown());
    }
    return region;
  }
}
