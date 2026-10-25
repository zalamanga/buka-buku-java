package id.bukabuku.web;

import id.bukabuku.data.Database;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class AppLifecycle implements ServletContextListener {
  @Override
  public void contextDestroyed(ServletContextEvent event) {
    Database.close();
  }
}
