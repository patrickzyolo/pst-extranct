import java.io.IOException;
import java.util.List;

import com.independentsoft.pst.Folder;
import com.independentsoft.pst.Item;
import com.independentsoft.pst.Message;
import com.independentsoft.pst.PstFile;

public class Example {

    public static void main(String[] args)
    {
        try
        {
            PstFile file = new PstFile("c:\\testfolder\\Outlook.pst");

            try
            {
                Folder inbox = file.getMailboxRoot().getFolder("Inbox");

                if (inbox != null)
                {
                    List<Item> items = inbox.getItems();

                    for (int i = 0; i < items.size(); i++)
                    {
                        if (items.get(i) instanceof Message)
                        {
                            Message message = (Message) items.get(i);

                            System.out.println("Id: " + message.getId());
                            System.out.println("Subject: " + message.getSubject());
                            System.out.println("DisplayTo: " + message.getDisplayTo());
                            System.out.println("DisplayCc: " + message.getDisplayCc());
                            System.out.println("SenderName: " + message.getSenderName());
                            System.out.println("SenderEmailAddress: " + message.getSenderEmailAddress());
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
