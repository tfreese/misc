// Created: 01.11.2009
/**
 * 01.11.2009
 */
package sudoku2;

/**
 */
class Sudoku
{
	/**
	 * Konstante fuer die Anzahl an Blöcken(9er/12er Sudoku) und die Breite einer Box (3 oder 4)
	 */
	public final int n;

	/**
	 *
	 */
	public final int box_n;

	/**
	 * Zaehlt die Anzahl der erprobten Platzierungen
	 */
	public int tries = 0;

	/**
	 * Sudoku Feld (noch nicht vorbelegt, da 9^12 moeglich sind)
	 */
	public byte[][] feld;

	/**
	 * Konstruktor args[0] = dateiname args[1] = smart
	 * 
	 * @param n int
	 * @param what String
	 * @param filename String
	 */
	public Sudoku(final int n, final String what, final String filename)
	{
		super();

		this.n = n;
		this.box_n = (int) Math.sqrt(n);
		this.feld = new byte[n][n];

		// Datei einlesen
		read(filename);

		// und loesen
		if (what.equals("2"))
		{
			solveBacktrack(0, 0);

		}
		else if (what.equals("3"))
		{
			smartSolve();

		}
		else if (what.equals("1"))
		{
			// belassen fuer einfache Ausgabe
		}

	}

	/**
	 * Liest ein Raetsel ein
	 * 
	 * @param filename String
	 */
	public void read(final String filename)
	{
		throw new UnsupportedOperationException();
		// int i = 0, j = 0;
		//
		// try
		// {
		// int[] file = ADSTool.readIntArray(filename);
		//
		// for (int digit : file)
		// {
		// if (j == this.n)
		// {
		// j = 0;
		// i++;
		// }
		// this.feld[i][j] = (byte) digit;
		// j++;
		// }
		// }
		// catch (Exception e)
		// {
		// System.out.println("Bitte mit dem korrekten Dateinamen als Konsolenparameter starten");
		// System.exit(0);
		// }
	}

	/**
	 * Loest das Raetsel durch Backtracking
	 * 
	 * @param i int
	 * @param j int
	 * @return boolean
	 */
	public boolean solveBacktrack(int i, int j)
	{
		if (j == this.n)
		{ /*
		 * Zeilenende erreicht, fange wieder links an, gehe eine Zeile nach unten oder breche ab,
		 * wenn du ganz fertig bist
		 */
			i++;
			if (i == this.n)
			{
				return true;
			}
			j = 0;
		}

		if (this.feld[i][j] > 0)
		{
			// Funktionsaufruf mit naechster Spalte
			return solveBacktrack(i, j + 1);
		}

		for (byte a = 1; a < this.n + 1; a++)
		{
			if (check(i, j, a) == false)
			{
				this.feld[i][j] = a;
				if (solveBacktrack(i, j + 1))
				{
					return true;
				}
			}
		}

		this.feld[i][j] = 0; // Der Versuch war wohl nichts
		return false;
	}

	/**
	 * Prueft ob dieses Feld gesetzt werden kann (ruft dabei 3 Subfunktionen auf)
	 * 
	 * @param i int
	 * @param j int
	 * @param value int
	 * @return boolean; Gibt true zurueck, wenn der Wert bereits existiert
	 */
	public boolean check(final int i, final int j, final int value)
	{
		this.tries++;

		// Fuer die Ausgabe mit SudokuSmart
		// System.out.println(""+i+"/"+j);

		if (checkHorizontal(j, value))
		{
			return true;
		}

		if (checkVertical(i, value))
		{
			return true;
		}

		if (checkBox(i, j, value))
		{
			return true;
		}

		return false;
	}

	/**
	 * Prueft ob der Wert bereits in der (horizontalen) SPALTE existiert
	 * 
	 * @param j int
	 * @param value int
	 * @return boolean; Gibt true zurueck, wenn der Wert bereits existiert
	 */
	public boolean checkHorizontal(final int j, final int value)
	{
		for (int a = 0; a < this.n; a++)
		{
			if (this.feld[a][j] == value)
			{
				return true;
			}
		}

		return false;
	}

	/**
	 * Prueft ob der Wert bereits in der (vertikalen) REIHE existiert
	 * 
	 * @param i int
	 * @param value int
	 * @return boolean; Gibt true zurueck, wenn der Wert bereits existiert
	 */
	public boolean checkVertical(final int i, final int value)
	{
		for (int a = 0; a < this.n; a++)
		{
			if (this.feld[i][a] == value)
			{
				return true;
			}
		}

		return false;
	}

	/**
	 * Prueft ob der Wert bereits in der BOX existiert
	 * 
	 * @param i int
	 * @param j int
	 * @param value int
	 * @return boolean; Gibt true zurueck, wenn der Wert bereits existiert
	 */
	public boolean checkBox(final int i, final int j, final int value)
	{
		// oberes, linkes Eck der Box herausfinden (2|8 zu 0|6)
		int i_start = (i / this.box_n) * this.box_n;
		int j_start = (j / this.box_n) * this.box_n;

		for (int a = i_start; a < i_start + this.box_n; a++)
		{
			for (int b = j_start; b < j_start + this.box_n; b++)
			{
				if (this.feld[a][b] == value)
				{
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * SMART: Loest das Raetsel intelligenter
	 * 
	 * @return boolean
	 */
	public boolean smartSolve()
	{
		int[] best = smartChoice();
		int i = best[0], j = best[1];

		if ((i == -1) && (j == -1))
		{
			return true;
		}

		for (byte a = 1; a < this.n + 1; a++)
		{
			// System.out.print("pruefe wert "+a+" in ");
			if (check(i, j, a) == false)
			{
				this.feld[i][j] = a;
				if (smartSolve())
				{
					return true;
				}
			}
		}

		this.feld[i][j] = 0; // Der Versuch war wohl nichts
		return false;
	}

	/**
	 * Sucht die Position der besten Möglichkeit gibt es eine Anzahl von Moeglichkeiten oefter, dann
	 * dann wird die letzte Zahl als beste Moeglichkeit gewaehlt, um in einem späteren
	 * Schleifendurchläufen Performance zu sparen
	 * 
	 * @return int[]; Gibt die Koordinaten zurueck
	 */
	public int[] smartChoice()
	{
		int minCount = this.n, minI = -1, minJ = -1;
		for (int i = 0; i < this.n; i++)
		{
			for (int j = 0; j < this.n; j++)
			{
				if (this.feld[i][j] == 0)
				{
					int c = smartCount(i, j);
					if (c <= minCount)
					{
						minCount = c;
						minI = i;
						minJ = j;
					}
				}
			}
		}
		int[] results =
		{
				minI, minJ
		};
		return results;
	}

	/**
	 * SMART: Zaehlt die leeren Felder (die Anzahl an moeglichen Loesungen)
	 * 
	 * @param i int
	 * @param j int
	 * @return int
	 */
	public int smartCount(final int i, final int j)
	{
		boolean[] list = new boolean[10];

		for (int a = 0; a < this.n; a++)
		{
			if (this.feld[i][a] > 0)
			{
				list[this.feld[i][a]] = true; // Durch "true" schliessen wir die Felder mit Wert aus
			}
			if (this.feld[a][j] > 0)
			{
				list[this.feld[a][j]] = true;
			}
		}

		// Box
		int i_start = (i / this.box_n) * this.box_n;
		int j_start = (j / this.box_n) * this.box_n;
		for (int a = i_start; a < i_start + this.box_n; a++)
		{
			if (a != i)
			{
				for (int b = j_start; b < j_start + this.box_n; b++)
				{
					if ((this.feld[a][b] > 0) && (b != j))
					{
						// >0 schliesst mehr Felder aus, dadurch an Pos1 der Abfrage
						list[this.feld[a][b]] = true;
					}
				}
			}
		}

		// Jetzt zaehlen wir die Felder, die wir gefunden haben
		byte max = 0, possible = 0;
		for (byte a = 1; a <= this.n; a++)
		{
			if (list[a] == false)
			{
				max++;
			}
			else
			{
				possible = a;
			}
		}

		if (max == 1)
		{
			this.feld[i][j] = possible;
		}

		return max;
	}

	/**
	 * Ueberschreibt die Ausgabefunktion Wir verwenden hier nur einen Zaehler n*n
	 * Stringerweiterungungen sind langsamer als StringBuffer
	 */
	@Override
	public String toString()
	{
		StringBuffer returns = new StringBuffer();

		for (int i = 0; i < this.n * this.n; i++)
		{

			if ((i % this.n == 0) && (i > 0))
			{
				returns.append("\n");
				if (i % (this.box_n * this.n) == 0)
				{
					returns.append("-----------------------\n");
				}
			}

			if ((i % this.box_n == 0) && (i % this.n != 0))
			{
				returns.append(" |");
			}

			returns.append(this.feld[i / this.n][i % this.n] == 0 ? "  " : " "
					+ this.feld[i / this.n][i % this.n]);
		}
		returns.append("\nEs wurden " + this.tries + " Platzierungen ausprobiert in ");
		return returns.toString();
	}
}
