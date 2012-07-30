package sudoku3;

/**
 * Umwandeln von Sudokus durch verschiedene Vertauschungsoperationen.
 * 
 * @author Heiner K&uuml;cker
 */
public class SudokuTransformations
{
	/**
	 * Anzahl Zeilen je Block
	 */
	public static int blockRowCount;

	/**
	 * Anzahl Spalten je Block
	 */
	public static int blockColCount;

	/**
	 * Die Matrix mit den Zellen des Sudokus.
	 */
	public static int[][] matrix;

	/**
	 * @return int
	 */
	public static int size()
	{
		return blockRowCount * blockColCount;
	}

	/**
	 * Gibt die Matrix in lesbarer Form auf die Konsole aus.
	 */
	public static void print()
	{
		final int size = size();

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
	 * Tauschen zweier Zellen.
	 * 
	 * @param row1 int
	 * @param col1 int
	 * @param row2 int
	 * @param col2 int
	 */
	public static void exchangeCells(final int row1, final int col1, final int row2, final int col2)
	{
		// System.out.println( "exchangeCells " + row1 + " " + col1 + " " + matrix[ row1 ][ col1 ]
		// );
		// System.out.println( "exchangeCells " + row2 + " " + col2 + " " + matrix[ row2 ][ col2 ]
		// );
		// System.out.println();

		final int tmpValue = matrix[row1][col1];
		matrix[row1][col1] = matrix[row2][col2];
		matrix[row2][col2] = tmpValue;
	}

	/**
	 * Tauschen zweier Zeilen.
	 * 
	 * @param row1 int
	 * @param row2 int
	 */
	public static void exchangeRows(final int row1, final int row2)
	{
		final int size = size();

		for (int col = 0; col < size; col++)
		{
			exchangeCells(row1, col, row2, col);
		}
	}

	/**
	 * Tauschen zweier Spalten.
	 * 
	 * @param col1 int
	 * @param col2 int
	 */
	public static void exchangeCols(final int col1, final int col2)
	{
		final int size = size();

		for (int row = 0; row < size; row++)
		{
			exchangeCells(row, col1, row, col2);
		}
	}

	/**
	 * Tauschen zweier Zeilen von ganzen Blöcken.
	 * 
	 * @param blockRow1 Zeilen-Nummer des ersten Blockes (bei 9er-Sudoku 0, 1 oder 2)
	 * @param blockRow2 Zeilen-Nummer des zweiten Blockes (bei 9er-Sudoku 0, 1 oder 2)
	 */
	public static void exchangeEntireBlockRows(final int blockRow1, final int blockRow2)
	{
		int tmpBlockRow1 = blockRow1 * blockRowCount;
		int tmpBlockRow2 = blockRow2 * blockRowCount;

		for (int blockRowOffset = 0; blockRowOffset < blockRowCount; blockRowOffset++)
		{
			exchangeRows(tmpBlockRow1++, tmpBlockRow2++);
		}
	}

	/**
	 * Tauschen zweier Spalten von ganzen Blöcken.
	 * 
	 * @param blockCol1 Spalten-Nummer des ersten Blockes (bei 9er-Sudoku 0, 1 oder 2)
	 * @param blockCol2 Spalten-Nummer des zweiten Blockes (bei 9er-Sudoku 0, 1 oder 2)
	 */
	public static void exchangeEntireBlockCols(final int blockCol1, final int blockCol2)
	{
		int tmpBlockCol1 = blockCol1 * blockColCount;
		int tmpBlockCol2 = blockCol2 * blockColCount;

		for (int blockColOffset = 0; blockColOffset < blockColCount; blockColOffset++)
		{
			exchangeCols(tmpBlockCol1++, tmpBlockCol2++);
		}
	}

	/**
	 * Sudoku horizontal spiegeln.
	 */
	public static void mirroringHorizontal()
	{
		final int size = size();

		// feststellen, ob das Sudoku eine gerade oder ungerade Anzahl Zeilen hat
		// final boolean sizeIsOdd = ( size % 2 ) != 0;

		/*
		 * durch das Abrunden wird erreicht, dass bei ungerader size die genau in Mitte sitzende
		 * Zeile nicht verändert wird.
		 */
		final int halfSize = (int) Math.floor(size / 2);

		for (int rowOffset = 0; rowOffset < halfSize; rowOffset++)
		{
			exchangeRows(rowOffset, (size - 1) - rowOffset);
		}
	}

	/**
	 * Sudoku vertikal spiegeln.
	 */
	public static void mirroringVertikal()
	{
		final int size = size();

		// feststellen, ob das Sudoku eine gerade oder ungerade Anzahl Zeilen hat
		// final boolean sizeIsOdd = ( size % 2 ) != 0;

		/*
		 * durch das Abrunden wird erreicht, dass bei ungerader size die genau in Mitte sitzende
		 * Spalte nicht verändert wird.
		 */
		final int halfSize = (int) Math.floor(size / 2);

		for (int colOffset = 0; colOffset < halfSize; colOffset++)
		{
			exchangeCols(colOffset, (size - 1) - colOffset);
		}
	}

	/**
	 * Sudoku diagonal spiegeln von rechts oben nach links unten. Setzt quadratische Blöcke
	 * {@link #blockRowCount} == {@link #blockColCount} voraus.
	 */
	public static void mirroringDiagonalFromTopRightToBottomLeft()
	{
		if (blockRowCount != blockColCount)
		{
			throw new IllegalStateException(
					"Die Methode mirroringDiagonalFromTopRightToBottomLeft setzt quardratische Blöcke, blockRowCount == blockColCount, voraus");
		}

		final int size = size();

		for (int rowOffset = 0; rowOffset < size - 1; rowOffset++)
		{
			for (int colOffset = 0; colOffset < (size - 1) - rowOffset; colOffset++)
			{
				exchangeCells(rowOffset, colOffset, (size - 1) - colOffset, (size - 1) - rowOffset);
			}
		}
	}

	/**
	 * Sudoku diagonal spiegeln von links oben nach rechts unten. Setzt quadratische Blöcke
	 * {@link #blockRowCount} == {@link #blockColCount} voraus.
	 */
	public static void mirroringDiagonalFromTopLeftToBottomRight()
	{
		if (blockRowCount != blockColCount)
		{
			throw new IllegalStateException(
					"Die Methode mirroringDiagonalFromTopLeftToBottomRight setzt quardratische Blöcke, blockRowCount == blockColCount, voraus");
		}

		final int size = size();

		for (int rowOffset = 0; rowOffset < size - 1; rowOffset++)
		{
			for (int colOffset = 0; colOffset < (size - 1) - rowOffset; colOffset++)
			{
				exchangeCells((size - 1) - rowOffset, colOffset, colOffset, (size - 1) - rowOffset);
			}
		}
	}

	/**
	 * Sudoku um 2 Quadranten ( 90 Grad Winkel ) rotieren. Es gibt kein rotateRight und rotateLeft,
	 * weil beim Rotieren um zwei Quadranten immer das gleiche rauskommt. Funktioniert nur bei
	 * gerader Size. Setzt quadratische Blöcke {@link #blockRowCount} == {@link #blockColCount}
	 * voraus.
	 */
	public static void rotate()
	{
		final int size = size();

		if (blockRowCount != blockColCount)
		{
			throw new IllegalStateException(
					"Die Methode rotate setzt quardratische Blöcke, blockRowCount == blockColCount, voraus");
		}

		// werfen Exception bei ungerader Size
		if (size % 2 != 0)
		{
			throw new IllegalStateException("Aufruf rotate nur bei gerader size möglich");
		}

		final int halfSize = size / 2;

		/*
		 * Schleife über alle Zellen des linken oberen Quadranten
		 */
		for (int rowOffset = 0; rowOffset < halfSize; rowOffset++)
		{
			for (int colOffset = 0; colOffset < halfSize; colOffset++)
			{
				/*
				 * Ringtausch um jeweils zwei Quadranten im Uhrzeigersinn
				 */
				// left top
				final int quadrantLeftTopCellValue = matrix[rowOffset][colOffset];

				// right top
				final int quadrantRightTopCellValue = matrix[rowOffset][colOffset + halfSize];

				// right bottom
				final int quadrantRightBottomCellValue =
						matrix[rowOffset + halfSize][colOffset + halfSize];

				// left bottom
				final int quadrantLeftBottomCellValue = matrix[rowOffset + halfSize][colOffset];

				// left top -> right bottom
				matrix[rowOffset + halfSize][colOffset + halfSize] = quadrantLeftTopCellValue;

				// right top -> left bottom
				matrix[rowOffset + halfSize][colOffset] = quadrantRightTopCellValue;

				// right bottom -> left top
				matrix[rowOffset][colOffset] = quadrantRightBottomCellValue;

				// left bottom -> right top
				matrix[rowOffset][colOffset + halfSize] = quadrantLeftBottomCellValue;
			}
		}
	}

	/**
	 * Quadranten kreuzweise tauschen. Funktioniert nur bei gerader Size. Setzt quadratische Blöcke
	 * {@link #blockRowCount} == {@link #blockColCount} voraus.
	 */
	public static void exchangeQuadrantsCrosswise()
	{
		final int size = size();

		if (blockRowCount != blockColCount)
		{
			throw new IllegalStateException(
					"Die Methode exchangeQuadrantsCrosswise setzt quardratische Blöcke, blockRowCount == blockColCount, voraus");
		}

		// werfen Exception bei ungerader Size
		if (size % 2 != 0)
		{
			throw new IllegalStateException(
					"Aufruf exchangeQuadrantsCrosswise nur bei gerader size möglich");
		}

		final int halfSize = size / 2;

		/*
		 * Schleife über alle Zellen des linken oberen Quadranten
		 */
		for (int rowOffset = 0; rowOffset < halfSize; rowOffset++)
		{
			for (int colOffset = 0; colOffset < halfSize; colOffset++)
			{
				/*
				 * Kreuz-Tausch zum jeweils gegenüber liegenden Quadranten
				 */
				final int quadrantLeftTopCellValue = matrix[rowOffset][colOffset];
				final int quadrantRightTopCellValue = matrix[rowOffset][colOffset + halfSize];
				final int quadrantLeftBottomCellValue = matrix[rowOffset + halfSize][colOffset];
				final int quadrantRightBottomCellValue =
						matrix[rowOffset + halfSize][colOffset + halfSize];

				// right bottom
				matrix[rowOffset + halfSize][colOffset + halfSize] = quadrantLeftTopCellValue;

				// left bottom
				matrix[rowOffset + halfSize][colOffset] = quadrantRightTopCellValue;

				// right top
				matrix[rowOffset][colOffset + halfSize] = quadrantLeftBottomCellValue;

				// left top
				matrix[rowOffset][colOffset] = quadrantRightBottomCellValue;
			}
		}
	}

	/**
	 * Vertauschen ganzer Zeilen, sofern sie innerhalb eines Blockes bleiben.
	 * 
	 * @param blockRow Zeilen-Nummer des zu bearbeitenden Blockes (bei 9er-Sudoku 0, 1 oder 2)
	 * @param rowOffset1 erste zu tauschende Zeile im Block relativ
	 * @param rowOffset2 zweite zu tauschende Zeile im Block relativ
	 */
	public static void exchangeRowsInOneBlockRow(final int blockRow, final int rowOffset1,
													final int rowOffset2)
	{
		// Check auf Einhaltung rowOffset1 und rowOffset2 > 0 und < blockRowCount
		if (rowOffset1 < 0)
		{
			throw new IllegalArgumentException("rowOffset1 < 0: " + rowOffset1);
		}
		if (rowOffset1 >= blockRowCount)
		{
			throw new IllegalArgumentException("rowOffset1 >= blockRowCount: " + rowOffset1);
		}
		if (rowOffset2 < 0)
		{
			throw new IllegalArgumentException("rowOffset2 < 0: " + rowOffset2);
		}
		if (rowOffset2 >= blockRowCount)
		{
			throw new IllegalArgumentException("rowOffset2 >= blockRowCount: " + rowOffset2);
		}

		int tmpBlockRow = blockRow * blockRowCount;

		exchangeRows(tmpBlockRow + rowOffset1, tmpBlockRow + rowOffset2);
	}

	/**
	 * Vertauschen ganzer Spalten, sofern sie innerhalb eines Blockes bleiben.
	 * 
	 * @param blockCol Zeilen-Nummer des zu bearbeitenden Blockes (bei 9er-Sudoku 0, 1 oder 2)
	 * @param colOffset1 erste zu tauschende Zeile im Block relativ
	 * @param colOffset2 zweite zu tauschende Zeile im Block relativ
	 */
	public static void exchangeColsInOneBlockCol(final int blockCol, final int colOffset1,
													final int colOffset2)
	{
		// Check auf Einhaltung colOffset1 und colOffset2 > 0 und < blockColCount
		if (colOffset1 < 0)
		{
			throw new IllegalArgumentException("colOffset1 < 0: " + colOffset1);
		}
		if (colOffset1 >= blockColCount)
		{
			throw new IllegalArgumentException("colOffset1 >= blockColCount: " + colOffset1);
		}
		if (colOffset2 < 0)
		{
			throw new IllegalArgumentException("colOffset2 < 0: " + colOffset2);
		}
		if (colOffset2 >= blockColCount)
		{
			throw new IllegalArgumentException("colOffset2 >= blockColCount: " + colOffset2);
		}

		int tmpBlockCol = blockCol * blockColCount;

		exchangeCols(tmpBlockCol + colOffset1, tmpBlockCol + colOffset2);
	}

}
