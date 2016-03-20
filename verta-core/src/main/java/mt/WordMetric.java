package mt;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Vector;

/**
 * 
 * @author jordiatserias
 *
 */
public class WordMetric { 
	MetricActivationCounter counters;
	private static final int MAX_FEATURE_WEIGHT = 100;

	//TODO a patch to capture all distances

	
	static int NOWORDMETRICS=2;
	
	public boolean FILTER_PUNCTUATION=false;
	
	//we should group metrics by id
	HashMap<String, WeightedWordMetric> featureMetrics;

	public boolean reversed;
	
	private double groupWeight;
	
	/// name
	String name;
	
	
	
	public static Similarity instantiateSimilarity(String className, double weight, String[] line, int npar){ 
		Similarity sm=null;
    	  try {
    	  Class<?> partypes[] = new Class[1];
          partypes[0] = java.lang.String.class;
			Class<?> cl = Class.forName(className);
			
			//@TODO check which constructor to call
			Constructor<?> ct = cl.getConstructors()[0];
			
			int narg = 3+npar;
			Object arglist[] = new Object[line.length-narg];
			for(int i=narg;i<line.length;++i) arglist[i-narg]= line[i];
			sm  = (Similarity) ct.newInstance(arglist);
			sm.setWeight(weight);
			
    	  }
    	  catch(Exception e) {
    		  System.err.println("Error trying to load Similarity Class >"+className+"<");
    		  e.printStackTrace();
    		  System.exit(-1);
    		  
    	  }
    	  return sm;
	}
	
	public WordMetric()  {
		 featureMetrics = new HashMap<String,WeightedWordMetric>();
	}
	
	public WordMetric(String name, BufferedReader config, double groupWeight,
		String configFilename, MetricActivationCounter counters) {
		this.name=name;
		this.counters=counters;
		featureMetrics = new HashMap<String,WeightedWordMetric>();
		load(config, groupWeight,configFilename);
	}


	/**
	

/*
 * word similarity is just the sum of feature similarity (@TODO normalization on the number of features maybe needed)
 */
public double similarity(Word proposedWord, Word targetWord) {
	return similarity(proposedWord, targetWord,null,"");
}

/**
 * 
 * How a set of metrics is applied to a pair of words
 * 
 * trace
 * 
 * <ft type ="TYPE_PARAM"/>
 *  <group id="NGROUP">
 *    <mt  feat="FEATURE_NAME"  sim="JAVACLASS" simid="ID" active="COLOR" pword="PROOSEDWORD" rword="TARGETWORD" weight="DIST"/>
 *     ...
 *    </group>
 * </ft>
 *     
 * @param proposedWord
 * @param targetWord
 * @param pout
 * @param type
 * @return
 */
public double similarity(final Word proposedWord, final Word targetWord, PrintStream pout, String type) {
	double sum=0;

	//@TODO FIX WEIGHT
	if(pout!=null) pout.println("<ft type=\""+type+"\">");

	/**
	 * there is an inconsistence between groupId and group number
	 */
	// for every group metric
	int ngroup=0;
	for(String group : featureMetrics.keySet()) {
		if(pout!=null)	pout.println("<group id=\""+ngroup+"\">");
		WeightedWordMetric x = featureMetrics.get(group);
		double contrib=0.0;
		int f=0;
		boolean active=true;
		while(contrib<=Similarity.MINVAL && f < x.size()) {
			FeatureMetric fm = x.get(f);
			fm.reversed=reversed;
			//contrib = fm.weight * fm.similarity(proposedWord, targetWord);
			contrib =  fm.similarity(proposedWord, targetWord);
			active = contrib > Similarity.MINVAL;

			counters.increase(fm.getClassName()+Arrays.asList(fm.featureNames),contrib,reversed);

			//trace
			if(pout!=null) { 
				pout.print("<mt feat=\""+XMLFormater.encodeXMLString(Arrays.deepToString(fm.featureNames))+"\"");
				pout.print(" sim=\""+fm.getClassName()+"\" simid=\""+(f+1)+"\"");
				pout.print(" active=\""+(active ? "#20B020" : "#B02020" )+"\"");
				pout.print(" pword=\""+XMLFormater.encodeXMLString(Arrays.deepToString(proposedWord.getFeatures(fm.featureNames)))+"\" ");
				pout.print(" rword=\""+XMLFormater.encodeXMLString(Arrays.deepToString(targetWord.getFeatures(fm.featureNames)))+"\" ");
				pout.println(" weight=\""+contrib+"\">");	
				pout.println("</mt>");
			}	
			f++;
		}
		//System.err.println("ngroups"+ngroup+" lenght "+weights.length);
		sum = sum + x.getWeight() * contrib;
		ngroup++;
		if(pout!=null)pout.println("</group>");
	}
	if(pout!=null)  pout.println("</ft>");
	return sum / MAX_FEATURE_WEIGHT;
}

public void dump(PrintStream trace) {
	
	trace.println("<fms weightsum=\""+groupWeight+"\">");
	for(String key: featureMetrics.keySet()) {
		Vector<FeatureMetric> x = featureMetrics.get(key);
		trace.println("<group>");
		for(FeatureMetric fm:x) {
			fm.dump(trace);
		}
		trace.println("</group>");
	}
	trace.println("</fms>");
}

public void load(BufferedReader config, double groupWeight, String filename) {
	try {
	String buff;
	while((buff=config.readLine())!=null && !buff.trim().startsWith("FGROUP")) {
	
	System.err.println("proc:"+buff);
    //if(buff==null)  {
  	//  System.err.println("Format ERROR on the metric config file >"+filename+"<");
    //    System.err.println("EMPTY LINE READING WORDMETRIC FILE");
	//      System.exit(-1);
   // }
    
    
   // weights = new double[buff.split("[\t]").length-NOWORDMETRICS];
    //int nm=0;
    //sumGroups =0;
  
     //coments start with #
  	  if(!buff.trim().startsWith("#")) {
  	  String line[]=buff.split("[ \t]+");
  	  if(line.length<4) {
  		  System.err.println("Format ERROR on the metric config file >"+filename+"<");
  		  System.err.println("AT LINE:"+buff);
  		  System.exit(-1);
  	  }
  	  //Similarity sm = null;//@TODO Load  Class by name line[1];
  	  int npar=1;
  	  String grupId= line[0];
  	  String className=line[npar+2];
  	  String featureName=line[npar];
  	  double weight = Double.parseDouble(line[npar+1]);
  	  if(weight>MAX_FEATURE_WEIGHT) System.err.println("Warning Weight>>"+MAX_FEATURE_WEIGHT+" in metric config file at "+line);//weight=100;
  	  //WEIGHTED SUM
  	  //weightSum += weight;
  	 
  	  
  	  Similarity sm=instantiateSimilarity(className,  weight,line, npar);
  	   	  
  	 if(!ReaderCONLL.hasFeatures(line[npar].split(","))) {
  		  System.err.println("Unknown feature name >"+line[npar]+"< metric config file >"+filename+"<");
	          System.err.println("At LINE:"+buff);
	          new Exception().printStackTrace();
	          System.exit(-1);
  	 }
  	 
     
  	  
		// Add a feature metric into the grup
  	   WeightedWordMetric group = featureMetrics.get(grupId);
  	   //ERROR we should relate ngroup to grouID (it may be inconsistent)
  	   if(group==null) group = new WeightedWordMetric(1.0); //@TODO CHECK what we need weight (groupWeight);
  	   group.add(new FeatureMetric(featureName, sm, weight));
  	   featureMetrics.put(grupId,group);
    } 

    
    //@TODO change weight
    //weightSum =100  * featureMetrics.size();
   // weightSum =100  * sumGroups;
  	//weightSum +=100  *  groupWeight;
   this.groupWeight = groupWeight;
	}
	
  	// @TODO CHECK THAT CAN ONLY BE DONE AT THE END
    // create a vector for similarity matching statistics
//    statistics = new int[featureMetrics.size()][]; int i=0;
//    bestStatistics  = new int[featureMetrics.size()][];
//    for(Entry<String, WeightedWordMetric  >  gr:featureMetrics.entrySet()) {
//  	  statistics[i] = new int[gr.getValue().size()];  
//  	  bestStatistics[i++] = new int[gr.getValue().size()];  
//    }

	 }catch(Exception e) {
		 System.err.println("Format Error in Metric configuration file");
		 e.printStackTrace();
		 System.exit(-1);
	 }
}


public double getWeight() {
	return groupWeight;
}


public String getName() {
	return name;
}

/**
 * 
 * 
 *  Sentence level
 * 
 * 
 * 
 */


/**
 * it is calculating / using an Alignment
 * @param proposedSentence
 * @param referenceSentence
 * @param dist
 * @param strace
 * @return
 */
//@Override
public double[] OLDsimilarity(
		final Sentence proposedSentence, 
		final Sentence referenceSentence, 
		DistanceMatrix dist,	
		PrintStream strace) {
	
	
	
	double[] res = new double[2];
	
	// empty align
	Integer align[] =  new Integer[Math.max(proposedSentence.size(), referenceSentence.size())];
	boolean taken[] = new boolean[Math.max(proposedSentence.size(), referenceSentence.size())];
	for(int i=0;i<align.length;++i) align[i]=-1;
	for(int i=0;i<taken.length;++i) taken[i]=false;
	
	double prec=sentenceSimilarity(align,taken,dist,false, proposedSentence,referenceSentence);		
	
	//dump the alignment
	if(MTsimilarity.DUMP) {
		strace.print("<align>");
		strace.print("<st>"); 
		for(int i=0;i<proposedSentence.size();++i) strace.println("<s  s=\""+i+"\" t=\""+align[i]+"\"/>");
		strace.print("</st>");
	}
	
	//clean
	for(int i=0;i<align.length;++i) align[i]=-1;
	for(int i=0;i<taken.length;++i) taken[i]=false;
	
	double rec=sentenceSimilarity(align,taken, dist,true,referenceSentence,proposedSentence);
	
	//dump the alignment
	if(MTsimilarity.DUMP) {
		strace.print("<ts>"); 
		for(int i=0;i<referenceSentence.size();++i) strace.println("<s  s=\""+i+"\" t=\""+align[i]+"\"/>");
		strace.print("</ts>");
		strace.print("</align>");
	}
	
	res[0]= prec;
	res[1]= rec;
	return res;
}

public double[] similarity(
		final Sentence proposedSentence, 
		final Sentence referenceSentence, 
		DistanceMatrix dist,	
		PrintStream strace) {
	
	
	
	double[] res = new double[2];
	
	AlignmentImpl align = new AlignmentImpl(proposedSentence.size(), referenceSentence.size());
	
	double prec=sentenceSimilarity(dist,align, false, proposedSentence,referenceSentence);			
	double rec=sentenceSimilarity(dist,align,true,referenceSentence,proposedSentence);
	res[0]= prec;
	res[1]= rec;
	
	//dump the alignment
	if(MTsimilarity.DUMP) {
		align.dump(strace);
	}
	
	
	return res;
}
/**
 * 
 * @param dist
 * @param a
 * @param reversed
 * @param proposedSentence
 * @param targetSentence
 * @return
 */
public double sentenceSimilarity(		
		DistanceMatrix dist, 	
		SentenceAlignment a,
		final boolean reversed,
		final Sentence proposedSentence,
		final Sentence targetSentence) {
	// calculate all distances
	int w=0;
	for(Word sw:proposedSentence) {
	 int iw=0;
	for(Word tw: targetSentence) {
			this.reversed=reversed;
			double mdist=this.similarity(sw, tw);
			dist.setDistance(reversed,w,iw,mdist, "lex");
			++iw;
		}
	++w;
	}
	
	//SentenceAlignment align = new AlignmentImpl(proposedSentence.size(), targetSentence.size());
	// build the alignment
	//SentenceAlignment align = 
	
	//TODO configure aligment strategy
	new AlignmentBuilderBestMatch().build(reversed,a ,dist);
	//new AlignmentBuilderFirstLeft2Rigth().build(reversed,a ,dist);
	
	// calculate scores given the selected alignment
    int [] al=a.aligned(reversed);
    int nwords=0;
    
    double res=0;
    for(int i=0;i<al.length;++i) {	
    	//System.err.println("check "+proposedSentence.size()+" x "+targetSentence.size()+" "+i+":"+al[i]+" sum="+res);
    	if( !FILTER_PUNCTUATION || !WordFilter.filter(proposedSentence.get(i))) {
        	if(al[i]>=0) res += dist.getDistance(reversed, i, al[i]);
    	    nwords++;
    	}
    }
	
//    System.err.println("RES:"+res);
//   a.dump(System.err);
//    dist.dump(System.err);
//    System.exit(-1);
	return res / nwords++; // proposedSentence.size();
}
/**
 * Calculates the similarity between two sentences
 * 
 * @param proposedSentence
 * @param targetSentence
 * @param wm the word metric to be used
 * @return the similarity [0-1]
 */
public double sentenceSimilarity(
		
		Integer align[], 
		boolean[] taken,
		DistanceMatrix dist, 
		
		final boolean reversed,
		final Sentence proposedSentence,
		final Sentence targetSentence) {
	//find the most similar two words
	//Integer align[] =  new Integer[Math.max(proposedSentence.size(), targetSentence.size())];
	//for(int i=0;i<align.length;++i) align[i]=-1;
	
	double distBestWord[] = new double[proposedSentence.size()];
	int sw=0;
	for(Word w:proposedSentence) {
		distBestWord[sw]=bestMatch(reversed,sw,w, targetSentence,align, taken, dist);		
		++sw;
	}
	
	double sum=0;
	
	for(int j=0;j<distBestWord.length;++j) {
		if(distBestWord[j]>0) sum += distBestWord[j];
	}
	
	//@BUG return sum+100*sum/proposedSentence.size();
	return sum/ proposedSentence.size();
}
/**
 * return the highest similarity according to Wordmetric wm  between Word w (at posisiton pos) with any the words in targetSentence
 * 
 * reversed was used to inverse relation in prec / recall like hypernym - hyponym 
 * 
 * @param reversed
 * @param pos
 * @param w
 * @param targetSentence
 * @param align
 * @param wm
 * @return
 */
private  double bestMatch(final boolean reversed, final int pos, final  Word w,  final Sentence targetSentence, Integer[] align, boolean[] taken, DistanceMatrix dist) {
	boolean found=false;
	double max=0;
	int maxword=-1;
	int iw=0;
	while(iw<targetSentence.size() && !found) {
		//Not Aligned before
		if(!taken[iw]) {
			this.reversed=reversed;
			double mdist=this.similarity(w, targetSentence.get(iw));
			dist.setDistance(reversed,pos,iw,mdist,"lex");

			//TODO store the metric info to count best-similarity usage group+metric
			if(mdist>max) {
				max=mdist;
				maxword=iw;
			}
		}
    ++iw;
	}
			
	// final result
	//System.err.print(w.getFeature("WORD")+" x ");
	if(maxword>-1) {
		//align[maxword]=pos;
		align[pos]=maxword;
		taken[maxword]=true;
	//	System.err.println( targetSentence.get(maxword).getFeature("WORD")+" = "+max);
	}
	//else {System.err.println("NO MATCH");}
	return max;
}

}
