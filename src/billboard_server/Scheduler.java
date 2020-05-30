package billboard_server;

import java.sql.SQLException;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import static billboard_server.engines.Server.*;

/**
 * Contains methods for handling scheduler logic
 */
public class Scheduler {
    /**
     * Updates & returns current billboard data.
     * @return the current billboard which should be displayed on the billboard viewer
     */
    public TreeMap<String, String> getCurrentBillboardData(){
        // update it before return
        return updateCurrentBillboardData();
    }

    /**
     * Obtains all billboard schedules from the database
     * @return all schedues from the database in an ArrayList<Object> format
     */
    public ArrayList<Object> getAllSchedulesFromDB(){
        String dbErrorMessage = "Sorry, cannot get Schedules from Database.";
        // save all schedules to TreeMap
        TreeMap<String, Object> schedulesTreeMap = null;
        try {
            schedulesTreeMap = database.getSchedules();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        // array to save all schedules
        ArrayList<Object> schedulesList = new ArrayList<>();
        // get each item and add it to a treemap
        for( Map.Entry<String, Object> schedule : schedulesTreeMap.entrySet() ){
            // save each componenet of the TreeMap
            TreeMap<String, Object> schedTreeMap = (TreeMap<String, Object>) schedule.getValue();
            String scheduleID = schedule.getKey();
            // get each element of this schedule
            String billboardID = (String) schedTreeMap.get("billboardId");
            String billboardName = (String) schedTreeMap.get("billboardName");
            OffsetDateTime schedStart = (OffsetDateTime) schedTreeMap.get("startTime");
            Integer schedDurationInMins  = (Integer) schedTreeMap.get("duration");
            Boolean isRecurring  = (Boolean) schedTreeMap.get("isRecurring");
            Integer recurFreqInMins  = (Integer) schedTreeMap.get("recurFreqInMins");
            String creatorName = "CREATOR PLACEHOLDER";
            // add each item to a schedule ArrayList of objects
            ArrayList<Object> schedArrayListObject = new ArrayList<>();
            schedArrayListObject.add(billboardName);
            schedArrayListObject.add(schedStart);
            schedArrayListObject.add(schedDurationInMins);
            schedArrayListObject.add(isRecurring);
            schedArrayListObject.add(recurFreqInMins);
            schedArrayListObject.add(creatorName);
            // add the objects to the
            schedulesList.add(schedArrayListObject);
        }
        return schedulesList;
    }


    /**
     * Checks if the given arguments for the schedule paramaters are allowed to be scheduled
     * @param billboardName
     * @param schedStart
     * @param schedDurationInMins
     * @param isRecurring
     * @param recurFreqInMins
     * @param creatorName
     * @return message indicating "success" if it is allowed, or otherwise
     *              a message specific to the reason it is not allowed
     */
    public String addScheduleCheckIfAllowed(String billboardName, OffsetDateTime schedStart, Integer schedDurationInMins,
                                         Boolean isRecurring, Integer recurFreqInMins, String creatorName){
        // Check to see if any problems
        String successMessage = "success";
        // Start is in past
        if( schedStart.isBefore(OffsetDateTime.now()) ) {
            successMessage = "Schedule start is in the past - don't live in the past!";
        }
        // Schedule is more frequent than duration of billboard
        else if ( recurFreqInMins < schedDurationInMins && recurFreqInMins != 0) {
            successMessage  = "Schedule frequency is more often than duration of schedule - recurrence is obsolete! Please try again.";
        }
        else {
            successMessage = "success";
        }
        return successMessage;
    }

    /**
     * Gets the logical precedence for which billboard should currently be displayed on the billboard viewer
     * @return the name of the billboard which should currently be scheduled
     */
    private String getScheduledBillboardWithCurrentPrecedence(){
        ArrayList<Object> schedules = getAllSchedulesFromDB();
        ArrayList<Object> schedulesCurrent = new ArrayList<Object>();
        // Check which schedules would currently run and add to refined list: schedulesCurrent
        // save time now to be fair when comparing
        OffsetDateTime dateTimeNow = OffsetDateTime.now();
        for (Object sched : schedules) {
            // cast current schedule object to list
            ArrayList<Object> schedNow = (ArrayList<Object>) sched;
            // save required details for this schedule
            OffsetDateTime schedStart = (OffsetDateTime) schedNow.get(1);
            Integer durationMins = (Integer) schedNow.get(2);
            Boolean isRecurring = (Boolean) schedNow.get(3);
            Integer recurFreqInMins = (Integer) schedNow.get(4);
            // if not recurring first for efficiency
            if ( !isRecurring ) {
                OffsetDateTime schedFinish = schedStart.plusMinutes(durationMins);
                if ( schedStart.isBefore( OffsetDateTime.now() ) &&  schedFinish.isAfter(OffsetDateTime.now()) ) {
                    // this schedule is current, so add to list
                    schedulesCurrent.add(schedNow);
                }
            }
            else if ( isRecurring ) {
                Duration timeSinceScheduleStart = Duration.between(schedStart, dateTimeNow);
                long timeSinceScheduleStartInMins = Math.round(timeSinceScheduleStart.toMinutes());
                long minsSinceStartOfCurrentRecurrence = timeSinceScheduleStartInMins % recurFreqInMins;
                // check if this current elapsed time is in the duration of each instance
                if ( minsSinceStartOfCurrentRecurrence < durationMins ) {
                    // this schedule is current, so add to list
                    schedulesCurrent.add(schedNow);
                }
            }
        }
        // Check which schedule in this list takes precedence
        ArrayList<Object> scheduleWithPrecedence = new ArrayList<>();
        String billboardNameWithPredecence = "default";
        if (schedulesCurrent.size() == 0){
            // Nothing currently scheduled, so use default
            billboardNameWithPredecence = "default";
        }
        else if (schedulesCurrent.size() == 1){
            // This is the ONLY current schedule, so add it
            scheduleWithPrecedence = (ArrayList<Object>) schedulesCurrent.get(0);
            System.out.println(scheduleWithPrecedence);
            billboardNameWithPredecence = (String) scheduleWithPrecedence.get(0);
        }
        else if ( schedulesCurrent.size() >= 2 ) {
            // Init oldest time
            OffsetDateTime mostRecentScheduleAddedAt = OffsetDateTime.MIN;
            // check which one takes precedence (scheduled most recently)
            for(Object schedule : schedulesCurrent) {
                ArrayList<Object> sch = (ArrayList<Object>) schedule; // cast Object to List
                String currentScheduleBillboardName = (String) sch.get(0); // billboardName
                OffsetDateTime thisScheduleAddedAt = (OffsetDateTime) sch.get(6); // scheduleAddedAt
                if ( thisScheduleAddedAt.isAfter(mostRecentScheduleAddedAt) ){
                    billboardNameWithPredecence = currentScheduleBillboardName;
                    mostRecentScheduleAddedAt = thisScheduleAddedAt;
                }
            }
        }
        return billboardNameWithPredecence;
    }

    /**
     * Updates the data for the billboard which should be currently scheduled
     * @return a billboard in the format required by the viewer of TreeMap<String, String>
     */
    public TreeMap<String, String> updateCurrentBillboardData(){
        // Find out which billboard to grab
        String billboardName = getScheduledBillboardWithCurrentPrecedence();
        // For billboard data
        TreeMap<String, String> currentBillboard = new TreeMap<>();
        // check if default
        if( !billboardName.equals("default") ){
            // Get billboard by Name from DB
            ArrayList<String> bb = new ArrayList<>(1);
            String billboardID = null;
            try {
                billboardID = database.billboardNameToId(billboardName);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            bb.add(billboardID);
            try {
                currentBillboard = (TreeMap<String, String>) database.getBillboards(bb).get(database.getBillboards(bb).firstKey());
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        else { // save default details
            currentBillboard.put("message", "Advertise Here!!!");
            currentBillboard.put("information", "Contact 1800 000 000 for more information, or visit www.example.com");
            currentBillboard.put("billboardBackground", "#008080" );    // teal
            currentBillboard.put("messageColour", "#FF0000");           // red
            currentBillboard.put("informationColour", "#0000FF");       // blue
        }
        return currentBillboard;
    }
}
