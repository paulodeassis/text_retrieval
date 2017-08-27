package br.cin.ufpe.lucene;

import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;

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
	
	public Indexer(String indexDirectoryPath) throws IOException{
		//Indexes Directory
		Directory indexDirectory = FSDirectory.open(Paths.get(indexDirectoryPath));
		config = new IndexWriterConfig();
		config.setMaxBufferedDocs(1000);
		writer = new IndexWriter(indexDirectory, config);
	}
	
	public void close() throws CorruptIndexException, IOException{
		writer.close();
	}
	
	private Document getDocument(File file) throws IOException{
		Document document = new Document();
		fieldType = new FieldType();
		fieldType.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
		
		//Index file contents
		Field contentField = new Field(LuceneKonstanten.CONTENTS, new FileReader(file), fieldType);
		
		fieldType = new FieldType();
		fieldType.setStored(true);
		fieldType.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);		
		//Index file name
		Field fileNameField = new Field(LuceneKonstanten.FILE_NAME, file.getName(), fieldType);
		
		fieldType = new FieldType();
		fieldType.setStored(true);
		fieldType.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
		
		Field filePath = new Field(LuceneKonstanten.FILE_PATH, file.getCanonicalPath(),fieldType);
		
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
	
}
