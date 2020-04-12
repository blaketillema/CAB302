package billboard_control_panel;



import com.mindfusion.common.DateTime;
import com.mindfusion.common.Rectangle;
import com.mindfusion.drawing.Brush;
import com.mindfusion.drawing.Brushes;
import com.mindfusion.drawing.awt.AwtImage;
import com.mindfusion.scheduling.*;
import com.mindfusion.scheduling.awt.AwtCalendar;
import com.mindfusion.scheduling.model.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;




/**
 * Created by MindFusion.
 */
public class SchedulerGUI extends JFrame {

    AwtCalendar calendar;
    Recurrence recurrence;

    protected SchedulerGUI() {

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setTitle("MindFusion.Scheduling Sample: Calendar With Events");
        setMinimumSize(new Dimension(400,600));

        BorderLayout layout = new BorderLayout();
        getContentPane().setLayout(layout);


        JLabel label = new JLabel("This sample demonstrates the TimeTable view and recurrent events. " +
                "Click on a cell and a new event will be created that will occur each week on the selected day.");
        label.setBorder(new EmptyBorder(10,10,10,10));
        getContentPane().add(label, BorderLayout.NORTH);

        // Calendar initialization start
        calendar = new AwtCalendar();
        calendar.beginInit();
        //set the current time
        calendar.setCurrentTime(DateTime.now());
        DateTime today = DateTime.today();
        //set the current date
        calendar.setDate(today);
        // Select the current date
        calendar.getSelection().set(DateTime.today());

        calendar.setCurrentView(CalendarView.SingleMonth);
        calendar.setCustomDraw(CustomDrawElements.CalendarItem);
        calendar.getMonthSettings().getDaySettings().setHeaderSize(20);
        calendar.getItemSettings().setSize(32);
        calendar.endInit();



        //add a listener for custom draw
        calendar.addCalendarListener(new CalendarAdapter()
        {
            @Override()
            public void draw(DrawEvent e) {
                onDraw(e);
            }
        });

        //add a listener for selecting events
        calendar.addCalendarListener(new CalendarAdapter(){
            public void dateClick(ResourceDateEvent e) {
                onDateClicked(e);
            }

        });


        // arrange the calendar
        getContentPane().add(calendar, BorderLayout.CENTER);

        pack();
        setVisible(true);
    }

    /* called when a date is clicked. Creates a recurring appointment. */
    protected void onDateClicked(ResourceDateEvent e) {

        int dayIndex = e.getDate().getDayOfWeek();

        Appointment item = new Appointment();
        item.setStartTime(e.getDate());
        item.setEndTime(e.getDate());
        item.setHeaderText(events[dayIndex]);
        item.getStyle().setBrush(brushes[dayIndex]);

        // Create the recurrence pattern.
        recurrence = new Recurrence();
        recurrence.setPattern(RecurrencePattern.Weekly);
        recurrence.setDaysOfWeek(getDayOfWeek(dayIndex));
        recurrence.setStartDate(e.getDate());
        recurrence.setRecurrenceEnd(RecurrenceEnd.Never);
        item.setRecurrence(recurrence);

        calendar.getSchedule().getItems().add(item);
    }

    /* gets the day of the week as a value of the DaysOfWeek enumeration.
    The arguments identifies the day of the week as an integer. */
    private int getDayOfWeek ( int i ) {

        switch (i) {
            case 1:
                return DaysOfWeek.Monday;
            case 2:
                return DaysOfWeek.Tuesday;
            case 3:
                return DaysOfWeek.Wednesday;
            case 4:
                return DaysOfWeek.Thursday;
            case 5:
                return DaysOfWeek.Friday;
            case 6:
                return DaysOfWeek.Saturday;
        }

        return DaysOfWeek.Sunday;

    }

    /* custom drawing is performed here/ */
    private void onDraw(DrawEvent e)
    {
        if(recurrence == null)
            return;
        if (e.getElement() == CustomDrawElements.CalendarItem)
        {
            if(e.getDate().getDay() == 6 )
            {
                java.awt.Image img = null;

                try {
                    // Read the image file from an input stream
                    InputStream is = new BufferedInputStream(
                            new FileInputStream("../cake.png"));
                    img = ImageIO.read(is);

                } catch (IOException ioe) {
                }

                //gets the bounds of the drawing area
                Rectangle r = e.getBounds();
                AwtImage awtImage = new AwtImage(img);
                //draw the image
                e.getGraphics().drawImage(awtImage, e.getBounds().getLeft(), e.getBounds().getTop(), 32, 32);

            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                SchedulerGUI window = null;
                try {
                    window = new SchedulerGUI();
                    window.setVisible(true);
                }
                catch (Exception exp) {
                }
            }
        });
    }

    Brush[] brushes = {
            Brushes.AliceBlue, Brushes.Beige, Brushes.LightBlue,
            Brushes.LightGreen, Brushes.LightGray, Brushes.LightPink,
            Brushes.LemonChiffon
    };

    String[] events = {
            "swimming", "yoga", "piano",
            "shopping", "cooking", "french",
            "party night"
    };

}

