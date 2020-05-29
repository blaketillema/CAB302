package billboard_server;


import billboard_server.engines.ServerFunctions;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import static billboard_server.engines.Server.*;
// TODO MOVE THIS CLASS TO SERVER


public class Scheduler {
    private static String currentCommandName;
    private static ArrayList<Object> currentCommandData = new ArrayList<>();
    private static TreeMap<String, String> currentBillboardData = new TreeMap<>();


    // ----------- STANDARD COMMANDS  ----------
    // Sent by Control Panel
    private static final String SCHEDULE_ADD = "schedule-add";
    private static final String SCHEDULE_DELETE = "schedule-delete";
    private static final String SCHEDULE_GET = "schedule-get";
    // Sent by Scheduler on Server
    private static final String RESPONSE_ADDED = "schedule-response:added";
    private static final String RESPONSE_REMOVED = "schedule-response:removed";
    private static final String RESPONSE_SCHEDULES = "schedule-response:schedules";
    private static final String RESPONSE_ERROR = "schedule-response:error";

    // ------------ GETTER / SETTER  ----------


    // TODO change to send these through connections methods to server Scheduler
    public static void setCommand(String command, ArrayList<Object> data) {
        // Add command name & list Data to map
        currentCommandName = command;
        currentCommandData = data;
        // .scheduleCommand(command, data);
    }

    public static String getCurrentCommandName() {
        return currentCommandName;
    }

    public static ArrayList<Object> getCurrentCommandData() {
        return currentCommandData;
    }

    public static void setCurrentBillboardData(TreeMap<String, String> data){
        currentBillboardData = data;
    }


    // TODO call this to send to VIEWER
    public TreeMap<String, String> getCurrentBillboardData(){
        // update it before return
        updateCurrentBillboardData();
        return currentBillboardData;
    }


    public static String deleteScheduleFromDB(String billboardName, OffsetDateTime schedStart ) {

        String successMessage = "";
        Boolean isDeleted = true;
        // Delete from DB
        // TODO -  ADD call to DB here to remove this billboard and return message properly

        if (isDeleted == true ){
            successMessage = RESPONSE_REMOVED;
        }
        // change isSuccessful to true if successfully added
        else if (isDeleted == false){
            successMessage = "CUSTOM MESSAGE FROM DATABASE HERE";
        }
        return successMessage;
    }


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
            String billboardID = (String) schedTreeMap.get("billboardId");
            String billboardName = (String) schedTreeMap.get("billboardName");
            OffsetDateTime schedStart = (OffsetDateTime) schedTreeMap.get("startTime");
            Integer schedDurationInMins  = (Integer) schedTreeMap.get("duration");
            Boolean isRecurring  = (Boolean) schedTreeMap.get("isRecurring");
            Integer recurFreqInMins  = (Integer) schedTreeMap.get("recurFreqInMins");
            // TODO add creator if needed
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

        /*
        // if unsuccessful, add error to schedules
        if ( schedulesList.isEmpty() ) {
            // TODO - check if error message can be passed from DB here
            // String dbErrorMessage = "ERROR MESSAGE FROM DB HERE";
            // add default error message and details to index 0, 1
            schedulesList.clear();
            schedulesList.add(RESPONSE_ERROR);
            schedulesList.add(dbErrorMessage);
        }
        else {
            // TODO REMOVE below dummy schedules
            // schedulesList = ScheduleController.getDummySchedules();
        }
         */
        return schedulesList;
    }

    // --------------- METHODS  ---------------

    // ----------- COMMAND PROCESSING VIA CONNECTIONS
    public void commandParser(String command, ArrayList<Object> data){
        // check where to send data & do so
        if ( command == SCHEDULE_ADD ) {
            // Process adding Schedule
            addSchedule( data );
        }
        else if (  command == SCHEDULE_DELETE ){
            // process deletion from DB
            removeSchedule( data );
        }
        else if ( command == SCHEDULE_GET ){
            // get current schedules from DB and respond to Control Panel
            getAllSchedulesFromDB();
        }
    }


    // add schedule
    public static ArrayList<Object> addSchedule(ArrayList<Object> scheduleToAdd){
        // separate components
        String billboardName = (String) scheduleToAdd.get(0);
        OffsetDateTime schedStart = (OffsetDateTime) scheduleToAdd.get(1);
        Integer schedDurationInMins  = (Integer) scheduleToAdd.get(2);
        Boolean isRecurring  = (Boolean) scheduleToAdd.get(3);
        Integer recurFreqInMins  = (Integer) scheduleToAdd.get(4);
        String creatorName = (String) scheduleToAdd.get(5);
        // message to add reply command if successful or not
        ArrayList<Object> successMessage = new ArrayList<>();
        // Check to see if any problems first
        // Start is in past
        if( schedStart.isBefore(OffsetDateTime.now()) ) {
            successMessage.add("Schedule start is in the past - don't live in the past!");
            setCommand(RESPONSE_ERROR, successMessage);
        }
        // Schedule is more frequent than duration of billboard
        else if ( recurFreqInMins < schedDurationInMins && recurFreqInMins != 0) {
            successMessage.add("Schedule frequency is more often than duration of schedule - recurrence is obsolete! Please try again.");
            setCommand(RESPONSE_ERROR, successMessage);
        }
        else {
            // try to add this data to DB
            String successMessageString = "defaultSuccess";
            // ServerFunctions.addSchedules(billboardName, schedStart, schedDurationInMins, isRecurring, recurFreqInMins, creatorName);
            // return message if successfully added
            if (successMessageString == RESPONSE_ADDED) {
                setCommand(RESPONSE_ADDED, scheduleToAdd); // include schedule added in response
            }
            else {
                // add error command and message from DB
                successMessage.add(successMessageString);
                setCommand(RESPONSE_ERROR, successMessage);
            }
        }
        return successMessage;
    }

    // remove scheduled for billboard
    public static ArrayList<Object> removeSchedule(ArrayList<Object> scheduleToRemove ) {
        // seperate components
        String billboardName = (String) scheduleToRemove.get(0);
        OffsetDateTime schedStart = (OffsetDateTime) scheduleToRemove.get(1);
        // attempt to delete from DB and save message from DB
        String dbMessage = deleteScheduleFromDB(billboardName, schedStart);
        // message String
        String successMessageString;
        // message to add reply command if successful or not
        ArrayList<Object> successMessage = new ArrayList<>();
        if (dbMessage == RESPONSE_REMOVED ) {
            successMessageString = "Successfully deleted schedule for billboard: " + billboardName + " starting at: " + schedStart;
            successMessage.add(successMessageString);
            setCommand(RESPONSE_REMOVED, successMessage);
        }
        else {
            successMessageString = "I'm sorry, there has been an error in deleting the billboard, because:\n" +
                    dbMessage;
            successMessage.add(successMessageString);
            setCommand(RESPONSE_ERROR, successMessage);
        }
        return scheduleToRemove;
    }

    /*
    // get list of all schedules
    public ArrayList<Object> getSchedules() {
        ArrayList<Object> schedules =  getAllSchedulesFromDB();
        Object indexZeroObject = schedules.get(0);
        if ( !indexZeroObject.equals(RESPONSE_ERROR) ) {
            // NO error message, just return the schedules
            setCommand(RESPONSE_SCHEDULES, schedules);
        }
        else {
            // there has been an error, since schedules.get(0) == RESPONSE_ERROR
            String dbErrorMessage = (String) schedules.get(1);
            // clear the list and add custom message to first index
            schedules.clear();
            schedules.add("I'm sorry, an error has occurred retrieving schedules from the database, please try again later \n" +
                    "The message from the database is: " + dbErrorMessage);
            setCommand(RESPONSE_ERROR, schedules);
        }
        return schedules;
    }
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


    public void updateCurrentBillboardData(){
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
        currentBillboardData.clear(); // clear current data in treeMap and save new data'
        currentBillboardData = currentBillboard;
    }



}
