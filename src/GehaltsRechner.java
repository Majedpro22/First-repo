import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Die Rechenzentrale des programmes.
 * Verarbeitet die Schichten, summiert die Gehälter und generiert die Abrechnung
 * Nutzt moderne Datenstrukturen (List) und Streams zur Auswertung
 */
public class GehaltsRechner {
    private List<Schicht> schichten;
    private final double stundenlohn;
    private final double monatsGrenze;
    private final double zuschlagSonntag;
    private final double zuschlagNacht;
    private final int nachtStartStunde;

    /**
     * Konstruktor zur Initialisierung der Rahmenbedingungen
     *
     * @param stundenlohn Basis-Lohn in Euro
     * @param monatsGrenze Maximaler Verdienst (z.B. 603 Euro)
     * @param zuschlagSonntag Faktor für Sonntagszuschlag (z.B. 0.5 für 50%)
     * @param zuschlagNacht Faktor für Nachtzuschlag (z.B. 0.25 für 25%)
     * @param nachtStartStunde Stunde, ab der der Nachtzuschlag gilt (z.B. 20)
     */
    public GehaltsRechner(double stundenlohn, double monatsGrenze, double zuschlagSonntag, double zuschlagNacht, int nachtStartStunde) {
        this.stundenlohn = stundenlohn;
        this.monatsGrenze = monatsGrenze;
        this.zuschlagSonntag = zuschlagSonntag;
        this.zuschlagNacht = zuschlagNacht;
        this.nachtStartStunde = nachtStartStunde;

        // leere Liste initialisieren
        this.schichten = new ArrayList<>();
    }

    /**
     * Befüllt den Rechner mit bereits existierenden Schichten (z.B. nach dem Laden aus der CSV)
     * @param geladeneSchichten Die eingelesene Liste vom DateiManager
     */
    public void setSchichten(List<Schicht> geladeneSchichten) {
        this.schichten = geladeneSchichten;
    }

    /**
     * Gibt die aktuelle Liste zurück (wird gebraucht, um sie später zu speichern)
     * @return Liste aller Schichten
     */
    public List<Schicht> getSchichten() {
        return schichten;
    }

    /**
     * Fügt eine neue Schicht hinzu und prüft sofort, ob das Limit überschritten wurde
     * @param schicht Die neue Schicht
     */
    public void schichtHinzufuegen(Schicht schicht) {
        schichten.add(schicht); //List nimmt die arbeit ab, kein Index-Zählen mehr!
        System.out.println("\u001B[32m" + "Schicht gespeichert" + "\u001B[0m");

        if (berechneGesamtMonatsLohn() > monatsGrenze) {
            System.out.println("\u001B[31m" + "WARNUNG: Verdienstgrenze von " + monatsGrenze + "€ überschritten !!!" + "\u001B[0m");
        }
    }

    /**
     * Diese Methode berechnet den Lohn für EINE Schicht Stunde für Stunde
     * (Fachliche Logik aus Sem 1 beibehalten, da sie perfekt funktioniert)
     */
    public double berechneSchichtLohn(Schicht s) {
        double lohnFuerdieseSchicht = 0;

        for (int stunde = s.getStartStunde(); stunde < s.getEndStunde(); stunde++) {
            double aktuellerStundenlohn = this.stundenlohn;

            if (s.istSonntag()) {
                aktuellerStundenlohn += (this.stundenlohn*this.zuschlagSonntag);
            }
            if (stunde>=this.nachtStartStunde) {
                aktuellerStundenlohn+= (this.stundenlohn*this.zuschlagNacht);
            }
            lohnFuerdieseSchicht+= aktuellerStundenlohn;
        }
        return lohnFuerdieseSchicht ;
    }
    /**
     * Summiert die Löhne aller gespeicherten Schichten
     * Nutzt die Java Stream API für einen sauberen Einzeiler
     * @return Der Gesamtlohn
        */
    public double berechneGesamtMonatsLohn() {
        //verwandelt die Schichten in ihre Geldbeträge
        // und summieren sie am Ende auf
        return schichten.stream()
                .mapToDouble(s -> berechneSchichtLohn(s))
                .sum();
            }

    /**
     * Druckt eine formatierte Lohnabrechnung auf die Konsole
     */
    public void druckeAbrechnung() {
        System.out.println("\u001B[36m\n          Lohnabrechnung      \u001B[0m");
        System.out.println("Basisdaten: " + stundenlohn + "€/h, Nacht ab " + nachtStartStunde + " Uhr");

        // Wir sortieren die Liste vor der Ausgabe
        // Das klappt automatisch, weil wir im 1. Schritt in Schicht.java ""implements Comparable" eingebaut haben!
        Collections.sort(schichten);

        // forEach mit Lambda aus Woche 8) anstatt alter for-Schleife
        schichten.forEach(s ->  {
            double lohn = berechneSchichtLohn(s);
            System.out.println(s.toString()+ " | \u001B[32m" + lohn + " €\u001B[0m");
        });
        System.out.println("----------------------");
        System.out.println("GESAMT: \u001B[32m" + berechneGesamtMonatsLohn() + "\u001B[0m € (Limit: \u001B[31m" + monatsGrenze + " €\u001B[0m)");

        //Comparator
        // Sucht die lukrativste Schicht heraus, falls es überhaupt eine gibt
        Optional<Schicht > teuerste = schichten.stream()
                .max(Comparator.comparingDouble(s ->  berechneSchichtLohn(s) ));
        //ifPresent schützt vor NullPointerExceptions, falls die Liste noch komplett leer ist
        teuerste.ifPresent(s ->
                System.out.println("Info: Deine lukrativste Schicht war am " + s.getDatum() + " mit " + berechneSchichtLohn(s) + " €")
        );
    }
}