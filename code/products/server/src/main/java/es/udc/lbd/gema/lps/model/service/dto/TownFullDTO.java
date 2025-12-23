package es.udc.lbd.gema.lps.model.service.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import es.udc.lbd.gema.lps.model.domain.*;
import es.udc.lbd.gema.lps.model.util.jackson.CustomGeometryDeserializer;
import es.udc.lbd.gema.lps.model.util.jackson.CustomGeometrySerializer;
import java.time.LocalDate;
import org.locationtech.jts.geom.Point;

public class TownFullDTO {
  private Long id;
  private String name;
  private Integer code;

  @JsonSerialize(using = CustomGeometrySerializer.class)
  @JsonDeserialize(using = CustomGeometryDeserializer.class)
  private Point location;

  private Double population;
  private LocalDate populationDate;

  public TownFullDTO() {}

  public TownFullDTO(Town town) {
    this.id = town.getId();
    this.name = town.getName();
    this.code = town.getCode();
    this.location = town.getLocation();
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

  public Point getLocation() {
    return location;
  }

  public void setLocation(Point location) {
    this.location = location;
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
    town.setLocation(this.getLocation());
    town.setPopulation(this.getPopulation());
    town.setPopulationDate(this.getPopulationDate());
    return town;
  }
}
