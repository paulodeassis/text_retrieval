package br.cin.ufpe.principal;

import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.ngram.NGramTokenFilter;
import org.apache.lucene.analysis.ngram.NGramTokenizer;
import org.apache.lucene.analysis.StopwordAnalyzerBase;

public class NgramAnalyzer extends StopwordAnalyzerBase {
	
	public NgramAnalyzer(CharArraySet stopWords) {
		super(stopWords);
	}

	@Override
	protected TokenStreamComponents createComponents(String arg0) {
		/*Utilizando o default do NgramTokenizer*/ 
		final Tokenizer source = new NGramTokenizer(Main.MIN_GRAM, Main.MAX_GRAM);
		    TokenStream result = new NGramTokenFilter(source);
		    result = new LowerCaseFilter(result);
		    return new TokenStreamComponents(source, result);
	}
	
}
