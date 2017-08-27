package br.cin.ufpe.lucene;

import java.io.File;
import java.io.FileFilter;

public class TextFileFilter implements FileFilter {

	@Override
	public boolean accept(File pathname) {
		if(pathname.getName().toLowerCase().endsWith(".txt")) {
			return true;
		}else if(pathname.getName().toLowerCase().endsWith(".csv")) {
			return true;
		}else {
			return false;
		}
	}
	
}
