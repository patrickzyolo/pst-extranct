import java.io.IOException;
import java.util.List;

import com.independentsoft.pst.Contact;
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
                Folder contacts = file.getMailboxRoot().getFolder("Contacts");

                if (contacts != null)
                {
                    List<Item> items = contacts.getItems();

                    for (int i = 0; i < items.size(); i++)
                    {
                        if (items.get(i) instanceof Contact)
                        {
                            Contact contact = (Contact) items.get(i);

                            System.out.println("Id: " + contact.getId());
                            System.out.println("GivenName: " + contact.getGivenName());
                            System.out.println("Email1Address: " + contact.getEmail1Address());
                            System.out.println("Email1DisplayName: " + contact.getEmail1DisplayName());
                            System.out.println("BusinessPhone: " + contact.getBusinessPhone());
                            System.out.println("BusinessAddress: " + contact.getBusinessAddress());
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
