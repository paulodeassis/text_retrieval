package br.cin.ufpe.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.ngram.NGramTokenizer;

public class NgramAnalyzer extends Analyzer {

	@Override
	protected TokenStreamComponents createComponents(String arg0) {
		// TODO Auto-generated method stub
		Tokenizer source = new NGramTokenizer(NGramTokenizer.DEFAULT_MIN_NGRAM_SIZE, NGramTokenizer.DEFAULT_MAX_NGRAM_SIZE);
	    
	    
	    return null;
	}

}
