package br.cin.ufpe.lucene;

import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

public class Searcher {
	IndexSearcher indexSearcher;
	Query query;
	IndexReader indexReaderQueryParser;
	QueryParser queryParser;
	Analyzer analyzer;
	
	public Searcher(String indexDirectoryPath) throws IOException {
		FSDirectory.open(Paths.get(LuceneConstant.FILE_PATH));
		//Directory indexDirectory = FSDirectory.open(Paths.get(LuceneKonstanten.FILE_PATH));
		indexReaderQueryParser = DirectoryReader.open(FSDirectory.open(Paths.get(LuceneConstant.FILE_PATH)));
		indexSearcher = new IndexSearcher(indexReaderQueryParser);
		queryParser =  new QueryParser(LuceneConstant.CONTENTS, new StandardAnalyzer());
	}
	
	public TopDocs search(String searchQuery) throws IOException, ParseException {
		query = queryParser.parse(searchQuery);
		return indexSearcher.search(query, LuceneConstant.MAX_SEARCH);
	}
	
	public Document getDocument(ScoreDoc scoreDoc) throws CorruptIndexException, IOException {
		return indexSearcher.doc(scoreDoc.doc);
	}
}