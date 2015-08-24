/*
	Java programm zum pst extrakt
*/
import java.io.*;
import java.net.*;
import java.util.*;

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

public class PstMailPost extends PstMailJSON
{
	private static void elasticsearch(String url, String data, String charset)
	{
		try
		{
			URLConnection connection = new URL(url).openConnection();
			connection.setDoOutput(true);
			connection.setRequestProperty("Accept-Charset", charset);
			connection.setRequestProperty("Content-Type", "application/json;charset=" + charset);

			try (OutputStream output = connection.getOutputStream())
			{
			  output.write(data.getBytes(charset));
			  output.flush();
			  output.close();
			}

			InputStream response = connection.getInputStream();

			// BufferedReader reader = new BufferedReader(new InputStreamReader(response));
			// StringBuilder result = new StringBuilder();
			// String line;
			//
			// while((line = reader.readLine()) != null)
			// {
			//     result.append(line);
			// }
			//
			// System.out.println(result.toString());
		}
		catch (IOException e)
		{
			System.err.println("IOException: elasticsearch");
			e.printStackTrace();
		}
	}

	public static void main(String[] args)
	{
		// pst soucre Datei
		String file_name = args[0];

		// json ablage Ordner
		String file_folder = "./data/";

		String charset = "UTF-8";

		String url = "http://debian.local:9200/hacking-team/mail";

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

								elasticsearch(url, root_json.toJSONString(), charset);
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
