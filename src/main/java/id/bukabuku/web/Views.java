package id.bukabuku.web;

import id.bukabuku.security.SecurityFilter;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

final class Views {
  private Views() {}

  static HttpSession session() {
    return (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
  }

  static void requireUser() {
    if (session().getAttribute(SecurityFilter.USER_ID) == null)
      throw new SecurityException("Masuk kembali untuk melanjutkan.");
  }

  static void error(String message) {
    FacesContext.getCurrentInstance()
        .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, message, null));
  }

  static void info(String message) {
    FacesContext.getCurrentInstance()
        .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, message, null));
  }
}
