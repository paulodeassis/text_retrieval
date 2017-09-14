package br.cin.ufpe.lucene;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

public class CustomAnalyzer {
	protected EnglishAnalyzer getEmptyAnalizer(CharArraySet noneEmptyCharSet) {
		EnglishAnalyzer noneAnalyser = new EnglishAnalyzer(noneEmptyCharSet);
		return noneAnalyser;
	}

	protected StandardAnalyzer getSemmingAndStopwordAnalyzer() {
		StandardAnalyzer stopwordAndStemmingAnalyzer = new StandardAnalyzer();
		return stopwordAndStemmingAnalyzer;
	}

	protected StandardAnalyzer getStemmingAnalyzer() {
		CharArraySet emptyCharSet = new CharArraySet(new ArrayList<>(), true);
		StandardAnalyzer stemmerAnalyzer = new StandardAnalyzer(emptyCharSet);
		return stemmerAnalyzer;
	}

	protected EnglishAnalyzer getStopwordAnalyzer() {
		EnglishAnalyzer stopWordAnalyser = new EnglishAnalyzer(getStopwords());
		return stopWordAnalyser;
	}
	
	protected CharArraySet getStopwords() {
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
	
	protected EnglishAnalyzer getNoneAnalyzer() {
		CharArraySet noneEmptyCharSet = new CharArraySet(new ArrayList<>(), true);
		EnglishAnalyzer noneAnalyser = getEmptyAnalizer(noneEmptyCharSet);
		return noneAnalyser;
	}

	public Analyzer getNgramAnalyzer() {
		// TODO Auto-generated method stub
		return null;
	}

}
