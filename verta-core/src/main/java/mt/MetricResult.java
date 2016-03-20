package mt;

import java.io.File;
import java.io.PrintStream;
import java.text.NumberFormat;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * representation of the whole result
 * @author jordi
 *
 */
public class MetricResult {
	public DistanceMatrix dist;
	private double prec;
	private double rec;
	private Vector<Double> v_w;
	private double sumpes;
	private Vector<Double> v_pres;
	private Vector<Double> v_rec;
	private Vector<String> v_name;
    
	public void init(final double prec, final double rec) {
		 assert(!Double.isNaN(prec));
		 assert(!Double.isNaN(rec));
		 assert(!Double.isInfinite(prec));
		 assert(!Double.isInfinite(rec));
		 sumpes=0;
		 this.prec=prec;
		 this.rec=rec;
		 v_w = new Vector<Double>();
		 v_pres= new Vector<Double>();
		 v_rec= new Vector<Double>();
		 v_name = new Vector<String>();
	}
	public MetricResult(double prec, double rec){ init(prec,rec);}
	
	public MetricResult() {
		init(0,0);
	}

	public MetricResult(DistanceMatrix dist) {
		this.dist=dist;
		init(0,0);
	}
	//public void setPrec(double prec){this.prec=prec;}
	//public void setRec(double rec){this.rec=rec;}
	public double getPrec(){return (prec>0 && sumpes >0) ? prec / sumpes : 0;}
	public double getRec(){return   (rec>0 && sumpes >0) ? rec / sumpes : 0;}	
	
	public double getPrec(int i){return  v_pres.get(i); }
	public double getRec(int i) {return v_rec.get(i);}	
	
	
	public double getWF1() { return  getSF1();}
	public double getF1(int i) { return (getPrec(i)>0 && getRec(i)> 0)  ? (( 2 * getPrec(i) * getRec(i) ) /   (getPrec(i) + getRec(i))) : 0;}
	
	private double getSF1() { 
		double sumf1=0;
		double sumw=0;
		for(int i=0;i<v_pres.size();++i) {
			sumf1 +=  v_w.get(i) * getF1(i);
			sumw += v_w.get(i);
		}
		return (sumf1>0  ? (sumf1 / sumw) : 0.0);
	}
	
/*
 *  SUM(W * F1) =??  F1(SUM(P * w) + SUM (F * w))
 */
	public void add(String name, double weight, double p_prec, double p_rec) {
		 assert(!Double.isNaN(new Double(p_prec)));
		 assert(!Double.isNaN(new Double(p_rec)));
		 assert(!Double.isNaN(new Double(weight)));
		 assert(!Double.isInfinite(p_prec));
		 assert(!Double.isInfinite(p_rec));
		 assert(weight>=0);
		 
		prec += p_prec * weight;
		rec += p_rec * weight;
		sumpes += weight;
		
		v_pres.add(p_prec);
		v_rec.add(p_rec);
		v_w.add(weight);
		
		v_name.add(name);
	}
	
	public String toString() {
		return getSF1()+"\t"+getPrec()+"\t"+getRec();
	}
	
	public void dump(PrintStream strace) {
		//strace.println("<metrics>");
		for(int i=0;i<v_pres.size();++i) {		
		String name = v_name.get(i);
		double prec = getPrec(i);
		double rec= getRec(i);
		double f1 = getF1(i);
		double wprec = v_w.get(i) * prec;
		double wrec = v_w.get(i) * rec;
		double wf1 = v_w.get(i) * f1;
		
		strace.println("<wmetric name='"+name+"'>");
		strace.println("<f>"+f1+"</f>");
		strace.println("<p>"+prec+"</p>");
		strace.println("<r>"+rec+"</r>");
		strace.println("<wf>"+wf1+"</wf>");
		strace.println("<wp>"+wprec+"</wp>");
		strace.println("<wr>"+wrec+"</wr>");
		strace.println("</wmetric>");
		}
		
		strace.println("<f>"+getSF1()+"</f>");
		strace.println("<prec>"+getPrec()+"</prec>");
		strace.println("<rec>"+getRec()+"</rec>");
		//strace.println("</metres>");
	}
	
	/**
	 * output format
	 * 
	 * F / P / R #MODULS MOD-NAME MOD-F1 MOD-PRE MOD-RECALL WMOD-F1 WMOD-PREC WMOD-REC .... 
	 * @param strace
	 * @param nf
	 */
	public void textdump(PrintStream strace, NumberFormat nf) {
		strace.print(nf.format(getSF1()));
		strace.print("\t"+nf.format(getPrec()));
		strace.print("\t"+nf.format(getRec()));
		strace.print("\t"+v_pres.size());
		for(int i=0;i<v_pres.size();++i) {		
		String name = v_name.get(i);
		double prec = getPrec(i);
		double f1 = getF1(i);
		double rec= getRec(i);
		double wprec = v_w.get(i) * prec;
		double wrec = v_w.get(i) * rec;
		double wf1 = v_w.get(i) * f1;
		strace.print("\t"+name);
		strace.print("\t"+nf.format(f1));
		strace.print("\t"+nf.format(prec));
		strace.print("\t"+nf.format(rec));
		strace.print("\t"+nf.format(wf1));
		strace.print("\t"+nf.format(wprec));
		strace.print("\t"+nf.format(wrec));
		}
		
		strace.println();
	}
	
	public static String getSysNameOLD(String hypFilename) {
		String ff = new File(hypFilename).getName().replace(".jtag", "");
		ff = ff.replace(".tag", "");
		ff = ff.replace("jtag","");
		//int s =ff.indexOf('.');
		int f = ff.lastIndexOf('.');
		return ff.substring(f+1);
	}
	
	public static String getSysName(String hypFilename) {
		return getSysName2( hypFilename, p );
	}
	
	public static String getSysName2(String hypFilename, Pattern lp[] ) {
		for(Pattern p: lp) { 
		Matcher m = p.matcher(hypFilename);
		if(m.find()) {
			return m.group(1);
		}
		}
		return null;
	}
	public void wmtdump(String hypFilename, PrintStream strace, NumberFormat nf, int nseg, String wmttag) {
//		String ff = new File(hypFilename).getName().replace(".jtag", "");
//		ff = ff.replace(".tag", "");
//		
//		ff = ff.replace("jtag","");
//		
//		int s =ff.indexOf('.');
//		int f = ff.lastIndexOf('.');
		//int p =ff.lastIndexOf(".");
		//if(p>0) ff = ff.substring(p+1);
		//strace.println("VERTa\tcs-en\tnewstest2012\t"+ff+"\t"+nseg+"\t"+nf.format(getSF1()));
		
		//strace.println("VERTa\t"+wmttag.replace(".","\t")+"\t"+nseg+"\t"+nf.format(getSF1()));
		//strace.print("VERTa\t"+wmttag.replace('.', '\t')+"\t"+ff.substring(f+1)+"\t"+nseg+"\t"+nf.format(getSF1()));
		strace.print("VERTa\t"+wmttag.replace('.', '\t')+"\t"+getSysName(hypFilename)+"\t"+nseg+"\t"+nf.format(getSF1()));
		for(int i=0;i<v_pres.size();++i) {	
			strace.print("\t"+nf.format(getF1(i)));
		}
		strace.println();
	}
	public static String[] names ={
	
		"newstest2012.cs-en.cu-bojar.tag",
		"newstest2012.cs-en.jhu-heiro.tag",
		"newstest2012.cs-en.onlineA.tag",
		"newstest2012.cs-en.onlineB.tag",
		"newstest2012.cs-en.uedin-wmt12.tag",
		"newstest2012.cs-en.uk-dan-moses.tag",
		"newstest2012.de-en.KIT_Phrase-Based_SMT_System.tag",
		"newstest2012.de-en.LIMSI-Ncode-Constrained-Primary.tag",
		"newstest2012.de-en.QCRI--primary.tag",
		"newstest2012.de-en.RWTH_primary.tag",
		"newstest2012.de-en.UG-WMT12.tag",
		"newstest2012.de-en.dfki-berlin.tag",
		"newstest2012.de-en.jhu-hiero.tag",
		"newstest2012.de-en.onlineA.tag",
		"newstest2012.de-en.onlineB.tag",
		"newstest2012.de-en.onlineC.tag",
		"newstest2012.de-en.onlineD.tag",
		"newstest2012.de-en.onlineE.tag",
		"newstest2012.de-en.onlineF.tag",
		"newstest2012.de-en.quaero_primary.tag",
		"newstest2012.de-en.uedin-wmt12.tag",
		"newstest2012.de-en.uk-dan-moses.tag",
		"newstest2012.es-en.GTH-UPM.tag",
		"newstest2012.es-en.QCRI--primary.tag",
		"newstest2012.es-en.UPC.tag",
		"newstest2012.es-en.jhu-hiero.tag",
		"newstest2012.es-en.onlineA.tag",
		"newstest2012.es-en.onlineB.tag",
		"newstest2012.es-en.onlineC.tag",
		"newstest2012.es-en.onlineD.tag",
		"newstest2012.es-en.onlineE.tag",
		"newstest2012.es-en.onlineF.tag",
		"newstest2012.es-en.uedin-wmt12.tag",
		"newstest2012.es-en.uk-dan-moses.tag",
		"newstest2012.fr-en.KIT_Phrase-Based_SMT_System.tag",
		"newstest2012.fr-en.LIMSI-Ncode-Constrained-Primary.tag",
		"newstest2012.fr-en.LIUM_FR-EN_12.tag",
		"newstest2012.fr-en.RWTH-PBT-constrained.tag",
		"newstest2012.fr-en.SFU_-_Kriya.tag",
		"newstest2012.fr-en.cmu-avenue.tag",
		"newstest2012.fr-en.jhu-hiero.tag",
		"newstest2012.fr-en.onlineA.tag",
		"newstest2012.fr-en.onlineB.tag",
		"newstest2012.fr-en.onlineC.tag",
		"newstest2012.fr-en.onlineD.tag",
		"newstest2012.fr-en.onlineE.tag",
		"newstest2012.fr-en.onlineF.tag",
		"newstest2012.fr-en.uedin-wmt12.tag",
		"newstest2012.fr-en.uk-dan-moses.tag",

		
		
		"newstest2013.cs-en.FDA.2855.tag",
		"newstest2013.cs-en.JHU.2903.tag",
		"newstest2013.cs-en.MES:.2796.tag",
		"newstest2013.cs-en.Shef-wproa.2679.tag",
		"newstest2013.cs-en.cu-tamchyna.2875.tag",
		"newstest2013.cs-en.cu-zeman.2710.tag",
		"newstest2013.cs-en.online-A.tag",
		"newstest2013.cs-en.online-B.tag",
		"newstest2013.cs-en.online-G.tag",
		"newstest2013.cs-en.uedin-heafield-unconstrained.2596.tag",
		"newstest2013.cs-en.uedin-syntax.2603.tag",
		"newstest2013.cs-en.uedin-wmt13.2834.tag",
		"newstest2013.de-en.CNGL_DCU.2703.tag",
		"newstest2013.de-en.FDA.2867.tag",
		"newstest2013.de-en.JHU.2887.tag",
		"newstest2013.de-en.KIT_primary.2653.tag",
		"newstest2013.de-en.LIMSI-Ncode-SOUL-primary.2591.tag",
		"newstest2013.de-en.MES-Szeged-reorder-split-primary.2682.tag",
		"newstest2013.de-en.MES:.2916.tag",
		"newstest2013.de-en.QUAERO_primary.2601.tag",
		"newstest2013.de-en.RWTH-Jane-primary.2615.tag",
		"newstest2013.de-en.Shef-wproa.2761.tag",
		"newstest2013.de-en.TUBITAK.2613.tag",
		"newstest2013.de-en.cu-zeman.2720.tag",
		"newstest2013.de-en.desrt.2704.tag",
		"newstest2013.de-en.online-A.tag",
		"newstest2013.de-en.online-B.tag",
		"newstest2013.de-en.online-C.tag",
		"newstest2013.de-en.online-G.tag",
		"newstest2013.de-en.rbmt-1.tag",
		"newstest2013.de-en.rbmt-3.tag",
		"newstest2013.de-en.rbmt-4.tag",
		"newstest2013.de-en.uedin-syntax.2605.tag",
		"newstest2013.de-en.uedin-wmt13.2636.tag",
		"newstest2013.de-en.umd.2922.tag",
		"newstest2013.es-en.DCU-Prompsit.2880.tag",
		"newstest2013.es-en.FDA.2901.tag",
		"newstest2013.es-en.JHU.2776.tag",
		"newstest2013.es-en.LIMSI-Ncode-SOUL-primary.2598.tag",
		"newstest2013.es-en.MES:.2801.tag",
		"newstest2013.es-en.Shef-wproa.2779.tag",
		"newstest2013.es-en.cu-zeman.2734.tag",
		"newstest2013.es-en.dcu-prompsit-pbsmt.2692.tag",
		"newstest2013.es-en.online-A.tag",
		"newstest2013.es-en.online-B.tag",
		"newstest2013.es-en.online-C.tag",
		"newstest2013.es-en.online-G.tag",
		"newstest2013.es-en.rbmt-1.tag",
		"newstest2013.es-en.rbmt-3.tag",
		"newstest2013.es-en.rbmt-4.tag",
		"newstest2013.es-en.uedin-heafield-unconstrained.2612.tag",
		"newstest2013.es-en.uedin-wmt13.2877.tag",
		"newstest2013.fr-en.CMU_Tree-to-Tree.2893.tag",
		"newstest2013.fr-en.DCU__primary.2828.tag",
		"newstest2013.fr-en.FDA.2898.tag",
		"newstest2013.fr-en.JHU.2684.tag",
		"newstest2013.fr-en.KIT_primary.2658.tag",
		"newstest2013.fr-en.LIMSI-Ncode-SOUL-primary.2585.tag",
		"newstest2013.fr-en.MES-SimplifiedFrench-primary.2662.tag",
		"newstest2013.fr-en.RWTH_primary.2595.tag",
		"newstest2013.fr-en.Shef-wproa.2780.tag",
		"newstest2013.fr-en.cu-zeman.2738.tag",
		"newstest2013.fr-en.online-A.tag",
		"newstest2013.fr-en.online-B.tag",
		"newstest2013.fr-en.online-C.tag",
		"newstest2013.fr-en.online-G.tag",
		"newstest2013.fr-en.rbmt-1.tag",
		"newstest2013.fr-en.rbmt-3.tag",
		"newstest2013.fr-en.rbmt-4.tag",
		"newstest2013.fr-en.uedin-heafield-unconstrained.2755.tag",
		"newstest2013.fr-en.uedin-wmt13.2838.tag",
		"newstest2013.ru-en.CMU-primary.2908.tag",
		"newstest2013.ru-en.FDA.2852.tag",
		"newstest2013.ru-en.JHU.2677.tag",
		"newstest2013.ru-en.LIA.2650.tag",
		"newstest2013.ru-en.MES-QCRI:.2843.tag",
		"newstest2013.ru-en.OmniFluent_Translate_Russian-to-English_constrained.2668.tag",
		"newstest2013.ru-en.OmniFluent_Translate_Russian-to-English_unconstrained.2671.tag",
		"newstest2013.ru-en.PROMT.2754.tag",
		"newstest2013.ru-en.QCRI-MES.2856.tag",
		"newstest2013.ru-en.UCAM_primary_multifrontend.2695.tag",
		"newstest2013.ru-en.balagur.2606.tag",
		"newstest2013.ru-en.commercial-3.tag",
		"newstest2013.ru-en.cu-karel.2896.tag",
		"newstest2013.ru-en.cu-zeman.2742.tag",
		"newstest2013.ru-en.online-A.tag",
		"newstest2013.ru-en.online-B.tag",
		"newstest2013.ru-en.online-G.tag",
		"newstest2013.ru-en.rbmt-1.tag",
		"newstest2013.ru-en.rbmt-3.tag",
		"newstest2013.ru-en.rbmt-4.tag",
		"newstest2013.ru-en.uedin-syntax.2614.tag",
		"newstest2013.ru-en.uedin-wmt13.2851.tag",
		"newstest2013.ru-en.umd.2892.tag",
		
		
	
		"newstest2014.AFRL-Post-edited.3431.ru-en.tag",
		"newstest2014.AFRL.3349.ru-en.tag",
		"newstest2014.AFRL.3456.hi-en.tag",
		"newstest2014.CMU.3461.de-en.tag",
		"newstest2014.CMU.3510.hi-en.tag",
		"newstest2014.DCU-HiEn.3564.hi-en.tag",
		"newstest2014.DCU-ICTCAS-Tsinghua-L.3444.de-en.tag",
		"newstest2014.IIIT-Hyderabad.3257.hi-en.tag",
		"newstest2014.LIMSI-KIT-Submission.3359.de-en.tag",
		"newstest2014.PROMT-Hybrid.3084.ru-en.tag",
		"newstest2014.PROMT-Rule-based.3085.ru-en.tag",
		"newstest2014.RWTH-primary.3266.de-en.tag",
		"newstest2014.Stanford-University.3496.fr-en.tag",
		"newstest2014.cu-moses.3383.cs-en.tag",
		"newstest2014.eubridge.3569.de-en.tag",
		"newstest2014.iitb-ranked-ppl.3173.hi-en.tag",
		"newstest2014.kaznu1.3549.ru-en.tag",
		"newstest2014.kit.3109.de-en.tag",
		"newstest2014.kit.3112.fr-en.tag",
		"newstest2014.onlineA.0.cs-en.tag",
		"newstest2014.onlineA.0.de-en.tag",
		"newstest2014.onlineA.0.fr-en.tag",
		"newstest2014.onlineA.0.hi-en.tag",
		"newstest2014.onlineA.0.ru-en.tag",
		"newstest2014.onlineB.0.cs-en.tag",
		"newstest2014.onlineB.0.de-en.tag",
		"newstest2014.onlineB.0.fr-en.tag",
		"newstest2014.onlineB.0.hi-en.tag",
		"newstest2014.onlineB.0.ru-en.tag",
		"newstest2014.onlineC.0.de-en.tag",
		"newstest2014.onlineC.0.fr-en.tag",
		"newstest2014.onlineG.0.ru-en.tag",
		"newstest2014.rbmt1.0.de-en.tag",
		"newstest2014.rbmt1.0.fr-en.tag",
		"newstest2014.rbmt1.0.ru-en.tag",
		"newstest2014.rbmt4.0.de-en.tag",
		"newstest2014.rbmt4.0.fr-en.tag",
		"newstest2014.rbmt4.0.ru-en.tag",
		"newstest2014.shad-wmt14.3464.ru-en.tag",
		"newstest2014.uedin-syntax.3035.de-en.tag",
		"newstest2014.uedin-syntax.3144.hi-en.tag",
		"newstest2014.uedin-syntax.3166.ru-en.tag",
		"newstest2014.uedin-syntax.3289.cs-en.tag",
		"newstest2014.uedin-wmt14.3024.fr-en.tag",
		"newstest2014.uedin-wmt14.3025.de-en.tag",
		"newstest2014.uedin-wmt14.3170.cs-en.tag",
		"newstest2014.uedin-wmt14.3364.ru-en.tag",
		"newstest2014.uedin-wmt14.3422.hi-en.tag",
		
		
	};
	
	public static Pattern[] p = { Pattern.compile("newstest20..\\.(.*)\\...-..\\.tag", Pattern.DOTALL), Pattern.compile("newstest20..\\...-..\\.(.*)\\.tag", Pattern.DOTALL) };
	public static void main(String[] args) {
		
		Pattern[] p = { Pattern.compile("newstest20..\\.(.*)\\...-..\\.tag", Pattern.DOTALL), Pattern.compile("newstest20..\\...-..\\.(.*)\\.tag", Pattern.DOTALL) };
		for(String file:names) {
			System.err.println(file+" -> "+getSysName2(file,p));
		}
	}
}