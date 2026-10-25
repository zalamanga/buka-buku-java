package id.bukabuku.web;

import id.bukabuku.data.Store;
import id.bukabuku.model.Book;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

@ManagedBean(name = "catalog")
@RequestScoped
public class CatalogBean {
  private List<Book> books;
  private boolean failed;
  private String query;

  private void load() {
    if (books != null) return;
    query =
        FacesContext.getCurrentInstance()
            .getExternalContext()
            .getRequestParameterMap()
            .getOrDefault("q", "")
            .strip();
    if (query.length() > 160) query = query.substring(0, 160);
    try {
      books = new Store().books(query);
    } catch (RuntimeException error) {
      books = List.of();
      failed = true;
    }
  }

  public List<Book> getBooks() {
    load();
    return books;
  }

  public boolean isFailed() {
    load();
    return failed;
  }

  public boolean isNoResults() {
    load();
    return books.isEmpty() && !failed;
  }

  public String getQuery() {
    load();
    return query;
  }
}
