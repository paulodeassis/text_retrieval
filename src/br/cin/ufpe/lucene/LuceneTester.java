package br.cin.ufpe.lucene;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.util.Version;

public class LuceneTester {
	String indexDir = "Index";
	String dataDir = "Data";
	Indexer indexer;
	Searcher searcher;

	public static void main(String[] args) {
		LuceneTester tester;
		try {
			tester = new LuceneTester();
			
			/*Criando Index para Stopword*/
			tester.createIndex(CustomIndexOption.STOPWORDS);
			
			tester.search("Learning");
			
		}catch(IOException e) {
			e.printStackTrace();
		}catch(ParseException p) {
			p.printStackTrace();
		}

	}
	
	private void createIndex(CustomIndexOption cio) throws IOException {
		switch(cio) {
		case STOPWORDS:
			indexer = new Indexer(LuceneConstant.STOPWORDS_INDEX_DATABASE, CustomIndexOption.STOPWORDS);
			int numIndexedStopword;
			long starttimeStopword = System.currentTimeMillis();
			numIndexedStopword = indexer.createIndex(dataDir, new TextFileFilter());
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

	}
	
	private void search(String searchQuery) throws IOException, ParseException {
		searcher = new Searcher(indexDir);
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
