/**
 * Eigene Checked-Exception für fehlerhafte Einträge in der Schicht-Datei
 * Wird geworfen, wenn beim Parsen der CSV-Datei eine Zeile nicht dem erwarteten Format entspricht
 * (z. B. fehlende Werte, falsche Trennzeichen oder ungültige Zahlen)
 *
 */
public class CorruptedShiftException extends Exception {

    // hier speichere ich die kaputte Zeile ab, damit ich dem User später genau zeigen kann, 
    // wo in der Datei der Fehler liegt.
    private final String fehlerhafteZeile;

    /**
     * Erstellt eine neue Exception mit einer genauen Fehlermeldung und der fehlerhaften Zeile
     *
     * @param grund            Die genaue Beschreibung, was schiefgelaufen ist (z.B. "Semikolon fehlt")
     * @param fehlerhafteZeile Der Original-String der Zeile aus der CSV-Datei
     */
    public CorruptedShiftException(String grund, String fehlerhafteZeile) {
        // super() ruft den Konstruktor von der Eltern-Klasse (Exception) auf
        // baut den fehlertext direkt zusammen, wie im skript vom prof
        super("Schicht-Daten fehlerhaft (" + grund + "): " + fehlerhafteZeile);

        // einfach nur abspeichern für später
        this.fehlerhafteZeile = fehlerhafteZeile;
    }

    /**
     * Gibt die fehlerhafte Zeile zurück, die diese Exception ausgelöst hat
     *
     * @return Die rohe, kaputte CSV-Zeile
     */
    public String getFehlerhafteZeile() {
        return fehlerhafteZeile;
    }
}