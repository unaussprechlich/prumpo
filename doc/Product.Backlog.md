# Product Backlog

## Epic 1 *Oberfläche*

> Als **Benutzer** möchte ich *eine einfache, funktionale Benutzeroberfläche bedienen*, um *effizient meine Arbeit zu verrichten.*

In diesem Abschnitt sind alle Activities bzw. Screens aufgeführt, die der **Benutzer** von der App zu sehen bekommt.

### Feature 1.1 *Design/Branding*

> Als **Benutzer** möchte ich *eine schöne Programmoberfläche*, um *beim Arbeiten mit der App angenehm zu gestalten.*

- Aufwandsschätzung: <span style="color:orange">[L]</span>
- Akzeptanztests:
    - [ ] *Das Design soll den* **Benutzer** *ansprechen.*
    - [ ] *Die Farben sollen einen angenehmen Eindruck vermitteln.*
    - [ ] *Der Wiedererkennungwert soll hoch sein.*

### Feature 1.2 *Login-Ansicht*

> Als **Benutzer** möchte ich *mich einloggen*, um *die App sinngemäß zu nutzen.*

- Aufwandsschätzung: <span style="color:green">[M]</span>
- Akzeptanztests:
    - [ ] *Wenn der Login fehlschlägt, soll ein Hinweis ausgegeben werden.*
    - [ ] *Nach erfolgreichem Einloggen soll die Hauptansicht aufgerufen werden.*

### Feature 1.3 *Hauptansicht (Karte)*

> Als **Benutzer** möchte ich *eine übersichtliche Karte*, um *positionsabhängige Aufgaben zu erledigen.*

- Aufwandsschätzung: <span style="color:red">[XL]</span>
- Akzeptanztests:
    - [ ] *Die Kartenansicht enthält Eingabemöglichkeiten, um Positionsmarker/Polygone setzen zu können.*
    - [ ] *Auf der Karte platzierte Elemente können für weitere Informationen angetippt werden.*
    - [ ] *Schadensfälle können während der Erfassung/Bearbeitung in der Kartenansicht dargestellt werden.*
    - [ ] *Alle markierten Flächen/Punkte der Karte werden offline angezeigt.*
    - [ ] *Die Kartenansicht des Schadens zeigt den Schaden als Polygon/Fläche innerhalb der versicherten Objekte.*
    - [ ] *Die Kartenansicht des Schadens zeigt Polygone der versicherten Objekte.*
    - [ ] *Die Kartenansicht soll nicht mehr als 3 zusätzliche Schaltflächen/Buttons außerhalb der Kartenfunktionen enthalten.*

### Feature 1.4 *Kontrollzentrum*

> Als **Benutzer** möchte ich *die Kontrolle über für mich relevante Anwendungsdaten/-optionen haben und diese in der "Offline Funktion" ändern können.*

- Aufwandsschätzung: <span style="color:orange">[L]</span>
- Akzeptanztests:
    - [ ] *Die Metadaten (Name, Kundennummer, etc.) sind einsehbar und gemäß der Nutzerrolle editierbar.*
    - [ ] *Die Listen von Verträgen/Versicherungsobjekten sind erreichbar.*
    - [ ] *Die Hauptansicht (Karte) soll mit höchstens zwei Eingaben erreichbar sein.*
    - [ ] *Weitere optionale Einstellungen der App müssen in das Kontrollzentrum eingebettet sein.*
    - [ ] *Der* **Benutzer** *muss sich ausloggen können.*

### Feature 1.5 *Detail-Ansichten*

> Als **Benutzer** möchte ich *Details zu einem Element (Vertrag, Schadensfall, etc.) einsehen/eingeben können.*

- Aufwandsschätzung: <span style="color:orange">[L]</span>
- Akzeptanztests:
    - [ ] *Die Detailansichten sind jederzeit über den Zurück-Button zu beenden.*
    - [ ] *Nach dem Schließen einer Detailansicht wird der Zustand wiederhergestellt, der vor dem Öffnen der Detailansicht vorhanden war.*
    
## Epic 2 *Hintergrundprozesse*

> Als **Benutzer** möchte ich *mir über die Programmlogik keine Gedanken machen, erwarte aber, dass die App funktioniert.*

Hier werden alle integralen Hintergrundprozesse aufgelistet, die die Gesamtfunktion der App gewährleisten sollen.

### Feature 2.1 *Map-Schnittstelle*

> Als **Benutzer** möchte ich, *dass die Karte der App einen großen Funktionsumfang hat, der mir meine Arbeit erleichtert.*

- Aufwandsschätzung: <span style="color:red">[XL]</span>
- Akzeptanztests:
    - [ ] *Flächeninhalte von Polygonen werden automatisch berechnet.*
    - [ ] *Vorgeladene/bereits betrachtete Areale der Karte können offline gespeichert werden.*

### Feature 2.2 *Lokalisierungs-Schnittstelle*

> Als **Benutzer** möchte ich, *dass meine Position mit hoher Präzision und Genauigkeit bestimmt wird.*

- Aufwandsschätzung: <span style="color:green">[M]</span>
- Akzeptanztests:
    - [ ] *Die Lokalisierung muss ohne Mobilfunk funktionieren.*
    - [ ] *Berechtigungen für Lokalisierungsdienste/GPS müssen vom Nutzer eingeholt werden.*
    - [ ] *Aktivierung der Lokalisierungsdienste (Google-Services/)Mobilfunk/GPS muss abgefragt werden, falls diese deaktiviert sind.*

### Feature 2.3 *Benutzerverwaltung*

> Als **Admin** möchte ich alle **Benutzer** verwalten.
> 
> Als **Gutachter** möchte ich **Kunden** hinzufügen und verwalten.

- Aufwandsschätzung: <span style="color:green">[M]</span>
- Akzeptanztests:
    - [ ] *Es gibt die Rollen* **Admin**, **Gutachter** *und* **Kunde**.
    - [ ] *Alle Rollen können stets nur die Rechte ausüben, die ihnen zustehen.*

### Feature 2.4 *Verwaltung von Versicherungsverträgen*

> Als **Admin** möchte ich *Versicherungsverträge erstellen, einsehen und verwalten.*
> 
> Als **Gutachter** möchte ich *Versicherungsverträge einsehen.*
>
> Als **Kunde** möchte ich *meine aktuellen Versicherungsverträge sehen.*

- Aufwandsschätzung: <span style="color:green">[M]</span>
- Akzeptanztests:
    - [ ] *Nur* **Admins** *und* **Gutachter** *dürfen alle Verträge sehen.*
    - [ ] *Nur* **Admins** *dürfen Verträge anlegen und bearbeiten.*

### Feature 2.5 *Verwaltung von Versicherungsobjekten*

> Als **Benutzer** möchte ich *einzelne Versicherungsobjekte anlegen, löschen und verwalten.*

- Aufwandsschätzung: <span style="color:green">[M]</span>
- Akzeptanztests:
    - [ ] *Es soll sichergestellt werden, dass nur berechtigte* **Benutzer** *Einsicht in die Versicherungsobjekte nehmen können.*

### Feature 2.6 *Verwaltung von Schadensfällen*

> Als **Benutzer** möchte ich *vorhandene Schäden dokumentieren.*

- Aufwandsschätzung: <span style="color:orange">[L]</span>
- Akzeptanztests:
    - [ ] *Das Dokumentieren von Schäden ist möglich.*
    - [ ] *Die Verwaltung ist ohne Internetverbindung möglich.*
    - [ ] *Schadensfälle können nach Name des Versicherungsnehmers gesucht werden.*
    - [ ] *Die Erfassung von Schadensfällen/-Koordinaten verwendet tatsächliche Sensorwerte eines Positionssensors im Gerät.*
    - [ ] *Schadensfälle können mit der Angabe des Versicherungsobjekts (Name des Versicherungsnehmers, Fläche und Koordinaten des Objekts, Region (mind. Landkreis)), Schadensinformationen (Schadensfläche, Schadensposition, Schadens-Koordinaten/-Polygon, Datum) und Name des* **Gutachters** *erfasst werden.*

### Feature 2.7 Datenverschlüsselung

> Als **Benutzer** möchte ich, dass *meine Daten verschlüsselt gespeichert werden*, um *sie vor dem Zugriff durch Dritte zu schützen.*

- Aufwandsschätzung: <span style="color:green">[M]</span>
- Akzeptanztests:
    - [ ] *Die Daten werden nicht im Klartext gespeichert.*
    - [ ] *Es kommen mindestens 256-Bit-Schlüssel zum Einsatz.*

### Feature 2.8 Laden/Speichern der Nutzerdaten

> Als **Benutzer** möchte ich, *dass beim Schließen der App der aktuelle Zustand gespeichert wird*, um *beim nächsten Öffnen nahtlos weiterzuarbeiten.*

- Aufwandsschätzung: <span style="color:green">[M]</span>
- Akzeptanztests:
    - [ ] *Vor dem Schließen der App werden aktuelle Daten im Hauptspeicher verschlüsselt abgelegt.*
    - [ ] *Nach dem Wiederöffnen der App befindet man sich an derselben Stelle, sobald man sich eingelogt hat.*

### Feature 2.9 Datenexport

> Als **Benutzer** möchte ich *meine Daten exportieren*, um *diese mit anderen zu teilen oder um Backups anzufertigen.*

- Aufwandsschätzung: <span style="color:lime">[S]</span>
- Akzeptanztests:
    - [ ] *Die Daten können in Textform oder JSON/XML-Format geteilt werden.*
    - [ ] *Die Daten können auf dem Hauptspeicher abgelegt werden.*
