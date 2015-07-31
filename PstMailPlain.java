import java.io.IOException;
import java.io.PrintWriter;
import java.io.File;

import java.util.*;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.JSONArray;

import com.independentsoft.pst.Folder;
import com.independentsoft.pst.Item;
import com.independentsoft.pst.Message;
import com.independentsoft.pst.PstFile;
import com.independentsoft.pst.Attachment;
import com.independentsoft.pst.Appointment;
import com.independentsoft.pst.Contact;
import com.independentsoft.pst.Recipient;
import com.independentsoft.pst.Task;


public class PstMailPlain
{
	public static String name_clean(String fileName)
	{
		if (fileName != null)
		{
			fileName = fileName.replace("\\", "_");
            fileName = fileName.replace("/", "_");
            fileName = fileName.replace(":", "_");
            fileName = fileName.replace("*", "_");
            fileName = fileName.replace("?", "_");
            fileName = fileName.replace("\"", "_");
            fileName = fileName.replace("<", "_");
            fileName = fileName.replace(">", "_");
            fileName = fileName.replace("|", "_");
		}
		return fileName;
	}

	public static Map<String, Date> get_task(Item inItem)
	{
		Map<String, Date> return_task = new HashMap<String, Date>();

		if (inItem instanceof Task)
		{
			Task task = (Task) inItem;

			return_task.put("Task StartTime", task.getStartDate());
			return_task.put("Task EndTime", task.getDueDate());
		}
		else
		{
			return_task.put("Task StartTime", null);
			return_task.put("Task EndTime", null);
		}
		return return_task;
	}

	public static JSONArray get_recipier(Item inItem)
	{
		JSONArray return_array = new JSONArray();

		if (inItem instanceof Message)
		{
			Message message = (Message) inItem;
			JSONObject tmp = new JSONObject();

			for (int r = 0; r < message.getRecipients().size(); r++)
			{
				Recipient recipient = message.getRecipients().get(r);

				tmp.put("Name", recipient.getDisplayName());
				tmp.put("Email address", recipient.getEmailAddress());
				tmp.put("Recipient type", recipient.getRecipientType().toString());

				return_array.add(tmp);
			}
		}
		else
		{
			return_array = null;
		}
		return return_array;
	}

	public static Map<String, String> get_contact(Item inItem)
	{
		Map<String, String> return_contact = new HashMap<String, String>();

		if (inItem instanceof Contact)
		{
			Contact contact = (Contact) inItem;

			return_contact.put("GivenName", contact.getGivenName());
			return_contact.put("Email1Address", contact.getEmail1Address());
			return_contact.put("Email1DisplayName", contact.getEmail1DisplayName());
			return_contact.put("BusinessPhone", contact.getBusinessPhone());
			return_contact.put("BusinessAddress", contact.getBusinessAddress());
		}
		else
		{
			return_contact.put("GivenName", null);
			return_contact.put("Email1Address", null);
			return_contact.put("Email1DisplayName",  null);
			return_contact.put("BusinessPhone",  null);
			return_contact.put("BusinessAddress",  null);
		}
		return return_contact;
	}

	public static Map<String, Date> get_appointment(Item inItem)
	{
		Map<String, Date> return_appointment = new HashMap<String, Date>();

		if (inItem instanceof Appointment)
		{
			Appointment appointment = (Appointment) inItem;

			return_appointment.put("Appointment StartTime", appointment.getStartTime());
			return_appointment.put("Appointment EndTime", appointment.getEndTime());
		}
		else
		{
			return_appointment.put("Appointment StartTime", null);
			return_appointment.put("Appointment EndTime", null);
		}

		return return_appointment;
	}

	public static JSONArray get_attachments(Item inItem, long id)
	{
		JSONArray return_attachments = new JSONArray();

		if (inItem.getAttachments().size() > 0)
		{
			for (int r = 0; r < inItem.getAttachments().size(); r++)
		    {
		    	Attachment attachment = inItem.getAttachments().get(r);

		        String fileName = (attachment.getFileName() != null) ? attachment.getFileName() : attachment.getDisplayName();

		        String filePath = "./Attachments/" + id + "-" + name_clean(fileName);

				try
				{
					attachment.save(filePath, true);
				}
				catch (IOException e)
				{
					System.err.println("IOException: get_attachments(Item inItem, long id)");
					// System.err.println(fileName);
					System.err.println("---> " + filePath);

					// e.printStackTrace();
				}

				return_attachments.add(filePath);
		    }
			return return_attachments;
		}
		else
		{
			return null;
		}
	}

	//rtf body
	public static String get_mail_body(Item inItem)
	{
		if (inItem.getBodyRtf() != null)
		{
			Charset charset = Charset.forName("UTF-8");

			return charset.decode(ByteBuffer.wrap(inItem.getBodyRtf())).toString();
		}
		else
		{
			return null;
		}
	}

    public static Map<String, String> get_mail(Item inItem)
    {
		Map<String, String> return_mail = new HashMap<String, String>();

    	if (inItem instanceof Message)
        {
        	Message message = (Message) inItem;

            // System.out.println("DisplayTo: " + (String) message.getDisplayTo());
            // System.out.println("DisplayCc: " + message.getDisplayCc());
            // System.out.println("SenderName: " +(String) message.getSenderName());
            // System.out.println("SenderEmailAddress: " + (String) message.getSenderEmailAddress());

			return_mail.put("DisplayTo", (String) message.getDisplayTo());
			return_mail.put("DisplayCc", (String) message.getDisplayCc());
			return_mail.put("SenderName", (String) message.getSenderName());
			return_mail.put("SenderEmailAddress", (String) message.getSenderEmailAddress());
        }
		else
		{
			return_mail.put("DisplayTo", null);
			return_mail.put("DisplayCc", null);
			return_mail.put("SenderName", null);
			return_mail.put("SenderEmailAddress", null);

		}
		return return_mail;
    }

	public static void main(String[] args)
	{
		String file_name = args[0];

        try
        {
            PstFile file = new PstFile(file_name);

            try
            {
                List<Folder> folders = file.getMailboxRoot().getFolders(true);

				for (int x = 0; x < folders.size(); x++)
				{
					try
					{
						Folder inbox = folders.get(x);

						System.out.format("%5d %-30s\n", inbox.getItemCount(), inbox.getDisplayName());

			        	if (inbox != null)
			            {
			            	List<Item> items = inbox.getItems();
							long tmp_id = 1;

			                for (int i = 0; i < items.size(); i++)
			                {
								long id = items.get(i).getId();

								File test_file = new File("./data/" + id + ".json");

								if (test_file.exists())
								{
									test_file = new File("./data/" + tmp_id + ".json");

									while (test_file.exists())
									{
										tmp_id = tmp_id + 1;
										test_file = new File("./data/" + tmp_id + ".json");
									}
									id = tmp_id;
									// System.out.print("New id: --> " + id + "\r");
								}

								PrintWriter output_file = new PrintWriter("./data/" + id + ".txt", "UTF-8");

								output_file.println("id: " + id);
								output_file.println("Subject: " + items.get(i).getSubject());
								output_file.println("Plain body: " + items.get(i).getBody());
								output_file.println("Html body: " + items.get(i).getBodyHtmlText());

								Map<String, Date> task = get_task(items.get(i));

								output_file.println("Task StartTime: " + task.get("Task StartTime"));
								output_file.println("Task EndTime: " + task.get("Task EndTime"));

								Map<String, String> contact = get_contact(items.get(i));

								output_file.println("GivenName: " + contact.get("GivenName"));
								output_file.println("Email1Address: " + contact.get("Email1Address"));
								output_file.println("Email1DisplayName: " + contact.get("Email1DisplayName"));
								output_file.println("BusinessPhone: " + contact.get("BusinessPhone"));
								output_file.println("BusinessAddress: " + contact.get("BusinessAddress"));

								Map<String, Date> appointment = get_appointment(items.get(i));

								output_file.println("Appointment StartTime: " + appointment.get("Appointment StartTime"));
								output_file.println("Appointment EndTime: " + appointment.get("Appointment EndTime"));

								output_file.println("Attachments: " + get_attachments(items.get(i), id));
								output_file.println("Rtf body: " + get_mail_body(items.get(i)));

								Map<String, String> mail = get_mail(items.get(i));

								output_file.println("DisplayTo: " + mail.get("DisplayTo"));
								output_file.println("DisplayCc: " + mail.get("DisplayCc"));
								output_file.println("SenderName: " + mail.get("SenderName"));
								output_file.println("SenderEmailAddress: " + mail.get("SenderEmailAddress"));

								output_file.println("Recipier: " + get_recipier(items.get(i)));

								output_file.close();
							}
			        	}
			        }
			        catch (IOException e)
			        {
			        	System.out.println("IOException: main");
			            e.printStackTrace();
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
        	System.out.println("IOException: main");
			e.printStackTrace();
		}
	}
}
