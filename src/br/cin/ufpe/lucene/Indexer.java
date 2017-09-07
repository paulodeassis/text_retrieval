package br.cin.ufpe.lucene;

import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import br.cin.ufpe.lucene.CustomIndexOption;

public class Indexer {
	private IndexWriter writer;
	private IndexWriterConfig config;
	private FieldType fieldType;
	private Indexer indexer;
	
	public Indexer(String indexDirectoryPath, CustomIndexOption customIndexOption) throws IOException{
		//Indexes Directory
		Directory indexDirectory = FSDirectory.open(Paths.get(indexDirectoryPath));
		config = new IndexWriterConfig();
		config.setMaxBufferedDocs(1000);
		
		switch(customIndexOption) {
		case STOPWORDS:
			/*Creating stopwords*/
			StandardAnalyzer stopWordAnalyser = new StandardAnalyzer(getStopwords());
			config = new IndexWriterConfig(stopWordAnalyser);
			writer = new IndexWriter(indexDirectory, config);
			break;
		case STEMING:
			StandardAnalyzer stopWordAnalyser_ = new StandardAnalyzer();
			config = new IndexWriterConfig(stopWordAnalyser_);
			break;
		case N_GRAM:
			//TODO
			break;
		default:
			config = new IndexWriterConfig();
			
			break;
		}
		writer = new IndexWriter(indexDirectory, config);
	}
	
	private CharArraySet getSteming() {
		// TODO Auto-generated method stub
		return null;
		
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
	
	public int createIndex(String dataDirPath, FileFilter filter)throws IOException {
		File [] files = new File(dataDirPath).listFiles();
		for(File file : files) {
			if(!file.isDirectory() && !file.isHidden() && file.exists() && file.canRead() && filter.accept(file)) {
				indexFile(file);
			}
		}
		return writer.maxDoc();
	}
	
	private CharArraySet getStopwords() {
		 final List<String> stopWords = Arrays.asList(
				   "a", "an", "and", "are", "as", "at", "be", "but", "by",
				   "for", "if", "in", "into", "is", "it",
				   "no", "not", "of", "on", "or", "such",
				   "that", "the", "their", "then", "there", "these",
				   "they", "this", "to", "was", "will", "with"
		 );
		 CharArraySet stopSet = new CharArraySet(stopWords, false);
		 return stopSet;
	}
}
