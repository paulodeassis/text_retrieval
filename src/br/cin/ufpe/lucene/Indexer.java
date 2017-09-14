package br.cin.ufpe.lucene;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.ngram.NGramTokenizer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class Indexer {
	private IndexWriter writer;
	private IndexWriterConfig config;
	private FieldType fieldType;
	private Indexer indexer;
	String dataDir = "Data";
	
	public Indexer(String indexDirectoryPath, CustomIndexOption customIndexOption) throws IOException{
		//Indexes Directory
		Directory indexDirectory = FSDirectory.open(Paths.get(indexDirectoryPath));
		config = new IndexWriterConfig();
		config.setMaxBufferedDocs(1000);
		CustomAnalyzer customAnalyzer = new CustomAnalyzer();
		
		switch(customIndexOption) {
		case STOPWORDS:
			/*Creating Analyzer eliminating stopwords no stemming*/
			//indexer = new Indexer(LuceneConstant.STOPWORDS_INDEX_DATABASE, CustomIndexOption.STOPWORDS);
			EnglishAnalyzer stopWordAnalyser = customAnalyzer.getStopwordAnalyzer();			
			int numIndexedStopword;
			long starttimeStopword = System.currentTimeMillis();
			config = new IndexWriterConfig(stopWordAnalyser);
			writer = new IndexWriter(indexDirectory, config);
			numIndexedStopword = createIndex();
			long endTimeStopword = System.currentTimeMillis();
			System.out.println(numIndexedStopword+" File indexed for Stopwords, time taken: "+(endTimeStopword-starttimeStopword)+" ms");
			close();
			break;
		case STEMING:
			/*Creating Analyzer dont eliminate stopword but doing stemming*/
			//indexer = new Indexer(LuceneConstant.STEMING_INDEX_DATABASE, CustomIndexOption.STEMING);
			int numIndexedSteming;
			
			StandardAnalyzer stemmerAnalyzer = customAnalyzer.getStemmingAnalyzer();
			config = new IndexWriterConfig(stemmerAnalyzer);
			writer = new IndexWriter(indexDirectory, config);
			long starttimeSteming = System.currentTimeMillis();
			numIndexedSteming = createIndex();
			long endTimeSteming = System.currentTimeMillis();
			System.out.println(numIndexedSteming+" File indexed for Stemming, time taken: "+(endTimeSteming-starttimeSteming)+" ms");
			close();
			break;
		case N_GRAM:
			//Creating Analyzer for NGram*/
			FileFilter filter = new TextFileFilter();
			File [] files = new File(dataDir).listFiles();
					
			for(int i=0;i<files.length;i++) {
				Tokenizer ngramTokenizer = new NGramTokenizer();
				ngramTokenizer.setReader(new FileReader(files[i]));
				CharTermAttribute charAtribute = ngramTokenizer.addAttribute(CharTermAttribute.class);
				ngramTokenizer.reset();
					String z;
					z = ngramTokenizer.toString();					
					if(filter.accept(files[i])) {
						StandardAnalyzer ngramAnalyzer = new StandardAnalyzer();
						ngramAnalyzer.tokenStream("titulo",z);
						config = new IndexWriterConfig(ngramAnalyzer);
						writer = new IndexWriter(indexDirectory, config);
						int numIndexedNgram;
						long starttimeNgram = System.currentTimeMillis();
						numIndexedNgram = createIndex();
						long endTimeNgram = System.currentTimeMillis();
						System.out.println(numIndexedNgram+" File indexed for Ngram, time taken: "+(endTimeNgram-starttimeNgram)+" ms");
						writer.close();
					}
			}
			break;
			
		case STOPWORDS_AND_STAMMING:
			/*Lucene default configuration considers both cases*/
			int numIndexedStopwordAndSteming;
			StandardAnalyzer stopwordAndStemmingAnalyzer = customAnalyzer.getSemmingAndStopwordAnalyzer();
			config = new IndexWriterConfig(stopwordAndStemmingAnalyzer);
			writer = new IndexWriter(indexDirectory, config);
			long starttimeStopwrodAndSteming = System.currentTimeMillis();
			numIndexedStopwordAndSteming = createIndex();
			long endTimeStopwordAndSteming = System.currentTimeMillis();
			System.out.println(numIndexedStopwordAndSteming+" File indexed for Stopword and Stemming, time taken: "+(endTimeStopwordAndSteming-starttimeStopwrodAndSteming)+" ms");
			writer.notifyAll();
			close();
			break;
		default:
			CharArraySet noneEmptyCharSet = new CharArraySet(new ArrayList<>(), true);
			EnglishAnalyzer noneAnalyser = customAnalyzer.getNoneAnalyzer();			
			int numIndexedNone;
			long starttimeNone = System.currentTimeMillis();
			config = new IndexWriterConfig(noneAnalyser);
			writer = new IndexWriter(indexDirectory, config);
			numIndexedNone = createIndex();
			long endTimeNone = System.currentTimeMillis();
			System.out.println(numIndexedNone+" File indexed for Stopwords, time taken: "+(endTimeNone-starttimeNone)+" ms");
			writer.notifyAll();
			close();
			break;
		}
	}
	

	public void close() throws CorruptIndexException, IOException{
		writer.close();
	}
	
	private Document getDocument(File file) throws IOException{
		/*DOCS 
		 * Only documents are indexed: term frequencies and positions are omitted.
		 * DOCS_AND_FREQ 
		 * Only documents and term frequencies are indexed: positions are omitted.
		 * DOCS_AND_FREQS_AND_POSITIONS 
		 * Indexes documents, frequencies and positions.
		 * DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS 
		 * Indexes documents, frequencies, positions and offsets.
		 * NONE 
		 * Not indexed*/
	
		Document document = new Document();
		fieldType = new FieldType();
		fieldType.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
		
		/*BufferedReader br = new BufferedReader(new FileReader(file));
		String line = "";
		List<Document> documentos = new ArrayList<>();
		int linha = 1;
		while ((line = br.readLine()) != null) {
		String[] text = line.split(";");
		}*/
		
		//Index file contents
		Field contentField = new Field(LuceneConstant.CONTENTS, new FileReader(file), fieldType);
		
		fieldType = new FieldType();
		fieldType.setStored(true);
		fieldType.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);		
		
		//Index file name
		Field fileNameField = new Field(LuceneConstant.FILE_NAME, file.getName(), fieldType);
		
		fieldType = new FieldType();
		fieldType.setStored(true);
		fieldType.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
		
		Field filePath = new Field(LuceneConstant.FILE_PATH, file.getCanonicalPath(),fieldType);
		
		document.add(contentField);
		document.add(fileNameField);
		document.add(filePath);
		return document;
	}
	
	private void  indexFile(File file) throws IOException {
		System.out.println("Indexing "+file.getCanonicalPath());
		Document document = getDocument(file);
		writer.addDocument(document);
	}
	
	public int createIndex()throws IOException {
		FileFilter filter = new TextFileFilter();
		File [] files = new File(dataDir).listFiles();
		for(File file : files) {
			if(!file.isDirectory() && !file.isHidden() && file.exists() && file.canRead() && filter.accept(file)) {
				indexFile(file);
			}
		}
		return writer.maxDoc();
	}
	
	public int createIndexForNgram(File file) throws IOException {
		FileFilter filter = new TextFileFilter();
		if(!file.isDirectory() && !file.isHidden() && file.exists() && file.canRead() && filter.accept(file)) {
			indexFile(file);
		}
		return writer.maxDoc();
	}
}
