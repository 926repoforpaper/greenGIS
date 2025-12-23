package es.udc.lbd.gema.lps.model.domain;

import jakarta.persistence.*;
import jakarta.persistence.Column;
import jakarta.persistence.SequenceGenerator;
import org.locationtech.jts.geom.MultiPolygon;

@Entity(name = "t_municipality")
@Table(name = "t_municipality")
public class Municipality {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "municipalityid")
  @SequenceGenerator(
      name = "municipalityid",
      sequenceName = "t_municipality_id_seq",
      initialValue = 1,
      allocationSize = 1)
  @Column(name = "id", unique = true)
  private Long id;

  @Column(name = "name")
  private String name;

  @Column(name = "ine_code")
  private String ineCode;

  @Column(name = "population")
  private Integer population;

  @Column(name = "extension", columnDefinition = "geometry(MultiPolygon, 4326)")
  private MultiPolygon extension;

  @Column(name = "area")
  private Double area;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "capital")
  private Town capital;

  public Municipality() {}

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

  public MultiPolygon getExtension() {
    return extension;
  }

  public void setExtension(MultiPolygon extension) {
    this.extension = extension;
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
