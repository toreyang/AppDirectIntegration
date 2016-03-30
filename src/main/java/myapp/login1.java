package myapp;

import java.io.IOException;
import javax.servlet.http.*;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletContext;

import javax.cache.Cache;
import javax.cache.CacheException;
import javax.cache.CacheFactory;
import javax.cache.CacheManager;
import java.util.Enumeration;
import java.io.PrintWriter;
import java.util.*;

public class login1 extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        resp.setContentType("text/html");
        PrintWriter pw = resp.getWriter();
        pw.println("<html>");
        pw.println("<head><title>Display the Subscription status</title></head>");
        pw.println("<body>");

        Map<String, String> paraMap = new HashMap<String, String>();
        Enumeration<String> parameterNames = req.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String paramName = parameterNames.nextElement();
            //pw.println(paramName+" : ");

            String[] paraValues = req.getParameterValues(paramName);
            for (int i = 0; i < paraValues.length; i++) {
                paraMap.put(paramName, paraValues[i]);
                //pw.println("      " + paraValues[i]+"<br>");
            }
        }
        String fullname = paraMap.get("openid.ext1.value.fullname");
        pw.println("<div align=\"center\"><h2>Welcome " + fullname + "</h2></div>");


        HashMap<String, AppSubscription> appSubMap=null;
        try {
            Cache cache;
            CacheFactory cacheFactory = CacheManager.getInstance().getCacheFactory();
            cache = cacheFactory.createCache(Collections.emptyMap());
            appSubMap = (HashMap<String, AppSubscription>) cache.get("SubscriptionMap");
        } catch (CacheException e) {

        }
        if (appSubMap != null) {

            if (appSubMap.isEmpty())
                pw.println("<h3> There is no order for this product</h3>");

            for (String creator : appSubMap.keySet()) {
                AppSubscription appSub = appSubMap.get(creator);
                pw.println("<ol>");
                if (fullname.equals(creator)) {
                    pw.println("<li><h3>You purchased the product NEXUS VIRTUAL ITEM1<h3></li>");
                    pw.println("<h3>Edition Code: " + appSub.getEditionCode() + "</h3>");
                }
                Set<String> assignees = appSub.getAssignees();
                if (assignees.contains(fullname)) {
                    pw.println("Your are the one of the users of this product");
                }
                pw.println("</ol>");
            }
        }
        pw.println("</body></html>");

    }
}

