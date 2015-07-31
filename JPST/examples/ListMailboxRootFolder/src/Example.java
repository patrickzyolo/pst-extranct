import java.io.IOException;
import java.util.List;

import com.independentsoft.pst.Folder;
import com.independentsoft.pst.PstFile;

public class Example {

    public static void main(String[] args)
    {
        try
        {
            PstFile file = new PstFile("c:\\testfolder\\Outlook.pst");

            try
            {
                List<Folder> folders = file.getMailboxRoot().getFolders();

                for (int i = 0; i < folders.size(); i++)
                {
                    System.out.println("Id: " + folders.get(i).getId());
                    System.out.println("Name: " + folders.get(i).getDisplayName());
                    System.out.println("Type: " + folders.get(i).getContainerClass());
                    System.out.println("Item count: " + folders.get(i).getItemCount());
                    System.out.println("--------------------------------------------------------");
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
