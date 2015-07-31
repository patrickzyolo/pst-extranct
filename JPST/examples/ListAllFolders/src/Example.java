import java.io.IOException;
import java.util.Hashtable;
import java.util.List;

import com.independentsoft.pst.Folder;
import com.independentsoft.pst.PstFile;

public class Example {

    public static void main(String[] args)
    {
        try
        {
            Hashtable<Long, String> parents = new Hashtable<Long, String>();

            PstFile file = new PstFile("c:\\testfolder\\Outlook.pst");

            try
            {
                List<Folder> folders = file.getMailboxRoot().getFolders(true);

                String parentFolderPath = file.getMailboxRoot().getDisplayName();

                parents.put(file.getMailboxRoot().getId(), parentFolderPath);

                for (int p = 0; p < folders.size(); p++)
                {
                    Folder currentFolder = folders.get(p);

                    parentFolderPath = parents.get(currentFolder.getParentId());

                    String currentFolderPath = parentFolderPath + "\\" + currentFolder.getDisplayName();
                    parents.put(currentFolder.getId(), currentFolderPath);

                    System.out.println("Id: " + currentFolder.getId());
                    System.out.println("Name: " + currentFolder.getDisplayName());
                    System.out.println("Type: " + currentFolder.getContainerClass());
                    System.out.println("Item count: " + currentFolder.getItemCount());
                    System.out.println("Path: " + currentFolderPath);
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
