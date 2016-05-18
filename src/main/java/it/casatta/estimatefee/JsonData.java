package it.casatta.estimatefee;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Riccardo Casatta @RCasatta on 13/07/15.
 */
public class JsonData extends HttpServlet {
    private final static Logger log  = Logger.getLogger(JsonData.class.getName());
    private final static SimpleDateFormat dayFormat = new SimpleDateFormat("yyyyMMdd");
    private final static SimpleDateFormat hourFormat = new SimpleDateFormat("yyyyMMddHH");
    private final static SimpleDateFormat minuteFormat = new SimpleDateFormat("yyyyMMddHHmm");
    private final static SimpleDateFormat minuteFormatSep = new SimpleDateFormat("yyyy,MM,dd,HH,mm");

    private MemcacheService memcache = MemcacheServiceFactory.getMemcacheService();


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {

            ////////FIVE MINUTES STEPS
            String result;
            Object cached=null;
            Object nullObject=null;
            Long lastTimestamp = (Long) memcache.get("lastTimestamp");
            if(lastTimestamp!=null)
                cached = memcache.get(lastTimestamp);

            if(cached==null) {
                List<EstimatedFee> estimatedFees = OfyService.ofy().load().type(EstimatedFee.class).order("-timestamp").limit(864).list();
                Collections.reverse( estimatedFees );
                int size=estimatedFees.size();
                log.info("total estimatedFees " + size);

                JSONObject dataTable = new JSONObject();

                JSONObject total = new JSONObject();
                JSONArray totalCols = new JSONArray();
                JSONArray totalRows = new JSONArray();

                JSONObject totalCol0 = new JSONObject();
                totalCol0.put("id", "x");
                totalCol0.put("label", "date");
                totalCol0.put("type", "datetime");

                /*JSONObject totalCol1 = new JSONObject();
                totalCol1.put("id", "A1");
                totalCol1.put("label", "1 block");
                totalCol1.put("type", "number");*/

                JSONObject totalCol2 = new JSONObject();
                totalCol2.put("id", "A2");
                totalCol2.put("label", "2 blocks");
                totalCol2.put("type", "number");

                JSONObject totalCol3 = new JSONObject();
                totalCol3.put("id", "A3");
                totalCol3.put("label", "6 blocks");
                totalCol3.put("type", "number");

                JSONObject totalCol4 = new JSONObject();
                totalCol4.put("id", "A4");
                totalCol4.put("label", "12 blocks");
                totalCol4.put("type", "number");

                JSONObject totalCol5 = new JSONObject();
                totalCol5.put("id", "A5");
                totalCol5.put("label", "25 blocks");
                totalCol5.put("type", "number");

                totalCols.put(totalCol0);
                //totalCols.put(totalCol1);
                totalCols.put(totalCol2);
                totalCols.put(totalCol3);
                totalCols.put(totalCol4);
                totalCols.put(totalCol5);

                total.put("cols", totalCols);

                EstimatedFee last=null;
                for (final EstimatedFee estimatedFee : estimatedFees) {
                    final String jDate = String.format("Date(%s)",minuteFormatSep.format(new Date(estimatedFee.getTimestamp()) ) );

                    JSONObject totalC = new JSONObject();
                    JSONArray totalCArr = new JSONArray();
                    JSONObject totalV0 = new JSONObject();
                    totalV0.put("v", jDate);
                    totalCArr.put(totalV0);

                    /*JSONObject totalV1 = new JSONObject();
                    totalV1.put("v", estimatedFee.getEstimateFee1Block()>0 ?  estimatedFee.getEstimateFee1Block() : nullObject );
                    totalCArr.put(totalV1);*/

                    JSONObject totalV2 = new JSONObject();
                    totalV2.put("v", estimatedFee.getEstimateFee2Block()>0 ?  estimatedFee.getEstimateFee2Block() : "" );
                    totalCArr.put(totalV2);

                    JSONObject totalV3 = new JSONObject();
                    totalV3.put("v", estimatedFee.getEstimateFee6Block()>0 ?  estimatedFee.getEstimateFee6Block() : "" );
                    totalCArr.put(totalV3);

                    JSONObject totalV4 = new JSONObject();
                    totalV4.put("v", estimatedFee.getEstimateFee12Block()>0 ?  estimatedFee.getEstimateFee12Block() : "" );
                    totalCArr.put(totalV4);

                    JSONObject totalV5 = new JSONObject();
                    totalV5.put("v", estimatedFee.getEstimateFee25Block()>0 ?  estimatedFee.getEstimateFee25Block() : "" );
                    totalCArr.put(totalV5);

                    totalC.put("c", totalCArr);
                    totalRows.put(totalC);
                    last=estimatedFee;

                }
                total.put("rows", totalRows);
                dataTable.put("total", total);

                JSONObject object = new JSONObject();
                object.put("timestamp", (long) last.getTimestamp());
                /*object.put("block1", last.getEstimateFee1Block());*/
                object.put("block2", last.getEstimateFee2Block());
                object.put("block6", last.getEstimateFee6Block());
                object.put("block12", last.getEstimateFee12Block());
                object.put("block25", last.getEstimateFee25Block());
                dataTable.put("last", object);

                result = dataTable.toString();

                memcache.put("lastTimestamp", last.getTimestamp());
                memcache.put(lastTimestamp, dataTable.toString() );
            } else {
                log.info("getting five minutes cached result");
                result = cached.toString();
            }
            ////////////////////////////////////




            ///////DAY STEPS
            String result2;
            final Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_YEAR, -1);
            final String day=dayFormat.format(cal.getTime());
            cached = memcache.get(day);

            if(cached==null) {
                List<EstimatedFeeDay> estimatedFeeDays = OfyService.ofy().load().type(EstimatedFeeDay.class).order("day").limit(1000).list();
                int size=estimatedFeeDays.size();
                log.info("total estimatedFeeDays " + size);

                JSONObject dataTable = new JSONObject();

                JSONObject total = new JSONObject();
                JSONArray totalCols = new JSONArray();
                JSONArray totalRows = new JSONArray();

                JSONObject totalCol0 = new JSONObject();
                totalCol0.put("id", "x");
                totalCol0.put("label", "date");
                totalCol0.put("type", "date");

                JSONObject totalCol1 = new JSONObject();
                totalCol1.put("id", "A1");
                totalCol1.put("label", "1 block");
                totalCol1.put("type", "number");

                JSONObject totalCol2 = new JSONObject();
                totalCol2.put("id", "A2");
                totalCol2.put("label", "2 blocks");
                totalCol2.put("type", "number");

                JSONObject totalCol3 = new JSONObject();
                totalCol3.put("id", "A3");
                totalCol3.put("label", "6 blocks");
                totalCol3.put("type", "number");

                JSONObject totalCol4 = new JSONObject();
                totalCol4.put("id", "A4");
                totalCol4.put("label", "12 blocks");
                totalCol4.put("type", "number");

                JSONObject totalCol5 = new JSONObject();
                totalCol5.put("id", "A5");
                totalCol5.put("label", "25 blocks");
                totalCol5.put("type", "number");

                totalCols.put(totalCol0);
                totalCols.put(totalCol1);
                totalCols.put(totalCol2);
                totalCols.put(totalCol3);
                totalCols.put(totalCol4);
                totalCols.put(totalCol5);

                total.put("cols", totalCols);

                EstimatedFee last=null;
                for (EstimatedFeeDay estimatedFeeDay : estimatedFeeDays) {
                    String data = estimatedFeeDay.getDay();
                    String year = data.substring(0, 4);
                    String month = data.substring(4, 6);
                    String dday = data.substring(6, 8);
                    String jDate = String.format("Date(%s,%d,%s)", year, Integer.parseInt(month) - 1, dday);

                    JSONObject totalC = new JSONObject();
                    JSONArray totalCArr = new JSONArray();
                    JSONObject totalV0 = new JSONObject();
                    totalV0.put("v", jDate);
                    totalCArr.put(totalV0);

                    JSONObject totalV1 = new JSONObject();
                    totalV1.put("v", estimatedFeeDay.getEstimateFee1Block() );
                    totalCArr.put(totalV1);

                    JSONObject totalV2 = new JSONObject();
                    totalV2.put("v", estimatedFeeDay.getEstimateFee2Block() );
                    totalCArr.put(totalV2);

                    JSONObject totalV3 = new JSONObject();
                    totalV3.put("v", estimatedFeeDay.getEstimateFee6Block() );
                    totalCArr.put(totalV3);

                    JSONObject totalV4 = new JSONObject();
                    totalV4.put("v", estimatedFeeDay.getEstimateFee12Block() );
                    totalCArr.put(totalV4);

                    JSONObject totalV5 = new JSONObject();
                    totalV5.put("v", estimatedFeeDay.getEstimateFee25Block() );
                    totalCArr.put(totalV5);

                    totalC.put("c", totalCArr);
                    totalRows.put(totalC);


                }
                total.put("rows", totalRows);

                result2 = total.toString();

                memcache.put(day, result2);

            } else {
                log.info("getting day cached result");
                result2 = cached.toString();
            }
            ///////////////////////////////////



            JSONObject totalResult=new JSONObject();
            totalResult.put("fiveminutes",new JSONObject(result));
            totalResult.put("days",new JSONObject(result2));

            resp.setContentType("application/json");
            resp.getWriter().write(totalResult.toString());

        } catch (JSONException e) {
            log.warning(e + " " + e.getMessage());
        }
    }
}
