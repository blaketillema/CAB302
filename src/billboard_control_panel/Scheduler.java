package billboard_control_panel;

import java.sql.ResultSet;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.TreeMap;


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
    public static TreeMap<String, String> getCurrentBillboardData(){
        // update it before return
        updateCurrentBillboardData();
        return currentBillboardData;
    }


    // --------------- DATABASE CALLS -----------------
    public static TreeMap<String, String> getBillboardByNameFromDB(String billboardName){
        TreeMap<String, String> billboardData = new TreeMap<>();

        // TODO -  ADD call to DB here to get this billboard

        return billboardData;
    }

    public static String addScheduleToDB(String billboardName, OffsetDateTime schedStart, Integer schedDurationInMins,
                                       Boolean isRecurring, Integer recurFreqInMins, String creatorName ){
        String successMessage = "";
        Boolean isAdded = true;
        // Add this to DB
        // TODO -  ADD call to DB here to add this billboard and return message properly

        if (isAdded == true ){
            successMessage = RESPONSE_ADDED;
        }
        // change isSuccessful to true if successfully added
        else if (isAdded == false){
            successMessage = "CUSTOM MESSAGE FROM DATABASE HERE";
        }
        return successMessage;
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

    public static ArrayList<Object> getAllSchedulesFromDB(){
        Boolean isAbleToGetSchedulesList = false;
        ResultSet dbResultsSchedules = null;
        // TODO -  ADD call to DB here to get all schedules for all billboards and
        //  return message properly if not successful
        String dbErrorMessage = "ERROR MESSAGE FROM DB HERE";
        ArrayList<Object> schedulesList = new ArrayList<>();
        // if successful, add schedules to list
        if ( isAbleToGetSchedulesList ) {
            // add schedules to list from DB resultset
            schedulesList = convertSchedulesResulSetToListOfObjects( dbResultsSchedules );
            // TODO REMOVE below dummy schedules
            schedulesList = ScheduleController.getDummySchedules();
        }
        else {
            // add error message and details to index 0, 1
            schedulesList.clear();
            schedulesList.add(RESPONSE_ERROR);
            schedulesList.add(dbErrorMessage);
        }
        return schedulesList;
    }

    // --------------- METHODS  ---------------

    // ----------- COMMAND PROCESSING VIA CONNECTIONS
    public static void commandParser(String command, ArrayList<Object> data){
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
            getSchedules();
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
        else if ( recurFreqInMins < schedDurationInMins) {
            successMessage.add("Schedule frequency is more often than duration of schedule - recurrence is obsolete! Please try again.");
            setCommand(RESPONSE_ERROR, successMessage);
        }
        else {
            // try to add this data to DB
            String successMessageString =
                    addScheduleToDB(billboardName, schedStart, schedDurationInMins, isRecurring,
                        recurFreqInMins, creatorName);
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

    // get list of all schedules
    public static ArrayList<Object> getSchedules() {
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

    public static ArrayList<Object> convertSchedulesResulSetToListOfObjects(ResultSet dbScheduleResults) {

        // TODO  Finish resultset conversion  below
        ArrayList<Object> schedulesList = new ArrayList<>();

        // Add each schedule to List of schedules before return
        ArrayList<Object> schedule = new ArrayList<>();
        for (Object sched : schedulesList) {
            schedule.add(sched);
        }
        // TODO replace above

        return schedulesList;
    }


    private static String getScheduledBillboardWithCurrentPrecedence(){
        ArrayList<Object> schedules = getSchedules();
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
            billboardNameWithPredecence = (String) scheduleWithPrecedence.get(0);
        }
        else if ( schedulesCurrent.size() > 1 ) {
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


    public static void updateCurrentBillboardData(){
        // Find out which billboard to grab
        String billboardName = getScheduledBillboardWithCurrentPrecedence();
        // For billboard data
        TreeMap<String, String> currentBillboard = new TreeMap<>();
        // check if default
        if( billboardName != "default" ){
            // Get billboard by Name from DB
            currentBillboard = getBillboardByNameFromDB(billboardName);
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





    // ------------- HELPER METHODS -------------

}
