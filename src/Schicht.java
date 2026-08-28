/**
 * Diese Klasse repräsentiert eine einzelne Arbeitsschicht.
 * Sie dient als reiner Datencontainer für Datum, Start- und Endzeit sowie den Sonntags-Status.
 * Das Interface Comparable wird implementiert, damit wir Listen von Schichten später 
 * einfach chronologisch sortieren können.
 */
public class Schicht implements Comparable<Schicht> {

    // Die Basisdaten für eine Schicht
    // Ich mache sie (final), weil sich eine Schicht nach dem Erstellen nicht mehr ändern darf
    private final String datum;
    private final int startStunde;
    private final int endStunde;
    private final boolean istSonntag;

    /**
     * Konstruktor, um ein neues Schicht-Objekt anzulegen.
     *
     * @param datum       Das Datum der Schicht (z.B 01.03.2026)
     * @param startStunde Die Startzeit in vollen Stunden (z.B 18)
     * @param endStunde   Die Endzeit in vollen Stunden (z.B 22)
     * @param istSonntag  true, wenn die Schicht an einem Sonntag stattfand, sonst false
     */
    public Schicht(String datum, int startStunde, int endStunde, boolean istSonntag) {
        this.datum = datum;
        this.startStunde = startStunde;
        this.endStunde = endStunde;
        this.istSonntag = istSonntag;
    }

    /**
     * Gibt das Datum der Schicht zurück
     * @return Das Datum als String
     */
    public String getDatum() {
        return datum;
    }

    /**
     * Gibt die Start-Uhrzeit zurück
     * @return Startstunde als int
     */
    public int getStartStunde() {
        return startStunde;
    }

    /**
     * Gibt die End-Uhrzeit zurück
     * @return Endstunde als int
     */
    public int getEndStunde() {
        return endStunde;
    }

    /**
     * Prüft, ob die Schicht an einem Sonntag war
     * @return true bei Sonntagsarbeit, sonst false
     */
    public boolean istSonntag() {
        return istSonntag;
    }

    /**
     * Wandelt das Objekt in einen Text-String für die CSV-Datei um
     * Die Werte werden mit einem Semikolon getrennt, um sie später wieder einlesen zu können
     *
     * @return Ein zusammengebauter String im CSV-Format
     */
    public String toCsvLine() {
        // Semikolon als Trennzeichen, wie in den Vorlesungs-Logs gelernt
        return datum + ";" + startStunde + ";" + endStunde + ";" + istSonntag;
    }

    /**
     * Gibt eine saubere Darstellung der Schicht für die Konsolen-Abrechnung zurück
     *
     * @return Lesbarer Text mit alle Infos zur Schicht
     */
    @Override
    public String toString() {
        // Wenn es ein Sonntag ist, hängen wir ein "(So)" dran, sonst einen leeren String
        String info = istSonntag ? " (So)" : "";

        // Zusammenbauen für die Ausgabe
        return datum + ": " + startStunde + "-" + endStunde + " Uhr" + info;
    }

    /**
     * Vergleicht diese schicht mit einer anderen Schicht, um eine natürliche Ordnung herzustellen
     *
     * @param other Die andere Schicht, mit der wir diese vergleichen wollen
     * @return ein negativer Wert, 0 oder ein positiver wert für das Sortier-Ergebnis
     */
    @Override
    public int compareTo(Schicht other) {
        // Da das Datum ein String ist, habe ich die eingebaute compareTo-Methode benutzt
        // der String-Klasse aufrufen. Das sortiert die Schichten automatisch
        return this.datum.compareTo(other.getDatum());
    }
}