/**
 * Created: 17.04.2018
 */

package de.freese.ga.examples.sudoku;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.atomic.DoubleAdder;
import java.util.stream.IntStream;

import de.freese.ga.algoritm.AbstractAlgorithm;
import de.freese.ga.chromonome.Chromosome;

/**
 * Beim Sudoku dürfen die fest vorgegebenen Zahlen nicht verändert werden !
 *
 * @author Thomas Freese
 */
public class SudokuAlgorithm extends AbstractAlgorithm<SudokuGene>
{
    /**
     * Beim Sudoku dürfen die fest vorgegebenen Zahlen nicht verändert werden !
     */
    private final Map<Integer, SudokuGene> fixNumbers = new TreeMap<>();

    /**
    *
    */
    private int puzzleBlockSize = (int) Math.sqrt(this.puzzleSize);

    /**
     *
     */
    private int puzzleSize = 9;

    /**
     * Erstellt ein neues {@link SudokuAlgorithm} Object.
     */
    public SudokuAlgorithm()
    {
        super(0, 0);
    }

    /**
     * @see de.freese.ga.algoritm.Algorithm#calcFitnessValue(de.freese.ga.chromonome.Chromosome)
     */
    @Override
    public double calcFitnessValue(final Chromosome<SudokuGene> chromosome)
    {
        double fitness = 0.0D;
        final DoubleAdder doubleAdder = new DoubleAdder();

        // max 81
        IntStream.range(0, this.puzzleSize).parallel().forEach(r ->
        {
            doubleAdder.add(calcRowFitness(r, chromosome));
        });

        // max 81
        IntStream.range(0, this.puzzleSize).parallel().forEach(c ->
        {
            doubleAdder.add(calcColumnFitness(c, chromosome));
        });

        // max 81
        IntStream.range(0, this.puzzleSize).parallel().forEach(b ->
        {
            doubleAdder.add(calcBlockFitness(b, chromosome));
        });

        // Gaußsche Summenformel = (n² + n) / 2

        fitness = doubleAdder.sum();

        return fitness;
    }

    /**
     * @see de.freese.ga.algoritm.Algorithm#getMaxFitness()
     */
    @Override
    public double getMaxFitness()
    {
        double fitness = 0.0D;

        // 243 = 3*81: In allen Rows, Spalten und Blöcken sind 9 unterschiedliche Zahlen.
        fitness += (9 * 9) + (9 * 9) + (9 * 9);

        // 1215 = 3*405: In allen Rows, Spalten und Blöcken ist die Summe 45.
        // Gaußsche Summenformel = (n² + n) / 2
        // fitness += 3 * (((Math.pow(this.puzzleSize, 2) + this.puzzleSize) / 2) * this.puzzleSize);

        return fitness;
    }

    /**
     * @see de.freese.ga.algoritm.Algorithm#mutate(de.freese.ga.chromonome.Chromosome)
     */
    @Override
    public void mutate(final Chromosome<SudokuGene> chromosome)
    {
        // @formatter:off
        IntStream.range(0, chromosome.size())
            .parallel()
            .forEach(i -> {
                if (Math.random() < getMutationRate())
                {
                    int j = (int) (chromosome.size() * Math.random());

                    SudokuGene gene1 = chromosome.getGene(i);
                    SudokuGene gene2 = chromosome.getGene(j);

                    // Nur veränderbare.
                    if (chromosome.getGene(i).isMutable())
                    {
                        chromosome.setGene(i, new SudokuGene(gene2.getValue(), true));
                        // chromosome.getGene(i).setValue(gene2.getValue());
                    }

                    if (chromosome.getGene(j).isMutable())
                    {
                        chromosome.setGene(j, new SudokuGene(gene1.getValue(), true));
                        // chromosome.getGene(j).setValue(gene1.getValue());
                    }
                }
            });
        // @formatter:on
    }

    /**
     * @param inputStream {@link InputStream}
     * @return {@link List}>
     * @throws IOException Falls was schief geht.
     */
    public List<String[]> parsePuzzle(final InputStream inputStream) throws IOException
    {
        List<String[]> puzzle = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8)))
        {
            // @formatter:off
            reader.lines()
                .filter(l -> !l.startsWith("-"))
                .map(l -> l.replace("|", " "))
                .map(l -> l.replace("_", "0"))
                .map(l -> l.replace("x", "0"))
                .map(l -> l.replace("X", "0"))
                .map(l -> l.split("[ ]"))
                .forEach(puzzle::add);
            // @formatter:on
        }

        return puzzle;
    }

    /**
     * @see de.freese.ga.algoritm.Algorithm#pupulateChromosome(de.freese.ga.chromonome.Chromosome)
     */
    @Override
    public void pupulateChromosome(final Chromosome<SudokuGene> chromosome)
    {
        SudokuGene[] genes = new SudokuGene[chromosome.size()];

        // TODO Population pro Zeile testen.
        // Set<Integer> set= IntStream.range(0, 9).collect(TreeSet::new, TreeSet::add,TreeSet::addAll);
        // Set<Integer> set= IntStream.range(0, 9).boxed().collect(Collectors.toSet());

        // @formatter:off
        IntStream.range(0, chromosome.size())
            .parallel()
            .forEach(i -> {
              // Erst nach fest vorgegeben Zahlen suchen.
              SudokuGene gene = this.fixNumbers.get(i);

              if (gene == null)
              {
                  // Dann welche generieren.
                  int n = 0;

                  do
                  {
                      n = (int) (Math.random() * 10);
                  }
                  while ((n < 1) || (n > 9));

                  gene = new SudokuGene(n, true);
              }

              genes[i] = gene;
            });
        // @formatter:on

        chromosome.setGenes(genes);
    }

    /**
     * Ungekannte Zahlen sind als X markiert.<br>
     * Index 0 = Rows<br>
     * Index 1 = columns
     *
     * @param puzzle {@link List}]
     */
    public void setPuzzle(final List<String[]> puzzle)
    {
        Objects.requireNonNull(puzzle, "puzzle required");

        if ((puzzle.size() > 9) || (puzzle.get(0).length > 9))
        {
            throw new IllegalArgumentException("only puzzle with 9x9 format supported");
        }

        // this.puzzle = puzzle;
        this.puzzleSize = puzzle.size();
        this.puzzleBlockSize = (int) Math.sqrt(puzzle.size());

        this.fixNumbers.clear();

        // Stream.of(puzzle).flatMap(Stream::of).filter(Objects::nonNull).forEach(System.out::println);

        for (int row = 0; row < this.puzzleSize; row++)
        {
            String[] columns = puzzle.get(row);

            for (int col = 0; col < this.puzzleSize; col++)
            {
                int index = (row * this.puzzleSize) + col;

                int number = getNumber(columns[col]);

                if (number > 0)
                {
                    this.fixNumbers.put(index, new SudokuGene(number, false));
                }
            }
        }

        setSizeChromosome(this.puzzleSize * this.puzzleSize);
    }

    /**
     * @see de.freese.ga.algoritm.Algorithm#toString(de.freese.ga.chromonome.Chromosome)
     */
    @Override
    public String toString(final Chromosome<SudokuGene> chromosome)
    {
        String s = null;

        // @formatter:off
//        s = Stream.of(chromosome.getGenes())
//                 .map(SudokuGene::getValue)
//                 .map(Object::toString)
//                 //.map(Optional::ofNullable)
//                 //.map(o -> o.map(Object::toString).orElse("null"))
//                 .collect(Collectors.joining());
        // @formatter:on

        StringBuilder sb = new StringBuilder();
        sb.append("\n");

        for (int row = 0; row < this.puzzleSize; row++)
        {
            for (int col = 0; col < this.puzzleSize; col++)
            {
                int index = (row * this.puzzleSize) + col;

                SudokuGene gene = chromosome.getGene(index);

                if (gene.isMutable())
                {
                    sb.append(String.format(" %d ", gene.getValue()));
                }
                else
                {
                    sb.append(String.format("(%d)", gene.getValue()));
                }

                if ((((col + 1) % this.puzzleBlockSize) == 0) && (col < (this.puzzleSize - 1)))
                {
                    sb.append("|");
                }
                else
                {
                    sb.append(" ");
                }
            }

            sb.append("\n");

            if ((((row + 1) % this.puzzleBlockSize) == 0) && (row < (this.puzzleSize - 1)))
            {
                char[] chars = new char[(this.puzzleBlockSize * 3) + 2];
                Arrays.fill(chars, '-');
                String separator = new String(chars);

                sb.append(String.format("%s|%s|%s%n", separator, separator, separator));
            }
        }

        s = sb.toString();

        // Gaußsche Summenformel = (n² + n) / 2

        return s;
    }

    /**
     * Wieviele unterschiedliche Zahlen sind im Block?<br>
     * Soll: 9
     *
     * @param block int
     * @param chromosome {@link Chromosome}
     * @return double
     */
    private double calcBlockFitness(final int block, final Chromosome<SudokuGene> chromosome)
    {
        double fitness = 0.0D;

        int start = 0;

        switch (block)
        {
            case 0:
            case 1:
            case 2:
                start = block * this.puzzleBlockSize;
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

        for (int i = start; i < (start + this.puzzleBlockSize); i++)
        {
            set.add(chromosome.getGene(i).getValue());
        }

        start += 9;

        for (int i = start; i < (start + this.puzzleBlockSize); i++)
        {
            set.add(chromosome.getGene(i).getValue());
        }

        start += 9;

        for (int i = start; i < (start + this.puzzleBlockSize); i++)
        {
            set.add(chromosome.getGene(i).getValue());
        }

        fitness += set.size();
        // fitness += set.stream().mapToInt(Integer::intValue).sum();

        return fitness;
    }

    /**
     * Wieviele unterschiedliche Zahlen sind in der Spalte ?<br>
     * Soll: 9
     *
     * @param column int
     * @param chromosome {@link Chromosome}
     * @return double
     */
    private double calcColumnFitness(final int column, final Chromosome<SudokuGene> chromosome)
    {
        double fitness = 0.0D;

        int start = column;

        Set<Integer> set = new HashSet<>();

        for (int i = start; i < (this.puzzleSize * this.puzzleSize); i += this.puzzleSize)
        {
            set.add(chromosome.getGene(i).getValue());
        }

        fitness += set.size();
        // fitness += set.stream().mapToInt(Integer::intValue).sum();

        return fitness;
    }

    /**
     * Wieviele unterschiedliche Zahlen sind in der Reihe ?<br>
     * Soll: 9
     *
     * @param row int
     * @param chromosome {@link Chromosome}
     * @return double
     */
    private double calcRowFitness(final int row, final Chromosome<SudokuGene> chromosome)
    {
        double fitness = 0.0D;

        int start = row * 9;
        int end = start + 9;

        Set<Integer> set = new HashSet<>();

        for (int i = start; i < end; i++)
        {
            set.add(chromosome.getGene(i).getValue());
        }

        fitness += set.size();
        // fitness += set.stream().mapToInt(Integer::intValue).sum();

        return fitness;
    }

    /**
     * @param s String
     * @return int
     */
    private int getNumber(final String s)
    {
        int n = 0;

        try
        {
            n = Integer.parseInt(s);
        }
        catch (Exception ex)
        {
            // Ignore
            n = 0;
        }

        return n;
    }
}
