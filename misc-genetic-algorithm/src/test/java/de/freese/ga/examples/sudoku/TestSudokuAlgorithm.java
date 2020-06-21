/**
 * Created: 21.06.2020
 */

package de.freese.ga.examples.sudoku;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import de.freese.ga.chromonome.Chromosome;
import de.freese.ga.chromonome.DefaultChromosome;

/**
 * @author Thomas Freese
 */
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
class TestSudokuAlgorithm
{
    /**
     *
     */
    private static SudokuAlgorithm algorithm = new SudokuAlgorithm();

    /**
    *
    */
    private static Chromosome<SudokuGene> chromosome = null;

    /**
     * @throws Exception Falls was schief geht.
     */
    @BeforeAll
    static void beforeAll() throws Exception
    {
        try (InputStream inputStream = ClassLoader.getSystemClassLoader().getResourceAsStream("sudoku_indices.txt"))
        {
            List<String[]> puzzle = algorithm.parsePuzzle(inputStream);
            algorithm.setPuzzle(puzzle);

            chromosome = new DefaultChromosome<>(algorithm);

            // @formatter:off
            List<SudokuGene> genes = puzzle.stream()
                .flatMap(Stream::of)
                .map(Integer::parseInt)
                .map(index -> index + 1) // Im Sudoku gibs keine 0
                .map(index -> new SudokuGene(index, false))
                .collect(Collectors.toList());
            // @formatter:on

            chromosome.setGenes(genes.toArray(new SudokuGene[0]));
        }
    }

    /**
     *
     */
    @Test
    void test010MaxFittnes()
    {
        double maxFitness = algorithm.getMaxFitness();

        assertEquals(1215, maxFitness);
    }

    /**
     * Von oben nach unten.
     */
    @Test
    void test020SumRows()
    {
        assertEquals(45, algorithm.calcRowFitness(0, chromosome));
        assertEquals(126, algorithm.calcRowFitness(1, chromosome));
        assertEquals(207, algorithm.calcRowFitness(2, chromosome));

        assertEquals(288, algorithm.calcRowFitness(3, chromosome));
        assertEquals(369, algorithm.calcRowFitness(4, chromosome));
        assertEquals(450, algorithm.calcRowFitness(5, chromosome));

        assertEquals(531, algorithm.calcRowFitness(6, chromosome));
        assertEquals(612, algorithm.calcRowFitness(7, chromosome));
        assertEquals(693, algorithm.calcRowFitness(8, chromosome));
    }

    /**
     * Von links nach rechts.
     */
    @Test
    void test030SumColumns()
    {
        assertEquals(333, algorithm.calcColumnFitness(0, chromosome));
        assertEquals(342, algorithm.calcColumnFitness(1, chromosome));
        assertEquals(351, algorithm.calcColumnFitness(2, chromosome));

        assertEquals(360, algorithm.calcColumnFitness(3, chromosome));
        assertEquals(369, algorithm.calcColumnFitness(4, chromosome));
        assertEquals(378, algorithm.calcColumnFitness(5, chromosome));

        assertEquals(387, algorithm.calcColumnFitness(6, chromosome));
        assertEquals(396, algorithm.calcColumnFitness(7, chromosome));
        assertEquals(405, algorithm.calcColumnFitness(8, chromosome));
    }

    /**
     * Von links nach rechts.
     */
    @Test
    void test040SumBlocks()
    {
        assertEquals(99, algorithm.calcBlockFitness(0, chromosome));
        assertEquals(126, algorithm.calcBlockFitness(1, chromosome));
        assertEquals(153, algorithm.calcBlockFitness(2, chromosome));

        assertEquals(342, algorithm.calcBlockFitness(3, chromosome));
        assertEquals(369, algorithm.calcBlockFitness(4, chromosome));
        assertEquals(396, algorithm.calcBlockFitness(5, chromosome));

        assertEquals(585, algorithm.calcBlockFitness(6, chromosome));
        assertEquals(612, algorithm.calcBlockFitness(7, chromosome));
        assertEquals(639, algorithm.calcBlockFitness(8, chromosome));
    }
}
