package it.casatta.estimatefee;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Riccardo Casatta @RCasatta on 13/07/15.
 */
public class Last extends HttpServlet {
    private final static Logger log  = Logger.getLogger(Last.class.getName());
    private MemcacheService memcache = MemcacheServiceFactory.getMemcacheService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        EstimatedFee estimatedFee = (EstimatedFee) memcache.get("lastEstimatedFee");
        if(estimatedFee!=null) {
            log.info("return lastEstimatedFee cached");
            final String result = getJson(estimatedFee);
            resp.setContentType("application/json");
            resp.getWriter().println(result);
            return;
        }

        final List<EstimatedFee> lastFee = OfyService.ofy().load().type(EstimatedFee.class).order("timestamp").limit(1).list();
        if(lastFee.size()>0) {
            EstimatedFee last = lastFee.get(0);
            memcache.put("lastEstimatedFee",last);
            final String result = getJson(last);
            if(result!=null) {

                resp.setContentType("application/json");
                resp.getWriter().println(result);
            }
        } else {
            log.warning("no last result");
        }
        resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    private String getJson(EstimatedFee last) throws IOException {
        JSONObject object= new JSONObject();
        try {
            object.put("timestamp", last.getTimestamp());
            object.put("block1", last.getEstimateFee1Block());
            object.put("block2", last.getEstimateFee2Block());
            object.put("block6", last.getEstimateFee6Block());
            object.put("block12", last.getEstimateFee12Block());
            object.put("block25", last.getEstimateFee25Block());
            return object.toString();
        } catch (JSONException e) {
            log.severe(e + " " + e.getMessage());
        }
        return null;
    }
}
