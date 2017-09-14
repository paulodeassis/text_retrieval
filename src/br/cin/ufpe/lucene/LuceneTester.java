package br.cin.ufpe.lucene;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.util.Version;

public class LuceneTester {
	String indexDir = "Index";
	String dataDir = "Data";
	
	Searcher searcher;

	public static void main(String[] args) throws ParseException {
		LuceneTester tester;
		try {
			tester = new LuceneTester();
			//CustomAnalyzer customAnalyzer = new CustomAnalyzer();
			/*Creating Index for Stopword*/
			Indexer indexerStopword = new Indexer(LuceneConstant.STOPWORDS_INDEX_DATABASE, CustomIndexOption.STOPWORDS);
			
			/*Creating Index for Stemming*/
			//Indexer indexerSteming = new Indexer(LuceneConstant.STEMING_INDEX_DATABASE, CustomIndexOption.STEMING);
			
			/*Creating Index for N-Gram*/
			Indexer indexerNgram = new Indexer(LuceneConstant.N_GRAM_INDEX_DATABASE, CustomIndexOption.N_GRAM);
			
			/*Creating Index for Stamming and Stopword*/
			//Indexer indexerStopwordAndStemming = new Indexer(LuceneConstant.STOPWORD_AND_STAMMING_INDEX_DATABASE, CustomIndexOption.STOPWORDS_AND_STAMMING);
			
			//Indexer indexerNone = new Indexer(LuceneConstant.NONE, CustomIndexOption.NONE);
						
			/*Search works only for stopwords*/
			//tester.search("neural".toLowerCase(),CustomIndexOption.STEMING);
			
			
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	/*private void createIndex(CustomIndexOption cio) throws IOException {
		switch(cio) {
		case STOPWORDS:
			indexer = new Indexer(LuceneConstant.STOPWORDS_INDEX_DATABASE, CustomIndexOption.STOPWORDS);
			int numIndexedStopword;
			long starttimeStopword = System.currentTimeMillis();
			numIndexedStopword = indexer.createIndex(LuceneConstant.STOPWORDS_INDEX_DATABASE,new TextFileFilter());
			long endTimeStopword = System.currentTimeMillis();
			indexer.close();
			System.out.println(numIndexedStopword+" File indexed, time taken: "+(endTimeStopword-starttimeStopword)+" ms");
			break;
		case STEMING:
			indexer = new Indexer(LuceneConstant.STEMING_INDEX_DATABASE, CustomIndexOption.STEMING);
			int numIndexedSteming;
			long starttimeSteming = System.currentTimeMillis();
			numIndexedSteming = indexer.createIndex(dataDir, new TextFileFilter());
			long endTimeSteming = System.currentTimeMillis();
			indexer.close();
			System.out.println(numIndexedSteming+" File indexed, time taken: "+(endTimeSteming-starttimeSteming)+" ms");
			break;
		case N_GRAM:
			System.out.println("NÃO FOI IMPLEMENTADO!");
			indexer = new Indexer(LuceneConstant.N_GRAM_INDEX_DATABASE, CustomIndexOption.N_GRAM);
			int numIndexedNGram;
			long starttimeNGram = System.currentTimeMillis();
			numIndexedNGram = indexer.createIndex(dataDir, new TextFileFilter());
			long endTimeNGram = System.currentTimeMillis();
			indexer.close();
			System.out.println(numIndexedNGram+" File indexed, time taken: "+(endTimeNGram-starttimeNGram)+" ms");
			break;
		default:
			indexer = new Indexer(indexDir, CustomIndexOption.NONE);
			int numIndexed;
			long starttime = System.currentTimeMillis();
			numIndexed = indexer.createIndex(dataDir, new TextFileFilter());
			long endTime = System.currentTimeMillis();
			indexer.close();
			System.out.println(numIndexed+" File indexed, time taken: "+(endTime-starttime)+" ms");
		}

	}*/
	
	private void search(String searchQuery, CustomIndexOption indexOption) throws IOException, ParseException {
		CustomAnalyzer customAnalyzer = new CustomAnalyzer();
		switch (indexOption) {
		case STOPWORDS:
			searcher = new Searcher(LuceneConstant.STOPWORDS_INDEX_DATABASE, customAnalyzer.getStopwordAnalyzer());
			break;
		case STOPWORDS_AND_STAMMING:
			searcher = new Searcher(LuceneConstant.STOPWORD_AND_STAMMING_INDEX_DATABASE, customAnalyzer.getSemmingAndStopwordAnalyzer());
			break;
		case STEMING:
			searcher = new Searcher(LuceneConstant.STEMING_INDEX_DATABASE, customAnalyzer.getStemmingAnalyzer());
			break;
		case N_GRAM:
			searcher = new Searcher(LuceneConstant.N_GRAM_INDEX_DATABASE, customAnalyzer.getNgramAnalyzer());
			break;
		default:
			searcher = new Searcher(LuceneConstant.NONE, customAnalyzer.getNgramAnalyzer());
			break;
		}
		
		long startTime = System.currentTimeMillis();
		TopDocs hits = searcher.search(searchQuery);
		long endTime = System.currentTimeMillis();
		
		System.out.println(hits.totalHits+ " documents found. Time: "+(endTime-startTime));
		
		for(ScoreDoc scoreDoc : hits.scoreDocs) {
			Document doc = searcher.getDocument(scoreDoc);
			System.out.println("File: "+doc.get(LuceneConstant.FILE_PATH));
		}
	}

}
