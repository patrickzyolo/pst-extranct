/*
	Java programm zum pst extrakt
*/
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

public class PstMailJSON_auto
{
	// Bereinigt den Namen des Attachments
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

	/*
		Konvertiert das Datum aus den funtionen
		get_task, get_appointment und get_mail_date
		fuer Kibana.

		Manchmal wird null uebergeben
	*/
	public static String kibana_string_format(Date date)
	{
		if (date != null)
		{
			SimpleDateFormat kibana_formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			String kibana_Date = kibana_formatter.format(date);

			return kibana_Date;
		}
		else
		{
			return null;
		}
	}

	/*
		Holt die Outlook informationen ueber Tasks.
		Datum wird direkt als Sting zurueck gegeben.
	*/
	public static Map<String, String> get_task(Item inItem)
	{
		// Rueckgabe Map
		Map<String, String> return_task = new HashMap<String, String>();

		// Checkt ob Komatibel
		if (inItem instanceof Task)
		{
			Task task = (Task) inItem;

			Date Task_StartTime = task.getStartDate();
			Date Task_EndTime   = task.getDueDate();

			return_task.put("Task_StartTime", kibana_string_format(Task_StartTime));
			return_task.put("Task_EndTime", kibana_string_format(Task_EndTime));
		}
		else
		{
			return_task.put("Task_StartTime", null);
			return_task.put("Task_EndTime", null);
		}
		return return_task;
	}

	/*
		Holt die Outlook informationen ueber Treffen.
		Datum wird direkt als Sting zurueck gegeben.
	*/
	public static Map<String, String> get_appointment(Item inItem)
	{
		// Rueckgabe JsonArray
		Map<String, String> return_appointment = new HashMap<String, String>();

		// Checkt ob Komatibel
		if (inItem instanceof Appointment)
		{
			Appointment appointment = (Appointment) inItem;

			Date Appointment_StartTime = appointment.getStartTime();
			Date Appointment_EndTime   = appointment.getEndTime();

			return_appointment.put("Appointment_StartTime", kibana_string_format(Appointment_StartTime));
			return_appointment.put("Appointment_EndTime", kibana_string_format(Appointment_EndTime));
		}
		else
		{
			return_appointment.put("Appointment_StartTime", null);
			return_appointment.put("Appointment_EndTime", null);
		}

		return return_appointment;
	}

	/*
		Gibt im JsonArray format die Entfaenger daten wieder

		"name": "name",
		"email": "email",
		"Recipient_type": "Recipient_type"
	*/
	public static JSONArray get_recipier(Item inItem)
	{
		// Rueckgabe JsonArray
		JSONArray return_recipier = new JSONArray();

		// Checkt ob Komatibel
		if (inItem instanceof Message)
		{
			Message message = (Message) inItem;

			/*
				JsonObjecte (name, email, Recipient_type) fuer das JsonArray
				Muss ein Array sein da an das Rueckgabe JsonArray nur Pointer
				uebergeben werden.
			*/
			JSONObject[] recipient_obj = new JSONObject[message.getRecipients().size()];

			/*
				Alle Entfaenger -> eigenes JsonObject (recipient_obj[r])
				Alle JsonObjecte -> JsonArray
			*/
			for (int r = 0; r < message.getRecipients().size(); r++)
			{
				Recipient recipient = message.getRecipients().get(r);

				recipient_obj[r] = new JSONObject();

				recipient_obj[r].put("name", recipient.getDisplayName());
				recipient_obj[r].put("email", recipient.getEmailAddress());
				recipient_obj[r].put("Recipient_type", recipient.getRecipientType().toString());

				return_recipier.add(recipient_obj[r]);
			}
		}
		else
		{
			return_recipier = null;
		}

		return return_recipier;
	}

	// Kontaktinformationen
	public static JSONObject get_contact(Item inItem)
	{
		// Rueckgabe JSONObject
		JSONObject return_contact = new JSONObject();

		// Checkt ob Komatibel
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

	/*
		funktion zum Extrahiren von Attachments

		Gibt im JsonArray format den Attachment url
		und asset_type wieder.
		id muss mit gegeben werden um Dopllungen zu vermeiden.
	*/
	public static JSONArray get_attachments(Item inItem, long id)
	{
		// Daten ablage Verzeichnis
		String dir = "Attachments/";

		// Rueckgabe JsonArray
		JSONArray return_attachments = new JSONArray();

		// Checkt ob Attachments vorhanden sind.
		if (inItem.getAttachments().size() > 0)
		{
			/*
				JsonObject mit dem das Rueckgabe JsonArray
				befuellt wird.

				Muss ein Array sein da an das Rueckgabe JsonArray nur Pointer
				uebergeben werden.
			*/
			JSONObject[] attachment_obj = new JSONObject[inItem.getAttachments().size()];

			// fuer alle Attachments
			for (int r = 0; r < inItem.getAttachments().size(); r++)
		    {
		    	Attachment attachment = inItem.getAttachments().get(r);

				// Name der datei
		        String fileName = (attachment.getFileName() != null) ? attachment.getFileName() : attachment.getDisplayName();

				// fileName wird bereinigt
				fileName = clean_name(fileName);

				// id wird in den fileName intigriert
				String filePath = dir + id + "-" + fileName;

				// anlegung des neuen eintrags
				attachment_obj[r] = new JSONObject();

				/*
					checkt ob die datei schon exsistiert

					fuegt eine weitere zalh(tmp_counter) an den name,
					wenn sie schon exsistiert.
				*/
				File test_file = new File(filePath);
				if (test_file.exists())
				{
					int tmp_counter = 1;
					String tmp_filePath = dir + id + "-" + tmp_counter + "-" + fileName;

					// zaehlt hoch bis ein passender name gefunden wurde.
					while (test_file.exists())
					{
						tmp_filePath = dir + id + "-" + tmp_counter + "-" + fileName;
						test_file = new File(tmp_filePath);
						tmp_counter = tmp_counter + 1;
					}

					// update filePath
					filePath = tmp_filePath;
				}

				try
				{
					// speichern vom Attachment
					attachment.save(filePath, true);
				}
				catch (IOException e)
				{
					System.err.println("IOException: get_attachments(Item inItem, long id)");
					System.err.println("id: " + id + " ---> " + filePath);

					e.printStackTrace();
				}

				// filterung des daten typs
				if (filePath.contains("."))
				{
					String type[] = filePath.split("\\.");
					// letzer eintrag wird genommem (z.B: png)
					attachment_obj[r].put("asset_type", type[type.length - 1]);
				}
				else
				{
					/*
						Setz asset_type zu mail_attachment
						falls kein type festgestellt wurde.
						Oft der fall bei E-Tickes
					*/
					attachment_obj[r].put("asset_type", "mail_attachment");
				}

				// follen System-path
				attachment_obj[r].put("url", Paths.get(filePath).toAbsolutePath().toString());
				return_attachments.add(attachment_obj[r]);
		    }
			return return_attachments;
		}
		else
		{
			return null;
		}
	}

	// windows rtf body (UTF-8)
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

	// Mail Date
	public static String get_mail_date(Item inItem)
	{
		if (inItem instanceof Message)
		{
			Message message = (Message) inItem;

			// formated return for kibana
			return kibana_string_format(message.getClientSubmitTime());
		}
		else
		{
			return null;
		}
	}

	// Mail Sender info + Date
	public static JSONObject get_mail_sender(Item inItem)
    {
		// Rueckgabe JSONObject
		JSONObject return_mail_header = new JSONObject();

		// Checkt ob Komatibel
		if (inItem instanceof Message)
		{
			Message message = (Message) inItem;

			return_mail_header.put("name", message.getSenderName());
			return_mail_header.put("email", message.getSenderEmailAddress());
        }
		else
		{
			return_mail_header.put("name", null);
			return_mail_header.put("email", null);
		}
		return return_mail_header;
    }

	// Cc Entfaenger
	public static JSONArray get_displayCc(Item inItem)
	{
		// Checkt ob Komatibel
		if (inItem instanceof Message)
		{
			Message message = (Message) inItem;

			// Alle Entfaenger als ein String
			String Cc = (String) message.getDisplayCc();

			// Nicht immer Cc Entfaenger
			if (Cc != null)
			{
				// Rueckgabe JsonArray
				JSONArray return_displayCc = new JSONArray();

				// Spliten der Cc Entfaenger
				if (Cc.contains("; "))
				{
					// adden der Entfaenger an das Rueckgabe JsonArray
					for(String entfaenger: Cc.split("; "))
					{
						return_displayCc.add(entfaenger);
					}
				}
				else
				{
					// Nur ein Cc Entfaenger
					return_displayCc.add(Cc);
				}
				return return_displayCc;
			}
			else
			{
				// Keine Cc Entfaenger
				return null;
			}
		}
		else
		{
			// Keine Cc Entfaenger
			return null;
		}
	}

	// Cc Entfaenger
	public static JSONArray get_displayTo(Item inItem)
	{
		// Checkt ob Komatibel
		if (inItem instanceof Message)
		{
			Message message = (Message) inItem;

			// Alle Entfaenger als ein String
			String To = (String) message.getDisplayTo();

			// Nicht immer To Entfaenger
			if (To != null)
			{
				// Rueckgabe JsonArray
				JSONArray return_displayTo = new JSONArray();

				// Spliten der To Entfaenger
				if (To.contains("; "))
				{
					// adden der Entfaenger an das Rueckgabe JsonArray
					for(String entfaenger: To.split("; "))
					{
						return_displayTo.add(entfaenger);
					}
				}
				else
				{
					// Nur ein To Entfaenger
					return_displayTo.add(To);
				}
				return return_displayTo;
			}
			else
			{
				// Keine To Entfaenger
				return null;
			}
		}
		else
		{
			// Keine To Entfaenger
			return null;
		}
	}

	public static void main(String[] args)
	{
		// pst soucre Datei
		String file_name = args[0];

		// json ablage Ordner
		String file_folder = "./data/";

        try
        {
            PstFile file = new PstFile(file_name);

            try
            {
				// Alle Ordner in der pst Datei --> anzahl
                List<Folder> folders = file.getMailboxRoot().getFolders(true);

				// tmp_id zur performant Verbesserung bei der neuen id Vergabe
				long tmp_id = 1;

				// Alle Ordner durchsuchen
				for (int x = 0; x < folders.size(); x++)
				{
					try
					{
						// Ordner mit Mail(Items)
						Folder inbox = folders.get(x);

						// Ausgabe des Ordners und siener Groesse
						System.out.format("%5d %-30s\n", inbox.getItemCount(), inbox.getDisplayName());

						// Checkt ob Ordner Daten inthaelt
			        	if (inbox != null)
			            {
							// Liste mit den Mails (Items) im Ordner inbox
			            	List<Item> items = inbox.getItems();

							// durchgehen aller Items
			                for (int i = 0; i < items.size(); i++)
			                {
								// id der Mail (Item)
								long id = items.get(i).getId();

								/*
									Checkt ob die json datei schon im
									file_folder exsistiert und generiet
									ggf. eine neue.
								*/
								File test_file = new File(file_folder + id + ".json");
								if (test_file.exists())
								{
									/*
										Checkt ob die tmp_id im
										file_folder exsistiert.
									*/
									test_file = new File(file_folder + tmp_id + ".json");

									/*
										Zaehlt solange hoch bis
										eine passende id gefunden wurde.
									*/
									while (test_file.exists())
									{
										tmp_id = tmp_id + 1;
										test_file = new File(file_folder + tmp_id + ".json");
									}

									// update tmp_id mit id
									id = tmp_id;
									// System.out.print("New id: --> " + id + "\r");
								}

								// output file mit id als namen im file_folder
								PrintWriter output_file = new PrintWriter(file_folder + id + ".json", "UTF-8");

								// JsonObject was in output_file geschrieben wird
								JSONObject root_json = new JSONObject();

								// daten die ohne initialisation entnommen werden.
								root_json.put("id", id);
								root_json.put("Subject", items.get(i).getSubject());
								root_json.put("Plain_body", items.get(i).getBody());
								root_json.put("Html_body", items.get(i).getBodyHtmlText());

								/*
									Task get_task
									Datum wird als String uebergeben
								*/
								Map<String, String> task = get_task(items.get(i));

								root_json.put("Task_StartTime", task.get("Task_StartTime"));
								root_json.put("Task_EndTime", task.get("Task_EndTime"));

								/*
								 	Appointment get_appointment
									Datum wird als String uebergeben
								*/
								Map<String, String> appointment = get_appointment(items.get(i));

								root_json.put("Appointment_StartTime", appointment.get("Appointment_StartTime"));
								root_json.put("Appointment_EndTime", appointment.get("Appointment_EndTime"));

								// Kontaktdaten
								root_json.put("Contact", get_contact(items.get(i)));

								// Attachments
								root_json.put("Attachments", get_attachments(items.get(i), id));

								// rtf body
								root_json.put("Rtf_body", get_rtf_body(items.get(i)));

								// Sender info
								root_json.put("Sender", get_mail_sender(items.get(i)));

								// Mail date
								root_json.put("Date", get_mail_date(items.get(i)));

								// Entfaenger
								root_json.put("DisplayTo", get_displayTo(items.get(i)));
								root_json.put("DisplayCc", get_displayCc(items.get(i)));

								// Entfaenger detail
								root_json.put("Recipier", get_recipier(items.get(i)));

								// schriebt alle Mail infos in output_file
								output_file.println(root_json);
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
