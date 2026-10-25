package id.bukabuku.security;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PasswordsTest {
  @Test
  void correctAndWrongPasswords() {
    String hash = Passwords.hash("contoh-uji-12-karakter");
    assertTrue(Passwords.verify("contoh-uji-12-karakter", hash));
    assertFalse(Passwords.verify("salah", hash));
    assertFalse(hash.contains("contoh-uji"));
  }

  @Test
  void eachHashHasDifferentSalt() {
    assertNotEquals(Passwords.hash("contoh-uji-password"), Passwords.hash("contoh-uji-password"));
  }

  @Test
  void malformedHashesAreRejected() {
    for (String hash :
        new String[] {
          "", "plaintext", "pbkdf2-sha256$999999999$AA==$AA==", "pbkdf2-sha256$600000$bad$bad"
        }) assertFalse(Passwords.verify("password", hash));
    assertFalse(Passwords.verify(null, null));
  }
}
