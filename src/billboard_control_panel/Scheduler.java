package billboard_control_panel;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.TreeMap;


// TODO MOVE THIS CLASS TO SERVER



public class Scheduler {
    private static String currentCommandName;
    private static ArrayList<Object> currentCommandData = new ArrayList<>();
    private static TreeMap<String, String> currentBillboardData = new TreeMap<>();

    // ------------ GETTER / SETTER  ----------
    // TODO change from map send these through connections methods to server Scheduler
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

    public static void addScheduleToDB(String billboardName, OffsetDateTime schedStart, Integer schedDurationInMins,
                                       Boolean isRecurring, Integer recurFreqInMins, String creatorName ){
        // Add this to DB
        // TODO -  ADD call to DB here to add this billboard
    }

    public static void deleteScheduleFromDB( ) {

        // TODO -  ADD call to DB here to add this billboard

    }



    // --------------- METHODS  ---------------

    // ----------- COMMAND PROCESSING VIA CONNECTIONS
    public static void commandParser(String command, ArrayList<Object> data){
        // check where to send data & do so
        if ( command == "schedule-add" ) {
            // Process adding Schedule
            setCommand("schedule-response:added", addSchedule( data ));
        }
        else if (  command == "schedule-delete" ){
            // process deletion from DB
            setCommand("schedule-response:removed", removeSchedule( data ));
        }
        else if ( command ==  "schedule-get" ){
            // get current schedules from DB and respond to Control Panel
            setCommand("schedule-response:schedules", getSchedulesListOfObjects() );
        }
    }

    // add schedule
    public static ArrayList<Object> addSchedule(ArrayList<Object> scheduleToAdd){
        // seperate components
        String billboardName = (String) scheduleToAdd.get(0);
        OffsetDateTime schedStart = (OffsetDateTime) scheduleToAdd.get(1);
        Integer schedDurationInMins  = (Integer) scheduleToAdd.get(2);
        Boolean isRecurring  = (Boolean) scheduleToAdd.get(3);
        Integer recurFreqInMins  = (Integer) scheduleToAdd.get(4);
        String creatorName = (String) scheduleToAdd.get(5);
        // Check to see if any problems first
        // Start is in past
        if( schedStart.isBefore(OffsetDateTime.now()) ) {
            ArrayList<Object> errorMessage = new ArrayList<>();
            errorMessage.add("Schedule start is in the past - please don't live in the past");
            setCommand("schedule-response:error", errorMessage);
        }
        // Schedule is more frequent than duration of billboard
        else if ( recurFreqInMins < schedDurationInMins) {
            ArrayList<Object> errorMessage = new ArrayList<>();
            errorMessage.add("Schedule frequency is more often than duration of schedule - recurrence is obsolete! Please try again.");
            setCommand("schedule-response:error", errorMessage);
        }
        else {
            // add this data to DB
            addScheduleToDB(billboardName, schedStart, schedDurationInMins, isRecurring,
                    recurFreqInMins, creatorName);
        }
        return scheduleToAdd;
    }

    // remove scheduled for billboard
    public static ArrayList<Object> removeSchedule(ArrayList<Object> scheduleToRemove ) {
        // seperate components
        String billboardName = (String) scheduleToRemove.get(0);
        OffsetDateTime schedStart = (OffsetDateTime) scheduleToRemove.get(1);
        // TODO replace with DB call below
        System.out.println("Delete schedule is for: " + billboardName + " starting at: " + schedStart);
        //String SQL = "DELETE FROM schedule WHERE name = '" + billboardName + "' AND schedule_start = '" + scheduleStart + "';";
        // delete row in database scheduling table corresponding to name and schedule start time
        return scheduleToRemove;
    }

    public static ArrayList<Object> getSchedulesListOfObjects() {
        // get resultset from DB
        // TODO - call DB here instead to get each schedule row instead of below

        //TODO  change to real data below
        // Add result set to List with list of objects
        // Replace with resultset
        ArrayList<Object> schedulesList = ScheduleController.getDummySchedules();
        // Add each schedule to List of schedules before return
        ArrayList<Object> schedule = new ArrayList<>();
        for (Object sched : schedulesList) {
            schedule.add(sched);
        }
        return schedulesList;
    }

    private static String getScheduledBillboardWithCurrentPrecedence(){
        ArrayList<Object> schedules = getSchedulesListOfObjects();
        ArrayList<Object> schedulesCurrent = new ArrayList<Object>();
        // Check which schedules would currently run and add to refined list: schedulesCurrent
        for (Object sched : schedules) {
            // cast to list
            ArrayList<Object> schedNow = (ArrayList<Object>) sched;
            // check if recurring
            Boolean isRecurring = (Boolean) schedNow.get(3);
            if ( !isRecurring ) {
                OffsetDateTime schedStart = (OffsetDateTime) schedNow.get(1);
                Integer durationMins = (Integer) schedNow.get(2);
                OffsetDateTime schedFinish = schedStart.plusMinutes(durationMins);
                if ( schedStart.isBefore( OffsetDateTime.now() ) &&  schedFinish.isAfter(OffsetDateTime.now()) ) {
                    // this schedule is current, so add to list
                    schedulesCurrent.add(schedNow);
                }
            }
            else if ( isRecurring ) {

                // TODO FINISH IMPLEMENTING RECURRING DURATION CHECKS FOR IF CURRENTLY RUNNING
                //  Perhaps modulo start modulo finish and check for each if time now is between

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
