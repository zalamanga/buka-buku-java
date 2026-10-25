package id.bukabuku.web;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ValidationTest {
  @Test
  void normalizesEmail() {
    assertEquals("demo@example.test", Validation.email("  DEMO@example.test "));
  }

  @Test
  void rejectsInvalidEmails() {
    for (String email : new String[] {"", "a@", "a b@test.com", "a@test"})
      assertThrows(IllegalArgumentException.class, () -> Validation.email(email));
  }

  @Test
  void trimsRequiredTextAndEnforcesBounds() {
    assertEquals("Buku", Validation.text(" Buku ", 1, 5, "Judul"));
    assertThrows(IllegalArgumentException.class, () -> Validation.text(" ", 1, 5, "Judul"));
    assertThrows(IllegalArgumentException.class, () -> Validation.text("panjang", 1, 5, "Judul"));
    assertThrows(IllegalArgumentException.class, () -> Validation.text("a\0b", 1, 5, "Judul"));
  }

  @Test
  void checksPasswordLengthWithoutTrimmingSecret() {
    assertDoesNotThrow(() -> Validation.password("contoh-password-12"));
    for (String value : new String[] {"pendek", " ".repeat(12), "x".repeat(129)})
      assertThrows(IllegalArgumentException.class, () -> Validation.password(value));
  }

  @Test
  void rejectsImpossibleDatesAndYearRange() {
    assertEquals(LocalDate.of(2024, 2, 29), Validation.date("2024-02-29"));
    for (String value : new String[] {"2023-02-29", "2024-13-01", "0001-01-01", "", "besok"})
      assertThrows(IllegalArgumentException.class, () -> Validation.date(value));
  }
}
