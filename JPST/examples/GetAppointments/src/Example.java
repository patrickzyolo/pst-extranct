import java.io.IOException;
import java.util.List;

import com.independentsoft.pst.Folder;
import com.independentsoft.pst.Item;
import com.independentsoft.pst.Appointment;
import com.independentsoft.pst.PstFile;

public class Example {

    public static void main(String[] args)
    {
        try
        {
            PstFile file = new PstFile("c.pozzi.pst");

            try
            {
                Folder calendar = file.getMailboxRoot().getFolder("Calendar");

                if (calendar != null)
                {
                    List<Item> items = calendar.getItems();

                    for (int i = 0; i < items.size(); i++)
                    {
                        if (items.get(i) instanceof Appointment)
                        {
                            Appointment appointment = (Appointment) items.get(i);

                            System.out.println("Id: " + appointment.getId());
                            System.out.println("Subject: " + appointment.getSubject());
                            System.out.println("StartTime: " + appointment.getStartTime());
                            System.out.println("EndTime: " + appointment.getEndTime());
                            System.out.println("Body: " + appointment.getBody());
                            System.out.println("----------------------------------------------------------------");
                        }
                    }
                }
            }
            finally
            {
                if (file != null)
                {
                    file.close();
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
