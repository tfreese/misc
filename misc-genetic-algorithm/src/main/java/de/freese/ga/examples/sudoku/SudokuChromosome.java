/**
 * Created: 29.06.2020
 */

package de.freese.ga.examples.sudoku;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;
import de.freese.ga.Chromosome;

/**
 * @author Thomas Freese
 */
public class SudokuChromosome extends Chromosome
{
    /**
     * Erstellt ein neues {@link SudokuChromosome} Object.
     *
     * @param config {@link SudokuConfig}
     */
    public SudokuChromosome(final SudokuConfig config)
    {
        super(config);
    }

    /**
     * Wieviele unterschiedliche Zahlen sind im Block und wie ist deren Summe ?<br>
     * Soll: 9 -> 45 in Summe
     *
     * @param block int
     * @param puzzleBlockSize int
     * @return double
     */
    double calcBlockFitness(final int block, final int puzzleBlockSize)
    {
        int start = 0;

        switch (block)
        {
            case 0:
            case 1:
            case 2:
                start = block * puzzleBlockSize;
                break;
            case 3:
                start = 27;
                break;
            case 4:
                start = 30;
                break;
            case 5:
                start = 33;
                break;
            case 6:
                start = 54;
                break;
            case 7:
                start = 57;
                break;
            case 8:
                start = 60;
                break;

            default:
                break;
        }

        Set<Integer> set = new HashSet<>();

        for (int i = start; i < (start + puzzleBlockSize); i++)
        {
            set.add(getGene(i).getValue());
        }

        start += 9;

        for (int i = start; i < (start + puzzleBlockSize); i++)
        {
            set.add(getGene(i).getValue());
        }

        start += 9;

        for (int i = start; i < (start + puzzleBlockSize); i++)
        {
            set.add(getGene(i).getValue());
        }

        // double fitness = set.size();

        double fitness = set.stream().mapToInt(Integer::intValue).sum();

        return fitness;
    }

    /**
     * Wieviele unterschiedliche Zahlen sind in der Spalte und wie ist deren Summe ?<br>
     * Soll: 9 -> 45 in Summe
     *
     * @param column int
     * @param puzzleSize int
     * @return double
     */
    double calcColumnFitness(final int column, final int puzzleSize)
    {
        int start = column;

        Set<Integer> set = new HashSet<>();

        for (int i = start; i < (puzzleSize * puzzleSize); i += puzzleSize)
        {
            set.add(getGene(i).getValue());
        }

        // double fitness = set.size();

        double fitness = set.stream().mapToInt(Integer::intValue).sum();

        return fitness;
    }

    /**
     * @see de.freese.ga.Chromosome#calcFitnessValue()
     */
    @Override
    public double calcFitnessValue()
    {
        int puzzleSize = getConfig().getPuzzleSize();
        int puzzleBlockSize = getConfig().getPuzzleBlockSize();

        double fitness = 0.0D;

        // Soll: 45 x 9 = 405
        fitness += IntStream.range(0, puzzleSize).mapToDouble(r -> calcRowFitness(r)).sum();

        // Soll: 45 x 9 = 405
        fitness += IntStream.range(0, puzzleSize).mapToDouble(c -> calcColumnFitness(c, puzzleSize)).sum();

        // Soll: 45 x 9 = 405
        fitness += IntStream.range(0, puzzleSize).mapToDouble(b -> calcBlockFitness(b, puzzleBlockSize)).sum();

        // Soll: 405 x 3 = 1215
        return fitness;
    }

    /**
     * Wieviele unterschiedliche Zahlen sind in der Reihe und wie ist deren Summe ?<br>
     * Soll: 9 -> 45 in Summe
     *
     * @param row int
     * @return double
     */
    double calcRowFitness(final int row)
    {
        int start = row * 9;
        int end = start + 9;

        Set<Integer> set = new HashSet<>();

        for (int i = start; i < end; i++)
        {
            set.add(getGene(i).getValue());
        }

        // double fitness = set.size();

        double fitness = set.stream().mapToInt(Integer::intValue).sum();

        return fitness;
    }

    /**
     * @see de.freese.ga.Chromosome#getConfig()
     */
    @Override
    protected SudokuConfig getConfig()
    {
        return (SudokuConfig) super.getConfig();
    }

    /**
     * @see de.freese.ga.Chromosome#getGene(int)
     */
    @Override
    public SudokuGene getGene(final int index)
    {
        return (SudokuGene) super.getGene(index);
    }

    /**
     * @see de.freese.ga.Chromosome#mutate()
     */
    @Override
    public void mutate()
    {
        for (int i = 0; i < size(); i++)
        {
            if (getConfig().getRandom().nextDouble() < getConfig().getMutationRate())
            {
                int j = getConfig().getRandom().nextInt(size());

                SudokuGene geneI = getGene(i);
                SudokuGene geneJ = getGene(j);

                // Nur veränderbare.
                if (geneI.isMutable())
                {
                    setGene(i, new SudokuGene(geneJ.getValue(), true));
                }

                if (geneJ.isMutable())
                {
                    setGene(j, new SudokuGene(geneI.getValue(), true));
                }
            }
        }

        // @formatter:off
//        IntStream.range(0, chromosome.size())
//            .parallel()
//            .forEach(i -> {
//                if (getConfig().getRandom().nextDouble() < getConfig().getMutationRate())
//                {
//                    int j = getRandom().nextInt(chromosome.size());
//
//                    SudokuGene geneI = chromosome.getGene(i);
//                    SudokuGene geneJ = chromosome.getGene(j);
//
//                    // Nur veränderbare.
//                    if (geneI.isMutable())
//                    {
//                        chromosome.setGene(i, new SudokuGene(geneJ.getValue(), true));
//                    }
//
//                    if (geneJ.isMutable())
//                    {
//                        chromosome.setGene(j, new SudokuGene(geneI.getValue(), true));
//                    }
//                }
//            });
        // @formatter:on
    }

    /**
     * @see de.freese.ga.Chromosome#populate()
     */
    @Override
    public void populate()
    {
        Map<Integer, SudokuGene> fixNumbers = getConfig().getFixNumbers();

        // Population pro Zeile testen.
        // Set<Integer> set= IntStream.range(0, 9).collect(TreeSet::new, TreeSet::add,TreeSet::addAll);
        // Set<Integer> set= IntStream.range(0, 9).boxed().collect(Collectors.toSet());

        for (int i = 0; i < size(); i++)
        {
            // Erst nach fest vorgegeben Zahlen suchen.
            SudokuGene gene = fixNumbers.get(i);

            if (gene == null)
            {
                // Dann welche generieren.
                int n = getConfig().getRandom().nextInt(9) + 1;

                gene = new SudokuGene(n, true);
            }

            setGene(i, gene);
        }

        // @formatter:off
//        IntStream.range(0, chromosome.size())
//            .parallel()
//            .forEach(i -> {
//              // Erst nach fest vorgegeben Zahlen suchen.
//              SudokuGene gene = this.fixNumbers.get(i);
//
//              if (gene == null)
//              {
//                  // Dann welche generieren.
//                  int n = getConfig().getRandom().nextInt(9) + 1;
//
//                  gene = new SudokuGene(n, true);
//              }
//
//              genes[i] = gene;
//            });
        // @formatter:on
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        int puzzleSize = getConfig().getPuzzleSize();
        int puzzleBlockSize = getConfig().getPuzzleBlockSize();

        StringBuilder sb = new StringBuilder();
        sb.append("\n");

        for (int row = 0; row < puzzleSize; row++)
        {
            for (int col = 0; col < puzzleSize; col++)
            {
                int index = (row * puzzleSize) + col;

                SudokuGene gene = getGene(index);

                if (gene.isMutable())
                {
                    sb.append(String.format(" %d ", gene.getInteger()));
                }
                else
                {
                    sb.append(String.format("(%d)", gene.getInteger()));
                }

                if ((((col + 1) % puzzleBlockSize) == 0) && (col < (puzzleSize - 1)))
                {
                    sb.append("|");
                }
                else
                {
                    sb.append(" ");
                }
            }

            sb.append("\n");

            if ((((row + 1) % puzzleBlockSize) == 0) && (row < (puzzleSize - 1)))
            {
                char[] chars = new char[(puzzleBlockSize * 3) + 2];
                Arrays.fill(chars, '-');
                String separator = new String(chars);

                sb.append(String.format("%s|%s|%s%n", separator, separator, separator));
            }
        }

        return sb.toString();
    }
}
