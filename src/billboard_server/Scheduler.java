package billboard_server;

import java.sql.ResultSet;
import java.time.Duration;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;


public class Scheduler {
    // currently scheduled billboards
    private ResultSet currentSchedulesResultSet;
    private TreeMap<Date, String> currentScheduledBillboardsTreeMap; // Start date-time, billboard name
    // TODO change getter methods to to update currentScheduledBillboardsTreeMap from result set

    // ------  TIME LOGIC NOTES  -----
    // START    times are INCLUSIVE
    // END      times are EXCLUSIVE
    // Database time format (at least for inputs): YYYY-MM-DD HH:mm:ss
    // format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    // Best modern java time to use to avoid zoning issues is OffsetDateTime


    /*
    // TODO check if valid session token (in main server?), then send list of scheduled billboards
    // ie do this in correct part of main in response to request

    // TODO remove test data below
    // TESTING:
    Date dateOne;
    currentScheduledBillboardsTreeMap.put( )



    private Map<String, List<Object>> getBillboardSchedules(){


    }


    public LocalDate convertDateToLocalDateInstant(Date date) {
        LocalDate localDate;
        localDate = date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        // TODO - remove print later
        // format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        System.out.println(localDate.format(DateTimeFormatter.ofPattern("YYYY-MM-DD HH:mm:ss")));
        return localDate;
    }


    public boolean isBillboardCurrentlyScheduled(){
        boolean isScheduled = false;

        String SQL = "";

        // key = start, value = end
        TreeMap<Date, Date> scheduleStartEnd = new TreeMap<Date, Date>();
        // TODO Save values from DB into this schedule

        // compare datenow with schedule
        Date dateTimeNow = new Date();
        // check if dateTimeNow between any of schedules
        for (Map.Entry<Date,Date> schedule:scheduleStartEnd.entrySet()) {
            boolean isAfterStart = dateTimeNow.after(schedule.getKey());
            boolean isBeforeEnd = dateTimeNow.before(schedule.getValue();
            if ( isAfterStart && isBeforeEnd ) {
                isScheduled = true;
            }
        }
        return isScheduled;
    }
    */



    /**
     * Checks if a billboard is scheduled for the current time
     * If so, saves this, otherwise saves default a billboard
     * @return
     */

    /*
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




    public ResultSet updateScheduledBillboards(){
        currentSchedules

        // Need to return String billboardName, Date scheduleStart, Duration scheduleDuration, boolean isRecurring, int recurFreqInMins

    }


    // TODO check if method to get billboard schedule for this week ONLY is needed



    // Send to Control panel: list of billboards scheduled, with;
    // billboard name, creator, time scheduled and duration
    public  getScheduledBillboardsForControlPanel() {

    }


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

    // remove scheduled for billboard
    public void removeScheduleForBillboard(String billboardName, Date scheduleStart) {
        String SQL = "DELETE FROM schedule WHERE name = '" + billboardName + "' AND schedule_start = '" + scheduleStart + "';";
        // delete row in database scheduling table corresponding to name and schedule start time

    }

    */

}
