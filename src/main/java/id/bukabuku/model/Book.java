package id.bukabuku.model;

import java.time.LocalDate;
import javax.persistence.*;

@Entity
@Table(name = "books")
public class Book implements java.io.Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 160)
  private String title;

  @Column(nullable = false, length = 120)
  private String author;

  @Column(name = "release_date", nullable = false)
  private LocalDate releaseDate;

  @Column(nullable = false, length = 2000)
  private String synopsis;

  @Column(length = 40)
  private String cover;

  @Version private long version;

  public Book() {}

  public Book(String title, String author, LocalDate releaseDate, String synopsis) {
    this.title = title;
    this.author = author;
    this.releaseDate = releaseDate;
    this.synopsis = synopsis;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getAuthor() {
    return author;
  }

  public void setAuthor(String author) {
    this.author = author;
  }

  public LocalDate getReleaseDate() {
    return releaseDate;
  }

  public void setReleaseDate(LocalDate releaseDate) {
    this.releaseDate = releaseDate;
  }

  public String getSynopsis() {
    return synopsis;
  }

  public void setSynopsis(String synopsis) {
    this.synopsis = synopsis;
  }

  public String getCover() {
    return cover;
  }

  public void setCover(String cover) {
    this.cover = cover;
  }

  public long getVersion() {
    return version;
  }

  public void setVersion(long version) {
    this.version = version;
  }

  public boolean isHasCover() {
    return cover != null && cover.matches("buku[1-9]\\.(jpg|png|jpeg)");
  }
}
