package id.bukabuku.data;

import id.bukabuku.model.Book;
import id.bukabuku.model.User;
import java.time.LocalDate;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class StoreTest {
  private SessionFactory factory;
  private Store store;

  @BeforeEach
  void database() {
    factory =
        Database.configuration(
                "jdbc:h2:mem:" + java.util.UUID.randomUUID() + ";MODE=MySQL", "sa", "")
            .setProperty("hibernate.connection.driver_class", "org.h2.Driver")
            .setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect")
            .setProperty("hibernate.hbm2ddl.auto", "create-drop")
            .buildSessionFactory();
    store = new Store(factory);
  }

  @AfterEach
  void close() {
    factory.close();
  }

  private Book book(String title) {
    return new Book(title, "Penulis contoh", LocalDate.of(2024, 1, 2), "Sinopsis contoh");
  }

  @Test
  void persistReadUpdateDelete() {
    Book book = book("Satu");
    store.save(book);
    assertNotNull(book.getId());
    Book saved = store.book(book.getId()).orElseThrow();
    assertEquals("Satu", saved.getTitle());
    saved.setTitle("Dua");
    store.save(saved);
    Book changed = store.book(book.getId()).orElseThrow();
    assertEquals("Dua", changed.getTitle());
    assertTrue(store.delete(changed.getId(), changed.getVersion()));
    assertTrue(store.books("").isEmpty());
  }

  @Test
  void searchIsCaseInsensitiveAndBound() {
    store.save(book("Buku pilihan"));
    assertEquals(1, store.books("PILIHAN").size());
    assertEquals(1, store.books("penulis").size());
    assertTrue(store.books("' OR 1=1 --").isEmpty());
    assertTrue(store.books("%").isEmpty());
  }

  @Test
  void missingRecordIsExplicit() {
    assertTrue(store.book(999).isEmpty());
    assertFalse(store.delete(999, 0));
  }

  @Test
  void staleUpdateDoesNotOverwrite() {
    Book book = book("Awal");
    store.save(book);
    Book first = store.book(book.getId()).orElseThrow(),
        stale = store.book(book.getId()).orElseThrow();
    first.setTitle("Baru");
    store.save(first);
    stale.setTitle("Lama");
    assertThrows(RuntimeException.class, () -> store.save(stale));
    assertEquals("Baru", store.book(book.getId()).orElseThrow().getTitle());
  }

  @Test
  void staleDeleteDoesNotRemove() {
    Book book = book("Awal");
    store.save(book);
    Book changed = store.book(book.getId()).orElseThrow();
    changed.setTitle("Baru");
    store.save(changed);
    assertThrows(IllegalStateException.class, () -> store.delete(book.getId(), 0));
    assertTrue(store.book(book.getId()).isPresent());
  }

  @Test
  void uniqueEmailFailureRollsBackAndNextTransactionWorks() {
    User first = user("one@example.test");
    store.addUser(first);
    assertThrows(RuntimeException.class, () -> store.addUser(user("one@example.test")));
    store.addUser(user("two@example.test"));
    assertTrue(store.user("two@example.test").isPresent());
    assertTrue(store.user("' OR 1=1 --").isEmpty());
  }

  private User user(String email) {
    User user = new User();
    user.setName("Pengguna uji");
    user.setEmail(email);
    user.setPasswordHash("synthetic-test-hash");
    return user;
  }
}
