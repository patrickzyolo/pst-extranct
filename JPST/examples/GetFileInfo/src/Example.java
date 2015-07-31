import java.io.IOException;

import com.independentsoft.pst.PstFile;

public class Example {

    public static void main(String[] args)
    {
        try
        {
            PstFile file = new PstFile("c:\\testfolder\\Outlook.pst");

            try
            {
                System.out.println("Message store name: " + file.getMessageStore().getDisplayName());
                System.out.println("Mailbox root folder: " + file.getMailboxRoot().getDisplayName());
                System.out.println("Encryption type: " + file.getEncryptionType());
                System.out.println("File size: " + file.getSize());
                System.out.println("Is 64-bit: " + file.is64Bit());
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
