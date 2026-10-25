package id.bukabuku.web;

import id.bukabuku.data.Store;
import id.bukabuku.model.User;
import id.bukabuku.security.Passwords;
import id.bukabuku.security.SecurityFilter;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;

@ManagedBean(name = "auth")
@RequestScoped
public class AuthBean {
  private static final Logger LOG = Logger.getLogger(AuthBean.class.getName());
  private String name, email, password, confirmation;

  public String register() {
    try {
      String cleanName = Validation.text(name, 2, 100, "Nama");
      String cleanEmail = Validation.email(email);
      Validation.password(password);
      if (!password.equals(confirmation))
        throw new IllegalArgumentException("Konfirmasi password tidak sama.");
      Store store = new Store();
      if (store.user(cleanEmail).isPresent()) {
        Views.error("Email sudah terdaftar. Gunakan halaman masuk.");
        return null;
      }
      User user = new User();
      user.setName(cleanName);
      user.setEmail(cleanEmail);
      user.setPasswordHash(Passwords.hash(password));
      store.addUser(user);
      FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
      Views.info("Akun dibuat. Silakan masuk dengan email dan password tadi.");
      return "/login?faces-redirect=true";
    } catch (IllegalArgumentException error) {
      Views.error(error.getMessage());
    } catch (RuntimeException error) {
      LOG.warning("Registrasi gagal: " + error.getClass().getSimpleName());
      Views.error("Akun belum tersimpan. Periksa koneksi atau coba email lain.");
    } finally {
      password = null;
      confirmation = null;
    }
    return null;
  }

  public String login() {
    try {
      String cleanEmail = Validation.email(email);
      Validation.password(password);
      User user = new Store().user(cleanEmail).orElse(null);
      if (user == null || !Passwords.verify(password, user.getPasswordHash())) {
        Views.error("Email atau password tidak cocok.");
        return null;
      }
      HttpServletRequest request =
          (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
      request.changeSessionId();
      Views.session().setAttribute(SecurityFilter.USER_ID, user.getId());
      Views.session().setAttribute("userName", user.getName());
      return "/dashboard?faces-redirect=true";
    } catch (IllegalArgumentException error) {
      Views.error(error.getMessage());
    } catch (RuntimeException error) {
      LOG.warning("Login gagal: " + error.getClass().getSimpleName());
      Views.error("Belum dapat masuk. Periksa koneksi lalu coba lagi.");
    } finally {
      password = null;
    }
    return null;
  }

  public String logout() {
    FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
    return "/index?faces-redirect=true";
  }

  public boolean isLoggedIn() {
    return Views.session().getAttribute(SecurityFilter.USER_ID) != null;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getConfirmation() {
    return confirmation;
  }

  public void setConfirmation(String confirmation) {
    this.confirmation = confirmation;
  }
}
