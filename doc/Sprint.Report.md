# Sprint Report (1)

In diesem Sprint wurden die bis zum [Meilenstein M4](https://sopra.informatik.uni-stuttgart.de/sopra-ws1718/SoPra-Doku-Entwickler/blob/master/Meilensteine.Abgaben.und.Zielplattform.md#m4)
erledigten User Stories abgearbeitet.


## Verbesserte Dokumente

* Aus dem Product Backlog wurde *Feature 1.1 Design/Branding* entfernt.
* Ein Akzeptanztest *Die Listen von Verträgen/Versicherungsobjekten sind erreichbar.* 
wurde vom *Feature 1.4 Kontrollzentrum* entfernt.

## Tests/Testprotokolle/Nachweis der Testabdeckung

Die Testcoverage befindet sich im doc Ordner oder unter folgendem Link
https://sopra.informatik.uni-stuttgart.de/sopra-ws1718/sopra-team-6/blob/m4/doc/coverage/testreport.zip

Die Testabdeckung beträgt 67%.


# Sprint Report (2)

In diesem Sprint wurden die bis zum [Meilenstein M5](https://sopra.informatik.uni-stuttgart.de/sopra-ws1718/SoPra-Doku-Entwickler/blob/master/Meilensteine.Abgaben.und.Zielplattform.md#m5)
erledigten User Stories abgearbeitet.

## Verbesserte Dokumente

### Enfernte Akzeptanztests

* Feature 1.3: Kontrollzentrum:
   - Die Listen von Verträgen/Versicherungsobjekten sind erreichbar.
   - Weitere optionale Einstellungen der App müssen in das Kontrollzentrum eingebettet sein.
   
* Feature 2.9: Datenexport
 - Die Daten können auf dem Hauptspeicher abgelegt werden
   
### Hinzugefügte Akzeptanztests   

* Feature 2.5: Verwaltung von Versicherungsobjekten
   - Kunden dürfen keine Verträge löschen können.

* Feature 2.9: Datenexport
   - Die Daten können als Fließtext geteilt werden.
   - Die Ausgabe der Daten kann sowohl im Klartext als auch verschlüsselt erfolgen.

Mehr Details finden sich in den Gitlab Issues.

## Tests/Testprotokolle/Nachweis der Testabdeckung

Die Testcoverage befindet sich im doc Ordner oder unter folgendem Link
https://sopra.informatik.uni-stuttgart.de/sopra-ws1718/sopra-team-6/blob/m5/doc/coverage

Die automatisierte Testabdeckung beträgt 58%, einige Features können jedoch nur gut von Hand gestestet werden, weshalb wir unsere hinzugefügten Features vor jedem Commit auf unseren localen Geräten direkt testen. Somit ist die eigentlich testabdeckung deutlich höher als die automatisierte. Testen war für uns ein wichtiger bestandteil des entwicklungsprozess, weshalb wir unsere Commits und Features gegeseitig immer gestestet haben, was sich durcxh zahlreiche Bugreports in Discord belegen lässt. 

## Additional Features

- Userverwaltung, die App untersetützt mehrer User zeitgleich
- Der User kann aus vorgefertigten Profilbildern auswählen
- Die Daten können verschlüsselt exportiert werden, sowohl in Json als Auch Plaintext. Dies kann man dann bequem direkt übder die Android-Teilen-Funtionen versenden
- Es wurde viel wert auf die Gestaltung der UI gelegt, und durch animationen aufgehübscht
- In der Readme finden sich noch viele andere Additional Features