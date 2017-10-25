# Product Backlog

## Epic 1 *Oberfläche*

> Als **Benutzer** möchte ich *eine einfache, funktionale Benutzeroberfläche bedienen*, um *effizient meine Arbeit zu verrichten*.

In diesem Abschnitt sind alle Activities bzw. Screens aufgeführt,
die der **Benutzer** (**Admin**, **Sachbearbeiter** und **Kunde**) während der Interaktion zu sehen bekommt. Darunter fallen Login-, Karten-, Kontroll-, und Detailansichten.

### Feature 1.1 *Design/Branding*

> Als **Kunde** möchte ich *eine schöne Oberfläche*, um *ein gutes Gefühl bei der Nutzung der App zu bekommen*.

- Aufwandsschätzung: [L]
- Akzeptanztests:
    - *Das Design soll den **Benutzer** ansprechen.*
    - *Die Farben sollen mit positiven Emotionen verbunden sein.*
    - *Der Wiedererkennungwert soll hoch sein.*
    - *Das Design soll einen professionellen Eindruck machen.*
    
### Feature 1.2 *Login-Ansicht*

> Als **Benutzer** möchte ich *mich einloggen*, um *die App sinngemäß nutzen zu können*.

- Aufwandsschätzung: [M]
- Akzeptanztests:
    - *Es gibt die Rollen **Admin**, **Sachbearbeiter** und **Kunde**.*
    - *Wenn der Login fehlschlägt, soll ein Hinweis ausgegeben werden.*
    - *Nach erfolgreichen Einloggen soll der nächste Screen (Activity) aufgerufen werden.*

### Feature 1.3 *Hauptansicht (Karte)*

> Als **Benutzer** möchte ich *eine übersichtlich strukturierte Kartenansicht haben*, um *effizient mit der App arbeiten zu können.*.

- Aufwandsschätzung: [XL]
- Akzeptanztests:
    - *Die Kartenansicht soll übersichtlich sein.*
    - *Die Kartenansicht soll intuitiv sein.*
    - *Die Kartenansicht enthält Eingabemöglichkeiten, um Marker setzen zu können.*

### Feature 1.4 *Kontrollzentrum*

> Als **Benutzer** möchte ich *Kontrolle über für mich relevante Metadaten/Optionen haben*, um *korrekte Programmabläufe sicherzustellen.*
- Aufwandsschätzung: [M]
- Akzeptanztests:
    - *Die Metadaten (Name, Kundennummer) werden in der Verwaltungs-Ansicht aufbereitet dargestellt.*
    - *Die Metadaten sind im Rahmen der Benutzerrolle editierbar.*
    - *Die Hauptansicht (Karte) soll mit höchstens zwei Eingaben erreichbar sein.*
    - *Der **Benutzer** muss sich ausloggen können.*

### Feature 1.5 *Detail-Ansicht*

> Als **Benutzer** möchte ich *Details zu einem Element einsehen/eingeben können*, um *auf kürzestem Wege gewünschte Aktionen durchzuführen*.

- Aufwandsschätzung: [L]
- Akzeptanztests:
    - *Wichtige Informationen sollen hervorgehoben werden.*
    - *Die Ansicht soll übersichtlich und intuitiv sein.*
    - *Man soll Detailansichten jederzeit über den Zurück-Button verlassen können und zum letzten Zustand zurückkehren.*
    - *Die Detailansicht erlaubt einen Export der aktuellen Inhalte.*
    
## Epic 2 *Schnittstellen*

> Als **Benutzer** möchte ich *KP! *, um *Nutzen*.

Die Schnittstellen agieren als Middleware zwischen den einzelnen Ansichten und den mit dem Backend andgebunden Systemen. Über die Schnittstellen sollen Daten geladen und für die jeweiligen Ansichten aufbereitet werden.


### Feature 2.1 *Benutzerverwaltung*

> Als **Admin** möchte ich **Benutzer** verwalten und deren **Rollen** ändern können*, um *die volle Kontrolle über alle **Benutzer** zu haben*.
> Als *Sachbearbeiter* möchte ich *ich Kunden hinzufügen und verwalten können*, um *nicht auf externe Systeme zur Verwaltung der **Benutzer** angewiesen zu sein*.

- Aufwandsschätzung: [M]
- Akzeptanztests:
    - *Die Benutzerverwaltung soll ein klar geteiltes Rechtesystem haben, wo ersichtlich ist, wer welche Rechte hat.*
    - *Es soll sichergestellt werden, dass der einzelne **Benutzer** nur die ihm zugewisenen Rechte hat.*

### Feature 2.2 *Verwaltung von Versicherungsverträgen*

> Als **Kunde** möchte ich *alle meine Versicherungsverträge einsehen und verwalten können*, um *diese immer an den aktuellen Stand anpassen zu können*.

- Aufwandsschätzung: [M]
- Akzeptanztests:
    - *Die Versicherungsverträge sollen wie sensible Daten behandelt werden.*
    - *Es soll sichergestellt werden, dass nur berechtigte **Benutzer** Einsicht in die Versicherungsverträge haben.*

### Feature 2.3 *Verwaltung von Versicherungsobjekten*

> Als **Kunde** möchte ich *einzelne Versicherungsobjekte anlegen, löschen und verwalten können*, um *TODO*.

- Aufwandsschätzung: [M]
- Akzeptanztests:
    - *Die Versicherungsobjekte sollen wie sensible Daten behandelt werden.*
    - *Es soll sichergestellt werden, dass nur berechtigte **Benutzer** Einsicht in die Versicherungsobjekte nehmen können.*
    - *TODO*

### Feature 2.4 *Map-Schnittstelle*

> Als **Benutzer** möchte ich *TODO*, um *TODO*.

- Aufwandsschätzung: [XL]
- Akzeptanztests:
    - *TODO (Beschreibung von Testfällen die das erwartete Verhalten des gesamten Features überprüfen.)*
    - *TODO*
    - *TODO*

### Feature 2.5 *Lokalisierungs-Schnittstelle*

> Als **Rolle** möchte ich *TODO*, um *TODO*.

- Aufwandsschätzung: [M]
- Akzeptanztests:
    - *TODO (Beschreibung von Testfällen die das erwartete Verhalten des gesamten Features überprüfen.)*
    - *TODO*
    - *TODO*


## Epic 3 *Backend*

> Als **Benutzer** möchte ich *nur indirekt mit dem Backend agieren*, um *eine möglichst intuitive App zu ermöglichen*.

Mit dem Backend können einzelne Zustände der App zwischengespeichert und archiviert werden. Es muss sichergestellt werden, dass niemand unberechtigten Zugriff auf die einzelnen Daten hat. Es darf nur über vorgegebene Schnittstellen mit dem Backend kommuniziert werden und der **Benutzer** darf unter keinen einfachen Umständen direkten Zugriff erlangen. 

### Feature 3.1 Datenverschlüsselung

> Als **Benutzer** möchte ich *meine Daten verschlüsselt wissen*, um *sie vor dem Zugriff durch Dritte zu schützen*.

- Aufwandsschätzung: [M]
- Akzeptanztests:
    - *Die Daten werden nicht im Klartext gespeichert.*
    - *Nach dem Beenden der App werden alle temporären Dateien gelöscht.*
    
### Feature 3.2 Laden/Speichern der Nutzerdaten

> Als **Benutzer** möchte ich, *dass beim Verlassen der App aktuelle Daten gespeichert werden*, um *beim nächsten Öffnen nahtlos weiterarbeiten zu können*.

- Aufwandsschätzung: [M]
- Akzeptanztests:
    - *Vor dem Schließen der App werden aktuelle Daten im Hauptspeicher verschlüsselt abgelegt.*
    - *Nach dem Wiederöffnen der App befindet man sich an derselben Stelle.*
    
### Feature 3.3 Datenexport

> Als **Benutzer** möchte ich *meine Daten exportieren*, um *sie einerseits sichern und andererseits teilen zu können.*.

- Aufwandsschätzung: [S]
- Akzeptanztests:
    - *Die Daten liegen beim Teilen im Textformat vor.*
    - *Die Daten liegen beim Export im JSON/XML-Format vor.*
    - *Die Daten werden beim Export auf dem internen Speicher des Smartphones abgelegt.*