package es.udc.lbd.gema.lps.model.service.dto;

import es.udc.lbd.gema.lps.model.domain.*;
import java.time.LocalDate;

public class TownDTO {

  private Long id;
  private String name;
  private Integer code;
  private Double population;
  private LocalDate populationDate;

  public TownDTO() {}

  public TownDTO(Town town) {
    this.id = town.getId();
    this.name = town.getName();
    this.code = town.getCode();
    this.population = town.getPopulation();
    this.populationDate = town.getPopulationDate();
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

  public Integer getCode() {
    return code;
  }

  public void setCode(Integer code) {
    this.code = code;
  }

  public Double getPopulation() {
    return population;
  }

  public void setPopulation(Double population) {
    this.population = population;
  }

  public LocalDate getPopulationDate() {
    return populationDate;
  }

  public void setPopulationDate(LocalDate populationDate) {
    this.populationDate = populationDate;
  }

  public Town toTown() {
    Town town = new Town();
    town.setId(this.getId());
    town.setName(this.getName());
    town.setCode(this.getCode());
    town.setPopulation(this.getPopulation());
    town.setPopulationDate(this.getPopulationDate());
    return town;
  }
}
