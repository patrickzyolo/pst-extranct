import java.io.IOException;
import java.util.List;

import com.independentsoft.pst.Folder;
import com.independentsoft.pst.Item;
import com.independentsoft.pst.Task;
import com.independentsoft.pst.PstFile;

public class Example {

    public static void main(String[] args)
    {
        try
        {
            PstFile file = new PstFile("c:\\testfolder\\Outlook.pst");

            try
            {
                Folder tasks = file.getMailboxRoot().getFolder("Tasks");

                if (tasks != null)
                {
                    List<Item> items = tasks.getItems();

                    for (int i = 0; i < items.size(); i++)
                    {
                        if (items.get(i) instanceof Task)
                        {
                            Task task = (Task) items.get(i);

                            System.out.println("Id: " + task.getId());
                            System.out.println("Subject: " + task.getSubject());
                            System.out.println("StartTime: " + task.getStartDate());
                            System.out.println("EndTime: " + task.getDueDate());
                            System.out.println("Body: " + task.getBody());
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
