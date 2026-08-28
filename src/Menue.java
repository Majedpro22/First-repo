import java.util.Scanner;

/**
 * Diese Klasse ist für die visuelle Darstellung des Hauptmenüs zuständig
 * Sie kümmert sich auch darum,die Menü-Wahl des Benutzers robust einzulesen
 */
public class Menue {

    /**
     * Zeigt die verfügbaren Optionen auf der Konsole an
     *
     */
    public void anzeigen() {
        System.out.println("\n\u001B[36m--- HAUPTMENÜ ---\u001B[0m");
        System.out.println("1 - Lohnabrechnung anzeigen");
        System.out.println("2 - Neue Schicht eintragen");
        System.out.println("3 - Limit-Check (Grenze prüfen)");
        System.out.println("4 - \u001B[31mBeenden\u001B[0m");
        System.out.print("Deine Wahl: ");
    }

    /**
     * Liest die Wahl des Benutzers robust ein.
     * Fängt Fehleingaben (z.B. Buchstaben statt Zahlen) direkt ab.
     *
     * @param sc Der Scanner für die Konsoleneingabe
     * @return Die gewählte Menünummer (1-4) oder 0 bei einem Fehler
     */
    public int auswahlEinlesen(Scanner sc) {
        if(sc.hasNextInt()) {
            int wahl = sc.nextInt();
            sc.nextLine(); // Puffer leeren sonst wird die nächste Eingabe ignoriert

            return wahl;

        } else {
            sc.nextLine(); //Den Müll (z.B. "Apfel") aus dem Scanner wegwerfen
            return 0;      //wir geben 0 zurück. Die switch case in der Main fängt das als "Ungültig" ab
        }
}
}