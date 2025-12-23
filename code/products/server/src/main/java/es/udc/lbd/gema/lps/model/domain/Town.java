package es.udc.lbd.gema.lps.model.domain;

import jakarta.persistence.*;
import jakarta.persistence.Column;
import jakarta.persistence.SequenceGenerator;
import java.time.LocalDate;
import org.locationtech.jts.geom.Point;

@Entity(name = "t_town")
@Table(name = "t_town")
public class Town {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "townid")
  @SequenceGenerator(
      name = "townid",
      sequenceName = "t_town_id_seq",
      initialValue = 1,
      allocationSize = 1)
  @Column(name = "id", unique = true)
  private Long id;

  @Column(name = "name")
  private String name;

  @Column(name = "code")
  private Integer code;

  @Column(name = "location", columnDefinition = "geometry(Point, 4326)")
  private Point location;

  @Column(name = "population")
  private Double population;

  @Column(name = "population_date")
  private LocalDate populationDate;

  @OneToOne(fetch = FetchType.LAZY, mappedBy = "capital")
  private Region region;

  @OneToOne(fetch = FetchType.LAZY, mappedBy = "capital")
  private Province province;

  @OneToOne(fetch = FetchType.LAZY, mappedBy = "capital")
  private Municipality municipality;

  public Town() {}

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

  public Region getRegion() {
    return region;
  }

  public void setRegion(Region region) {
    this.region = region;
  }

  public Province getProvince() {
    return province;
  }

  public void setProvince(Province province) {
    this.province = province;
  }

  public Municipality getMunicipality() {
    return municipality;
  }

  public void setMunicipality(Municipality municipality) {
    this.municipality = municipality;
  }
}
