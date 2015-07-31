import java.io.IOException;
import java.util.List;

import com.independentsoft.pst.Attachment;
import com.independentsoft.pst.Folder;
import com.independentsoft.pst.Item;
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
                        for (int r = 0; r < items.get(i).getAttachments().size(); r++)
                        {
                            Attachment attachment = items.get(i).getAttachments().get(r);

                            String fileName = (attachment.getFileName() != null) ? attachment.getFileName() : attachment.getDisplayName();

                            String filePath = "c:\\temp\\" + fileName;

                            attachment.save(filePath, true);
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
