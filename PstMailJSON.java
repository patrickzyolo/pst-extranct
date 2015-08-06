import java.io.IOException;
import java.io.PrintWriter;
import java.io.File;

import java.util.*;

import java.text.SimpleDateFormat;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Paths;

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


public class PstMailJSON
{
	public static String clean_name(String fileName)
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

	public static String convertDateToString(Date date)
	{
		if (date != null)
		{
			// SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			String s = formatter.format(date);

			return s;
		}
		else
		{
			return null;
		}
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
			JSONObject[] tmp = new JSONObject[message.getRecipients().size()];

			for (int r = 0; r < message.getRecipients().size(); r++)
			{
				Recipient recipient = message.getRecipients().get(r);

				tmp[r] = new JSONObject();

				tmp[r].put("Name", recipient.getDisplayName());
				tmp[r].put("Email address", recipient.getEmailAddress());
				tmp[r].put("Recipient type", recipient.getRecipientType().toString());

				return_array.add(tmp[r]);
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
		String dir = "Attachments/";
		JSONArray return_attachments = new JSONArray();

		if (inItem.getAttachments().size() > 0)
		{
			JSONObject[] tmp = new JSONObject[inItem.getAttachments().size()];

			for (int r = 0; r < inItem.getAttachments().size(); r++)
		    {
		    	Attachment attachment = inItem.getAttachments().get(r);

		        String fileName = (attachment.getFileName() != null) ? attachment.getFileName() : attachment.getDisplayName();
				fileName = clean_name(fileName);
				String filePath = dir + id + "-" + fileName;

				tmp[r] = new JSONObject();

				File test_file = new File(filePath);

				if (test_file.exists())
				{
					int tmp_counter = 1;
					String tmp_filePath = dir + id + "-" + tmp_counter + "-" + fileName;

					while (test_file.exists())
					{
						tmp_filePath = dir + id + "-" + tmp_counter + "-" + fileName;
						test_file = new File(tmp_filePath);
						tmp_counter = tmp_counter + 1;
					}

					filePath = tmp_filePath;
				}

				try
				{
					attachment.save(filePath, true);
				}
				catch (IOException e)
				{
					System.err.println("IOException: get_attachments(Item inItem, long id)");
					// System.err.println(fileName);
					System.err.println("id: " + id + " ---> " + filePath);

					// e.printStackTrace();
				}

				if (filePath.contains("."))
				{
					String type[] = filePath.split("\\.");
					tmp[r].put("asset_type", type[type.length - 1]);
				}
				else
				{
					tmp[r].put("asset_type", "mail_attachment");
					// tmp[r].put("type", "unknown");
					// tmp[r].put("type", null);
				}

				// tmp[r].put("asset_type", "mail_attachment");
				tmp[r].put("path", Paths.get(filePath).toAbsolutePath().toString());
				return_attachments.add(tmp[r]);
		    }
			return return_attachments;
		}
		else
		{
			return null;
		}
	}

	public static String get_rtf_body(Item inItem)
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

			return_mail.put("SenderName", message.getSenderName());
			return_mail.put("SenderEmailAddress", message.getSenderEmailAddress());
			return_mail.put("Date", convertDateToString(message.getClientSubmitTime()));
        }
		else
		{
			return_mail.put("SenderName", null);
			return_mail.put("SenderEmailAddress", null);
			return_mail.put("Date", null);
		}
		return return_mail;
    }

	public static Map<String, JSONArray> get_display(Item inItem)
	{
		Map<String, JSONArray> return_display = new HashMap<String, JSONArray>();

		if (inItem instanceof Message)
		{
			Message message = (Message) inItem;

			String To = (String) message.getDisplayTo();
			String Cc = (String) message.getDisplayCc();

			if (To != null)
			{
				JSONArray tmp_json_array_To = new JSONArray();

				if (To.contains(";"))
				{
					String[] tmp_array = To.split("; ");

					for(int x = 0; x < tmp_array.length; x++)
					{
						tmp_json_array_To.add(tmp_array[x]);
					}

				}
				else
				{
					tmp_json_array_To.add(To);
				}
				return_display.put("DisplayTo", tmp_json_array_To);
			}
			else
			{
				return_display.put("DisplayTo", null);
			}

			if (Cc != null)
			{
				JSONArray tmp_json_array_Cc = new JSONArray();

				if (Cc.contains(";"))
				{
					String[] tmp_array = Cc.split("; ");

					for(int x = 0; x < tmp_array.length; x++)
					{
						tmp_json_array_Cc.add(tmp_array[x]);
					}

				}
				else
				{
					tmp_json_array_Cc.add(Cc);
				}

				return_display.put("DisplayCc", tmp_json_array_Cc);
			}
			else
			{
				return_display.put("DisplayCc", null);
			}

		}
		else
		{
			return_display.put("DisplayTo", null);
			return_display.put("DisplayCc", null);
		}
		return return_display;
	}

	public static void main(String[] args)
	{
		String file_name = args[0];
		String file_folder = "./data/";

        try
        {
            PstFile file = new PstFile(file_name);

            try
            {
                List<Folder> folders = file.getMailboxRoot().getFolders(true);
				long tmp_id = 1;

				for (int x = 0; x < folders.size(); x++)
				{
					try
					{
						Folder inbox = folders.get(x);

						System.out.format("%5d %-30s\n", inbox.getItemCount(), inbox.getDisplayName());

			        	if (inbox != null)
			            {
			            	List<Item> items = inbox.getItems();

			                for (int i = 0; i < items.size(); i++)
			                {
								long id = items.get(i).getId();

								File test_file = new File(file_folder + id + ".json");

								if (test_file.exists())
								{
									test_file = new File(file_folder + tmp_id + ".json");

									while (test_file.exists())
									{
										tmp_id = tmp_id + 1;
										test_file = new File(file_folder + tmp_id + ".json");
									}
									id = tmp_id;
									// System.out.print("New id: --> " + id + "\r");
								}

								PrintWriter output_file = new PrintWriter(file_folder + id + ".json", "UTF-8");

								JSONObject jsonOBJ = new JSONObject();

								jsonOBJ.put("id", id);
								jsonOBJ.put("Subject", items.get(i).getSubject());
								jsonOBJ.put("Plain_body", items.get(i).getBody());
								jsonOBJ.put("Html_body", items.get(i).getBodyHtmlText());

								Map<String, Date> task = get_task(items.get(i));

								jsonOBJ.put("Task_StartTime", convertDateToString(task.get("Task StartTime")));
								jsonOBJ.put("Task_EndTime", convertDateToString(task.get("Task EndTime")));

								Map<String, Date> appointment = get_appointment(items.get(i));

								jsonOBJ.put("Appointment_StartTime", convertDateToString(appointment.get("Appointment StartTime")));
								jsonOBJ.put("Appointment_EndTime", convertDateToString(appointment.get("Appointment EndTime")));

								Map<String, String> contact = get_contact(items.get(i));
								JSONObject contact_JSON = new JSONObject();

								contact_JSON.put("name", contact.get("GivenName"));
								contact_JSON.put("email", contact.get("Email1Address"));
								contact_JSON.put("email_name", contact.get("Email1DisplayName"));
								contact_JSON.put("BusinessPhone", contact.get("BusinessPhone"));
								contact_JSON.put("BusinessAddress", contact.get("BusinessAddress"));

								jsonOBJ.put("Contact", contact.get("BusinessAddress"));

								jsonOBJ.put("Attachments", get_attachments(items.get(i), id));
								jsonOBJ.put("Rtf_body", get_rtf_body(items.get(i)));

								Map<String, String> mail = get_mail(items.get(i));
								JSONObject mail_JSON = new JSONObject();

								mail_JSON.put("name", mail.get("SenderName"));
								mail_JSON.put("email", mail.get("SenderEmailAddress"));

								jsonOBJ.put("Sender", mail_JSON);
								jsonOBJ.put("Date", mail.get("Date"));

								Map<String, JSONArray> display = get_display(items.get(i));

								jsonOBJ.put("DisplayTo", display.get("DisplayTo"));
								jsonOBJ.put("DisplayCc", display.get("DisplayCc"));

								jsonOBJ.put("Recipier", get_recipier(items.get(i)));

								output_file.println(jsonOBJ);
								output_file.close();
							}
			        	}
			        }
			        catch (IOException e)
			        {
			        	System.err.println("IOException: main");
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
        	System.err.println("IOException: main");
			e.printStackTrace();
		}
	}
}
