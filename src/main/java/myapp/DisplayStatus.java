package myapp;

import java.io.IOException;
import javax.servlet.http.*;
import java.io.PrintWriter;
import java.util.*;
import javax.cache.Cache;
import javax.cache.CacheException;
import javax.cache.CacheFactory;
import javax.cache.CacheManager;

public class DisplayStatus extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String message = "";
        try {
            Cache cache;
            CacheFactory cacheFactory = CacheManager.getInstance().getCacheFactory();
            cache = cacheFactory.createCache(Collections.emptyMap());
            PrintWriter pw=resp.getWriter();
            resp.setContentType("text/html");
            pw.println("<html>");
            pw.println("<head><title>Display the Subscription status</title></head>");
            pw.println("<body>");
            pw.println("<div align=\"center\"><h2>Orders and Users of product NEXUS VIRTUAL ITEM1</h2></div>");
            if (cache.get("SubscriptionMap") != null) {

                HashMap<String, AppSubscription> appSubMap = (HashMap<String, AppSubscription>) cache.get("SubscriptionMap");
                if (appSubMap.isEmpty())
                    pw.println("<h3> There is no order for this product</h3>");
                pw.println("<ol>");
                for (String creator : appSubMap.keySet()) {
                    AppSubscription appSub = appSubMap.get(creator);
                    pw.println("<li><h3>Order</h3></li>");
                    pw.println("<h3>Creator: " + creator+"</h3>");
                    pw.println("<h3>Edition Code: " + appSub.getEditionCode()+"</h3>");
                    pw.println("<h3>Users</h3>");
                    pw.println("<ol>");
                    Set<String> assignees = appSub.getAssignees();
                    for (String assignee : assignees) {
                        pw.println("<h4><li>" + assignee+"</li></h4>");
                    }
                    pw.println("</ol>");
                }
            }else
                pw.println("<h3> There is no order for this product</h3>");
            pw.println("</ol>");
            pw.println("</body></html>");
        } catch (CacheException e) {

        }


    }

}

