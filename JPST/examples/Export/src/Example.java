import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.List;

import com.independentsoft.pst.Folder;
import com.independentsoft.pst.Item;
import com.independentsoft.pst.PstFile;

public class Example {

    public static void main(String[] args)
    {
        try
        {
            PstFile file = new PstFile("c:\\testfolder\\High32.pst");

            try
            {
                List<Folder> allFolders = file.getRoot().getFolders(true);

                Hashtable<Long, String> parents = new Hashtable<Long, String>();

                String parentFolderPath = "c:\\test\\msg";

                File newFolder = new File(parentFolderPath);
                newFolder.mkdirs();

                parents.put(file.getRoot().getId(), parentFolderPath);

                for (int p = 0; p < allFolders.size(); p++)
                {
                    Folder currentFolder = allFolders.get(p);

                    parentFolderPath = parents.get(currentFolder.getParentId());

                    newFolder = new File(parentFolderPath + "\\" + currentFolder.getDisplayName());
                    newFolder.mkdirs();

                    parents.put(currentFolder.getId(), parentFolderPath + "\\" + currentFolder.getDisplayName());
                }

                for (int j = 0; j < allFolders.size(); j++)
                {
                    for (int s = 0; s < allFolders.get(j).getChildrenCount(); s += 100)
                    {
                        List<Item> items = allFolders.get(j).getItems(s, s + 100);

                        for (int k = 0; k < items.size(); k++)
                        {
                            String parentFolder = parents.get(items.get(k).getParentId());
                            String fileName = getFileName(items.get(k).getSubject());

                            String filePath = parentFolder + "\\" + fileName;

                            if (filePath.length() > 244)
                            {
                                filePath = filePath.substring(0, 244);
                            }

                            filePath = filePath + "-" + items.get(k).getId() + ".msg";

                            items.get(k).save(filePath, true);
                        }
                    }
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
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

    private static String getFileName(String subject)
    {
        if (subject == null || subject.length() == 0)
        {
            String fileName = "NoSubject";
            return fileName;
        }
        else
        {
            String fileName = "";

            for (int i = 0; i < subject.length(); i++)
            {
                if (subject.charAt(i) > 31 && subject.charAt(i) < 127)
                {
                    fileName += subject.charAt(i);
                }
            }

            fileName = fileName.replace("\\", "_");
            fileName = fileName.replace("/", "_");
            fileName = fileName.replace(":", "_");
            fileName = fileName.replace("*", "_");
            fileName = fileName.replace("?", "_");
            fileName = fileName.replace("\"", "_");
            fileName = fileName.replace("<", "_");
            fileName = fileName.replace(">", "_");
            fileName = fileName.replace("|", "_");

            return fileName;
        }
    }
}
