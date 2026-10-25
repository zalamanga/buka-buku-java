package id.bukabuku.web;

import id.bukabuku.data.Store;
import id.bukabuku.model.Book;
import java.io.Serializable;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

@ManagedBean(name = "editor")
@ViewScoped
public class EditorBean implements Serializable {
  private static final long serialVersionUID = 1L;
  private Book book = new Book();
  private String date = "";
  private String query = "";
  private List<Book> books = List.of();
  private boolean failed, confirming;

  public String open() {
    Views.requireUser();
    reload();
    String edit =
        FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("edit");
    if (edit != null) {
      try {
        long id = Long.parseLong(edit);
        if (id <= 0) throw new IllegalArgumentException();
        Book found = new Store().book(id).orElse(null);
        if (found == null) Views.error("Buku tidak ditemukan.");
        else {
          book = found;
          date = book.getReleaseDate().toString();
        }
      } catch (IllegalArgumentException error) {
        Views.error("ID buku harus berupa angka positif.");
      } catch (RuntimeException error) {
        failed = true;
        Views.error("Data buku belum dapat dimuat. Coba lagi.");
      }
    }
    return null;
  }

  public void reload() {
    Views.requireUser();
    try {
      books = new Store().books(query == null ? "" : query);
      failed = false;
    } catch (RuntimeException error) {
      books = List.of();
      failed = true;
      Views.error("Daftar buku belum dapat dimuat. Coba lagi.");
    }
  }

  public String save() {
    Views.requireUser();
    try {
      book.setTitle(Validation.text(book.getTitle(), 1, 160, "Judul"));
      book.setAuthor(Validation.text(book.getAuthor(), 1, 120, "Penulis"));
      book.setSynopsis(Validation.text(book.getSynopsis(), 1, 2000, "Sinopsis"));
      book.setReleaseDate(Validation.date(date));
      new Store().save(book);
      FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
      Views.info("Buku tersimpan. Perubahan sudah tampil di katalog.");
      return "/dashboard?faces-redirect=true";
    } catch (IllegalArgumentException error) {
      Views.error(error.getMessage());
    } catch (RuntimeException error) {
      Views.error(
          "Buku belum tersimpan. Data mungkin berubah atau koneksi terputus. Muat ulang daftar sebelum mencoba lagi.");
    }
    return null;
  }

  public void confirmDelete() {
    Views.requireUser();
    confirming = book.getId() != null;
  }

  public void cancelDelete() {
    confirming = false;
  }

  public String delete() {
    Views.requireUser();
    if (!confirming || book.getId() == null) {
      Views.error("Pilih buku dan konfirmasi penghapusan terlebih dahulu.");
      return null;
    }
    try {
      boolean removed = new Store().delete(book.getId(), book.getVersion());
      FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
      Views.info(removed ? "Buku dihapus dari katalog." : "Buku sudah tidak tersedia.");
      return "/dashboard?faces-redirect=true";
    } catch (RuntimeException error) {
      Views.error("Buku belum dihapus. Muat ulang data dan coba lagi.");
      return null;
    }
  }

  public Book getBook() {
    return book;
  }

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public String getQuery() {
    return query;
  }

  public void setQuery(String query) {
    this.query = query;
  }

  public List<Book> getBooks() {
    return books;
  }

  public boolean isFailed() {
    return failed;
  }

  public boolean isNoResults() {
    return books.isEmpty() && !failed;
  }

  public boolean isEditing() {
    return book.getId() != null;
  }

  public boolean isConfirming() {
    return confirming;
  }
}
