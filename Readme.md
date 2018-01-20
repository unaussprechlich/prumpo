# Prumpo

![Das Interface zur Schadenerfassung](doc/images/header.png)

Für Landwirtinnen und Landwirte, die viele Bodenflächen für den Anbau von
Nutzpflanzen ihr Eigen nennen, sind Schäden auf den Feldern nicht nur lästig,
sondern mitunter auch existenzbedrohend.

Um sich gegen Hagelschäden, Überschwemmungen und Schädlinge abzusichern,
hgibt es schon länger die Möglichkeit,
Versicherungen abzuschließen.

Die Aufnahme der Schäden, die von den Landwirtinnen und Landwirten gemeldet und von Gutachter*innen erfasst werden,
soll durch diese App erleichtert und beschleunigt werden.

Die App ermöglicht es, Felder mittels GPS zu erfassen und Schäden darauf zu markieren.
Für den Schadensfall eingeteilte Gutachter können die Schäden nach dem Namen der Versicherungsnehmer filtern
um auch bei vielen Versicherungsnehmern/Kunden den Überblick zu behalten.
Auch ein Export aller bisherig erfassten Schäden ist vorgesehen.

## Features

**TODO:** Hier die Features (Additional Features) aufzählen und evtl. mit einem Screenshot/Gif demonstrieren o. ä.

## Installation

1. Repository klonen: `git clone`
2. Android Studio Projekt öffnen
3. Android Studio Projekt bauen
4. Android Studio Projekt im Emulator ausführen oder APK erstellen lassen.
5. Die erstellte APK auf das Gerät kopieren.
6. Um die APK zu installieren, müssen *Unbekannte Quellen* zugelassen werden.
Weitere Hinweise können der offiziellen Dokumentation entnommen werden:
Sihe *User opt-in for installing unknown apps* unter https://developer.android.com/distribute/marketing-tools/alternative-distribution.html

## Verwendung der App

### Schädensflächen erfassen

Gutachter sollen die Möglichkeit haben, während der Besichtigung der Schäden
die Feld- und Schadensfeldgrößen der versicherten Objekte zu erfassen.

### Metadaten erfassen und bearbeiten

Wichtige Metadaten wie Name des Gutachters bzw. des Versicherungsnehmers
müssen eingegeben werden.
Andere Metadaten wie zum Beispiel die Fläche werden automatisch berechnet.

## Changelog

Die Entwicklungsgeschichte befindet sich in [CHANGELOG.md](CHANGELOG.md).

## Verwendete Bibliotheken

**TODO:** Verwendete Bibliotheken auflisten

Android Support
* android support preference-v7 v26.1.0
* android support appcompat-v7 v26.1.0
* android support constraint constraint-layout v1.0.2
* android support design v26.1.0
* android support cardview-v7 v26.1.0
* android support recyclerview-v7 v26.1.0

Arch lifecycle
* android arch persistence room runtime v1.0.0
* android arch lifecycle extensions v1.0.0
* android arch lifecycle runtime v1.0.0
* android arch persistence room compiler v1.0.0  
* android arch lifecycle compiler v1.0.0

Butterknife
* jakewharton butterknife v8.8.1  
* jakewharton butterknife-compiler v8.8.1

Dagger
* google dagger dagger v2.13
* google dagger dagger-android v2.13
* google dagger dagger-android-support v2.13  
* google dagger dagger-android-processor v2.13  
* google dagger dagger-compiler v2.13

Sonstige
* greenrobot eventbus v3.1.1
* android gms play-services-maps v11.6.2
* google code gson gson v2.8.2
* joda-time joda-time v2.9.9

## Lizenz

Apache License 2.0

Genaue Bedingungen der Lizenz können in [LICENSE](LICENSE) nachgelesen werden.