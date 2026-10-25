package id.bukabuku.data;

import id.bukabuku.model.Book;
import id.bukabuku.model.User;
import java.sql.SQLException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.exception.JDBCConnectionException;

public final class Database {
  private static SessionFactory factory;

  private Database() {}

  public static synchronized SessionFactory factory() {
    if (factory == null || factory.isClosed()) {
      factory =
          configuration(required("DB_URL"), required("DB_USER"), required("DB_PASSWORD"))
              .buildSessionFactory();
    }
    return factory;
  }

  public static Configuration configuration(String url, String user, String password) {
    Configuration cfg =
        new Configuration().addAnnotatedClass(Book.class).addAnnotatedClass(User.class);
    cfg.setProperty("hibernate.connection.driver_class", "com.mysql.cj.jdbc.Driver");
    cfg.setProperty("hibernate.connection.url", url);
    cfg.setProperty("hibernate.connection.username", user);
    cfg.setProperty("hibernate.connection.password", password);
    cfg.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect");
    cfg.setProperty("hibernate.hbm2ddl.auto", "validate");
    cfg.setProperty("hibernate.connection.pool_size", "5");
    cfg.setProperty("hibernate.show_sql", "false");
    return cfg;
  }

  private static String required(String name) {
    String value = System.getenv(name);
    if (value == null || value.isBlank())
      throw new IllegalStateException("Konfigurasi " + name + " belum tersedia.");
    return value;
  }

  public static synchronized void close() {
    SessionFactory closing = factory;
    factory = null;
    if (closing != null) closing.close();
  }

  static boolean connectionFailure(Throwable failure) {
    Throwable current = failure;
    for (int depth = 0; current != null && depth < 16; depth++, current = current.getCause()) {
      if (current instanceof JDBCConnectionException) return true;
      if (current instanceof SQLException sql
          && sql.getSQLState() != null
          && sql.getSQLState().startsWith("08"))
        return true;
    }
    return false;
  }

  static synchronized void invalidate(SessionFactory failed, RuntimeException originalFailure) {
    if (factory != failed) return;
    factory = null;
    try {
      failed.close();
    } catch (RuntimeException cleanupFailure) {
      if (cleanupFailure != originalFailure) originalFailure.addSuppressed(cleanupFailure);
    }
  }
}
