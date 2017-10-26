# Product Backlog

## Epic 1 *Oberfläche*

> Als **Benutzer** möchte ich *eine einfache, funktionale Benutzeroberfläche bedienen*, um *effizient meine Arbeit zu verrichten*.

In diesem Abschnitt sind alle Activities bzw. Screens aufgeführt,
die der **Benutzer** (**Admin**, **Sachbearbeiter** und **Kunde**) während der Interaktion zu sehen bekommt. Darunter fallen Login-, Karten-, Kontroll-, und Detailansichten.

### Feature 1.1 *Design/Branding*

> Als **Kunde** möchte ich *eine schöne Programmoberfläche*, um *beim Arbeiten mit der App konzentriert zu bleiben*.

- Aufwandsschätzung: <span style="color:orange">[L]</span>
- Akzeptanztests:
    - *Das Design soll den **Benutzer** ansprechen.*
    - *Die Farben sollen mit positiven Emotionen verbunden sein.*
    - *Der Wiedererkennungwert soll hoch sein.*
    
### Feature 1.2 *Login-Ansicht*

> Als **Benutzer** möchte ich *mich einloggen*, um *die App sinngemäß zu nutzen*.

- Aufwandsschätzung: <span style="color:green">[M]</span>
- Akzeptanztests:
    - *Wenn der Login fehlschlägt, soll ein Hinweis ausgegeben werden.*
    - *Nach erfolgreichen Einloggen soll der nächste Screen (Activity) aufgerufen werden.*

### Feature 1.3 *Hauptansicht (Karte)*

> Als **Benutzer** möchte ich *eine interaktive Karte*, um *positionsabhängige Aufgaben zu erledigen*.

- Aufwandsschätzung: <span style="color:red">[XL]</span>
- Akzeptanztests:
    - *Die Kartenansicht soll übersichtlich und intuitiv sein.*
    - *Schadensfälle können während des Erfassens/Bearbeitung in der Kartenansicht dargestellt werden.*
    - *Die Kartenansicht enthält Eingabemöglichkeiten, um Positionsmarker/Polygone setzen zu können.*
    - *Auf der Karte platzierte Elemente können für weitere Informationen angetippt werden.*

### Feature 1.4 *Kontrollzentrum*

> Als **Benutzer** möchte ich *die Kontrolle über für mich relevante Anwendungsdaten/-optionen haben*.
- Aufwandsschätzung: <span style="color:orange">[L]</span>
- Akzeptanztests:
    - *Die Metadaten (Name, Kundennummer, etc.) sind einsehbar und gemäß der Nutzerrolle editierbar.*
    - *Die Listen von Verträgen/Versicherungsobjekten sind erreichbar.*
    - *Die Hauptansicht (Karte) soll mit höchstens zwei Eingaben erreichbar sein.*
    - *Weitere optionale Einstellungen der App müssen über das Kontrollzentrum verlinkt sein.*
    - *Der **Benutzer** muss sich ausloggen können.*

### Feature 1.5 *Detail-Ansicht*

> Als **Benutzer** möchte ich *Details zu einem Programminhalt einsehen/eingeben können*.

- Aufwandsschätzung: <span style="color:orange">[L]</span>
- Akzeptanztests:
    - *Wichtige Informationen sollen hervorgehoben werden.*
    - *Man soll Detailansichten jederzeit über den Zurück-Button verlassen können und zum letzten Zustand zurückkehren.*
    - *Die Detailansicht erlaubt einen Export der aktuellen Inhalte.*
    
## Epic 2 *Hintergrundprozesse*

> Als **Benutzer** möchte ich *mir über die Programmlogik keine Gedanken machen, erwarte aber, dass die App funktioniert*.

Keine Uhr ohne ausgeklügeltes Uhrwerk! Hier werden alle integralen Hintergrundprozesse aufgelistet, die die Gesamtfunktion der App gewährleisten sollen.

### Feature 2.1 *Map-Schnittstelle*

> Als **Benutzer** möchte ich, *dass die Karte der App einen  großen Funktionsumfang hat, der mir meine Arbeit erleichtert*.

- Aufwandsschätzung: <span style="color:red">[XL]</span>
- Akzeptanztests:
    - *Alle markierten Flächen/Punkte der Karte werden offline angezeigt*
    - *Flächeninhalte von Polygonen werden automatisch berechnet und angezeigt*
    - *Vorgeladene/bereits betrachtete Areale der Karte können offline dargestellt werden*

### Feature 2.2 *Lokalisierungs-Schnittstelle*

> Als **Benutzer** möchte ich, *dass meine Position mit hoher Präzision und Genauigkeit bestimmt wird*.

- Aufwandsschätzung: <span style="color:green">[M]</span>
- Akzeptanztests:
    - *Die Lokalisierung muss ohne Mobilfunk funktionieren.*
    - *Berechtigungen für Lokalisierungsdienste/GPS müssen vom Nutzer abgefragt werden.*
    - *Aktivierung der Lokalisierungsdienste (Google-Services/)Mobilfunk/GPS muss abgefragt werden, falls diese deaktiviert sind.*

### Feature 2.3 *Benutzerverwaltung*

> Als **Admin** möchte ich ***Benutzer** verwalten und deren **Rollen** ändern*.
> 
> Als **Sachbearbeiter** möchte ich ***Kunden** hinzufügen und verwalten*.

- Aufwandsschätzung: <span style="color:green">[M]</span>
- Akzeptanztests:
    - *Es gibt die Rollen **Admin**, **Sachbearbeiter** und **Kunde**.*
    - *Die Benutzerverwaltung soll ein klar geteiltes Rechtesystem besitzen, das keine Fragen darüber offen lässt, wer welche Rechte besitzt.*
    - *Es soll sichergestellt sein, dass jeder **Benutzer** nur die ihm zugewiesenen Rechte hat.*

### Feature 2.4 *Verwaltung von Versicherungsverträgen*

> Als **Admin** möchte ich *Versicherungsverträge erstellen, einsehen und verwalten*.
> 
> Als **Sachbearbeiter** möchte ich *Versicherungsverträge einsehen und zuweisen*.
>
> Als **Kunde** möchte ich *meine aktuellen Versicherungsverträge sehen*.
- Aufwandsschätzung: <span style="color:green">[M]</span>
- Akzeptanztests:
    - *Die Versicherungsverträge werden verschlüsselt gespeichert*.
    - *Nur Admins und Sachbearbeiter dürfen alle Verträge sehen.*

### Feature 2.5 *Verwaltung von Versicherungsobjekten*

> Als **Kunde** möchte ich *einzelne Versicherungsobjekte anlegen, löschen und verwalten*.
- Aufwandsschätzung: <span style="color:green">[M]</span>
- Akzeptanztests:
    - *Die Versicherungsobjekte sollen wie sensible Daten behandelt werden.*
    - *Es soll sichergestellt werden, dass nur berechtigte **Benutzer** Einsicht in die Versicherungsobjekte nehmen können.*
    - *TODO*

### Feature 2.6 Datenverschlüsselung

> Als **Benutzer** möchte ich, dass *meine Daten verschlüsselt gespeichert werden*, um *sie vor dem Zugriff durch Dritte zu schützen*.

- Aufwandsschätzung: <span style="color:green">[M]</span>
- Akzeptanztests:
    - *Die Daten werden nicht im Klartext gespeichert.*
    - *Nach dem Beenden der App werden alle temporären Dateien gelöscht.*
    
### Feature 2.7 Laden/Speichern der Nutzerdaten

> Als **Benutzer** möchte ich, *dass beim Schließen der App der aktuelle Zustand gespeichert wird*, um *beim nächsten Öffnen nahtlos weiterzuarbeiten*.

- Aufwandsschätzung: <span style="color:green">[M]</span>
- Akzeptanztests:
    - *Vor dem Schließen der App werden aktuelle Daten im Hauptspeicher verschlüsselt abgelegt.*
    - *Nach dem Wiederöffnen der App befindet man sich an derselben Stelle.*
    
### Feature 2.8 Datenexport

> Als **Benutzer** möchte ich *meine Daten exportieren*, um *diese mit anderen zu teilen oder um Backups anzufertigen.*

- Aufwandsschätzung: <span style="color:lime">[S]</span>
- Akzeptanztests:
    - *Die Daten können in Textform oder JSON/XML-Format geteilt werden*.
    - *Die Daten können auf dem Hauptspeicher abgelegt werden*.
