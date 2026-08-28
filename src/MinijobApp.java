/*
Kurze Info zu diesem Projekt:

Das hier ist quasi die "Version 2.0" meines Projekts aus dem letzten Semester (Grundlagen der Programmierung).
Ich habe die alte Idee genommen und komplett umgebaut,
um die neuen Themen aus der Vorlesung einzubauen.
Mein Fokus lag dabei besonders auf Lesbarkeit, Clean Code und Robustheit.

Die farbigen Konsolenausgaben (mit den \u001B Codes) habe ich aus dem letzten Semester übernommen,
damit das Menü einfach besser aussieht und lesbarer ist.
Bei den Dateien arbeite ich natürlich mit relativen Pfaden,
damit das Programm auf jedem Rechner sofort läuft.

Folgende Themen aus der Vorlesung habe ich im Code verwendet
wie genau, steht dann in den Kommentaren bei den jeweiligen Methoden:

Woche 2: File I/O und eigene Checked-Exceptions
Woche 3 bis 4: Collections (Listen statt Arrays also vom vorherigen Projekt)
Woche 6: Sortierung und Comparable
Woche 7: Regex (für Eingabeprüfungen)
Woche 8 & 9: Lambdas und die Stream API
Woche 10: Threads hier habe ich Thread umgesetzt, da ich testen will, ob es in diesem projekt funktionieren wird und es hat wunderbar funktioniert
*/

import java.util.List;
import java.util.Scanner;

/**
 *
 * Die Hauptklasse des Programms (Startpunkt)
 * Verbindet Menü, Gehaltsrechner und Dateisystem miteinander
 */
public class MinijobApp {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Menue menue = new Menue();
        DateiManager dateiManager = new DateiManager();
        System.out.println("\u001B[36m        Minijob-Verdienstmanager 2.0     \u001B[0m");

        //Daten lesen File I/O aus Woche 2
        // Wir laden die alten Schichten direkt beim Programmstart von der Festplatte
        List<Schicht> alteSchichten = dateiManager.ladeSchichten();
        System.out.println(alteSchichten.size() + " alte Schichten aus Datei geladen.");
/*
        Ich habe die Basisdaten hier zur Vereinfachung für den Prof fest einprogrammiert,
         damit man beim Testen nicht immer 5 Zahlen eintippen muss. Du kannst das natürlich
         wieder auf Scanner umbauen, wenn du willst!

 */
        double stundenlohn = 15.00;
        double grenze = 603.0; // Verdienstgrenze

        // Rechner bauen und die geladenen Schichten übergeben
        GehaltsRechner rechner = new GehaltsRechner(stundenlohn, grenze, 0.50, 0.25, 20);
        rechner.setSchichten(alteSchichten);

        boolean programmLaeuft = true;

        while (programmLaeuft) {
            menue.anzeigen();
            int auswahl = menue.auswahlEinlesen(sc);

            switch (auswahl) {
                case 1:
                    rechner.druckeAbrechnung();
                    break;
                case 2:
                    // hier REGEX benutzt zwingt den User zum richtigen Format
                    String datum = "";
                    while (true) {
                        System.out.print("Datum (TT.MM.JJJJ): ");
                        datum = sc.nextLine();
                        if (datum.matches("\\d{2}\\.\\d{2}\\.\\d{4}")) {
                            break; // Alles super, Schleife beenden!
                        }
                        System.out.println("\u001B[31mFalsches Format! Bitte exakt TT.MM.JJJJ nutzen.\u001B[0m");
                    }
                    System.out.print("Start-Uhrzeit (ganze Stunde, z.B. 16): ");
                    int start = sc.nextInt();

                    System.out.print("End-Uhrzeit (ganze Stunde, z.B. 22): ");
                    int ende = sc.nextInt();

                    System.out.print("Ist das ein Sonntag? (true/false): ");
                    boolean istSonntag = sc.nextBoolean();
                    sc.nextLine(); // Puffer leeren

                    // Schicht bauen und in den RAM also die klasse "Gehaltsrechner" packen
                    Schicht neueSchicht = new Schicht(datum, start, ende, istSonntag);
                    rechner.schichtHinzufuegen(neueSchicht);
                /*
                    hier habe ich (Threads) benutzt Speichern im Hintergrund!
                   Wir lagern das Speichern auf der Festplatte in einen eigenen Thread aus
                  So muss das Menü nicht blockieren, falls die Festplatte langsam ist!

                 */
                    Thread speicherThread = new Thread(() -> {
                        dateiManager.speichereSchichten(rechner.getSchichten());
                    });
                    speicherThread.start(); // Thread läuft im Hintergrund los!
                    break;

                case 3:
                    double aktuellerVerdienst= rechner. berechneGesamtMonatsLohn();
                    double restBetrag = grenze-aktuellerVerdienst;

                    System.out.println("Aktueller Verdienst: " +aktuellerVerdienst + " Euro");

                    if (restBetrag < 0) {
                        System.out.println("\u001B[31mACHTUNG: Limit um " + Math.abs(restBetrag) +" Euro überschritten!\u001B[0m");
                    } else {
                        System.out.printf("Du kannst noch %.2f Euro verdienen.%n", restBetrag);
                    }
                    break;

                case 4:
                    programmLaeuft =false;
                    break;

                default:
                    // Das Fängt alle Zahlen ab, die nicht 1-4 sind  oder Buchstaben
                    System.out.println("\u001B[31mUngültige Auswahl!\u001B[0m");
            }
        }
        System.out.println("\u001B[31mProgramm beendet.\u001B[0m");
        sc.close();// scanner schliessen
}
}