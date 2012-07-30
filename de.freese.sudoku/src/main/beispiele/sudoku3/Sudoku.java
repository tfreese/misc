package sudoku3;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;

/**
 * Programm zum Loesen und Erzeugen von Sudokus. Zum Erzeugen von Sudokus wird ein komplett
 * geloestes Sudoku erzeugt. Aus dem komplett geloesten Sudoku werden in einem zweiten Schritt
 * Zellen gelöscht, um ein Sudoku-Raetsel zu erzeugen. Je mehr Zellen geloescht werden, umso
 * schwieriger wird das erzeugte Sudoku-Rätsel. Nach dem Löschen von Zellen müsste korrekterweise
 * geprüft werden, ob das Sudoku noch eindeutig lösbar ist. Das ist diesem Programm nicht
 * realisiert. Ein Sudoku besteht aus Blöcken, die wiederum aus Zellen bestehen. Jeder Block hat
 * eine bestimmte Anzahl Zeilen und Spalten. Aus der Anzahl Zeilen und Spalten eines Blockes ergibt
 * sich die Anzahl Blöcke des Sudokus. Die Blöcke werden so übereinander und nebeneinander
 * geschichtet, dass das gesamte Sudoku ein Quadrat aus Zellen bildet. Bei einer Blockgrösse von 3
 * Zeilen und 3 Spalten werden jeweils 3 Blöcke übereinander und nebeneinander angeordnet, so dass
 * sich eine Seitenlänge des Sudokus von 9 ergibt. Bei einer Blockgrösse von 2 Zeilen und 4 Spalten
 * werden jeweils 4 Blöcke übereinander und 2 Blöcke nebeneinander angeordnet, so dass sich eine
 * Seitenlänge des Sudokus von 8 ergibt. Aus der Seitenlänge des Sudokus ergibt sich auch die Anzahl
 * Symbole des Sudokus. Als Symbole können Zahlen, Buchstaben, Strings oder beliebige Grafiken
 * benutzt werden, die sich aber unterscheiden müssen (im Moment sind nur Zahlen als Symbole
 * realisiert). Für eine bessere Performance wird mit statischen Variablen und Methoden und ohne
 * rekursion gearbeitet.
 * 
 * @author Heiner Köcker TODO mal probieren, ob das Programm mit Byte-Arrays schneller ist
 */
public final class Sudoku
{
	/**
	 * In dieses Verzeichnis werden die als HTML-Dateien ezeugten Sudokus ausgegeben.
	 */
	@SuppressWarnings("unused")
	private static final String OUTPUT_FOLDER = "C:\\Sudoku";

	/**
	 * Hilfs-Konstante für quadratische Block-Grössen.
	 */
	private static final int SQUARE_BLOCK_SIZE = 6;

	/**
	 * Anzahl Zeilen je Block
	 */
	final static int blockRowCount = SQUARE_BLOCK_SIZE;

	/**
	 * Anzahl Spalten je Block
	 */
	final static int blockColCount = SQUARE_BLOCK_SIZE;

	/**
	 * Validierungen für die Entwicklung zum Schutz vor Programm-Fehlern.
	 */
	private static final boolean USE_DEVELOP_CHECKS = true;

	/**
	 * Die Seitenlänge (Grüsse) des Sudokus. Die Seitenlänge des Sudokus bestimmt auch die Anzahl
	 * von Symbolen des Sudokus.
	 */
	final static int size = blockRowCount * blockColCount;

	/**
	 * Die Matrix mit den Zellen des Sudokus.
	 */
	static int[][] matrix = new int[size][size];

	/**
	 * Das Verhältnis der Anzahl zu löschender Zellen zur Gesamtanzahl Zellen. Je mehr Zellen
	 * gelöscht werden, desto schwieriger wird das Sudoku. Maximum ist 1 (100 Prozent).
	 */
	static final double cellsToClearRatio = 0.6;

	/**
	 * Klone der Matrix. Dieser Klone wird angelegt, bevor die Zellen gelöscht werden, damit das
	 * Sudoku ein Rätsel wird. Der Klone dient zum anschliessenden Ausdrucken der Lösung.
	 */
	static int[][] matrixClone;

	/**
	 * Die aktuelle Zeile, Basis 0.
	 */
	static int runRow;

	/**
	 * Die aktuelle Spalte, Basis 0.
	 */
	static int runCol;

	/**
	 * Merker für die Anfangs-Zeile des Blockes der aktuell bearbeiteten Zeile, Basis 0.
	 */
	static int runBlockStartRow;

	/**
	 * Merker für die Anfangs-Spalte des Blockes der aktuell bearbeiteten Spalte, Basis 0.
	 */
	static int runBlockStartCol;

	/**
	 * In diesem Array werden die Anfangs-Zeilen-Positionen des jeweiligen Blockes für die einzelnen
	 * Zellen-Zeilen vermerkt. Die Block-Anfangs-Zeilen-Positionen werden beim Beginn des Programmes
	 * in der Methode {@link #createRowToBlockStartRowArr()} ermittelt und beim Setzen der
	 * Zeilen-Position in den Methoden {@link #skipNextCell()} und {@link #skipPrevCell()} von
	 * diesem Array abgefragt. Dient zur Beschleunigung des Programmes.
	 */
	final static int[] rowToBlockStartRowArr = createRowToBlockStartRowArr();

	/**
	 * In diesem Array werden die Anfangs-Spalten-Positionen des jeweiligen Blockes für die
	 * einzelnen Zellen-Spalten vermerkt. Die Block-Anfangs-Spalten-Positionen werden beim Beginn
	 * des Programmes in der Methode {@link #createColToBlockStartColArr()} ermittelt und beim
	 * Setzen der Spalten-Position in den Methoden {@link #skipNextCell()} und
	 * {@link #skipPrevCell()} von diesem Array abgefragt. Dient zur Beschleunigung des Programmes.
	 */
	final static int[] colToBlockStartColArr = createColToBlockStartColArr();

	/**
	 * In diesem boolean-Array wird je Zeile vermerkt, welche Werte in der entsprechenden Zeile
	 * schon verwendet wurden. Dadurch muss dieses Programm nicht bei jedem zu prüfendem Wert in der
	 * Methode {@link #rowValid(int)} die entsprechende Zeile auf der Suche, ob der Wert schon in
	 * der Zeile enthalten ist, durchlaufen, sondern muss nur ein Abfrage auf dieses Array
	 * durchführen. Damit dies funktioniert, muss dieses Array in den Methoden {@link #set(int)} und
	 * {@link #reset()} aktualisiert werden.
	 */
	final static boolean[][] usedValuesPerRow = new boolean[size][size + 1];

	/**
	 * In diesem boolean-Array wird je Spalte vermerkt, welche Werte in der entsprechenden Spalte
	 * schon verwendet wurden. Dadurch muss dieses Programm nicht bei jedem zu prüfendem Wert in der
	 * Methode {@link #rowValid(int)} die entsprechende Spalte auf der Suche, ob der Wert schon in
	 * der Spalte enthalten ist, durchlaufen, sondern muss nur ein Abfrage auf dieses Array
	 * durchführen. Damit dies funktioniert, muss dieses Array in den Methoden {@link #set(int)} und
	 * {@link #reset()} aktualisiert werden.
	 */
	final static boolean[][] usedValuesPerCol = new boolean[size][size + 1];

	/**
	 * In diesem boolean-Array wird je Block vermerkt, welche Werte in entsprechendem Block schon
	 * verwendet wurden. Dadurch muss dieses Programm nicht bei jedem zu prüfendem Wert in der
	 * Methode {@link #rowValid(int)} den entsprechenden Block auf der Suche, ob der Wert schon in
	 * der Block enthalten ist, durchlaufen, sondern muss nur ein Abfrage auf dieses Array
	 * durchführen. Damit dies funktioniert, muss dieses Array in den Methoden {@link #set(int)} und
	 * {@link #reset()} aktualisiert werden.
	 */
	final static boolean[][][] usedValuesPerBlock = new boolean[size][size][size + 1];

	/**
	 * In diesem int-Array werden je Zelle die vom Zufallsgenerator {@link #random} in der Methode
	 * {@link #shuffle(int[])} durchgemischten Werte-Vorschläge vermerkt. Ein Zeiger auf den jeweils
	 * nächsten zu verwendenden Wert enthält das int-Array {@link #unusedValuesPerCellPointer}.
	 */
	final static int[][][] unusedValuesPerCell = new int[size][size][size];

	/**
	 * In diesem int-Array wird je Zelle ein Zeiger auf den nächsten zu verwendenden Wert aus dem
	 * int-Array mit den Werte-Vorschlägen {@link #unusedValuesPerCell} vermerkt.
	 */
	final static int[][] unusedValuesPerCellPointer = new int[size][size];

	/**
	 * In der Methode {@link #createUnusedValuesForCurrentCell()} wird beim Erzeugen der
	 * Werte-Vorschläge mit Hilfe der Methode {@link #rowColBlockValid(int)} geprüft, ob der
	 * jeweilige Wert valide ist. Es werden nur die validen Werte im Array
	 * {@link #unusedValuesPerCell} vermerkt. In diesem Array {@link #unusedValuesPerCellSize} wird
	 * die Anzahl der validen Werte-Vorschläge vermerkt.
	 */
	final static int[][] unusedValuesPerCellSize = new int[size][size];

	/**
	 * Random number generator for {@link #shuffle}
	 */
	private static final Random random = new Random();

	/**
	 * In diesem Cache werden wiedervendbare Zufallswerte vermerkt. Da die Erzeugung der
	 * Zufallswerte einen hohen Anteil an der Rechenlast beim Generieren des Sudokus haben, werden
	 * hier Zufallswerte zur Wiederverwendung zwischengespeichert. Dadurch sinkt zwar die Qualität
	 * der Zufallswerte, was aber bei manuell zu lösenden Sudokus immer noch zu einer ausreichenden
	 * Variantenvielfalt führt, aber der Bedarf an Rechenleistung sinkt. Speicherung als Byte-Array
	 * damit mehr gecachte Zufallszahlen in den Cache der CPU passen.
	 */
	private static final int[][] randomCache = createRandomCache();

	/**
	 * Array mit Zeigern auf den je nach Random-Bound aktuell zu verwendenden Zufallswert aus dem
	 * Random-Cache {@link #randomCache}.
	 */
	private static final int[] randomCachePointerArr = new int[size];

	/**
	 * Array mit den vorbelegten Werten des Sudokus. Vorbelegte Werte sind Werte, die bei einem
	 * Backtracking nicht wieder rückgängig gemacht werden dürfen.
	 */
	private static final boolean[][] isPresetCellArr = new boolean[size][size];

	/**
	 * Start-Zeit des Programmes in Millisekunden ab 01.01.1970.
	 */
	private static final long startTimeMillis = System.currentTimeMillis();

	/**
	 * Start-Zeit des Programmes als String im Format 'yyyy_MM_dd_HH_mm_ss' für die Unterscheidung
	 * von erzeugten Sudoku-Dateien anhand eines Zeitstempels.
	 */
	private static String startTimeStr = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss")
			.format(new Date(startTimeMillis));

	/**
	 * Merker-Array für Array-Positionen, an denen in der {@link #shuffle(int[], int)}-Methode
	 * bereits ein Tausch erfolgt ist. Dient zur Beschleunigung.
	 */
	private static final boolean[] shuffleAlreadyChangedIndexArr = new boolean[size];

	/**
	 * Statisches Array für die von der Methode {@link #nextValueForGenerate()} in immer gleicher
	 * Reihenfolge zurückzuliefernden Werte.
	 */
	private static int[] globalValueArr = createGlobalValueArr();

	/**
	 * Statischer Positionsmerker (Zeiger) für die von der Methode {@link #nextValueForGenerate()}
	 * in immer gleicher Reihenfolge zurückzuliefernden Werte.
	 */
	private static int globalNextValueNeuMerker = 0;

	/**
	 * Prüfen, ob der übergebene Wert valide für den aktuellen Block des Sudokus ist.
	 * 
	 * @param value zu prüfender Wert, Basis 1
	 * @return ob der übergebene Wert valide für den aktuellen Block des Sudokus ist
	 */
	static private boolean blockValid(final int value)
	{
		return !usedValuesPerBlock[runBlockStartRow][runBlockStartCol][value];
	}

	/**
	 * Prüfen, ob die Blöcke des Sudokus valide sind, also je Block jeder Wert höchstens einmal
	 * auftaucht.
	 * 
	 * @param allowEmptyCells ob leere Zellen erlaubt sind
	 */
	private static void checkBlockValidation(final boolean allowEmptyCells)
	{
		for (int blockRow = 0; blockRow < size; blockRow += blockRowCount)
		{
			for (int blockCol = 0; blockCol < size; blockCol += blockColCount)
			{
				checkBlockValidation(blockRow, blockCol, allowEmptyCells);
			}
		}
	}

	/**
	 * Prüfen, ob der spezifizierte Block des Sudokus valide ist, also je Block jeder Wert höchstens
	 * einmal auftaucht.
	 * 
	 * @param blockRow Beginnzeile des zu prüfenden Blockes, Basis 0
	 * @param blockCol Beginnspalte des zu prüfenden Blockes, Basis 0
	 * @param allowEmptyCells ob leere Zellen erlaubt sind
	 */
	private static void checkBlockValidation(final int blockRow, final int blockCol,
												final boolean allowEmptyCells)
	{
		final boolean[] usedValueArr = new boolean[size + 1];

		for (int row = blockRow; row < (blockRow + blockRowCount); row++)
		{
			for (int col = blockCol; col < (blockCol + blockColCount); col++)
			{
				if (matrix[row][col] == 0)
				{
					if (allowEmptyCells)
					{
						continue;
					}

					print();
					throw new RuntimeException("Zelle nicht gesetzt[" + row + "][" + col + "]");
				}

				final int value = matrix[row][col];

				if (usedValueArr[value])
				{
					print();
					throw new RuntimeException("Zelle in Block doppelt gesetzt[" + row + "][" + col
							+ "]" + value);
				}
				usedValueArr[value] = true;
			}
		}
	}

	/**
	 * Prüfen, ob die Zeilen des Sudokus valide sind, also je Zeile jeder Wert hüchstens einmal
	 * auftaucht.
	 * 
	 * @param allowEmptyCells ob leere Zellen erlaubt sind
	 */
	private static void checkColValidation(final boolean allowEmptyCells)
	{
		for (int col = 0; col < size; col++)
		{
			checkColValidation(col, allowEmptyCells);
		}
	}

	/**
	 * Prüfen, ob die spezifizierte Zeile des Sudokus valide ist, also je Zeile jeder Wert höchstens
	 * einmal auftaucht.
	 * 
	 * @param col Index der zu prüfenden Zeile, Basis 0
	 * @param allowEmptyCells ob leere Zellen erlaubt sind
	 */
	private static void checkColValidation(final int col, final boolean allowEmptyCells)
	{
		final boolean[] usedValueArr = new boolean[size + 1];

		for (int row = 0; row < size; row++)
		{
			if (matrix[row][col] == 0)
			{
				if (allowEmptyCells)
				{
					continue;
				}

				print();
				throw new RuntimeException("Zelle nicht gesetzt[" + row + "][" + col + "]");
			}

			final int value = matrix[row][col];

			if (usedValueArr[value])
			{
				print();
				throw new RuntimeException("Zelle in Column doppelt gesetzt[" + row + "][" + col
						+ "]" + value);
			}

			usedValueArr[value] = true;
		}
	}

	/**
	 * Prüfen aller Zeilen des Sudokus auf Gültigkeit. Nur für Entwicklung.
	 * 
	 * @param allowEmptyCells ob leere Zellen erlaubt sind
	 */
	private static void checkRowValidation(final boolean allowEmptyCells)
	{
		for (int row = 0; row < size; row++)
		{
			checkRowValidation(row, allowEmptyCells);
		}
	}

	/**
	 * Prüfen einer Zeile des Sudokus auf Gültigkeit. Nur für Entwicklung.
	 * 
	 * @param row Index der zu prüfenden Zeile, Basis 0
	 * @param allowEmptyCells ob leere Zellen erlaubt sind
	 */
	private static void checkRowValidation(final int row, final boolean allowEmptyCells)
	{
		final boolean[] usedValueArr = new boolean[size + 1];

		for (int col = 0; col < size; col++)
		{
			if (matrix[row][col] == 0)
			{
				if (allowEmptyCells)
				{
					continue;
				}

				print();
				throw new RuntimeException("Zelle nicht gesetzt[" + row + "][" + col + "]");
			}

			final int value = matrix[row][col];

			if (usedValueArr[value])
			{
				print();
				throw new RuntimeException("Zelle in Row doppelt gesetzt[" + row + "][" + col + "]"
						+ value);
			}

			usedValueArr[value] = true;
		}
	}

	/**
	 * Prüfen, ob wirklich ein gültiges Sudoku erzeugt wurde. Nur für Entwicklung.
	 * 
	 * @param allowEmptyCells ob leere Zellen erlaubt sind
	 */
	private static void checkValidation(final boolean allowEmptyCells)
	{
		checkRowValidation(allowEmptyCells);
		checkColValidation(allowEmptyCells);
		checkBlockValidation(allowEmptyCells);
	}

	/**
	 * Prüfen des übergebenen Arrays daraufhin, dass jeder Wert (Basis 1) genau einmal enthalten
	 * ist.
	 * 
	 * @param globalValueArrToCheck zu prüfendes Array
	 */
	private static void checkValueArrForUniqueness(final int[] globalValueArrToCheck)
	{
		final boolean[] isContainedValueArr = new boolean[globalValueArrToCheck.length];

		for (final int value : globalValueArrToCheck)
		{
			if (isContainedValueArr[value - 1])
			{
				throw new RuntimeException("doppelt enthalten " + value + " in "
						+ Arrays.toString(globalValueArrToCheck));
			}

			isContainedValueArr[value - 1] = true;
		}

		for (int value = 1; value <= size; value++)
		{
			if (!isContainedValueArr[value - 1])
			{
				throw new RuntimeException("nicht enthalten " + value + " in "
						+ Arrays.toString(globalValueArrToCheck));
			}
		}
	}

	/**
	 * Zellen löschen. Dadurch wird aus dem gelösten Sudoku ein Sudoku-Rätsel.
	 */
	private static void clearCells()
	{
		if (cellsToClearRatio > 1)
		{
			throw new RuntimeException("cellsToClearRatio maximum is 1, " + cellsToClearRatio);
		}

		final int countOfCellsToClear = (int) Math.round(size * size * cellsToClearRatio);

		System.out.println("countOfCellsToClear: " + countOfCellsToClear);

		// Array mit den Anzahl verbliebener Zellen je Wert
		// zum Absichern, dass jeder Wert mindestens einmal im
		// ausgedruckten Rätsel enthalten ist
		final int[] countOfNoClearedCellsPerValueArr = new int[size + 1];

		Arrays.fill(countOfNoClearedCellsPerValueArr, size);

		int countOfNoClearedCells = 0;

		for (int countOfClearedCells = 0; countOfClearedCells < countOfCellsToClear;)
		{
			final int row =
			// random.nextInt(
					getFromRandomCache(size);

			final int col =
			// random.nextInt(
					getFromRandomCache(size);

			if (matrix[row][col] == 0)
			{
				// diese Zelle wurde bereits zurückgesetzt
				// countOfNoClearedCells++;
				// System.out.println(
				// "diese Zelle wurde bereits zurückgesetzt, countOfNoClearedCells " +
				// countOfNoClearedCells );

				if (countOfNoClearedCells > (size * size * size * size))
				{
					// endloses Laufen dieser Methode verhindern
					System.out.println("Methode clearCells abgebrochen " + countOfNoClearedCells);
					break;
				}

				continue;
			}

			final int value = matrix[row][col];

			if (countOfNoClearedCellsPerValueArr[value] < 2)
			{
				countOfNoClearedCells++;
				// System.out.println(
				// "Absichern, dass jeder Wert mindestens einmal im Rätsel enthalten ist, countOfNoClearedCells++ "
				// + countOfNoClearedCells );

				continue;
			}

			matrix[row][col] = 0;

			if (((size > 9) && (size <= 100)
			// wenn es sich um mehrstellige Zahlen im Sudoku handelt,
			// je Spalte in allen Blöcken dieser Spalte mindestens eine
			// mehrstellige Zahl behalten,
			// um das Flattern der HTML-Tabellen-Spalten zu vermeiden
					&& (value > 9) && (!minOneTwoDigitValueInColumnPerBlock(row, col)))
					|| ((size > 100)
					// wenn es sich um mehrstellige Zahlen im Sudoku handelt,
					// je Spalte in allen Blöcken dieser Spalte mindestens eine
					// mehrstellige Zahl behalten,
					// um das Flattern der HTML-Tabellen-Spalten zu vermeiden
							&& (value > 99) && (!minOneThreeDigitValueInColumnPerBlock(row, col))))
			{
				matrix[row][col] = value;

				countOfNoClearedCells++;
				// System.out.println( "Vermeiden Flattern, countOfNoClearedCells++ " +
				// countOfNoClearedCells );

				continue;
			}
			else
			{
				// System.out.println( "countOfNoClearedCells = 0" );
				countOfNoClearedCells = 0;

				countOfClearedCells++;
				// System.out.println( "countOfClearedCells " + countOfClearedCells );

				countOfNoClearedCellsPerValueArr[value]--;
			}
		}

		System.out.println("Anzahl gelöschter Zellen: " + countOfEmptyCells() + " von insgesamt "
				+ (size * size) + " Zellen");
	}

	/**
	 * Prüfen, ob der übergebene Wert valide für die aktuelle Spalte des Sudokus ist.
	 * 
	 * @param value zu prüfender Wert, Basis 1
	 * @return ob der übergebene Wert valide für die aktuelle Spalte des Sudokus ist
	 */
	static private boolean colValid(final int value)
	{
		return !usedValuesPerCol[runCol][value];
	}

	/**
	 * Diese Methode ermittelt die Anzahl leerer Zellen.
	 * 
	 * @return die Anzahl leerer Zellen
	 */
	public static int countOfEmptyCells()
	{
		int countOfEmptyCells = 0;
		for (int row = 0; row < size; row++)
		{
			for (int col = 0; col < size; col++)
			{
				if (matrix[row][col] == 0)
				{
					countOfEmptyCells++;
				}
			}
		}
		return countOfEmptyCells;
	}

	/**
	 * @return int[]
	 */
	private static int[] createColToBlockStartColArr()
	{
		final int[] tmpColToBlockColArr = new int[size];

		for (int col = 0; col < size; col++)
		{
			tmpColToBlockColArr[col] = col - (col % blockColCount);
		}

		return tmpColToBlockColArr;
	}

	/**
	 * @return int[]
	 */
	private static int[] createGlobalValueArr()
	{
		final int[] newGlobalValueArr = new int[size];

		for (int i = 0; i < newGlobalValueArr.length; i++)
		{
			newGlobalValueArr[i] = i + 1;
		}

		// hier wird das gründlichere originale Shuffle verwendet, weil einmaliger Aufruf
		shuffle(newGlobalValueArr);

		return newGlobalValueArr;
	}

	/**
	 * @return int[][]
	 */
	private static int[][] createRandomCache()
	{
		final int[][] tempRandomCache = new int[size][size * size * 4];

		for (int i = 0; i < tempRandomCache.length; i++)
		{
			final int[] tempRandomCachePerSize = tempRandomCache[i];

			for (int j = 0; j < tempRandomCachePerSize.length; j++)
			{
				tempRandomCachePerSize[j] = random.nextInt(i + 1);
			}
		}

		return tempRandomCache;
	}

	/**
	 * @return int[]
	 */
	private static int[] createRowToBlockStartRowArr()
	{
		final int[] tmpRowToBlockRowArr =
		// das Array ein Element grösser machen,
		// weil beim terminieren der solve-Methode
		// runRow == size (nur runRow < size ist möglich)
		// und ohne dieses zusütliche Element kommt es
		// zur ArrayIndexOutOfBoundsException
				new int[size + 1];

		for (int row = 0; row < size; row++)
		{
			tmpRowToBlockRowArr[row] = row - (row % blockRowCount);
		}

		return tmpRowToBlockRowArr;
	}

	/**
	 * Diese Methode erzeugt ein Array in der spezifizierten Grösse, befällt dies mit Werten
	 * beginnend mit 0, shuffled diese und gibt das Array zurück.
	 * 
	 * @param shuffledIntArrSize die Grösse des zu erzeugenden Arrays
	 * @return erzeugtes Array
	 */
	private static int[] createShuffledIntArrBase0(final int shuffledIntArrSize)
	{
		final int[] newShuffledIntArr = new int[shuffledIntArrSize];

		for (int i = 0; i < newShuffledIntArr.length; i++)
		{
			newShuffledIntArr[i] = i;
		}

		// hier wird das gründlichere originale Shuffle verwendet, weil einmaliger Aufruf
		shuffle(newShuffledIntArr);

		return newShuffledIntArr;
	}

	/**
	 * In dieser Methode werden für die aktuelle Zelle des Sudokus, welche durch die globalen Merker
	 * {@link #runRow} und {@link #runCol} adressiert wird, die noch möglichen Werte in dem globalen
	 * Array {@link #unusedValuesPerCell} für die aktuelle Zelle vermerkt und die globalen Merker
	 * {@link #unusedValuesPerCellSize} und {@link #unusedValuesPerCellPointer} gesetzt.
	 */
	private static void createUnusedValuesForCurrentCell()
	{
		// gleich hier auf valide Werte prüfen, anstatt in der Methode
		/*
		 * Erzeugen zufüllig durcheinander gewürfelter Werte für diese Zelle
		 */
		final int[] unusedValues = unusedValuesPerCell[runRow][runCol];

		// System.out.println( "vorgeschlagen von Werten Werte für Zeile: " + runRow + " Spalte " +
		// runCol );

		int pos = 0;
		for (int value = 1; value <= size; value++)
		{
			if (!rowColBlockValid(value))
			{
				// System.out.println( "abgelehnt: " + value );
			}
			else
			{
				// System.out.println( "angenommen: " + value );
				unusedValues[pos++] = value;
			}
		}

		unusedValuesPerCellSize[runRow][runCol] = pos;

		// System.out.print( "vorgeschlagene Werte für Zeile: " + runRow + " Spalte " + runCol + ":"
		// );
		// for ( int i = 0 ; i < pos ; i++ )
		// {
		// System.out.print( " " + unusedValues[ i ] );
		// }
		// System.out.println();

		shuffle(unusedValues, pos);

		unusedValuesPerCellPointer[runRow][runCol] = 0;
	}

	/**
	 * Blockweises Vorbelegen. Es wird über alle Blöcke gelaufen und in allen Blöcken jeweils die
	 * gleiche relative Zelle belegt. Ich bin zu dem Schluss gekommen, dass beim Durchlaufen der
	 * Blöcke mit jeweils eines Relativ-Position im Block jedesmal der gleiche
	 * Symbol-Relations-Vektor entsteht. Wenn man davon ausgeht, dass die Symbole beliebig getauscht
	 * werden können, dann unterscheiden sich die Sudokus nur durch die Relationen der Symbole. Die
	 * Relation der symbole könnte man folgendermassen feststellen: Man nimmt den linken oberen
	 * Block und geht diesen Zelle für Zelle durch. Für jedes Symbol in der jeweiligen Zelle wird
	 * festgestellt, wie sich die Positionen der gleichen Symbole in den anderen Blöcken
	 * unterscheiden. Bei einem 3 x 3 (9er) Sudoku könnte das so aussehen, hier am Beispiel des
	 * symbols '1': +===+===+===+===+===+===+===+===+===+ # 1 | | # | | # | | #
	 * +---+---+---+---+---+---+---+---+---+ # | | # 1 | | # | | #
	 * +---+---+---+---+---+---+---+---+---+ # | | # | | # 1 | | #
	 * +===+===+===+===+===+===+===+===+===+ # | 1 | # | | # | | #
	 * +---+---+---+---+---+---+---+---+---+ # | | # | 1 | # | | #
	 * +---+---+---+---+---+---+---+---+---+ # | | # | | # | 1 | #
	 * +===+===+===+===+===+===+===+===+===+ # | | 1 # | | # | | #
	 * +---+---+---+---+---+---+---+---+---+ # | | # | | 1 # | | #
	 * +---+---+---+---+---+---+---+---+---+ # | | # | | # | | 1 #
	 * +===+===+===+===+===+===+===+===+===+ Position in Block, Zeile 0 , Spalte 0 vertikal Block +0
	 * , horizontal Block +1 , Zeile +1 , Spalte 0 vertikal Block +0 , horizontal Block +2 , Zeile
	 * +2 , Spalte 0 vertikal Block +1 , horizontal Block +0 , Zeile +0 , Spalte +1 vertikal Block
	 * +1 , horizontal Block +1 , Zeile +1 , Spalte +1 vertikal Block +1 , horizontal Block +2 ,
	 * Zeile +2 , Spalte +1 vertikal Block +2 , horizontal Block +0 , Zeile +0 , Spalte +2 vertikal
	 * Block +2 , horizontal Block +1 , Zeile +1 , Spalte +2 vertikal Block +2 , horizontal Block +2
	 * , Zeile +2 , Spalte +2 Dies nun für jede Zeilen- und Spalten-Position im ersten Block
	 * wiederholen. Der so erzeugt Relations-Vektor beschreibt das Sudoku unabhängig vom konkret
	 * ausgegeben Symbol. Wenn nach jeder Generierung der gleiche Relations-Vektor entsteht, so
	 * werden nicht ausreichend unterschiedliche Sudokus erzeugt. Man könnte sich nun Vertauschungen
	 * oder andere Transformations-Operationen im Relations-Vektor vorstelle. Diehe dazu:
	 * Wikipedia-Sudoku und http://www.sudokubay.de/HandbuchSDKJB301.pdf (Seite 21, Vertauschungen)
	 */
	private static void generate()
	{
		final int[] shuffledRowOffsetArr = createShuffledIntArrBase0(blockRowCount);
		final int[] shuffledColOffsetArr = createShuffledIntArrBase0(blockColCount);

		/*
		 * Schleife über die Zellen je Block
		 */
		for (int rowOffset = 0; rowOffset < blockRowCount; rowOffset += 1)
		{
			final int shuffledRowOffset = shuffledRowOffsetArr[rowOffset];

			for (int colOffset = 0; colOffset < blockColCount; colOffset += 1)
			{
				final int shuffledColOffset = shuffledColOffsetArr[colOffset];
				/*
				 * Schleife über Blöcke
				 */
				for (int blockRow = 0; blockRow < size; blockRow += blockRowCount)
				{
					for (int blockCol = 0; blockCol < size; blockCol += blockColCount)
					{
						// Setzen über die set-Methode, um korrektes Nachziehen von
						// usedValuesPerRow, -Col, -Block abzusichern
						runRow = blockRow + shuffledRowOffset; // rowOffset;
						runCol = blockCol + shuffledColOffset; // colOffset;
						runBlockStartRow = rowToBlockStartRowArr[runRow];
						runBlockStartCol = colToBlockStartColArr[runCol];

						// System.out.println( "runRow " + runRow + " runCol " + runCol );

						int nextValue;
						while (!rowColBlockValid(nextValue = nextValueForGenerate()))
						{
							// System.out.println( "verworfen row " + runRow + " col " + runCol +
							// " value " + nextValue );
							// System.out.println( "globalValueArr " + Arrays.toString(
							// globalValueArr ) );
							// checkValueArrForUniqueness( globalValueArr );
							// print();
						}
						// System.out.println( "passt " + nextValue );

						set(nextValue);
						isPresetCellArr[runRow][runCol] = true;
					}
				}
			}
		}
	}

	/**
	 * @param bound int
	 * @return int
	 */
	private static int getFromRandomCache(final int bound)
	{
		// System.out.println( "bound " + bound );
		final int boundMinus1 = bound - 1;
		randomCachePointerArr[boundMinus1]++;

		if (randomCachePointerArr[boundMinus1] >= randomCache[boundMinus1].length)
		{
			randomCachePointerArr[boundMinus1] = 0;
		}

		return randomCache[boundMinus1][randomCachePointerArr[boundMinus1]];
	}

	/**
	 * Main-Methode.
	 * 
	 * @param args Kommandozeilenparameter, werden nicht ausgewertet
	 */
	public static void main(final String[] args)
	{
		System.out.println("Start " + blockRowCount + " x " + blockColCount);

		generate();

		print();

		checkValidation(true);

		solve();

		checkValidation(false);

		print();

		System.out.println("Dauer ms: " + (System.currentTimeMillis() - startTimeMillis));

		/*
		 * Das relativ gleichartige Sudoku durch Transformationen verändern
		 */
		SudokuTransformations.blockRowCount = blockRowCount;
		SudokuTransformations.blockColCount = blockColCount;
		SudokuTransformations.matrix = matrix;

		// Zufälliges Vertauschen zweier gesamter Block-Zeilen
		SudokuTransformations.exchangeEntireBlockRows(random.nextInt(blockColCount),
				random.nextInt(blockColCount));
		checkValidation(true);

		// zufälliges Vertauschen zweier gesamter Block-Spalten
		SudokuTransformations.exchangeEntireBlockCols(random.nextInt(blockRowCount),
				random.nextInt(blockRowCount));
		checkValidation(true);

		// Sudoku horizontal spiegeln
		if (random.nextBoolean())
		{
			SudokuTransformations.mirroringHorizontal();
			checkValidation(true);
		}

		// Sudoku vertikal spiegeln
		if (random.nextBoolean())
		{
			SudokuTransformations.mirroringVertikal();
			checkValidation(true);
		}

		// Sudoku diagonal spiegeln von rechts oben nach links unten
		if ((blockRowCount == blockColCount) && random.nextBoolean())
		{
			SudokuTransformations.mirroringDiagonalFromTopRightToBottomLeft();
			checkValidation(true);
		}

		// Sudoku diagonal spiegeln von links oben nach rechts unten
		if ((blockRowCount == blockColCount) && random.nextBoolean())
		{
			SudokuTransformations.mirroringDiagonalFromTopLeftToBottomRight();
			checkValidation(true);
		}

		// Sudoku rotieren, funktioniert nur bei quadratischen Blöcken und gerader Size
		if ((blockRowCount == blockColCount) && ((size % 2) == 0) && random.nextBoolean())
		{
			SudokuTransformations.rotate();
			checkValidation(true);
		}

		// Quadranten kreuzweise tauschen, funktioniert nur bei quadratischen Blöcken und gerader
		// Size
		if ((blockRowCount == blockColCount) && ((size % 2) == 0) && random.nextBoolean())
		{
			SudokuTransformations.exchangeQuadrantsCrosswise();
			checkValidation(true);
		}

		// Vertauschen ganzer Zeilen, sofern sie innerhalb eines Blockes bleiben
		for (int i = 0; i < (blockColCount / 2); i++)
		// jede zweite Block-Zeile tauschen
		{
			SudokuTransformations.exchangeRowsInOneBlockRow(random.nextInt(blockColCount), // Block-Zeile
					random.nextInt(blockRowCount), // Zeile im Block
					random.nextInt(blockRowCount) // Zeile im Block
					);
			checkValidation(true);
		}

		// Vertauschen ganzer Spalten, sofern sie innerhalb eines Blockes bleiben
		for (int i = 0; i < (blockRowCount / 2); i++)
		// jede zweite Block-Spalte tauschen
		{
			SudokuTransformations.exchangeColsInOneBlockCol(random.nextInt(blockRowCount), // Block-Spalte
					random.nextInt(blockColCount), // Spalte im Block
					random.nextInt(blockColCount) // Spalte im Block
					);
			checkValidation(true);
		}

		print();
		checkValidation(true);

		System.out.println("ok");

		// komplett gelöstes Sudoku sichern
		makeClone();

		clearCells();

		/*
		 * Ausgabe Rätsel (Zellen wurden gelöscht) für Druck
		 */
		SudokuPrinter.blockRowCount = blockRowCount;
		SudokuPrinter.blockColCount = blockColCount;
		SudokuPrinter.size = size;
		SudokuPrinter.matrix = matrix;
		SudokuPrinter.startTimeStr = startTimeStr;

		// SudokuPrinter.writeHtmlToFile(OUTPUT_FOLDER + File.separator + "out_" + blockRowCount +
		// "_"
		// + blockColCount + "_" + startTimeStr + ".html");

		/*
		 * Ausgabe Lösung (Zellen wurden nicht gelöscht) für Druck
		 */
		// wieder komplett gelöstes Sudoku
		matrix = matrixClone;
		SudokuPrinter.matrix = matrix;

		// SudokuPrinter.writeHtmlToFile(OUTPUT_FOLDER + File.separator + "out_" + blockRowCount +
		// "_"
		// + blockColCount + "_" + startTimeStr + "_solved.html");
	}

	/**
	 * komplett gelöstes Sudoku sichern
	 */
	private static void makeClone()
	{
		matrixClone = new int[size][size];
		for (int row = 0; row < size; row++)
		{
			for (int col = 0; col < size; col++)
			{
				matrixClone[row][col] = matrix[row][col];
			}
		}
	}

	/**
	 * Prüfen, ob in der Spalte innerhalb des Blockes, in welcher die spezifizierte Zelle liegt,
	 * mindestens ein Wert enthalten ist, der dreistellig ( > 99 ) ist.
	 * 
	 * @param row Zeile der zu prüfenden Zelle, Basis 0
	 * @param col Spalte der zu prüfenden Zelle, Basis 0
	 * @return boolean
	 */
	private static boolean minOneThreeDigitValueInColumnPerBlock(final int row, final int col)
	{
		final int blockStartRow = rowToBlockStartRowArr[row];

		for (int tmpRow = blockStartRow; tmpRow < (blockStartRow + blockRowCount); tmpRow++)
		{
			if (matrix[tmpRow][col] > 99)
			{
				return true;
			}
		}

		return false;
	}

	/**
	 * Prüfen, ob in der Spalte innerhalb des Blockes, in welcher die spezifizierte Zelle liegt,
	 * mindestens ein Wert enthalten ist, der zweistellig ( > 9 ) ist.
	 * 
	 * @param row Zeile der zu prüfenden Zelle, Basis 0
	 * @param col Spalte der zu prüfenden Zelle, Basis 0
	 * @return boolean
	 */
	private static boolean minOneTwoDigitValueInColumnPerBlock(final int row, final int col)
	{
		final int blockStartRow = rowToBlockStartRowArr[row];

		for (int tmpRow = blockStartRow; tmpRow < (blockStartRow + blockRowCount); tmpRow++)
		{
			if (matrix[tmpRow][col] > 9)
			{
				return true;
			}
		}

		return false;
	}

	/**
	 * Zurückgeben des nächsten passenden Wertes für die aktuelle Zelle.
	 * 
	 * @return der nächste passende Wert für die aktuelle Zelle
	 */
	private static int nextValue()
	{
		final int[] runUnusedValuesPerCell = unusedValuesPerCell[runRow][runCol];

		final int unusedValuesSize = unusedValuesPerCellSize[runRow][runCol];
		for (; unusedValuesPerCellPointer[runRow][runCol] < unusedValuesSize;

		unusedValuesPerCellPointer[runRow][runCol]++)
		{
			int nextValue = runUnusedValuesPerCell[unusedValuesPerCellPointer[runRow][runCol]];
			// System.out.println( "übrige Werte an Zeile " + runRow +
			// " und Spalte " + runCol + " und Pointer " +
			// unusedValuesPerCellPointer[ runRow ][ runCol ] + ": " +
			// Arrays.toString( ArrayUtil.subArr( unusedValuesPerCell[ runRow ][
			// runCol ] , unusedValuesPerCellPointer[ runRow ][ runCol ] ,
			// unusedValuesPerCell[ runRow ][ runCol ].length ) ) );
			if (rowColBlockValid(nextValue))
			{
				// System.out.println( "benutzt: " + nextValue );
				return nextValue;
			}
			// System.out.println( "verworfen: " + nextValue );
		}

		// kein Wert mehr übrig für diese Position
		return -1;
	}

	/**
	 * Diese Methode liefert die vorher ge-shuffelten Werte in einer definiert gleichen Reihenfolge
	 * für die Methode {@link #generate()}.
	 * 
	 * @return nächster zu verwendender Wert
	 */
	private static int nextValueForGenerate()
	{
		globalNextValueNeuMerker += 1;

		if (globalNextValueNeuMerker >= size)
		{
			globalNextValueNeuMerker = 0;
		}

		return globalValueArr[globalNextValueNeuMerker];
	}

	/**
	 * Gibt die Matrix in lesbarer Form auf die Konsole aus.
	 */
	public static void print()
	{
		for (int row = 0; row < size; row++)
		{
			for (int col = 0; col < size; col++)
			{
				System.out.print(matrix[row][col] + " ");
			}

			System.out.println();
		}

		System.out.println();
	}

	/**
	 * Zurücksetzen des aktuellen Feldes in der Matrix
	 */
	static private void reset()
	{
		if (USE_DEVELOP_CHECKS && isPresetCellArr[runRow][runCol])
		{
			// Zelle ist vorbelegt, setzen ist nicht erlaubt
			print();
			throw new RuntimeException("Zelle vorbelegt: " + runRow + " " + runCol);
		}

		final int oldValue = matrix[runRow][runCol];

		usedValuesPerRow[runRow][oldValue] = false;
		usedValuesPerCol[runCol][oldValue] = false;
		usedValuesPerBlock[runBlockStartRow][runBlockStartCol][oldValue] = false;

		matrix[runRow][runCol] = 0;
	}

	/**
	 * Ob der übergebene Wert für die aktuelle Zelle passt, also weder in der Zeile, in der Spalte
	 * oder im aktuellen Block vorhanden ist.
	 * 
	 * @param value zu prüfender Wert
	 * @return ob valid
	 */
	private static boolean rowColBlockValid(final int value)
	{
		return rowValid(value) && colValid(value) && blockValid(value);
	}

	/**
	 * Prüfen, ob der übergebene Wert valide für die aktuelle Zeile des Sudokus ist.
	 * 
	 * @param value zu prüfender Wert, Basis 1
	 * @return ob der übergebene Wert valide für die aktuelle Zeile des Sudokus ist
	 */
	static private boolean rowValid(final int value)
	{
		return !usedValuesPerRow[runRow][value];
	}

	/**
	 * @param value int
	 */
	static private void set(final int value)
	{
		if (USE_DEVELOP_CHECKS && isPresetCellArr[runRow][runCol])
		{
			// Zelle ist vorbelegt, setzen ist nicht erlaubt
			print();
			throw new RuntimeException("Zelle ist bereits vorbelegt: " + runRow + " " + runCol);
		}

		if (USE_DEVELOP_CHECKS && (value == 0))
		{
			// Zelle auf 0 setzen ist nicht erlaubt
			print();
			throw new RuntimeException("use reset for set null value: " + runRow + " " + runCol);
		}

		final int oldValue = matrix[runRow][runCol];

		usedValuesPerRow[runRow][oldValue] = false;
		usedValuesPerCol[runCol][oldValue] = false;
		usedValuesPerBlock[runBlockStartRow][runBlockStartCol][oldValue] = false;

		matrix[runRow][runCol] = value;

		usedValuesPerRow[runRow][value] = true;
		usedValuesPerCol[runCol][value] = true;
		usedValuesPerBlock[runBlockStartRow][runBlockStartCol][value] = true;
	}

	/**
	 * Durcheinander Mischen des übergebenen int-Arrays.
	 * 
	 * @param intArr int[]
	 */
	private static void shuffle(final int[] intArr)
	{
		// --- Shuffle by exchanging each element randomly
		for (int i = 0; i < intArr.length; i++)
		{
			int randomPosition = random.nextInt(intArr.length);

			// Tauschen
			int temp = intArr[i];
			intArr[i] = intArr[randomPosition];
			intArr[randomPosition] = temp;
		}
	}

	/**
	 * Durcheinander Mischen des übergebenen Arrays.
	 * 
	 * @param intArr int[]
	 * @param shuffleSize die Anzahl Stellen des Arrays, welche zu mischen sind. Durch die Angabe
	 *            dieses Parameters anstelle der Benutzung der Grösse des Arrays, wird es unnnötig,
	 *            bei nur teilweiser Verwendung des Arrays bei weniger möglichen Werten für die
	 *            aktuelle Zelle, das Array neu zuzuweisen und so den Garbage Collector in Ansprich
	 *            zu nehmen. Verbesserung Performance.
	 */
	private static void shuffle(final int[] intArr, final int shuffleSize)
	{
		if (shuffleSize < 2)
		{
			return;
		}

		for (int i = 0; i < shuffleSize; i++)
		{
			shuffleAlreadyChangedIndexArr[i] = false;
		}

		// --- Shuffle by exchanging each element randomly
		for (int i = 0; i < shuffleSize; i++)
		{
			if (shuffleAlreadyChangedIndexArr[i])
			{
				// an dieser Position erfolgte bereits ein Tausch
				continue;
			}

			final int randomPosition =
			// random.nextInt(
			// shuffleSize );
					getFromRandomCache(shuffleSize);

			// Tauschen
			int temp = intArr[i];
			intArr[i] = intArr[randomPosition];
			intArr[randomPosition] = temp;

			shuffleAlreadyChangedIndexArr[randomPosition] = true;
		}
	}

	/**
	 * Eine Zelle vorwärts positionieren.
	 */
	private static void skipNextCell()
	{
		while (true)
		{
			if (runCol < (size - 1))
			{
				runCol++;

				runBlockStartCol = colToBlockStartColArr[runCol];

				// System.out.print( " " + runCol );
			}
			else
			{
				runRow++;

				// System.out.println( "row " + runRow );

				runCol = 0;

				runBlockStartRow = rowToBlockStartRowArr[runRow];

				runBlockStartCol = 0;

				// System.out.println( runRow );
			}

			if (runRow >= size)
			{
				// letzte Zelle erreicht, fertig
				return;
			}
			if (!isPresetCellArr[runRow][runCol])
			{
				// Zelle ist nicht vorbelegt
				return;
			}
		}
	}

	/**
	 * Eine Zelle zurück positionieren. Dies tritt beim Backtracking, also wenn ein Pfad des
	 * Lösungsbaumes erfolglos durchlaufen wurde, also kein valide Lösung gefunden wurde, auf.
	 */
	private static void skipPrevCell()
	{
		while (true)
		{
			if (runCol > 0)
			{
				runCol--;

				runBlockStartCol = colToBlockStartColArr[runCol];
			}
			else
			{
				runRow--;

				// System.out.println( "row " + runRow );

				if (runRow < 0)
				{
					print();
					throw new RuntimeException("Sudoku nicht lösbar");
				}

				runCol = size - 1;

				runBlockStartRow = rowToBlockStartRowArr[runRow];

				// am Beginn des Programms je ein row to blockRow sowie col to
				// blockCol erzeugen und dann immer bloss die blockRow- und
				// blockCol-Werte aus diesem Array nehmen
				runBlockStartCol = colToBlockStartColArr[runCol];
			}

			if (!isPresetCellArr[runRow][runCol])
			{
				// Zelle ist nicht vorbelegt
				return;
			}
		}
	}

	/**
	 * Nicht-Rekursive Lösungs-Methode für das Sudoku.
	 */
	private static void solve()
	{
		runRow = 0;
		runCol = 0;
		runBlockStartRow = 0;
		runBlockStartCol = 0;

		while (isPresetCellArr[runRow][runCol])
		{
			// Zelle ist vorbelegt, setzen ist nicht erlaubt
			skipNextCell();

			if (runRow >= size)
			{
				// Lösung gefunden
				return;
			}
		}

		if (USE_DEVELOP_CHECKS)
		{
			checkValidation(true);
		}

		createUnusedValuesForCurrentCell();

		while (true)
		{
			int nextValue;
			if ((nextValue = nextValue()) == -1)
			{
				// kein Wert mehr übrig für diese Position

				// System.out.println( "back track" );

				reset();

				skipPrevCell();
			}
			else
			{
				set(nextValue);

				skipNextCell();

				if (runRow >= size)
				{
					// Lösung gefunden
					return;
				}

				createUnusedValuesForCurrentCell();
			}

			if (USE_DEVELOP_CHECKS)
			{
				checkValidation(true);
			}
		}
	}

}
