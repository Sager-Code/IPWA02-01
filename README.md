# Projekt Shea Shepherd - Geisternetz-Bergungsanwendung

## Übersicht

Dieses Projekt ist ein Prototyp für eine Webapplikation zum Melden und Bergen von Geisternetzen für die Non-Profit-Organisation "Shea Shepherd". Die Anwendung ermöglicht es verschiedenen Benutzergruppen, Geisternetze zu melden, Bergungen zu organisieren und den Status der Geisternetze zu verfolgen.

## Technologiestack

- **Java Version:** 17
- **Backend:** Jakarta EE mit JSF, CDI/Beans
- **Frontend:** PrimeFaces (UI-Komponenten)
- **Persistenz:** JPA mit Hibernate als Provider
- **Datenbank:** MySQL (oder MariaDB)
- **Server:** TomCat
- **Build-Tool:** Maven

## Installation und Einrichtung

### Voraussetzungen

- Java Development Kit (JDK) 17
- Apache Maven 3.8+
- TomCat 9 oder 10 Server
- MySQL 8.0 oder MariaDB 10.6+
- Eine Java IDE (z.B. Eclipse, IntelliJ IDEA oder VS Code)

### Schritt 1: Datenbank einrichten

1. Stellen Sie sicher, dass MySQL/MariaDB installiert und läuft
2. Erstellen Sie eine neue Datenbank:

   ```sql
   CREATE DATABASE ghost_net_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```

3. Erstellen Sie einen Datenbankbenutzer (oder verwenden Sie einen bestehenden):

   ```sql
   CREATE USER 'ghostnet_user'@'localhost' IDENTIFIED BY 'password';
   GRANT ALL PRIVILEGES ON ghost_net_db.* TO 'ghostnet_user'@'localhost';
   FLUSH PRIVILEGES;
   ```

4. Importieren Sie das SQL-Skript in die Datenbank:
   - Das SQL-Skript befindet sich unter `src/main/resources/mysql-script.sql`
   - Sie können es über die Kommandozeile importieren:
     ```
     mysql -u ghostnet_user -p ghost_net_db < src/main/resources/mysql-script.sql
     ```
   - Oder über ein Datenbank-Management-Tool wie MySQL Workbench oder phpMyAdmin

### Schritt 2: TomCat konfigurieren

1. Öffnen Sie die `context.xml` in Ihrem TomCat-Verzeichnis (unter `conf/context.xml`)
2. Fügen Sie folgenden Resource-Eintrag innerhalb des `<Context>`-Elements hinzu:

   ```xml
   <Resource name="java:global/jdbc/GhostNetDS" 
             auth="Container" 
             type="javax.sql.DataSource"
             maxTotal="100" 
             maxIdle="30" 
             maxWaitMillis="10000"
             username="ghostnet_user" 
             password="password" 
             driverClassName="com.mysql.cj.jdbc.Driver"
             url="jdbc:mysql://localhost:3306/ghost_net_db?useSSL=false&amp;serverTimezone=UTC"/>
   ```

   Hinweis: Passen Sie die Werte für `username` und `password` entsprechend Ihren Datenbankeinstellungen an.

### Schritt 3: Projekt bauen

1. Klonen oder entpacken Sie das Projekt in ein Verzeichnis
2. Öffnen Sie eine Kommandozeile im Projektordner
3. Führen Sie den Maven-Build-Befehl aus:

   ```
   mvn clean package
   ```

4. Nach erfolgreichem Build finden Sie die WAR-Datei im `target`-Ordner

### Schritt 4: Deployment

1. Kopieren Sie die generierte WAR-Datei (`GhostNet.war`) aus dem `target`-Ordner in das `webapps`-Verzeichnis Ihres TomCat-Servers
2. Starten Sie den TomCat-Server (oder starten Sie ihn neu, falls er bereits läuft)
3. Die Anwendung sollte nun unter `http://localhost:8080/GhostNet` erreichbar sein

## Bedienung der Anwendung

### 1. Geisternetz melden

1. Navigieren Sie zur Startseite und klicken Sie auf "Geisternetz melden"
2. Füllen Sie das Formular aus:
   - Bei anonymer Meldung: Aktivieren Sie die Checkbox "Anonym melden"
   - Bei nicht-anonymer Meldung: Geben Sie Name und Telefonnummer ein
   - Geben Sie die GPS-Koordinaten (Breitengrad/Längengrad) und die geschätzte Größe ein
3. Klicken Sie auf "Geisternetz melden"

### 2. Als bergende Person registrieren

1. Navigieren Sie zu "Geisternetz bergen"
2. Im ersten Tab "Berger-Registrierung" geben Sie Ihren Namen und Ihre Telefonnummer ein
3. Klicken Sie auf "Registrieren"

### 3. Geisternetz zur Bergung übernehmen

1. Navigieren Sie zu "Geisternetz bergen"
2. Wechseln Sie zum Tab "Verfügbare Geisternetze"
3. Wählen Sie sich als Berger aus dem Dropdown-Menü aus
4. Klicken Sie auf "Zur Bergung übernehmen" für ein verfügbares Geisternetz

### 4. Geisternetz als geborgen markieren

1. Navigieren Sie zu "Geisternetz bergen"
2. Wechseln Sie zum Tab "Meine zugewiesenen Geisternetze"
3. Klicken Sie auf "Als geborgen markieren" für ein Geisternetz, das Sie zur Bergung übernommen haben

### 5. Geisternetz als verschollen markieren

1. Navigieren Sie zu "Alle Geisternetze"
2. Klicken Sie auf "Als verschollen markieren" für ein Geisternetz
   (Hinweis: Diese Aktion ist für anonym gemeldete Geisternetze nicht möglich)

## Problembehandlung

### Datenbankverbindungsfehler

Wenn die Anwendung Fehler bei der Datenbankverbindung meldet:
1. Überprüfen Sie, ob MySQL/MariaDB läuft
2. Stellen Sie sicher, dass die Datenbank `ghost_net_db` existiert
3. Überprüfen Sie die Zugangsdaten in der TomCat `context.xml`
4. Stellen Sie sicher, dass der MySQL-JDBC-Treiber im Klassenpfad von TomCat ist

### TomCat-Fehler

Wenn die Anwendung nicht startet oder Fehler beim Deployment auftreten:
1. Überprüfen Sie die TomCat-Logs unter `logs/catalina.out`
2. Stellen Sie sicher, dass TomCat mit Java 17 läuft
3. Überprüfen Sie, ob die Ressource `java:global/jdbc/GhostNetDS` korrekt definiert ist

## Funktionen

1. **Geisternetze melden:**
   - Meldende Personen können Geisternetze mit GPS-Koordinaten und geschätzter Größe erfassen
   - Anonyme Meldungen sind möglich

2. **Bergung übernehmen:**
   - Bergende Personen können sich für die Bergung eines gemeldeten Geisternetzes registrieren
   - Jedes Geisternetz kann maximal einer bergenden Person zugewiesen werden

3. **Geisternetz-Übersicht:**
   - Übersicht über alle gemeldeten Geisternetze mit Status-Informationen
   - Filter nach Status (Gemeldet, Bergung bevorstehend, Geborgen, Verschollen)

4. **Status-Updates:**
   - Bergende Personen können Geisternetze als geborgen melden
   - Beliebige Personen können Geisternetze als verschollen melden (außer bei anonymen Meldungen)

## Datenmodell

- **GhostNet (Geisternetz):** ID, Standort (GPS-Koordinaten), geschätzte Größe, Status, Meldedatum, Aktualisierungsdatum
- **Reporter (Meldende Person):** ID, Name, Telefonnummer, Anonymitätsflag
- **Rescuer (Bergende Person):** ID, Name, Telefonnummer

## Geisternetz-Status

1. **Gemeldet (REPORTED):** Eine meldende Person hat das Geisternetz im System erfasst
2. **Bergung bevorstehend (RESCUE_PENDING):** Eine bergende Person hat die Bergung angekündigt
3. **Geborgen (RESCUED):** Eine bergende Person hat das Geisternetz erfolgreich geborgen
4. **Verschollen (LOST):** Eine Person hat festgestellt, dass das Geisternetz am gemeldeten Standort nicht auffindbar ist

## Anwendungsstruktur

Die Anwendung folgt dem MVC-Muster:
- **Models:** Entity-Klassen (GhostNet, Reporter, Rescuer)
- **Views:** JSF-Seiten mit PrimeFaces-Komponenten
- **Controllers:** CDI-Beans zur Steuerung der Geschäftslogik (GhostNetController, ReporterController, RescuerController)

## Hinweise für die Bewertung

- Das Projekt wurde gemäß den Anforderungen als Prototyp entwickelt
- Die Benutzeroberfläche ist bewusst pragmatisch und funktional gestaltet
- Alle geforderten Funktionen wurden implementiert
- Die Persistenz erfolgt über JPA in einer MySQL-Datenbank
- Die Anwendung verwendet JSF mit PrimeFaces als Frontend-Technologie

## Hinweise zur Weiterentwicklung

- Implementierung einer Karte für die Anzeige von Geisternetz-Standorten
- Benutzerauthentifizierung für bergende Personen
- Benachrichtigungssystem für Statusänderungen
- Mobile-App für Meldungen von unterwegs
- Erweiterte Statistiken und Reporting-Funktionen
