package br.cin.ufpe.test;

import java.io.BufferedReader;
import java.io.File;
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
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
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
	private static final boolean CREATE_INDEX = false;
	
	private static final Analyzer NONE_ANALYZER = new StandardAnalyzer(new CharArraySet(new ArrayList<>(), true));
	private static final Analyzer STEMMING_ANALYZER = new EnglishAnalyzer(new CharArraySet(new ArrayList<>(), true));
	private static final Analyzer STOPWORD_ANALYZER = new StandardAnalyzer(getStopWords());
	private static final Analyzer STOP_STEMMING_ANALYZER = new EnglishAnalyzer(getStopWords());
	
	private static final boolean CONSULTA_3 = false;
	
	private static final String [] CAMPOS_BUSCA = {
				"titulo", 
				"autor", 
				"resumo"
			};
	
	private static final String [] CONSULTAS = {
				"Area Protection in Adversarial", 
				"\"Area Protection in Adversarial\""
			};

	public static void main(String[] args) throws IOException, ParseException {
		Main app = new Main();

		if(CREATE_INDEX) {						
			app.createIndex(INDEX_DIR_NONE, NONE_ANALYZER);
			app.createIndex(INDEX_DIR_STEMMING, STEMMING_ANALYZER);
			app.createIndex(INDEX_DIR_STOPWORDS, STOPWORD_ANALYZER);
			app.createIndex(INDEX_DIR_STOPWORDS_STEMMING, STOP_STEMMING_ANALYZER);			
		}

		app.runSearchs();
		
	}
	
	public void runSearchs() throws IOException, ParseException {
		System.out.println("------------------------------------------------- CONSULTAS INDEXAÇÃO PADRÃO --------------------------------------------------");
		IndexSearcher searcher_none = createSearcher(INDEX_DIR_NONE);
		QueryParser query_parser_none = new MultiFieldQueryParser(CAMPOS_BUSCA, NONE_ANALYZER);				
		
		//Consulta 1
		Query query_none = query_parser_none.parse(CONSULTAS[0]);
		search(searcher_none, query_none);
		
		//Consulta 2
		Query query_none2 = query_parser_none.parse(CONSULTAS[1]);
		search(searcher_none, query_none2);
		
		if(CONSULTA_3) {
			//Consulta 3
			Query query_none3 = query_parser_none.parse(CONSULTAS[2]);
			search(searcher_none, query_none3);
		}
		
		System.out.println("------------------------------------------------- CONSULTAS STEMMING ----------------------------------------------------");
		IndexSearcher searcher_stemming = createSearcher(INDEX_DIR_STOPWORDS);
		QueryParser query_parser_stemming = new MultiFieldQueryParser(CAMPOS_BUSCA, STEMMING_ANALYZER);
		
		//Consulta 1
		Query query_stemming = query_parser_stemming.parse(CONSULTAS[0]);
		search(searcher_stemming, query_stemming);
		
		//Consulta 2		
		Query query_stemming2 = query_parser_stemming.parse(CONSULTAS[1]);
		search(searcher_stemming, query_stemming2);
		
		if(CONSULTA_3) {
			//Consulta 3		
			Query query_stemming3 = query_parser_stemming.parse(CONSULTAS[2]);
			search(searcher_stemming, query_stemming3);
		}
		
		System.out.println("------------------------------------------------- CONSULTAS STOPWORD -------------------------------------------------");
		IndexSearcher searcher_stopwords = createSearcher(INDEX_DIR_STOPWORDS);
		QueryParser query_parser_stopwords = new MultiFieldQueryParser(CAMPOS_BUSCA, STOPWORD_ANALYZER);
		
		//Consulta 1
		Query query_stopwords = query_parser_stopwords.parse(CONSULTAS[0]);
		search(searcher_stopwords, query_stopwords);
		
		//Consulta 2
		Query query_stopwords2 = query_parser_stopwords.parse(CONSULTAS[1]);
		search(searcher_stopwords, query_stopwords2);
		
		if(CONSULTA_3) {
			//Consulta 3
			Query query_stopwords3 = query_parser_stopwords.parse(CONSULTAS[2]);
			search(searcher_stopwords, query_stopwords3);
		}
		
		System.out.println("------------------------------------------------- CONSULTAS STEMMING E STOPWORDS --------------------------------------------");

		IndexSearcher searcher_stopwords_stemming = createSearcher(INDEX_DIR_STOPWORDS_STEMMING);
		QueryParser query_parser_stopwords_stemming = new MultiFieldQueryParser(CAMPOS_BUSCA, STOP_STEMMING_ANALYZER);
		
		//Consulta 1
		Query query_stopwords_stemming = query_parser_stopwords_stemming.parse(CONSULTAS[0]);
		search(searcher_stopwords_stemming, query_stopwords_stemming);
		
		//Consulta 2
		Query query_stopwords_stemming2 = query_parser_stopwords_stemming.parse(CONSULTAS[1]);
		search(searcher_stopwords_stemming, query_stopwords_stemming2);
		
		if(CONSULTA_3) {
			Query query_stopwords_stemming3 = query_parser_stopwords_stemming.parse(CONSULTAS[2]);
			search(searcher_stopwords_stemming, query_stopwords_stemming3);
		}
	}

	public void search(IndexSearcher searcher, Query q) throws IOException {
		TopDocs hits = searcher.search(q, 10);
		System.out.println("*******************************************************************************************");
		System.out.println(hits.totalHits + " documentos encontrados para consulta " + q.toString());
		System.out.println("*******************************************************************************************");
		int resultado = 1;
		for (ScoreDoc sd : hits.scoreDocs) {
			Document d = searcher.doc(sd.doc);
			System.out.println("["+resultado+"]");
			System.out.println("Titulo: " + d.get("titulo")); 
			System.out.println("Autor: "+d.get("autor"));
			System.out.println("Link: "+d.get("link"));
			System.out.println("Linha: " + d.get("linha")); 
			System.out.println("Arquivo: " + d.get("arquivo") + "\n");
			resultado++;
		}
		System.out.println("");
	}

	public void createIndex(String dir, Analyzer analyzer) throws IOException {
		IndexWriter writer = createWriter(dir, analyzer);
		writer.deleteAll();
		List<Document> docs = createDocs();
		writer.addDocuments(docs);
		writer.commit();
		writer.close();
	}

	private static CharArraySet getStopWords() {		
		CharArraySet stopSet = null;
		try {
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
			stopSet = new CharArraySet(stopWords, false);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return stopSet;
	}

	public List<Document> createDocs() throws IOException {
		List<Document> docs = new ArrayList<>();

		FieldType titleType = new FieldType();
		titleType.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
		titleType.setStored(true);
		titleType.setTokenized(true);
		
		FieldType authorType = new FieldType();
		//authorType.setIndexOptions(IndexOptions.DOCS_AND_FREQS);
		authorType.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
		authorType.setStored(true);
		authorType.setOmitNorms(true);
		authorType.setTokenized(true);
		authorType.setStoreTermVectors(true);

		FieldType summaryType = new FieldType();
		//summaryType.setIndexOptions(IndexOptions.DOCS);
		summaryType.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
		summaryType.setStored(true);
		summaryType.setTokenized(true);
		summaryType.setStoreTermVectors(true);
		summaryType.setStoreTermVectorPositions(true);
		summaryType.setStoreTermVectorOffsets(true);
		summaryType.setStoreTermVectorPayloads(true);

		File[] files = new File(DATA_DIR).listFiles();
		BufferedReader br;
		String line = "";
		int qtdDocumentosTotal = 1;
		for (File file : files) {
			br = new BufferedReader(new FileReader(file));
			System.out.println("Indexando arquivo: "+ file.getName());
			int linha = 1;
			while ((line = br.readLine()) != null) {
				String[] text = line.split(";");

				Field titulo = new Field("titulo", text[0], titleType);
				Field autor = new Field("autor", text[1], authorType);
				Field resumo = new Field("resumo", text[2], summaryType);

				Document doc = new Document();
				doc.add(titulo);
				doc.add(autor);
				doc.add(resumo);
				doc.add(new StringField("link", text[3], Field.Store.YES));
				doc.add(new StringField("linha", "" + linha, Field.Store.YES));
				doc.add(new StringField("arquivo", file.getName(), Field.Store.YES));

				docs.add(doc);
				linha++;
				qtdDocumentosTotal++;
			}
			System.out.println("Indexou "+linha+" documentos \n");
		}
		System.out.println("***** Total Documentos Indexados: "+qtdDocumentosTotal);
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
