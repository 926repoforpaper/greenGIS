package es.udc.lbd.gema.lps.model.service.dto;

import es.udc.lbd.gema.lps.model.domain.*;

public class MunicipalityDTO {

  private Long id;
  private String name;
  private String ineCode;
  private Integer population;
  private Double area;
  private TownDTO capital;

  public MunicipalityDTO() {}

  public MunicipalityDTO(Municipality municipality) {
    this.id = municipality.getId();
    this.name = municipality.getName();
    this.ineCode = municipality.getIneCode();
    this.population = municipality.getPopulation();
    this.area = municipality.getArea();
    if (municipality.getCapital() != null) {
      this.capital = new TownDTO(municipality.getCapital());
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

  public String getIneCode() {
    return ineCode;
  }

  public void setIneCode(String ineCode) {
    this.ineCode = ineCode;
  }

  public Integer getPopulation() {
    return population;
  }

  public void setPopulation(Integer population) {
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

  public Municipality toMunicipality() {
    Municipality municipality = new Municipality();
    municipality.setId(this.getId());
    municipality.setName(this.getName());
    municipality.setIneCode(this.getIneCode());
    municipality.setPopulation(this.getPopulation());
    municipality.setArea(this.getArea());
    if (this.getCapital() != null) {
      municipality.setCapital(this.getCapital().toTown());
    }
    return municipality;
  }
}
