package id.bukabuku.security;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.*;

@WebFilter("/*")
public class SecurityFilter implements Filter {
  public static final String USER_ID = "userId";
  public static final String TOKEN = "csrfToken";
  private final SecureRandom random = new SecureRandom();

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    HttpServletRequest req = (HttpServletRequest) request;
    HttpServletResponse res = (HttpServletResponse) response;
    req.setCharacterEncoding("UTF-8");
    res.setCharacterEncoding("UTF-8");
    res.setHeader("X-Content-Type-Options", "nosniff");
    res.setHeader("X-Frame-Options", "DENY");
    res.setHeader("Referrer-Policy", "same-origin");
    res.setHeader(
        "Content-Security-Policy", "frame-ancestors 'none'; base-uri 'self'; object-src 'none'");
    String path = req.getServletPath();
    if (path.startsWith("/resources/") || path.startsWith("/javax.faces.resource/")) {
      chain.doFilter(req, res);
      return;
    }
    res.setHeader("Cache-Control", "no-store");
    HttpSession session = req.getSession();
    if (session.getAttribute(TOKEN) == null) {
      byte[] bytes = new byte[32];
      random.nextBytes(bytes);
      session.setAttribute(TOKEN, Base64.getUrlEncoder().withoutPadding().encodeToString(bytes));
    }
    if (path.equals("/dashboard.xhtml") && session.getAttribute(USER_ID) == null) {
      res.sendRedirect(req.getContextPath() + "/login.xhtml");
      return;
    }
    if (req.getMethod().equals("POST")
        && !validToken((String) session.getAttribute(TOKEN), req.getParameter(TOKEN))) {
      res.sendError(403, "Form kedaluwarsa. Muat ulang halaman lalu coba lagi.");
      return;
    }
    if (!req.getMethod().equals("GET")
        && !req.getMethod().equals("POST")
        && !req.getMethod().equals("HEAD")) {
      res.sendError(405);
      return;
    }
    chain.doFilter(req, res);
  }

  static boolean validToken(String expected, String actual) {
    return expected != null
        && actual != null
        && actual.length() <= 100
        && MessageDigest.isEqual(
            expected.getBytes(StandardCharsets.UTF_8), actual.getBytes(StandardCharsets.UTF_8));
  }
}
