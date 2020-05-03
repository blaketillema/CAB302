package billboard_control_panel;

import java.time.OffsetDateTime;
import java.util.ArrayList;

public class Scheduler {
    private static String currentCommandName;
    private static ArrayList<Object> currentCommandData = new ArrayList<>();

    // TODO change from map send these through connections methods to server Scheduler
    public static void addCommand(String command, ArrayList<Object> data) {
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

    // add schedule
    public static ArrayList<Object> addSchedule(ArrayList<Object> scheduleToAdd){
        // seperate components
        String billboardName = (String) scheduleToAdd.get(0);
        OffsetDateTime schedStart = (OffsetDateTime) scheduleToAdd.get(1);
        Integer schedDurationInMins  = (Integer) scheduleToAdd.get(2);
        Boolean isRecurring  = (Boolean) scheduleToAdd.get(3);
        Integer recurFreqInMins  = (Integer) scheduleToAdd.get(4);
        String creatorName = (String) scheduleToAdd.get(5);
        // Check if okay to add

        // TODO check logic


        // Call to add this data to DB
        // TODO replace this print with DB call
        // System.out.println("Add schedule to DB is: " + scheduleToString(scheduleToAdd));
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

}
