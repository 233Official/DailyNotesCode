package com.summer233;

import java.lang.reflect.Field;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.RequestFacade;

public class SummerBasicListener implements ServletRequestListener {
   public SummerBasicListener() {
   }

   public void requestDestroyed(ServletRequestEvent servletRequestEvent) {
      try {
         RequestFacade request = (RequestFacade) servletRequestEvent.getServletRequest();
         Field f = request.getClass().getDeclaredField("request");
         f.setAccessible(true);
         Request req = (Request) f.get(request);
         req.getResponse().getWriter().println("\nBasicListener requestDestroyed Injected");
      } catch (Exception var5) {
         var5.printStackTrace();
      }

   }

   public void requestInitialized(ServletRequestEvent servletRequestEvent) {
   }
}
