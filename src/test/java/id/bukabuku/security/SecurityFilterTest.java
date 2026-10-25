package id.bukabuku.security;

import java.io.IOException;
import javax.servlet.*;
import javax.servlet.http.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SecurityFilterTest {
  private HttpServletRequest req;
  private HttpServletResponse res;
  private HttpSession session;
  private FilterChain chain;

  @BeforeEach
  void setup() {
    req = mock(HttpServletRequest.class);
    res = mock(HttpServletResponse.class);
    session = mock(HttpSession.class);
    chain = mock(FilterChain.class);
    when(req.getSession()).thenReturn(session);
    when(req.getContextPath()).thenReturn("/demo");
    when(req.getServletPath()).thenReturn("/login.xhtml");
    when(req.getMethod()).thenReturn("GET");
    when(session.getAttribute(SecurityFilter.TOKEN)).thenReturn("test-token");
  }

  private void run() throws IOException, ServletException {
    new SecurityFilter().doFilter(req, res, chain);
  }

  @Test
  void dashboardRequiresSessionEvenWhenOriginalUriWasEncoded() throws Exception {
    when(req.getServletPath()).thenReturn("/dashboard.xhtml");
    when(req.getRequestURI()).thenReturn("/demo/%64ashboard.xhtml");
    run();
    verify(res).sendRedirect("/demo/login.xhtml");
    verifyNoInteractions(chain);
  }

  @Test
  void authorizedDashboardLoads() throws Exception {
    when(req.getServletPath()).thenReturn("/dashboard.xhtml");
    when(session.getAttribute(SecurityFilter.USER_ID)).thenReturn(1L);
    run();
    verify(chain).doFilter(req, res);
  }

  @Test
  void forgedPostIsRejected() throws Exception {
    when(req.getMethod()).thenReturn("POST");
    run();
    verify(res).sendError(eq(403), anyString());
    verifyNoInteractions(chain);
  }

  @Test
  void validPostIsAccepted() throws Exception {
    when(req.getMethod()).thenReturn("POST");
    when(req.getParameter("csrfToken")).thenReturn("test-token");
    run();
    verify(chain).doFilter(req, res);
  }

  @Test
  void unusedHttpMethodRejected() throws Exception {
    when(req.getMethod()).thenReturn("DELETE");
    run();
    verify(res).sendError(405);
  }

  @Test
  void tokenComparisonIsExactAndBounded() {
    assertTrue(SecurityFilter.validToken("abc", "abc"));
    assertFalse(SecurityFilter.validToken("abc", "ABC"));
    assertFalse(SecurityFilter.validToken("abc", null));
    assertFalse(SecurityFilter.validToken("x", "x".repeat(101)));
  }
}
