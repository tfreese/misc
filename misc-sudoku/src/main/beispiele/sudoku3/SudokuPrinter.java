package sudoku3;

import java.io.File;
import java.io.FileWriter;

/**
 * Ausgeben von Sudokus für den Druck.
 *
 * @author Heiner K&uuml;cker
 */
public class SudokuPrinter
{
    /**
     * Anzahl Spalten je Block
     */
    public static int blockColCount = -1;

    /**
     * Anzahl Zeilen je Block
     */
    public static int blockRowCount = -1;

    /**
     * Die Matrix mit den Zellen des Sudokus.
     */
    public static int[][] matrix;

    /**
     * Die Seitenlänge (Grösse) des Sudokus. Die Seitenlänge des Sudokus bestimmt auch die Anzahl von Symbolen des Sudokus.
     */
    public static int size = -1;

    /**
     * Ins HTML auszugebender String für Leerstellen.
     */
    private static final String SPACE =
            // "" + (char) 160; // weisses Zeichen 160
            "&nbsp;";

    /**
     * TODO
     */
    public static String startTimeStr;

    /**
     * Speichern eines String in einer Datei ohne weitere Informationen.
     *
     * @param pOutStr auszugebender String
     * @param pOutFile Datei, in welcher der String gespeichert werden soll
     * @return ob erfolgreich
     */
    public static boolean strToFile(final String pOutStr, final File pOutFile)
    {
        System.out.println("Schreibe: " + pOutFile.getAbsolutePath());
        boolean bReturn = false;

        try
        {
            if (pOutFile.exists())
            {
                pOutFile.delete();
            }

            pOutFile.createNewFile();

            try (FileWriter outFileWriter = new FileWriter(pOutFile))
            {
                outFileWriter.write(pOutStr);
            }

            bReturn = true;
        }
        catch (Exception ex)
        {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }

        return bReturn;

        // TODO in StringUtil.str2file verlagern
        // File writeDir = new File(writeDirName);
        // writeDir.mkdirs();
    }

    /**
     * Speichern eines String in einer Datei ohne weitere Informationen
     *
     * @param outStr auszugebender String
     * @param fileName Name der Datei, in welcher der String gespeichert werden soll
     * @return ob erfolgreich
     */
    public static boolean strToFile(final String outStr, final String fileName)
    {
        return strToFile(outStr, new File(fileName));
    } // end method strToFile

    /**
     * Ausgabe eines Blockes des Sudoku als HTML-Tabelle
     *
     * @param strBuff {@link StringBuffer}
     * @param blockRow int
     * @param blockCol int
     */
    private static void writeBlockHtml(final StringBuffer strBuff, final int blockRow, final int blockCol)
    {
        // gleichmässige Breite aller Zellen eines Blockes absichern
        // final int columnWidthInPercent = Math.round( 100 / blockColCount );

        // strBuff.append(
        // "<table _border=\"2\" height=\"100%\" width=\"100%\" >\n" );
        strBuff.append("<table  style=\"border-style: solid; border-color: black; border-width: 1px; border-collapse: separate; border-spacing: 0 0;\" cellpadding=\"3\" height=\"100%\" width=\"100%\" cellspacing='0' cellpadding='0'>\n");

        for (int row = blockRow; row < (blockRow + blockRowCount); row++)
        {
            strBuff.append("<tr>");

            for (int col = blockCol; col < (blockCol + blockColCount); col++)
            {
                // strBuff.append(
                // "<td cellpadding=\"2\" align=\"right\" style=\"border: 1px solid black;\">"
                // );
                // strBuff.append(
                // "<td style=\"border-style: solid; border-color: black; border-width: 1px; border-collapse: separate; border-spacing: 0 0; font-family: arial; font-size: 12pt; \" align=\"right\" width=\""
                // + columnWidthInPercent +
                // "%\" cellspacing='0' cellpadding='0'>" );
                strBuff.append("<td style=\"border-style: solid; border-color: black; border-width: 1px; border-collapse: separate; border-spacing: 0 0; font-family: arial; font-size: 12pt; \" align=\"right\" width='20pt' cellspacing='0' cellpadding='0'>");

                final int value = matrix[row][col];

                if (value != 0)
                {
                    if ((size > 100) && (value < 100))
                    {
                        // dreistellig ausgeben
                        strBuff.append(SPACE);
                    }
                    if ((size > 9) && (value < 10))
                    {
                        // zweistellig ausgeben
                        strBuff.append(SPACE);
                        // strBuff.append( SPACE );
                    }
                    if (size == 100)
                        // beim 100er-Sudoku 0-99 ausgeben
                    {
                        strBuff.append(value - 1);
                    }
                    else
                    {
                        strBuff.append(value);
                    }
                }
                else
                {
                    // weisses Zeichen 160,
                    // weil bei Leerstring oder Space kein Rahmen um die
                    // Zelle im HTML gezeichnet wird
                    strBuff.append(SPACE);
                    // strBuff.append( SPACE );

                    if (size > 100)
                    {
                        // dreistellig ausgeben
                        strBuff.append(SPACE);
                        strBuff.append(SPACE);
                    }
                    else if (size > 9)
                    {
                        // zweistellig ausgeben
                        strBuff.append(SPACE);
                        // strBuff.append( SPACE );
                    }
                }

                strBuff.append("</td>");
            }

            strBuff.append("</tr>\n");
        }

        strBuff.append("</table>\n");
    }

    /**
     * Ausgabe des Sudoku als HTML-Tabelle
     *
     * @param strBuff {@link StringBuffer}
     */
    private static void writeHtml(final StringBuffer strBuff)
    {
        strBuff.append("<html>\n");
        strBuff.append("<body>\n");
        strBuff.append("<font size=\"7\">\n");

        strBuff.append("<b>\n");

        // strBuff.append(
        // "<table cellpadding=\"0\" cellspacing=\"0\" _border=\"2\">\n" );
        strBuff.append("<table  style=\"border-style: solid; border-color: black; border-width: 1px; border-collapse: separate; border-spacing: 0 0;\" cellpadding='0' cellspacing='0'>\n");

        for (int blockRow = 0; blockRow < size; blockRow += blockRowCount)
        {
            strBuff.append("<tr>");

            for (int blockCol = 0; blockCol < size; blockCol += blockColCount)
            {
                // strBuff.append(
                // "<td style=\"border-style: solid; border-color: black; border-width: 1px; border-collapse: separate; border-spacing: 0 0;\" cellspacing='0' cellpadding='0'>\n"
                // );
                strBuff.append("<td border='0'>\n");

                writeBlockHtml(strBuff, blockRow, blockCol);

                strBuff.append("</td>");
            }

            strBuff.append("</tr>\n");
        }

        strBuff.append("</table>\n");

        strBuff.append("</b>\n");

        strBuff.append("</font>\n");

        strBuff.append("</body>\n");
        strBuff.append("</html>\n");
    }

    /**
     * TODO
     *
     * @param fileNameWithPath Dateiname mit Pfad
     */
    public static void writeHtmlToFile(final String fileNameWithPath)
    {
        final StringBuffer strBuff = new StringBuffer();

        writeHtml(strBuff);

        strToFile(strBuff.toString(), fileNameWithPath);
    }
}
