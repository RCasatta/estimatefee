package it.casatta.estimatefee;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Created by Riccardo Casatta @RCasatta on 18/04/15.
 * /daystatsbuilder
 */
public class DayStatsBuilder extends HttpServlet {
    private final static Logger log  = Logger.getLogger(DayStatsBuilder.class.getName());
    private final static SimpleDateFormat dayFormat = new SimpleDateFormat("yyyyMMdd");


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        process(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        process(req, resp);
    }

    protected void process(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final String cronHeader = req.getHeader("X-AppEngine-Cron");
        final String debug      = req.getParameter("debug");

        if( "true".equals(cronHeader) || "true".equals(debug) ) {
            String    day = req.getParameter("day");
            if(day==null) {
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.HOUR, -24);
                day = dayFormat.format(cal.getTime());
            }
            log.info("day=" + day);

            List<EstimatedFee> estimatedFeeList = OfyService.ofy().load().type(EstimatedFee.class).filter("day",day).list();
            Double b1=0.0,b2=0.0,b6=0.0,b12=0.0,b25=0.0;
            int c1=0,c2=0,c6=0,c12=0,c25=0;

            final int size = estimatedFeeList.size();
            if(size >0) {

                for (EstimatedFee estimatedFee : estimatedFeeList) {
                    if(estimatedFee.getEstimateFee1Block()>0) {
                        b1 += estimatedFee.getEstimateFee1Block();
                        c1++;
                    }
                    if(estimatedFee.getEstimateFee2Block()>0) {
                        b2+=estimatedFee.getEstimateFee2Block();
                        c2++;
                    }
                    if(estimatedFee.getEstimateFee6Block()>0) {
                        b6+=estimatedFee.getEstimateFee6Block();
                        c6++;
                    }
                    if(estimatedFee.getEstimateFee12Block()>0) {
                        b12 += estimatedFee.getEstimateFee12Block();
                        c12++;
                    }
                    if(estimatedFee.getEstimateFee25Block()>0) {
                        b25 += estimatedFee.getEstimateFee25Block();
                        c25++;
                    }
                }

                final EstimatedFeeDay estimatedFeeDay = new EstimatedFeeDay();
                estimatedFeeDay.setEstimateFee1Block( b1 / c1);
                estimatedFeeDay.setEstimateFee2Block( b2 / c2);
                estimatedFeeDay.setEstimateFee6Block( b6 / c6);
                estimatedFeeDay.setEstimateFee12Block(b12 / c12);
                estimatedFeeDay.setEstimateFee25Block(b25 / c25);
                estimatedFeeDay.setDay(day);

                resp.setContentType("text/plain");
                resp.getWriter().write( estimatedFeeDay.toString() );
                OfyService.ofy().save().entity(estimatedFeeDay).now();
            }

        } else {
            log.warning("no cron or debug parameter to true");
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }

    }
}
