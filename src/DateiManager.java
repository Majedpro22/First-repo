import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Diese Klasse kümmert sich ausschließlich um das Speichern und Laden der Schichten
 *
 *
 */
public class DateiManager {

    // hier leg ich den dateinamen fest relativer pfad
    private static final Path DATEI_PFAD = Path.of("schichten.csv");

    /**
     * Speichert eine Liste von Schichten in die CSV-Datei.
     * Alte Daten in der Datei werden dabei überschrieben.
     *
     * @param schichten Die Liste der Schichten, die gespeichert werden sollen
     */
    public void speichereSchichten(List<Schicht> schichten) {
        // try-with-resources
        try (BufferedWriter writer = Files.newBufferedWriter(DATEI_PFAD, StandardCharsets.UTF_8)) {

            // gehe einfach mit for-each schleife durch die liste
            for (Schicht s : schichten) {
                writer.write(s.toCsvLine()); // wandelt das objekt in den string mit semikolons um
                writer.newLine();            // zilenumbruch
            }

        } catch (IOException e) {
            // wenn die festplatte voll ist , drucken wir den fehler einfach rot aus
            System.out.println("\u001B[31mFEHLER beim Speichern der Datei: " + e.getMessage() + "\u001B[0m");
        }
    }

    /**
     * Liest die CSV-Datei zeilenweise ein und wandelt sie wieder in Schicht-Objekte um
     *
     * @return Eine Liste mit allen  erfolgreich gelesenen Schichten
     */
    public List<Schicht> ladeSchichten() {
        List<Schicht> geladeneSchichten = new ArrayList<>();

        // wenn die datei gar nicht existiert (z.b beim allerersten programmstart)
        // geben wir einfach eine leere liste zurück und machen keinen stress
        if (!Files.exists(DATEI_PFAD)) {
            return geladeneSchichten;
        }

        try (BufferedReader reader = Files.newBufferedReader(DATEI_PFAD, StandardCharsets.UTF_8)) {
            String zeile;
            int zeilenNummer = 0;
            // liest die datei zeile für zeile bis nix mehr da ist (null)
            while ((zeile = reader.readLine()) != null) {
                zeilenNummer++;
                // leere zeilen einfach ignorieren also continue, damit das programm nicht abstürzt
                if (zeile.isBlank()) continue;
                try {
                    //schieben die rohe zeile in unsere extra hilfsmethode unten
                    Schicht s = parseZeile(zeile);
                    geladeneSchichten.add(s);

                } catch (CorruptedShiftException e) {
                    //unsere eigene exception fangen  (wie im skript woche 2)
                    //drucken den fehler und machen mit der nächsten zeile weiter
                    System.out.println("\u001B[31mZeile " + zeilenNummer + " fehlerhaft übersprungen: " + e.getMessage() + "\u001B[0m");
                }
            }
        } catch (IOException e) {
            System.out.println("\u001B[31mFEHLER beim Lesen der Datei: " + e.getMessage() + "\u001B[0m");
}
 return geladeneSchichten;
    }

    /**
     * Hilfsmethode, die einen CSV-String in ein Schicht-Objekt verwandelt
     * Prüft die Daten auf Gültigkeit mit Regex
     *
     * @param zeile Ein String im Format "Datum;Start;Ende;IstSonntag"
     * @return Ein fertiges Schicht-Objekt
     * @throws CorruptedShiftException wenn das Format kaputt ist
     */
    private Schicht parseZeile(String zeile) throws CorruptedShiftException {
        // am semikolon zerschneiden
        String[] parts = zeile.split(";");

        // es MÜSSEN genau 4 teile sein, sonst hat einer in der datei rumgepfuscht
        if (parts.length != 4) {
            throw new CorruptedShiftException("Falsche Anzahl an Spalten", zeile);
        }

        String datum = parts[0].trim();

        // REGEX-CHECK (Woche 7)das Prüft ob das Datum genau TT.MM.JJJJ ist
        if (!datum.matches("\\d{2}\\.\\d{2}\\.\\d{4}")) {
            throw new CorruptedShiftException("Datum hat falsches Format", zeile);
        }

        try {
            //aus den texten echte zahlen und booleans zu machen
            int start = Integer.parseInt(parts[1].trim());
            int ende = Integer.parseInt(parts[2].trim());
            boolean istSo = Boolean.parseBoolean(parts[3].trim());

            return new Schicht(datum, start, ende, istSo);

        } catch (NumberFormatException e) {
            //wenn da "Hund" statt "16" uhr steht, stürzt parseInt ab, wir fangen das
            // und werfen unsere schöne fachliche Exception weiter nach oben
            throw new CorruptedShiftException("Zahlenformat kaputt", zeile);
        }
    }
}