package id.bukabuku.data;

import id.bukabuku.model.Book;
import id.bukabuku.model.User;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

public class Store {
  private final SessionFactory factory;

  public Store() {
    this(Database.factory());
  }

  public Store(SessionFactory factory) {
    this.factory = factory;
  }

  private <T> T transaction(Function<Session, T> operation) {
    try (Session session = factory.openSession()) {
      Transaction tx = session.beginTransaction();
      try {
        T result = operation.apply(session);
        tx.commit();
        return result;
      } catch (RuntimeException error) {
        try {
          if (tx.isActive()) tx.rollback();
        } catch (RuntimeException rollbackFailure) {
          if (rollbackFailure != error) error.addSuppressed(rollbackFailure);
        }
        throw error;
      }
    } catch (RuntimeException failure) {
      if (Database.connectionFailure(failure)) Database.invalidate(factory, failure);
      throw failure;
    }
  }

  public List<Book> books(String term) {
    return transaction(
        s ->
            s.createQuery(
                    "from Book where locate(:term, lower(title)) > 0 or locate(:term, lower(author)) > 0 order by id desc",
                    Book.class)
                .setParameter("term", term.strip().toLowerCase(java.util.Locale.ROOT))
                .getResultList());
  }

  public Optional<Book> book(long id) {
    return transaction(s -> Optional.ofNullable(s.get(Book.class, id)));
  }

  public Optional<User> user(String email) {
    return transaction(
        s ->
            s.createQuery("from User where email = :email", User.class)
                .setParameter("email", email)
                .uniqueResultOptional());
  }

  public void addUser(User user) {
    transaction(
        s -> {
          s.persist(user);
          return null;
        });
  }

  public void save(Book book) {
    transaction(
        s -> {
          if (book.getId() == null) s.persist(book);
          else s.merge(book);
          return null;
        });
  }

  public boolean delete(long id, long version) {
    return transaction(
        s -> {
          Book book = s.get(Book.class, id);
          if (book == null) return false;
          if (book.getVersion() != version)
            throw new IllegalStateException("Data sudah berubah; muat ulang sebelum menghapus.");
          s.delete(book);
          return true;
        });
  }
}
