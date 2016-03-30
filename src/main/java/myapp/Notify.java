package myapp;

import java.io.IOException;
import javax.servlet.http.*;
import java.net.*;
import java.util.*;
import java.util.Collections;
import javax.cache.Cache;
import javax.cache.CacheException;
import javax.cache.CacheFactory;
import javax.cache.CacheManager;

import oauth.signpost.basic.*;

import java.io.*;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

public class Notify extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String message = "";
        String action = req.getParameter("action");
        String xml = "";
        Cache cache;
        try {
            DefaultOAuthConsumer consumer = new DefaultOAuthConsumer("nexus-virtual-item1-97024", "pt0UZegjiNoIdXp9");
            URL url = new URL(req.getParameter("url"));
            HttpURLConnection request = (HttpURLConnection) url.openConnection();
            request.setRequestMethod("GET");
            CacheFactory cacheFactory = CacheManager.getInstance().getCacheFactory();
            cache = cacheFactory.createCache(Collections.emptyMap());

            try {
                consumer.sign(request);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(request.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                xml = response.toString();
                cache.put("myapp_first_message", response.toString());
                //cache.put("myapp_first_message", req.getParameter("url"));
                cache.put("action", action);
                cache.put("authorization", req.getHeader("Authorization"));
                process(cache, req.getParameter("action"), response.toString());

            } catch (Exception e) {
                cache.put("action", req.getParameter("action"));
                cache.put("myapp_first_message", e.getMessage());
            }

        } catch (CacheException e) {

        }

        resp.setContentType("text/plain");
        resp.getWriter().println("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
        resp.getWriter().println("<result>");
        resp.getWriter().println("<success>true</success>");
        if (action.equals("create") || action.equals("cancel") || action.equals("change"))
            resp.getWriter().println("<accountIdentifier>" + get_creator_name(xml) + "</accountIdentifier>");
        else
            resp.getWriter().println("<accountIdentifier>" + get_user_name(xml) + "</accountIdentifier>");
        resp.getWriter().println("</result>");
    }


    private void process(Cache cache, String action, String xml) {
        Map<String, AppSubscription> appSubMap = new HashMap<String, AppSubscription>();
        if (cache.get("SubscriptionMap") != null)
            appSubMap = (HashMap<String, AppSubscription>) cache.get("SubscriptionMap");
        String creator = get_creator_name(xml);

        if (action.equals("create") || action.equals("cancel")) {
            if (action.equals("create")) {
                AppSubscription appSub = new AppSubscription();
                appSub.setCreator(creator);
                appSub.setEditionCode(get_editionCode(xml));
                appSubMap.put(creator, appSub);
            } else if (appSubMap.containsKey(creator)) {
                appSubMap.remove(creator);
            }
            cache.put("SubscriptionMap", appSubMap);
        }

        if (action.equals("assign") || action.equals("unassign")) {
            String assignee = get_user_name(xml);
            if (appSubMap.containsKey(creator)) {
                AppSubscription appSub = appSubMap.get(creator);
                if (action.equals("assign"))
                    appSub.addAssignee(assignee);
                else
                    appSub.removeAssignee(assignee);
                cache.put("SubscriptionMap", appSubMap);
            }
        }

        if (action.equals("change")) {
            String editionCode = get_editionCode(xml);
            if (appSubMap.containsKey(creator)) {
                AppSubscription appSub = appSubMap.get(creator);
                appSub.setEditionCode(editionCode);
                cache.put("SubscriptionMap", appSubMap);
            }
        }
    }

    private String get_creator_name(String xml) {
        return get_field(xml, "/event/creator", "firstName", "lastName");
    }

    private String get_user_name(String xml) {
        return get_field(xml, "/event/payload/user", "firstName", "lastName");
    }

    private String get_editionCode(String xml) {
        return get_field(xml, "/event/payload/order", "editionCode", "pricingDuration");
    }

    private String get_field(String xml, String expression, String fieldName1, String fieldName2) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        String fullname = null;
        try {
            DocumentBuilder dBuilder = factory.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(xml));
            Document doc = dBuilder.parse(is);
            doc.getDocumentElement().normalize();
            XPath xPath = XPathFactory.newInstance().newXPath();

            NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(doc, XPathConstants.NODESET);
            if (nodeList.getLength() > 0) {
                Node nNode = nodeList.item(0);
                Element element = (Element) nNode;
                fullname = (element.getElementsByTagName(fieldName1).item(0).getTextContent() + " " + element.getElementsByTagName(fieldName2).item(0).getTextContent());
            }
        } catch (Exception e) {

        }
        return fullname;
    }
}

