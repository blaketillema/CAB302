package billboard_control_panel;

import java.time.OffsetDateTime;
import java.util.*;

/**
 * Contorls commands related to scheduling
 *
 * Commands names sent by Control Panel:
 * "schedule-add"
 * "schedule-delete"
 * "schedule-get"
 * "schedule-response:schedules"
 * "schedule-response:removed"
 * "schedule-response:added"
 *
 */
public class ScheduleController {
    private static String currentCommandName;
    private static ArrayList<Object> currentCommandData = new ArrayList<>();

    // TODO change from map send these through connections methods to server Scheduler
    private static void addCommand(String command, ArrayList<Object> data) {
        // Add command name & list Data to map
        currentCommandName = command;
        currentCommandData = data;
    }

    private static String getCurrentCommandName() {
        return currentCommandName;
    }

    private static ArrayList<Object> getCurrentCommandData() {
        return currentCommandData;
    }


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
                                          Boolean isRecurring, Integer recurFreqInMins){
        //
        String commandName = "schedule-add";
        ArrayList listOfObjects = new ArrayList();
        // save relevant objects to list
        listOfObjects.add(billboardName);
        listOfObjects.add(schedStart);
        listOfObjects.add(schedDurationInMins);
        listOfObjects.add(isRecurring);
        listOfObjects.add(recurFreqInMins);
        // Add command name & data
        addCommand(commandName, listOfObjects);
    }

    public static void commandRemoveSchedule(String billboardName, OffsetDateTime schedStart){
        String commandName = "schedule-delete";
        ArrayList listOfObjects = new ArrayList();
        // save relevant objects to list
        listOfObjects.add(billboardName);
        listOfObjects.add(schedStart);
        // Add command name & data
        addCommand(commandName, listOfObjects);
    }

    public static void commandGetSchedules(){
        String commandName = "schedule-get";
        ArrayList listOfObjects = new ArrayList();
        listOfObjects.add("Empty list");
        addCommand(commandName, listOfObjects);
    }






    // --------------- SERVER SIDE  ---------------
    // TODO FOR RECIEVING
    // TODO move below to server
    // COMMANDS RECEIVED FOR PROCESSING
    public static void commandParser(String command, ArrayList<Object> data){
        // check where to send data & do so
        if ( command == "schedule-add" ) {
            // Process adding Schedule
            addCommand("schedule-response:added", addSchedule( data ));
        }
        else if (  command == "schedule-delete" ){
            // process deletion from DB
            addCommand("schedule-response:removed", removeSchedule( data ));
        }
        else if ( command ==  "schedule-get" ){
            // get current schedules from DB and respond to Control Panel
            addCommand("schedule-response:schedules", getSchedules() );
        }
    }

    // add
    public static ArrayList<Object> addSchedule(ArrayList<Object> scheduleToAdd){
        // seperate components
        String billboardName = (String) scheduleToAdd.get(0);
        OffsetDateTime schedStart = (OffsetDateTime) scheduleToAdd.get(1);
        Integer schedDurationInMins  = (Integer) scheduleToAdd.get(2);
        Boolean isRecurring  = (Boolean) scheduleToAdd.get(3);
        Integer recurFreqInMins  = (Integer) scheduleToAdd.get(4);
        // Check if okay to add

        // TODO check logic



        // Call to add this data to DB
        // TODO replace this with DB call
        System.out.println("Add schedule to DB is: " +
                "\nbillboardName: " + billboardName +
                "\nschedStart: " + schedStart +
                "\nschedDurationInMins: " + schedDurationInMins +
                "\nisRecurring: " + isRecurring +
                "\nrecurFreqInMins: " + recurFreqInMins
                );

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

    public static ArrayList<Object> getSchedules() {
        // get resultset from DB
        // TODO - call DB here instead to get each schedule row instead of below
        // EXAMPLE DATA TO ADD
        OffsetDateTime timeNowPlus10Days = OffsetDateTime.now().plusDays(10);
        String billboardName = "Bilboard: ";
        OffsetDateTime schedStart = timeNowPlus10Days;
        Integer duration = 60;
        Boolean recur = true;
        Integer recurFreqMins = 30;
        String userName = "userExample";
        ArrayList<Object> scheduleAExampleA = new ArrayList<>();
        scheduleAExampleA.add(billboardName + "A");
        scheduleAExampleA.add(schedStart);
        scheduleAExampleA.add(duration);
        scheduleAExampleA.add(recur);
        scheduleAExampleA.add(recurFreqMins);
        scheduleAExampleA.add(userName);
        ArrayList<Object> scheduleAExampleB = new ArrayList<>();
        scheduleAExampleB.add(billboardName + "B");
        scheduleAExampleB.add(schedStart);
        scheduleAExampleB.add(duration);
        scheduleAExampleB.add(recur);
        scheduleAExampleB.add(recurFreqMins);
        scheduleAExampleB.add(userName);
        // EXAMPLE DATA - ADD to EXAMPLE list
        ArrayList<Object> schedulesListExample = new ArrayList<>();
        schedulesListExample.add(scheduleAExampleA);
        schedulesListExample.add(scheduleAExampleB);
        //TODO  change to real data below
        // Add result set to List with list of objects
        // Replace with resultset
        ArrayList<Object> schedulesList = schedulesListExample;
        ArrayList<Object> schedule = new ArrayList<>();
        for (Object sched : schedulesList) {
            schedule.add(sched);
        }
        return schedulesList;
    }










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

        // CHANGE CALL BELOW
        // commandAddSchedule(billboardName, schedStart, duration, recur, recurFreqMins);
        // commandRemoveSchedule(billboardName, schedStart);
        commandGetSchedules();

        // CHECK OUTPUT
        System.out.println("From command sent: " + getCurrentCommandName() + ", the data is: "
                + getCurrentCommandData().toString() );

        System.out.println("Check if parser is working properly:\n" );
        commandParser( getCurrentCommandName(), getCurrentCommandData() );
        System.out.println("Command data after commandParser " + getCurrentCommandData().toString() );
    }
}