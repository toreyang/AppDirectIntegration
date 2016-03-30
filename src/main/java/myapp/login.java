package myapp;

import java.io.IOException;
import javax.servlet.http.*;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletContext;
import java.util.Collections;
import javax.cache.Cache;
import javax.cache.CacheException;
import javax.cache.CacheFactory;
import javax.cache.CacheManager;

import org.expressme.openid.Association;
import org.expressme.openid.Authentication;
import org.expressme.openid.Endpoint;
import org.expressme.openid.OpenIdManager;

import java.util.List;

import org.openid4java.discovery.Identifier;
import org.openid4java.message.ax.AxMessage;

public class login extends HttpServlet {
    private OpenIdManager manager;

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String identifier = req.getParameter("openid");
        resp.setContentType("text/plain");
        resp.getWriter().println("identifier=" + identifier);
        String respStr = this.authRequest(identifier, req, resp);
        resp.getWriter().println("respStr=" + respStr);
    }

    public void init() {
        manager = new OpenIdManager();
    }

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        ServletContext context = config.getServletContext();
        manager = new OpenIdManager();

    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String identifier = req.getParameter("openid");
        //this.authRequest(identifier, req, resp);
    }

    // --- placing the authentication request ---
    public String authRequest(String userSuppliedString,
                              HttpServletRequest httpReq, HttpServletResponse httpResp)
            throws IOException {
        try {
            // configure the return_to URL
            String returnToUrl = httpReq.getRequestURL().toString();
            httpResp.getWriter().println((this.manager == null) ? "manager is null" : "manager is not null");
            manager.setReturnTo("http://global-standard-125814.appspot.com/login1");
            Endpoint endpoint = manager.lookupEndpoint("https://www.appdirect.com/openid/id");
            Association association = manager.lookupAssociation(endpoint);
            String url = manager.getAuthenticationUrl(endpoint, association);
            httpResp.sendRedirect(url);

        } catch (Exception e) {//catch (OpenIDException e) {
            httpResp.getWriter().println("Exception:" + e.getMessage());
            // present error to the user
        }
        return null;
    }

    // --- processing the authentication response ---

}

