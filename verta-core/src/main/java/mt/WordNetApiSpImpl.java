package mt;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.zip.GZIPInputStream;
import java.util.Set;
import java.util.Vector;

import javax.management.RuntimeErrorException;

import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
/**
 * 
 * TODO fix this should be a singleton
 * TODO not thread safe
 * 
 * @author jordi
 *
 */
public class WordNetApiSpImpl implements WordNetAPI {
	static HashMap<String,String> hypern ;
	static HashMap<String,Set<SPSynset>> lemmas2synset;
	static HashMap<String,SPSynset> synset2lemmas ;
	
	public WordNetApiSpImpl(String lang)  {
		try { load(lang);} catch(Exception  e) { throw new RuntimeException(e);}
	}
	
	final static String variant2lemma(String var) {
		return var.substring(0,var.lastIndexOf("_"));
	}
	
	final static String variantIinputFile="/variants.gz";
	final static String hyperIinputFile="/hyper.gz";
	//final static String[]  file_fields= { "source_id", "source_variant", "rel", "target_id", "target_variant"};
	// format synset variat synset variant
	
	public static void load(String lang) throws IOException {
		hypern = new HashMap<String,String>();
		lemmas2synset = new HashMap<String,Set<SPSynset>>();
		synset2lemmas = new HashMap<String,SPSynset>();
		System.err.println("Loading "+lang);
		BufferedReader fin =new BufferedReader(new InputStreamReader((InputStream) new GZIPInputStream(WordNetApiSpImpl.class.getResourceAsStream("/wn/"+lang+variantIinputFile)),"UTF-8"));
		//BufferedReader fin = new BufferedReader(new FileReader(inputFile));
		assert(fin!=null);
		String buff;
		while((buff=fin.readLine())!=null){
			String[] ifields = buff.split("[ \t]");
			String lemma1 = variant2lemma(ifields[1]);
			
			// add variant - synset 
	        String syn = ifields[0];	
	        SPSynset v1 =  synset2lemmas.containsKey(syn) ? synset2lemmas.get(syn) : new SPSynset(syn);
	        v1.add(lemma1);
	        synset2lemmas.put(syn,v1);
			
			Set<SPSynset> s1 =  lemmas2synset.containsKey(lemma1) ? lemmas2synset.get(lemma1) : new HashSet<SPSynset>();
			s1.add(v1);
			lemmas2synset.put(lemma1,s1);
		}
		System.err.println("Loaded "+lemmas2synset.size()+" lemmas");
		
		// add hypernm relations
		fin =new BufferedReader(new InputStreamReader((InputStream) new GZIPInputStream(WordNetApiSpImpl.class.getResourceAsStream("/wn/"+lang+hyperIinputFile))));
		assert(fin!=null);
		//BufferedReader fin = new BufferedReader(new FileReader(inputFile));
		while((buff=fin.readLine())!=null){
			String[] ifields = buff.split("[ \t]");
			SPSynset hyp = synset2lemmas.get(ifields[1]);
			if(hyp==null) ; //System.err.println("Can not find synset:"+ifields[1]);
			else {
			 SPSynset syn = synset2lemmas.get(ifields[0]);
			 if(syn!=null) syn.addHyper(hyp);
			}
		}
	}
	
	public static final SPSynset[] EmptyArraySPSynset = new SPSynset[0];
	
	
	// this is the only think to index lemma, pos -> synsets[]
	@Override
	public JABSynset[] getSynsets(String wordForm, SynsetType pos) {
		Vector<SPSynset> res = new Vector<SPSynset>();
		String rpos = "n";
		if(pos== SynsetType.ADJECTIVE || pos== SynsetType.ADJECTIVE_SATELLITE) rpos= "a";
		else if(pos== SynsetType.ADVERB) rpos="r";
		else if(pos== SynsetType.VERB) rpos="v";
		
		for(SPSynset  s : getSynsets(wordForm)) {
			if (s.id.endsWith(rpos)) { res.add(s);}
		}
		return res.toArray(new SPSynset[0]);
	}
	
	@Override
	public SPSynset[] getSynsets(String wordForm) {
		if(lemmas2synset.containsKey(wordForm)) return lemmas2synset.get(wordForm).toArray(EmptyArraySPSynset);
		else return EmptyArraySPSynset;
	}
	
	// we may access to the lemma field
	@Override
	public String[] getBaseFormCandidates(String wordForm, SynsetType pos) {
		// TODO Auto-generated method stub
		throw new RuntimeException("Lemmatization is not yet implemented in SP Wordnet");
	}
	
	/**
	 * 
	 * @param args
	 * @throws IOException
	 */
   public static void main(String[] args) throws IOException {
	   WordNetApiSpImpl.load("es");
	   int nhyp=0; 
	   for(SPSynset x : synset2lemmas.values()) {
		   if(x.hypers.size()==0)  nhyp++;
	   }	   
	   System.err.println("no hyper "+nhyp+" of "+synset2lemmas.values().size());
	   for(SPSynset x : lemmas2synset.get("gato")) {
		   x.dump();
	   }
	   
	   //Let's check if sym works
	   SimilarityHypernymWn f = new SimilarityHypernymWn("SINGLE");
	   SimilarityHyponymWn fhypo = new SimilarityHyponymWn("SINGLE");
	   SimilaritySynonymWn fsyn = new SimilaritySynonymWn();
	   
	   Word proposedWord = new Word("1","gato");
	   Word referenceWord = new Word("2","tramposo");
	   Word synWord = new Word("3","pájaro");
	   String[] featureNames={ "WORD" };
	   System.err.println(f.similarity(featureNames,  proposedWord, referenceWord)==1);
	   System.err.println(f.similarity(featureNames,   referenceWord,proposedWord)==1);
	   System.err.println(fhypo.similarity(featureNames,  referenceWord, proposedWord )==1);
	   System.err.println(fsyn.similarity(featureNames,  referenceWord, proposedWord )==1);
	   System.err.println(fsyn.similarity(featureNames,  synWord, proposedWord )==1);
	   
	   
	   // eli test 
	   String[][] elitest= { {"crear","hacer"},
			   {"conseguir", "lograr"},
	   {"acción","acto"},
	   {"cosa", "objeto"}};
	   for(String[] et: elitest) {
		  Word pWord= new Word("1", et[0]);
		  Word rWord = new Word("2",et[1]);
	      double mres = fsyn.similarity(featureNames,  rWord, pWord );
	    	  System.err.println(mres+" "+pWord+"-"+rWord);
	      
	   }
	   
	   System.err.println("DONE");
   }
   
	static SynsetType[] _SN = {SynsetType.NOUN};
	static SynsetType[] _SV = {SynsetType.VERB};
	static SynsetType[] _SA = {SynsetType.ADJECTIVE,SynsetType.ADJECTIVE_SATELLITE};
	static SynsetType[] _SR = {SynsetType.ADVERB};
	
   @Override
	public SynsetType[] getSynsetTypeFromPos(String pos) {
		switch(pos.charAt(0))  {
		case 'N':
		case 'n': return _SN;
	
		case 'V':
		case 'v':
			return _SV;
			
		case 'A':
		case 'a':
			return _SA;
			
		case 'R':
		case 'r':
			return _SR;
		default:
			return null;
			
		}
     }
}