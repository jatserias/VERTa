package mt;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Vector;
import com.martiansoftware.jsap.JSAP;



/**
 * 
 * input: Multitag file and references files, and metric configuration files and a name for the experiment
 * 
 * output:
 *  - a browsable XML in folder <BASEFOLDER>
 *  - a cvs tab separated  per segment (max F1 among the references) <precision>\t<recall>\t<f1>
 *  - a file per metric module with all the information needed to compute the metric
 *  
 *  each module outputs  mod(segment hyp x segment ref) -> P R F1
 *  if it is a weighted module of several companents the module must putput mod(hyp x ref) c1 c2 c3 ... cn 
 *  
 *  
 *  convertion form old conll format
 *  
 *  grep -v '%%#SEG' Sentences_with_issues_hyp.conll | awk 'BEGIN {S=2;print "%%#SEG\t1";} /^[ ]*$/ { print "\n%%#SEG\t"S;S+S+1;next} { print $1"\t"$2"\t"$4"\t"$4"\t"$3"\t-\t-\t-\t"$8"\t"$7}' > Sentences_with_issues_Hyp.tag 
 * 
 * @author jordi
 *
 *
 */
public class MTsimilarity {

	/// Word Metrics
	public  List<WordMetric> wms;
	
	/// Sentence Level metrics
	public List<WeightedSentenceMetric> sm;
	

	

	private static final String BASEFOLDER = "exp/";
    private static final String precrecallfile = "precrec.txt";

    MetricActivationCounter counters;
	static  boolean DUMP;   // true; // false; 

	public static final String TOPXSL = "match"; // "ex2" ;

	public static void usage() {
		System.err.println("Usage: Three parameters needed");
		System.err.println("- Metric config filename");
		System.err.println("- Metric config filename");
		System.err.println("- Experiment Name");
		System.err.println("- proposed filename in conll format");
		System.err.println("- references filenames in conll format");
		System.err.println("plus an optional parameter: the name of the experiment");
	}
	
	public static SentenceMetric  instantiateSentenceMetric(String className, final String[] line, MetricActivationCounter counters) {
		SentenceMetric sm=null;
  	  try {
  	  Class<?> partypes[] = new Class[1];
        partypes[0] = java.lang.String.class;
			Class<?> cl = Class.forName(className);
			
			Object arglist[];
			Class<?> clist[];
		
			
			int narg = 5;
			arglist = new Object[line.length-narg+1];
			clist = new Class[line.length-narg+1];
			
			if(line.length-narg==0) {
				clist[0]=counters.getClass(); 
				arglist[0]=counters;
			}
			else {
			System.err.println("sentenceMetric with "+(line.length-narg)+" args");
			
			clist[0]=counters.getClass();
			arglist[0]=counters;		
			
			for(int i=narg+1;i<=line.length;++i) {
			 clist[i-narg]= String.class;
			 arglist[i-narg]= line[i-1];
			}
			}	
			
			//@TODO check which constructor to call
			Constructor<?> ct = cl.getConstructor(clist);
			sm  = (SentenceMetric ) ct.newInstance(arglist);
			
			//sm.setWeight(weigth);
  	  }
  	  catch(Exception e) {
  		  System.err.println("Error trying to load SentenceMetric>"+className+"<");
  		  e.printStackTrace();
  		  System.exit(-1);
  		  
  	  }
	return sm;
	}
	
	
	public MTsimilarity(String configFilename) { 
		init();
		load(configFilename);
		
	}

	
	public MTsimilarity() {
		init();
	}
	
	
	public MTsimilarity(String configFielname,BufferedReader bufferedReader) {
		init();
		load(configFielname,bufferedReader);
	}

	private void init() {
		wms = new  Vector<WordMetric>();
		sm= new Vector<WeightedSentenceMetric>();
		counters = new MetricActivationCounter();
	}
	
	
	
	
	

	
	public void  setFilter(boolean filter) {
		for(WordMetric w:wms) {
			w.FILTER_PUNCTUATION=filter;
		}
	}

	
	
	/**
	 * sets up a metric from a config file
	 * @param configFilename
	 */
	public void load(String configFilename) {
		
		 BufferedReader config=null;
		 
		 try {
		  config = new BufferedReader(new FileReader(configFilename));
		 }
		 catch(Exception e) {
			System.err.println("ERROR can not open/find file >"+configFilename+"<");
			e.printStackTrace();
			System.exit(-1);
		 }
	      
		 load(configFilename,config);
	}
	
	public void load(String configFilename, BufferedReader config) {
		 String buff; 
	      try {
	    	 
	      // read weights
	      while((buff=config.readLine())!=null ) {
	      if(! buff.trim().startsWith("#") && buff.trim().length() >0) {
	     // if(buff==null)  {
	    //	  System.err.println("Format ERROR on the metric config file >"+configFilename+"<");
	     //     System.err.println("EMPTY FILE");
		  //    System.exit(-1);
	     // }

	      if(! buff.startsWith("GROUP")) {
	    	  System.err.println("Format ERROR on the metric config file >"+configFilename+"<");
	    	  System.err.println("At line:"+buff);
	    	  System.err.println("GROUP head expected");
	      }
	      else{
	      //GROUP HEAD: GROUP <tab> ID <tab> WEIGHT <tab> CLASSNAME     	  
	    	  String line[]=buff.split("\t");
	    	  if(line.length<5) {
	    		  System.err.println("Format ERROR on the metric config file >"+configFilename+"<");
	    	  }
	    	  System.err.println("READING GROUP>"+buff+"<");
	    	  String classname = line[4];
	    	  double groupWeight=Double.parseDouble(line[3]);
	    	  
	    	  // int id
	    	  String name = line[2];
	    	  
			if(classname.compareTo("mt.WordMetric")==0) {
	    		  // add wordmetric group
	    		  wms.add( new WordMetric(name,config,groupWeight , configFilename, counters));
	    	  }
	    	  else {
	    		  // instantiate class
	    		  sm.add(new WeightedSentenceMetric(name,groupWeight,instantiateSentenceMetric(classname , line,counters)));
	    	  }
	      }
	      
	      } 
	      }
		 }catch(Exception e) {
			 System.err.println("Format Error in Metric configuration file");
			 e.printStackTrace();
			 System.exit(-1);
		 }
	}

	
	
	
	public static void printHeader(PrintStream trace,String experimentName, String topxsl) {
		trace.println(XMLFormater.header());
		//trace.println("<?xml-stylesheet type=\"text/xsl\" href=\""+TOPXSL+".xsl\" ?>");
		trace.println("<?xml-stylesheet type=\"text/xsl\" href=\""+topxsl+".xsl\" ?>");
		trace.println("<exp><name>"+XMLFormater.encodeXMLString(experimentName)+"</name>");
		Date d= new Date(System.currentTimeMillis());
	    trace.println("<date>"+XMLFormater.encodeXMLString(d.toLocaleString())+"</date>");
	}
	
	
	/**
	 * 
	 * return  MetricResult  P / R /F 
	 * @param referenceSentence
	 * @param proposedSentence
	 * @param strace
	 * @return
	 */
	public	MetricResult similarity(final Sentence  referenceSentence, final Sentence  proposedSentence, PrintStream strace)	 {
		if(DUMP) strace.println("<presults>");
		
		
		if(DUMP)strace.println("<lexresults>");
		
		/// Applies word-aligment metrics
		DistanceMatrix dist = new DistanceMatrix(proposedSentence,referenceSentence);
		
		/// store the partial results
		MetricResult tres = new MetricResult(dist);
		
		
		/**
		 * for w X w apply all word metrics
		 * 
		 * As a side result a matrix of distance (tres.dist) and an alignment between the sentences is build 
		 */
		for(WordMetric iwm : wms) {
			//Return also dist matrix (internally uses align)
			double[] res  =iwm.similarity(proposedSentence, referenceSentence, tres.dist,strace);			
			double prec=res[0];
			double rec=res[1];
		
			tres.add(iwm.getName(),iwm.getWeight(), prec, rec);
		
		
			
			if(DUMP){
				//@TODO HACK recalculate all distances...
				dumpAllDist(strace,proposedSentence,referenceSentence,iwm);
			}
			
			/** @TODO ALERT we only align using the first lexical metric (should we add them all?)
			 * 
			 * this assignment is to avoid to update the distance matrix so only the first lexical metric is used to calculate the alignment
			 */
			tres.dist = new DistanceMatrix(proposedSentence,referenceSentence);
		}
		
		if(DUMP) strace.println("</lexresults>");
		


		
		/**
		 * build Alignment based on the distance Matrix from the lexical components
		 */
		//SentenceAlignment align = new AlignmentBuilderFirstLeft2Rigth().build(dist);
		//SentenceAlignment align = new AlignmentBuilderBestMatch().build(dist);
		SentenceAlignment align = dist;
		//dist.dump(System.err);
	
		/***
		 * Call sentence level metrics using align[] for word metrics
		 */
		
		if(DUMP)strace.println("<senresults>");
		
		
		
		for(WeightedSentenceMetric iwm : sm) {
			SimilarityResult res  =iwm.similarity(proposedSentence, referenceSentence, align,strace);
			double prec= res.getPrec();
			double rec=res.getRec();
			
			tres.add(iwm.getName(),iwm.getWeight(), prec, rec);
			
		}
		if(DUMP)strace.println("</senresults>");
		
		if(DUMP) {
			tres.dump(strace);

		strace.println("</presults>");
		}
		return tres;
}
	
	
	
	/*
	 * 
	 */
	public static void main(String[] args) throws Exception {
		
	// Read arguments
		try { 
		com.martiansoftware.jsap.FlaggedOption mopt =new com.martiansoftware.jsap.FlaggedOption( "hypothesis", JSAP.STRING_PARSER, JSAP.NO_DEFAULT, JSAP.REQUIRED, 'h',  JSAP.NO_LONGFLAG, "Proposed-hypotesis translation files" );
		mopt.setList(true);
		mopt.setListSeparator(',');
		com.martiansoftware.jsap.FlaggedOption topt =new com.martiansoftware.jsap.FlaggedOption( "references", JSAP.STRING_PARSER, JSAP.NO_DEFAULT, JSAP.REQUIRED, 'r',  JSAP.NO_LONGFLAG, "Reference Translations files" );
		topt.setList(true);
		topt.setListSeparator(',');
		
		final com.martiansoftware.jsap.SimpleJSAP jsap = new com.martiansoftware.jsap.SimpleJSAP( MTsimilarity.class.getName(), 
				"VERTa metric  ",
				new com.martiansoftware.jsap.Parameter[] {
						new com.martiansoftware.jsap.UnflaggedOption( "conf", JSAP.STRING_PARSER, JSAP.NO_DEFAULT, JSAP.REQUIRED, JSAP.NOT_GREEDY, "metric confic file" ),
						new com.martiansoftware.jsap.UnflaggedOption( "exp", JSAP.STRING_PARSER, JSAP.NO_DEFAULT, JSAP.REQUIRED, JSAP.NOT_GREEDY, "experiment name" ),
						new com.martiansoftware.jsap.UnflaggedOption( "wmttag", JSAP.STRING_PARSER, "newstest2012\tcs-en", JSAP.NOT_REQUIRED, JSAP.NOT_GREEDY, "wmt tag" ),
						topt,
						mopt,
						new com.martiansoftware.jsap.Switch("xml", 'x', "xml", "Generate xml trace files" ),
						new com.martiansoftware.jsap.Switch("punc", 'p', "punc", "Filter punctuation" ),
						new com.martiansoftware.jsap.Switch("top", 't', "top", "Include TOP dependencies" ),
						new com.martiansoftware.jsap.Switch("old", 'o', "old", "try running OLD triples module" ),
						new com.martiansoftware.jsap.Switch("wmt", 'w', "wmt", "WMT output" )
					}		
				);
		
		final com.martiansoftware.jsap.JSAPResult jsapResult = jsap.parse( args );
		if ( jsap.messagePrinted() ) return;
		
		
        Boolean wmt = jsapResult.getBoolean( "wmt",false );
		String metricConfigFile = jsapResult.getString( "conf" );
		String hypFilenames[] = jsapResult.getStringArray("hypothesis");
		String  experimentName=jsapResult.getString( "exp" );
		String refFilenames[] = jsapResult.getStringArray("references");
		String wmttag = jsapResult.getString("wmttag");
		DUMP=jsapResult.getBoolean("xml", false);
		System.err.println("XML trace" + ( DUMP ? "Activated" : "Deactivated"));
		if(DUMP) {	
			copyxsl("/xsl/"+TOPXSL+".xsl", BASEFOLDER+"/"+TOPXSL+".xsl");
			copyxsl("/xsl/conf.xsl", BASEFOLDER+"/conf.xsl");
			copyxsl("/xsl/match.xsl", BASEFOLDER+"/match.xsl");
		}
		long tStart=System.currentTimeMillis();
		
		//Hack to format numbers in Spanish format (some of my machine has locale set to English)
		if(! wmt ) {
			Locale local = new Locale("es", "ES");
			Locale.setDefault(local);
		}
		//		Locale nl = Locale.getDefault();
		//NumberFormat nf = NumberFormat.getInstance(nl); 
		NumberFormat nf = new DecimalFormat("#,###,##0.0000000");
		
		int nSystem=0;
		
		SentenceSimilarityTripleOverlapping.USE_OLD=jsapResult.getBoolean("old", false);
		SentenceSimilarityTripleOverlapping.FILTER_TOP=! jsapResult.getBoolean("top", false);
		
		MTsimilarity mt = new MTsimilarity(metricConfigFile);
     	mt.setFilter(jsapResult.getBoolean("punc", false));
		
     	PrintStream precrec = wmt ? new PrintStream("VERTa.segment_level."+wmttag+".txt","UTF-8") :  null;
		

		// foreach hyp file (e.g. system)
		for(String hypFilename: hypFilenames) {
		++nSystem;
		
		//DUMP configuration
		
		PrintStream trace=null;
		if(!wmt) precrec = new PrintStream(BASEFOLDER+experimentName+"_"+nSystem+"_"+precrecallfile,"UTF-8");
		
	if(DUMP) {	
		
		trace = new PrintStream(BASEFOLDER+"trace"+experimentName+"_"+nSystem+".xml");
		
		//@TODO change the index page
		trace.println(XMLFormater.header());
		trace.println("<?xml-stylesheet type=\"text/xsl\" href=\"conf.xsl\" ?>");
		trace.println("<exp><name>"+XMLFormater.encodeXMLString(experimentName)+"</name>");
		Date d= new Date(System.currentTimeMillis());
	    trace.println("<date>"+XMLFormater.encodeXMLString(d.toLocaleString())+"</date>");
		trace.println("<conf>");
		trace.println("<hypotesis filename=\""+XMLFormater.encodeXMLString(hypFilename)+"\"/>");
		for(String refFilename:refFilenames) {
	 	trace.println("<reference filename=\""+XMLFormater.encodeXMLString(refFilename)+"\"/>");
		}
		trace.println("<metricconf filename=\""+XMLFormater.encodeXMLString(metricConfigFile)+"\">");
	}	
		// @TODO we should generalize to Sentence Metric as NGram is not Word Metric
		// load similarityFunction class by name
		// create a WordSimilarity function
		//WordMetric wm = new WordMetric(metricConfigFile);
		//wm.dump(trace);
	
		
	if(DUMP) {
		mt.dump(trace);
		trace.println("</metricconf>");
		trace.println("</conf>");
		//trace.println("</exp>");
		// END DUMP CONF
	}	
		
	
	CONLLformat fmt = new CONLLformat("conf/conll08.fmt");
		
		//TEST reading just one sentence
	BufferedReader proposedFile = new BufferedReader(new FileReader(hypFilename));
	BufferedReader referenceFiles [] = new BufferedReader[refFilenames.length];
		int j=0;
		for(String refFilename:refFilenames) {
		   referenceFiles[j++] = new BufferedReader(new FileReader(refFilename));
		}
		
		
		int nseg=1;
		// read sentences CONLL format (Establish the possible features names WORD LEMA ...)
		Segment proposedSeg;
		Sentence  proposedSentence;
		try {
			proposedSeg = ReaderCONLL.readSegment(proposedFile,fmt); 
			proposedSentence = proposedSeg.toSentence();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			proposedSentence =null;
			proposedSeg=null;
		}
		
		
		
		int nsen=1;
		//for every proposed sentence
		while(proposedSentence!=null) {
		
		//proposedSentence.dump(System.err);
		PrintStream gtrace=null;
		PrintStream strace=null;
		
		if(DUMP) {
			gtrace= new PrintStream(BASEFOLDER+"trace"+experimentName+"_"+nSystem+"_s"+nseg+"."+nsen+".xml");		
			printHeader(gtrace,experimentName,TOPXSL);
			mt.dump(gtrace);
			printXmlSentence(proposedSentence, "hyp",gtrace);
		}
		
		MetricResult MAXRes = null;

		
		int nref=0;
		/** 
		 * for every reference
		 * we should create a different file dump??
		 */
		for(BufferedReader referenceFile: referenceFiles) {
		
		Segment referenceSeg=null;
		Sentence  referenceSentence =null;
		try {
			referenceSeg = ReaderCONLL.readSegment(referenceFile,fmt); referenceSentence = referenceSeg.toSentence();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		
	    // it should be always a "sentence" per segment
	    if(referenceSentence !=null) {
			//referenceSeg.dump(System.out);	
			//referenceSentence.dump(System.err);
			
	    	if(DUMP) {
	    		 
	    		 if(strace!= null){ 
	    			 strace.println("</exp>");
	  		         strace.close();
	  		       }
	    		    String sname = "trace"+experimentName+"_"+nSystem+"_ref"+nref+"_s"+nseg+"."+nsen+".xml";
	    		 	gtrace.println("<reflink link=\""+sname+"\"/>");
	    			strace= new PrintStream(BASEFOLDER+sname);		
	    			printXMLHeader(experimentName, mt, proposedSentence,
							strace, referenceSentence,TOPXSL);
	    		
	    	}
          nref++;
		 
		// Read the metric configuration: featureValue SimiliarityFunction weigh
	     MetricResult res = mt.similarity(referenceSentence, proposedSentence, strace);
		
	     if(DUMP) { res.dump(gtrace);}
	    	 
		// get the better F1
	    if(MAXRes==null || res.getWF1()>MAXRes.getWF1()) {
	    	MAXRes = res;
	    }
	    
	    
	   // res.textdump(precrec, nf);
		 nsen++;
	    } // refsent1!=null
	    //nseg++;
		
		}// for references file
		
		// final result
		if(DUMP) {
		trace.println("<res link=\""+"trace"+experimentName+"_"+nSystem+"_s"+nseg+"."+(nsen-1)+".xml"+"\"><f>"+MAXRes.getWF1()+"</f></res>");
		gtrace.println("<results>");
		gtrace.println("<f>"+MAXRes.getWF1()+"</f>");
		gtrace.println("<prec>"+MAXRes.getPrec()+"</prec>");
		gtrace.println("<rec>"+MAXRes.getRec()+"</rec>");
		gtrace.println("</results>");
		}
		// output the text plain file prec/ rec
		//precrec.println(nf.format(MAXfinalprec)+"\t"+nf.format(MAXfinalrec)+"\t"+nf.format(2 * MAXfinalprec * MAXfinalrec / (MAXfinalprec + MAXfinalrec)));
		//precrec.println(nf.format(MAXRes.getPrec())+"\t"+nf.format(MAXRes.getRec())+"\t"+nf.format(MAXRes.getF1()));
		
		if(wmt)
			
		//WMT
			MAXRes.wmtdump(hypFilename,precrec,  nf,nseg,wmttag);
		else
		  //	VERTA 
			MAXRes.textdump(precrec, nf);
		
		//
		// TODO
		//
		
		
		// next pair
		  try {
			proposedSeg = ReaderCONLL.readSegment(proposedFile,fmt);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 // referenceSeg= referenceFile.readSegment();
		  
		 // proposedSentence = proposedFile.read();
		 // referenceSentence = referenceFile.read(); 
		  
		  
		  
		   nseg++;
		   nsen=1;
		  
		   if(proposedSeg !=null) { 
			    if(DUMP) {gtrace.println("<next link=\"trace"+experimentName+"_"+nSystem+"_s"+nseg+".1.xml"+"\"/>");}
			   proposedSentence = proposedSeg.toSentence();
			 //  referenceSentence = referenceSeg.toSentence();
		   }
		   else proposedSentence =null;
		   
		   if(DUMP){ 
			   
			   gtrace.println("</exp>");
		       gtrace.close();
		   		strace.println("</exp>");
		   		strace.close();
		   }
		   
		   
		   //One segment
		   //if(nseg==8) System.exit(-1);
		   if(nseg % 10 ==0) System.err.println("Processed sys: "+nSystem+" seg:"+nseg+" ...  milsec: "+(System.currentTimeMillis()-tStart));
			
		  }
		// proposed sentence
	  
		if(DUMP) {
			mt.dumpStatistics(trace);
			trace.println("</exp>");
			trace.close();
		}
		
		if(!wmt) precrec.close();
	 }
		
		mt.counters.dump();
		System.err.println("TOTAL TIME milsec: "+(System.currentTimeMillis()-tStart));
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
	}

	public static void printXMLHeader(String experimentName,
			MTsimilarity mt,
			Sentence proposedSentence, PrintStream strace,
			Sentence referenceSentence, String topXSL) {
		printHeader(strace,experimentName,topXSL);
		mt.dump(strace);
		printXmlSentence(proposedSentence, "hyp",strace);
		printXmlSentence(referenceSentence, "ref",strace);
	}

	public static void printXmlSentence(Sentence proposedSentence, String tag,
			PrintStream gtrace) {
		gtrace.println("<"+tag+">");
		proposedSentence.dump(gtrace);
		gtrace.println("</"+tag+">");
	}
	
	
	
	
	
	
	
	
	//@TODO redu XML trace
	private void dumpStatistics(PrintStream trace) {
		//wm.dumpStatistics(trace);
		
	}

	public void dump(PrintStream strace) {
		//dump word metrics
		for(WordMetric iwm : wms) {
		    strace.println("<wordmetrics>");	
			iwm.dump(strace);
			strace.println("</wordmetrics>");
		}
		
		//dump sentence metrics
		for(WeightedSentenceMetric iwm : sm) {
			 strace.println("<senmetrics>");	
				iwm.dump(strace);
				strace.println("</senmetrics>");			
		}
	}

	public static int sumatori(int minsize, int maxsize, int size) {
		assert(maxsize>=size);
		int res=0;
        for(int i=minsize;i<=Math.min(size,maxsize) ;i++) res += (size - i + 1);
		return res;
	}


	private static void copyxsl(String sourceFilename, String outfilename) {
		try {
			
		BufferedReader in = new BufferedReader(new InputStreamReader(MTsimilarity.class.getResourceAsStream(sourceFilename))); // new FileReader(sourceFilename));
    	PrintStream outfile = new PrintStream(outfilename);
    	
    	String buff;
        while((buff=in.readLine())!=null) {
        	outfile.println(buff);
        }
        outfile.close();
		}
		catch(Exception e) {
			System.err.println("Error copy XSL file from "+sourceFilename+ " to "+outfilename);
			System.err.println("Check that file and destination folder exists");
			e.printStackTrace();
			System.exit(-1);
		}
	}
	/**
	 * 
	 */
	
	
	/**
	 * 
	 * dumps a table s1 X s2 
	 *  <simmatrix>
	 *     <tw w="WORD_T">
	 *      <s w="WORD_R"  bst="" bts""/>
	 *      ...
	 *     </tw>
	 * <simmatrix>
	 * 
	 *   bst: 1 iif bestmatch source target
	 *   bts: 2 iif bestmatch target source
	 *   
	 * @param out
	 * @param proposedSentence
	 * @param targetSentence
	 * @param wm
	 */
	public static void dumpAllDist(PrintStream out,final Sentence proposedSentence, final Sentence targetSentence, final WordMetric wm) {
		
		int ipw=0;
		DistanceMatrix d = new DistanceMatrix(proposedSentence,targetSentence);
		
		out.println("<simmatrix>");
		// mt dump
		wm.dump(out);
		for(Word pw:proposedSentence) {
			out.println("<tw w=\""+XMLFormater.encodeXMLString(pw.getFeature("WORD"))+"\">");
			int itw=0; 
			for(Word tw:targetSentence) {
			  double simu = wm.similarity(pw, tw,out,"p2t");
			  wm.reversed=true;
			  double simd = wm.similarity(tw,pw,out,"t2p");
			  wm.reversed=false;
			  //@TODO we should add bestMatch info in both ways
			  out.print("<s w=\""+XMLFormater.encodeXMLString(tw.getFeature("WORD"))+"\" st=\""+simu+"\" ts=\""+simd+"\" ");
			  // we should store which is the distance used
			  out.print(" bst='"+( (d.bestMatchH(ipw)==itw) ? "1" : "0" )+"'");
			  out.print(" bts='"+( (d.bestMatchV(itw)==ipw) ? "2" : "0" )+"'");
			  out.println("/>");
				 ++itw;
			}
			out.println("</tw>");
			++ipw;
		}
		out.println("</simmatrix>");
	}

	public static double totalngrams(int s, int size) {
		return  (size - s + 1);
	}
	
	
}
