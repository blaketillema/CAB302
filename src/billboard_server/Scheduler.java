package billboard_server;

import java.sql.ResultSet;
import java.time.Duration;
import java.time.LocalDate;
import java.time.Period;
import java.util.Date;
import java.util.ListIterator;
import java.util.Map;
import java.util.TreeMap;


public class Scheduler {

    private ResultSet currentSchedules;

    // TIME LOGIC NOTES
    // START times are INCLUSIVE
    // END times are EXCLUSIVE

    // TODO check if valid session token (in main server?), then send list of scheduled billboards
    // ie do this in correct part of main in response to request

    //




    public boolean isBillboardCurrentlyScheduled(){
        boolean isScheduled = false;

        String SQL = "";

        // key = start, value = end
        TreeMap<Date, Date> scheduleStartEnd = new TreeMap<Date, Date>();
        // TODO Save values from DB into this schedule

        // compare datenow with schedule
        Date dateTimeNow = new Date();
        // check if dateTimeNow between any of schedules
        for (Map.Entry<Date,Date> schedule:scheduleStartStop.entrySet()) {
            boolean isAfterStart = dateTimeNow.after(schedule.getKey());
            boolean isBeforeEnd = dateTimeNow.before(schedule.getValue();
            if ( isAfterStart && isBeforeEnd ) {
                isScheduled = true;
            }
        }
        return isScheduled;
    }

    /**
     * Checks if a billboard is scheduled for the current time
     * If so, saves this, otherwise saves default a billboard
     * @return
     */
    public TreeMap getCurrentScheduledBillboard() {
        TreeMap currentBillboard = new TreeMap<>();
        // if a billboard is currently scheduled, save it
        if ( isBillboardCurrentlyScheduled() ) {
            // save current billboard to treemap
            // TODO call methods here to get current billboard from DB

        }
        // else no billboard is currently scheduled, save default
        else {
            currentBillboard.put("message", "Advertise Here!!!");
            currentBillboard.put("information", "Contact 1800 000 000 for more information, or visit www.example.com");
            currentBillboard.put("billboardBackground", "#008080" );    // teal
            currentBillboard.put("messageColour", "#FF0000");           // red
            currentBillboard.put("informationColour", "#0000FF");       // blue
        }
        return currentBillboard;
    }

    // get billboard scheduled at time








    public getCurrentlyScheduledBillboards(){


        // Need to return String billboardName, Date scheduleStart, Duration scheduleDuration, boolean isRecurring, int recurFreqInMins

    }


    // TODO check if method to get billboard schedule for this week ONLY is needed


    // add schedule for billboard
    public void addScheduleForBillboard(String billboardName, Date scheduleStart, Duration scheduleDuration) {
        boolean isRecurring = false;
        int recurFreqInMins = 0;
        // add schedule for billboard to scheduling table

    }

    // TODO implement recurring sched method overload here:
    // add recurring schedule for billboard
    public void addScheduleForBillboard(String billboardName, Date scheduleStart, Duration scheduleDuration, int recurFreqInMins) {
        boolean isRecurring = true;

        // add schedule for billboard to scheduling table for recurring schedule
    }

    // remove scheduled billboard
    public void removeScheduleForBillboard(String billboardName, Date scheduleStart) {
        String SQL = "DELETE FROM schedule WHERE name = '" + billboardName + "' AND schedule_start = '" + scheduleStart + "';";

        // delete row in database scheduling table corresponding to name and schedule start time

    }

}
