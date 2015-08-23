----------------------------- Pst-Extranct -----------------------------

Das Hauptprogramm befindet sich in der Datei PstMailJSON.java/class.

----------------------------- Compilation -----------------------------

Das Program ben�tigt die beiden Java Bibliotheken jpst-1.0.jar und json-simple-1.1.1.jar.

	javac -cp jpst-1.0.jar:json-simple-1.1.1.jar PstMailJSON.java

jpst-1.0.jar ist die Bibliothek die ben�tigt wird um aus den .pst Dateien zu lesen. 
Die eingecheckte jpst-Bibliothek ist der Zeit aber nur eine Evaluations version 
dessen Support ausl�uft. (http://www.independentsoft.de/jpst/index.html)

json-simple-1.1.1.jar wird ben�tigt um die extrahierten Daten in ein json zu Schreiben.
(https://code.google.com/p/json-simple/)

----------------------------- Ausf�hrung -----------------------------

Das Program braucht die beiden Bibliotheken, die ebenfalls zu Compilation n�tig waren,
zur Ausf�hrung. Die .pst Datei wird als Parameter �bergeben.

Wichtig: Es muss gen�gend Arbeitsspeicher zugeteilt werden (ungef�hre  Gr��e der Pst Datei)
um Java-OutofMemory Errors zu verhindern.

	java -Xmx12g -cp .:jpst-1.0.jar:json-simple-1.1.1.jar PstMailJSON Datei.pst

Die extrahierten Daten werden im json Format im Ordner data abgelegt.
Die E-Mail anh�nge werden im Ordner Attachments abgelegt.
Die Ordner data und Attachments m�ssen vor der Ausf�hrung des Programms erstellt werden. 
Das Program ist f�r UNIX �hnliche Maschinen konfiguriert,
es m�ssen daher die Fade von / in \ ge�ndert werden(Zeile 217 und 470), 
sollte es auf Windows Ger�ten zum Einsatz kommen.

----------------------------- Funktionsweise -----------------------------

Das Program kriegt die .pst Datei als Input. Initialisiert sie und geht alle Ordner und Mails (Items) durch.
Die ids der Mails dienen als Name f�r das json. Da sich die id �berschneiden k�nnen,
wenn man mehrere pst Daten extrahiert, generiert das Program eine neue id.
�berpr�ft wird dies in dem festgestellt wird ob die Datei schon im data Ordner existiert.
Die Mail wird als Item dann an die Funktionen weiter gegeben die die Date extrahieren. 
Die Attachments enthalten im Namen die id des json/der Mail.

Weiterverarbeitung der Extrahierten daten

Die jsons werden an CloverETL �bergeben und in einen Elasticsearch such Index geschrieben.
Der Datenpath wird an CloverETL mit einem * �bergeben (z.B: ./data/*.json).
CloverETL schreibt die daten 1:1 in den Elasticsearch Suchindex http://hacking-team/mail.

----------------------------- Elasticsearch -----------------------------

Elasticsearch muss von der Herrstellerseite (https://www.elastic.co) heruntergeladen werden. 

Es m�ssen folgende dinge an der datei config/elasticsearch.yml beigef�gt werden:

http.cors.enabled: true
http.cors.allow-origin: "/.*/"

Es muss vor dem ausf�hren des CloverETL-graph gestartet werden.

----------------------------- Kibana -----------------------------

Der Apache server muss konfiguriert werden. (Root Privilegien werden ben�tigt)

Mac OSX
/etc/apache/httpd.conf

Das php-Module muss aktiviert werden:

LoadModule php5_module libexec/apache2/libphp5.so

Und die Berechtigungen ge�ndert werden

<Directory /> 
	AllowOverride none
	Order allow,deny
	allow from all
#	Require all denied
</Directory>

<Directory "/Library/WebServer/Documents">
	Options Indexes FollowSymLinks Multiviews
	MultiviewsMatch Any
	AllowOverride All
	Require all granted
</Directory>

Kibana muss aus dem Spiegel Hausinternen git-repository ausgecheckt werden.
Der Inhalt muss dann den Ordner /Library/WebServer/Documents kopiert werden. 
(Sicherstellen das .htaccess mit kopiert wurde.)

Apache Server starten: sudo apachectl start

Kibana kann nun �ber http://localhost aufgerufen werden.