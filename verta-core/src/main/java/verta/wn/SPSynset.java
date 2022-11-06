package verta.wn;

import java.io.PrintStream;
import java.util.HashSet;
import java.util.Set;


public class SPSynset implements ISynset {
    public String id;
	Set<String> variants;
	Set<SPSynset> hypers;
	//
	public SPSynset(String id) {
		this.id=id;
		variants = new HashSet<>();
		hypers= new HashSet<>();
	}
	
	// hyernyms
	@Override
	public ISynset[] getHypernyms() {
		return hypers.toArray(new SPSynset[0]);
	}

	// variants
	@Override
	public String[] getWordForms() {
		return variants.toArray(new String[0]);
	}

	// no freq info
	@Override
	public int getTagCount(String wf) {
		return 0;
	}

	public void add(String lemma1) {
		variants.add(lemma1);
	}
	
	
	public String toString() {
		StringBuilder res =new StringBuilder();
		res.append("Synset spa-30-");
		res.append(id);
		res.append(" #w:");
		res.append(variants.size());
		for(String v:variants) {
			res.append(v);
			res.append("\n");
		}
		for(SPSynset s:hypers) {
			res.append("hyper:");
			res.append(s.id);
			for(String v: s.getWordForms()) {
				res.append(" ");
				res.append(v);
			}
			res.append("\n");
		}
		return res.toString();
	}

	public void dump(PrintStream out) {
		out.println("Synset spa-30-"+id+" #w:"+variants.size());
		for(String v:variants) {
			out.println(v);
		}
		for(SPSynset s:hypers) {
			out.print("hyper:"+s.id);
			for(String v: s.getWordForms()) {
				out.print(" "+v);
			}
			out.println();
		}
	}

	public void addHyper(SPSynset s) {
		hypers.add(s);
	}

	public boolean equals(Object aThat) {
		if(aThat instanceof SPSynset)
		 return (id.compareTo(((SPSynset) aThat ).id)==0);
		 else return false;
	}
}
