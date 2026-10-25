package id.bukabuku.data;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;
import org.hibernate.exception.JDBCConnectionException;
import org.junit.jupiter.api.Test;

class DatabaseFailureTest {
  @Test
  void distinguishesLostConnectionsFromValidationAndConstraintFailures() {
    assertTrue(
        Database.connectionFailure(
            new IllegalStateException(new SQLException("demo", "08S01"))));
    assertTrue(
        Database.connectionFailure(
            new JDBCConnectionException("demo", new SQLException("demo"))));
    assertFalse(Database.connectionFailure(new SQLException("demo duplicate", "23000")));
    assertFalse(Database.connectionFailure(new IllegalArgumentException("demo invalid input")));
    assertFalse(Database.connectionFailure(null));
  }
}
