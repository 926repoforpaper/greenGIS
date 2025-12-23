package es.udc.lbd.gema.lps.model.domain;

import jakarta.persistence.*;
import jakarta.persistence.Column;
import jakarta.persistence.SequenceGenerator;
import org.locationtech.jts.geom.MultiPolygon;

@Entity(name = "t_region")
@Table(name = "t_region")
public class Region {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "regionid")
  @SequenceGenerator(
      name = "regionid",
      sequenceName = "t_region_id_seq",
      initialValue = 1,
      allocationSize = 1)
  @Column(name = "id", unique = true)
  private Long id;

  @Column(name = "name")
  private String name;

  @Column(name = "extension", columnDefinition = "geometry(MultiPolygon, 4326)")
  private MultiPolygon extension;

  @Column(name = "population")
  private Double population;

  @Column(name = "area")
  private Double area;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "capital")
  private Town capital;

  public Region() {}

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

  public Town getCapital() {
    return capital;
  }

  public void setCapital(Town capital) {
    this.capital = capital;
  }
}
