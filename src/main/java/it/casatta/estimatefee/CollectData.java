/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Java Servlet Module" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/HelloWorld
*/

package it.casatta.estimatefee;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CollectData extends HttpServlet {
    private final static Logger log  = Logger.getLogger(CollectData.class.getName());
    private final static SimpleDateFormat dayFormat = new SimpleDateFormat("yyyyMMdd");
    private MemcacheService memcache = MemcacheServiceFactory.getMemcacheService();

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        final String content = getBody(req);
        log.info(content);
        try {
            JSONArray array = new JSONArray(content);
            Double block1 = array.getJSONObject(0).getDouble("result");
            Double block2 = array.getJSONObject(1).getDouble("result");
            Double block6 = array.getJSONObject(2).getDouble("result");
            Double block12 = array.getJSONObject(3).getDouble("result");
            Double block25 = array.getJSONObject(4).getDouble("result");
            EstimatedFee estimatedFee = new EstimatedFee();
            final Long timestamp = System.currentTimeMillis();
            estimatedFee.setTimestamp(timestamp);

            estimatedFee.setEstimateFee1Block(block1);
            estimatedFee.setEstimateFee2Block(block2);
            estimatedFee.setEstimateFee6Block(block6);
            estimatedFee.setEstimateFee12Block(block12);
            estimatedFee.setEstimateFee25Block(block25);
            estimatedFee.setDay(dayFormat.format(new Date(timestamp)));
            OfyService.ofy().save().entity(estimatedFee).now();
            log.info("saved " + estimatedFee);
            memcache.put("lastTimestamp", timestamp);
            memcache.put("lastEstimatedFee",estimatedFee);
        } catch (JSONException e) {
            log.severe(e + " " + e.getMessage());
        }
        resp.setContentType("text/plain");
        resp.getWriter().println("ok");
    }


    public String getBody(HttpServletRequest request) throws IOException {

        String body = null;
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = null;

        try {
            InputStream inputStream = request.getInputStream();
            if (inputStream != null) {
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                char[] charBuffer = new char[128];
                int bytesRead = -1;
                while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
                    stringBuilder.append(charBuffer, 0, bytesRead);
                }
            } else {
                stringBuilder.append("");
            }
        } catch (IOException ex) {
            throw ex;
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException ex) {
                    throw ex;
                }
            }
        }

        body = stringBuilder.toString();
        return body;
    }
}
