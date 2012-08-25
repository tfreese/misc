/**
 * Created: 25.08.2012
 */

package de.freese.sonstiges.lucene;

import java.io.IOException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.NoMergePolicy;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

/**
 * @author Thomas Freese
 */
public class SimpleDemo
{
	/**
	 * 
	 */
	private static final Version VERSION = Version.LUCENE_36;

	/**
	 * @param indexWriter {@link IndexWriter}
	 * @param value String
	 * @throws IOException Falls was schief geht.
	 */
	private static void addDoc(final IndexWriter indexWriter, final String value)
		throws IOException
	{
		Document doc = new Document();
		doc.add(new Field("title", value, Field.Store.YES, Field.Index.ANALYZED));
		doc.add(new Field("length", Integer.toString(value.length()), Field.Store.YES,
				Field.Index.NO));
		indexWriter.addDocument(doc);

		indexWriter.commit();
	}

	/**
	 * @param args String[]
	 * @throws IOException Falls was schief geht.
	 * @throws ParseException Falls was schief geht.
	 */
	public static void main(final String[] args) throws IOException, ParseException
	{
		// 0. Specify the analyzer for tokenizing text.
		// The same analyzer should be used for indexing and searching
		StandardAnalyzer analyzer = new StandardAnalyzer(VERSION);

		// 1. create the index
		Directory directory = new RAMDirectory();
		// Directory directory = FSDirectory.open(new File("lucene-index/demo"));

		// org.apache.lucene.store.jdbc.JdbcDirectory
		// org.compass.core.lucene.util.LuceneUtils; RAMDirectory<->JdbcDirectory

		IndexWriterConfig config = new IndexWriterConfig(VERSION, analyzer);
		config.setMergePolicy(NoMergePolicy.NO_COMPOUND_FILES);

		// if (config.getMergePolicy() instanceof TieredMergePolicy)
		// {
		// ((TieredMergePolicy) config.getMergePolicy()).setUseCompoundFile(false);
		// }
		//
		// if (config.getMergePolicy() instanceof LogMergePolicy)
		// {
		// ((LogMergePolicy) config.getMergePolicy()).setUseCompoundFile(false);
		// }

		IndexWriter indexWriter = new IndexWriter(directory, config);
		addDoc(indexWriter, "Lucene in Action");
		addDoc(indexWriter, "Lucene for Dummies");
		addDoc(indexWriter, "Managing Gigabytes");
		addDoc(indexWriter, "The Art of Computer Science");

		// Index speichern.
		try
		{
			indexWriter.close();
		}
		finally
		{
			if (IndexWriter.isLocked(directory))
			{
				IndexWriter.unlock(directory);
			}
		}

		// 2. query
		String querystr = args.length > 0 ? args[0] : "lucene";

		// the "title" arg specifies the default field to use
		// when no field is explicitly specified in the query.
		Query q = new QueryParser(VERSION, "title", analyzer).parse(querystr);

		// 3. search
		int hitsPerPage = 10;
		IndexReader reader = IndexReader.open(directory);
		IndexSearcher searcher = new IndexSearcher(reader);
		TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage, true);
		searcher.search(q, collector);
		ScoreDoc[] hits = collector.topDocs().scoreDocs;

		// 4. display results
		System.out.println("Found " + hits.length + " hits.");

		for (int i = 0; i < hits.length; ++i)
		{
			int docId = hits[i].doc;
			Document d = searcher.doc(docId);
			System.out.println((i + 1) + ". " + d.get("title") + "; lenght=" + d.get("length"));
		}

		// searcher can only be closed when there
		// is no need to access the documents any more.
		searcher.close();
	}

	/**
	 * Erstellt ein neues {@link SimpleDemo} Object.
	 */
	public SimpleDemo()
	{
		super();
	}
}
