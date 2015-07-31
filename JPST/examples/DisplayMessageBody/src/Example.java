import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.List;

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
                        System.out.println("Id: " + items.get(i).getId());
                        System.out.println("Subject: " + items.get(i).getSubject());

                        System.out.println("Plain body:");
                        System.out.println(items.get(i).getBody());
                        System.out.println("-------------------------------------------------------");

                        System.out.println("Html body:");
                        System.out.println(items.get(i).getBodyHtmlText());
                        System.out.println("-------------------------------------------------------");

                        if (items.get(i).getBodyRtf() != null)
                        {
                            Charset charset = Charset.forName("UTF-8");

                            System.out.println("Rtf body:");

                            System.out.println(charset.decode(ByteBuffer.wrap(items.get(i).getBodyRtf())).toString());
                            System.out.println("-------------------------------------------------------");
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
