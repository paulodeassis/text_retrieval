package br.cin.ufpe.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class Main {
	private static final String INDEX_DIR_NONE = "index_none";
	private static final String INDEX_DIR_STOPWORDS = "index_stoprword";
	private static final String INDEX_DIR_STEMMING = "index_stemming";
	private static final String INDEX_DIR_STOPWORDS_STEMMING = "index_stoprword_stemming";	
	private static final String DATA_DIR = "Data";

	public static void main(String[] args) throws IOException, ParseException {
		Main app = new Main();

		app.createIndex(INDEX_DIR_NONE, new StandardAnalyzer(new CharArraySet(new ArrayList<>(), true)));
		app.createIndex(INDEX_DIR_STOPWORDS, new StandardAnalyzer(app.getStopWords()));
		app.createIndex(INDEX_DIR_STEMMING, new EnglishAnalyzer(new CharArraySet(new ArrayList<>(), true)));
		app.createIndex(INDEX_DIR_STOPWORDS_STEMMING, new EnglishAnalyzer(app.getStopWords()));

		IndexSearcher searcher_none = app.createSearcher(INDEX_DIR_NONE);
		QueryParser query_parser_none = new QueryParser("titulo", new EnglishAnalyzer(new CharArraySet(new ArrayList<>(), true)));
		Query query_none = query_parser_none.parse("\"Comparison of Some\"");
		app.search(searcher_none, query_none);

		IndexSearcher searcher_stopwords = app.createSearcher(INDEX_DIR_STOPWORDS);
		QueryParser query_parser_stopwords = new QueryParser("titulo", new EnglishAnalyzer(app.getStopWords()));
		Query query_stopwords = query_parser_stopwords.parse("\"Area Protection in Adversarial\"");
		app.search(searcher_stopwords, query_stopwords);

	}
	
	//Analyzer analyzer = new SnowballAnalyzer(Version.LUCENE_35,language);

	public void search(IndexSearcher searcher, Query q) throws IOException {
		TopDocs hits = searcher.search(q, 100);
		System.out.println("-----------------------------------------------------------------------");
		System.out.println(hits.totalHits + " documentos encontrados para consulta " + q.toString());
		System.out.println("-----------------------------------------------------------------------");
		for (ScoreDoc sd : hits.scoreDocs) {
			Document d = searcher.doc(sd.doc);
			System.out.println("Titulo: " + d.get("titulo")); 
			System.out.println("Linha: " + d.get("linha")); 
			System.out.println("Arquivo: " + d.get("arquivo") + "\n");
		}		
	}

	public void createIndex(String dir, Analyzer analyzer) throws IOException {
		IndexWriter writer = createWriter(dir, analyzer);
		writer.deleteAll();
		List<Document> docs = createDocs();
		writer.addDocuments(docs);
		writer.commit();
		writer.close();
	}

	private CharArraySet getStopWords() throws IOException {
		List<String> stopWords = new ArrayList<>();
		BufferedReader br = new BufferedReader(new FileReader("lista_stopwords.csv"));
		String line = "";

		while ((line = br.readLine()) != null) {
			String[] text = line.split(";");

			for (String t : text) {
				stopWords.add(t);
			}
		}
		br.close();
		CharArraySet stopSet = new CharArraySet(stopWords, false);
		return stopSet;
	}

	public List<Document> createDocs() throws IOException {
		List<Document> docs = new ArrayList<>();

		FieldType titleType = new FieldType();
		titleType.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
		titleType.setStored(true);
		titleType.setTokenized(true);

		File[] files = new File(DATA_DIR).listFiles();
		BufferedReader br;
		String line = "";
		int linha = 1;
		for (File file : files) {
			br = new BufferedReader(new FileReader(file));

			while ((line = br.readLine()) != null) {
				String[] text = line.split(";");

				Field titulo = new Field("titulo", text[0], titleType);

				Document doc = new Document();
				doc.add(titulo);
				doc.add(new StringField("linha", "" + linha, Field.Store.YES));
				doc.add(new StringField("arquivo", file.getCanonicalPath(), Field.Store.YES));

				docs.add(doc);
				linha++;
			}
		}

		return docs;
	}

	private IndexWriter createWriter(String diretorio, Analyzer analyzer) throws IOException {
		FSDirectory dir = FSDirectory.open(Paths.get(diretorio));
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		IndexWriter writer = new IndexWriter(dir, config);
		return writer;
	}

	private IndexSearcher createSearcher(String diretorio) throws IOException {
		Directory dir = FSDirectory.open(Paths.get(diretorio));
		IndexReader reader = DirectoryReader.open(dir);
		IndexSearcher searcher = new IndexSearcher(reader);
		return searcher;
	}
}
