package billboard_control_panel;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.*;

public class ScheduleController {
    private static String currentCommandName;
    private static ArrayList<Object> currentCommandData = new ArrayList<>();

    // TODO change from map send these through connections methods to server Scheduler
    private static void addCommand(String command, ArrayList<Object> data) {
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


    // --------------- CONTROL PANEL SIDE  ---------------
    // COMMANDS FROM CONTROL PANEL GUI
    /**
     * Forms the command and data to add a new schedule to be sent to server
     * @param billboardName
     * @param schedStart
     * @param schedDurationInMins
     * @param isRecurring
     * @param recurFreqInMins
     */
    public static void commandAddSchedule(String billboardName, OffsetDateTime schedStart, Integer schedDurationInMins,
                                          Boolean isRecurring, Integer recurFreqInMins, String creatorName){
        String commandName = SCHEDULE_ADD;
        ArrayList listOfObjects = new ArrayList();
        // save relevant objects to list
        listOfObjects.add(billboardName);
        listOfObjects.add(schedStart);
        listOfObjects.add(schedDurationInMins);
        listOfObjects.add(isRecurring);
        listOfObjects.add(recurFreqInMins);
        listOfObjects.add(creatorName);
        // Add command name & data
        addCommand(commandName, listOfObjects);
    }

    public static void commandRemoveSchedule(String billboardName, OffsetDateTime schedStart){
        String commandName = SCHEDULE_DELETE;
        ArrayList listOfObjects = new ArrayList();
        // save relevant objects to list
        listOfObjects.add(billboardName);
        listOfObjects.add(schedStart);
        // Add command name & data
        addCommand(commandName, listOfObjects);
    }

    public static void commandGetSchedules(){
        String commandName = SCHEDULE_GET;
        ArrayList listOfObjects = new ArrayList();
        listOfObjects.add("Empty list");
        addCommand(commandName, listOfObjects);
    }

    // Control Panel Parser for response
    // RESPONSE PROCESSING
    public static String commandReplyParser(String command, ArrayList<Object> data){
        // TODO REMOVE TEMP VARIABLE ASSIGNMENT FOR COMMAND HERE:
        addCommand(command, data);
        // TODO add necessary calls for the control panel here
        String successMessage = "";
        // check reply
        if ( command == RESPONSE_ADDED  ) {
            String billboardName = (String) data.get(0);
            OffsetDateTime schedStart = (OffsetDateTime) data.get(1);
            // Process message
            successMessage = "Billboard schedule has been successfully added for: "+
                    billboardName + "to start at " + schedStart;
        }
        else if (  command == RESPONSE_REMOVED ){
            // Process action required (if any)
            String billboardName = (String) data.get(0);
            OffsetDateTime schedStart = (OffsetDateTime) data.get(1);
            // Process message
            successMessage = "Billboard schedule has been successfully deleted for: "+
                    billboardName + "which had a start time of " + schedStart;
        }
        else if ( command == RESPONSE_SCHEDULES ) {
            // get current schedules from DB and respond to Control Panel
            successMessage = "The current list of schedules is: ";
            for (Object schedule : data) {
                ArrayList<Object> scheduleArray = (ArrayList<Object>) schedule;
                successMessage += "\n" + scheduleToString(scheduleArray);
            }
        }
        else if ( command == RESPONSE_ERROR){
            successMessage = (String) data.get(0);
        }
        // call to display this on the GUI
        return successMessage;
    }









    // --------------- HELPER METHODS  ---------------
    // Schedule string message
    public static String scheduleToString(ArrayList<Object> schedule){
        // seperate components
        String billboardName = (String) schedule.get(0);
        OffsetDateTime schedStart = (OffsetDateTime) schedule.get(1);
        Integer schedDurationInMins  = (Integer) schedule.get(2);
        Boolean isRecurring  = (Boolean) schedule.get(3);
        Integer recurFreqInMins  = (Integer) schedule.get(4);
        String creatorName = (String) schedule.get(5);
        // add to string
        String scheduleString = "Schedule is: " +
                "\nbillboardName: " + billboardName +
                "\nschedStart: " + schedStart +
                "\nschedDurationInMins: " + schedDurationInMins +
                "\nisRecurring: " + isRecurring +
                "\nrecurFreqInMins: " + recurFreqInMins +
                "\nrecurFreqInMins: " + creatorName;
        return scheduleString;
    }

    // returns a dummy list of schedules
    public static ArrayList<Object> getDummySchedules() {
        // EXAMPLE DATA TO ADD
        OffsetDateTime timeNowPlus10Days = OffsetDateTime.now().plusDays(10);
        String billboardName = "Bilboard: ";
        OffsetDateTime schedStart = timeNowPlus10Days;
        Integer duration = 60;
        Boolean recur = true;
        Integer recurFreqMins = 30;
        String userName = "userExample";
        OffsetDateTime scheduleAddedAt = OffsetDateTime.now().minusDays(10);
        // Dummy schedules
        ArrayList<Object> scheduleExampleA = new ArrayList<>();
        scheduleExampleA.add(billboardName + "A");
        scheduleExampleA.add(schedStart);
        scheduleExampleA.add(duration);
        scheduleExampleA.add(recur);
        scheduleExampleA.add(recurFreqMins);
        scheduleExampleA.add(userName);
        scheduleExampleA.add(scheduleAddedAt);
        ArrayList<Object> scheduleExampleB = new ArrayList<>();
        scheduleExampleB.add(billboardName + "B");
        scheduleExampleB.add(schedStart);
        scheduleExampleB.add(duration);
        scheduleExampleB.add(recur);
        scheduleExampleB.add(recurFreqMins);
        scheduleExampleB.add(userName);
        scheduleAddedAt.plusDays(3); // change time added to give B precedence
        scheduleExampleB.add(scheduleAddedAt);
        // EXAMPLE DATA - ADD to EXAMPLE list
        ArrayList<Object> schedulesListExample = new ArrayList<>();
        schedulesListExample.add(scheduleExampleA);
        schedulesListExample.add(scheduleExampleB);
        return schedulesListExample;
    }



    // -------------- TEMPORARY MAIN -----------------
    // TODO calls will be from Control Panel GUI instead of main test here
    // TODO Test
    public static void main(String[] args) {
        OffsetDateTime timeNowPlus10Days = OffsetDateTime.now().plusDays(10);
        // TEST DATA
        String billboardName = "Great Billboard!";
        OffsetDateTime schedStart = timeNowPlus10Days;
        Integer duration = 60;
        Boolean recur = true;
        Integer recurFreqMins = 30;
        String creatorName = "John";

        // CHANGE COMMAND CALL BELOW
        //commandAddSchedule(billboardName, schedStart, duration, recur, recurFreqMins, creatorName);
        //commandRemoveSchedule(billboardName, schedStart);
        commandGetSchedules();

        // CHECK OUTPUT
        System.out.println("Instructions from GUI for Command:  " + getCurrentCommandName() + ", the data is:\n"
                + getCurrentCommandData().toString() +"\n" );
        // --> POST to server from this Control Panel
        Scheduler.commandParser( getCurrentCommandName(), getCurrentCommandData() );
        System.out.println("Command data on server commandParser for Command:  "
                + Scheduler.getCurrentCommandName() + "\n" + Scheduler.getCurrentCommandData().toString() +"\n"  );
        // --> Get reply from server and parse
//        commandReplyParser( Scheduler.getCurrentCommandName(), Scheduler.getCurrentCommandData() );
//        System.out.println("Command data after commandReplyParser for Command:  " + getCurrentCommandName() + "\n"
//                + getCurrentCommandData().toString() );




        Duration d = Duration.ofDays(1);
        Duration h = Duration.ofHours(1);
        Duration dh = d.plus(h);
        int durationInMins = (int) dh.toMinutes();
        System.out.println("\n\nDuration in mins is: " + durationInMins);
        System.out.println("Duration calculated 1day + 1hour in mins is: " + (24+1)*60 );

    }

}