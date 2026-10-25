package id.bukabuku.web;

import java.time.LocalDate;
import java.util.Locale;

public final class Validation {
  private Validation() {}

  public static String text(String value, int min, int max, String label) {
    String clean = value == null ? "" : value.strip();
    if (clean.length() < min || clean.length() > max || clean.indexOf('\0') >= 0)
      throw new IllegalArgumentException(
          label + " harus berisi " + min + " sampai " + max + " karakter.");
    return clean;
  }

  public static String email(String value) {
    String clean = text(value, 3, 254, "Email").toLowerCase(Locale.ROOT);
    if (!clean.matches("[^\\s@]+@[^\\s@]+\\.[^\\s@]+"))
      throw new IllegalArgumentException("Masukkan alamat email yang valid.");
    return clean;
  }

  public static void password(String value) {
    if (value == null || value.length() < 12 || value.length() > 128 || value.isBlank())
      throw new IllegalArgumentException("Password harus berisi 12 sampai 128 karakter.");
  }

  public static LocalDate date(String value) {
    try {
      LocalDate date = LocalDate.parse(value);
      if (date.getYear() < 1000 || date.getYear() > 9999) throw new IllegalArgumentException();
      return date;
    } catch (RuntimeException error) {
      throw new IllegalArgumentException("Tanggal terbit harus valid dengan format YYYY-MM-DD.");
    }
  }
}
