package mt;

import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mt.TripleMatchPattern.OPERATOR;
/**
 * A pattern to match triples
 * 
 * @author jordi
 *
 */
public class Tpattern {
	private static final char PREFIX_SET = 'S';

	private static final char PREFIX_LABLE = 'L';

/// Pattern to read tpattern from text
  static Pattern mtriple=Pattern.compile("[(]([^, \t]+),([^, \t]+),([^, \t]+)[)][ \t]*:[ \t]*([0-9.,]+)");
  
  /// match operator for head
  final OPERATOR head;
  
  /// mod match operator
  final OPERATOR mod; 
  
  /// LAbel operator if null LabelSet apply
 final  OPERATOR label; 
  
  /// Labelset for source triple
  final  LabelSet labelSet;
  
  /// LabelSet for target triple
 final LabelSet targetLabelSet;
  
  /// weight of the pattern
  final double weight;
  
  Tpattern(OPERATOR head, OPERATOR label, OPERATOR mod, double weight) {
	  this.head=head; this.mod=mod; this.weight=weight;this.label=label; this.labelSet=null; this.targetLabelSet=null;}

  /**
   * parse Label set from text label
   * 
   * @param vlabel
   * @param buff
   * @param lset
   * @return
   * @throws IOException
   */
  public LabelSet readLabel(String vlabel, String buff, final HashMap<String,LabelSet> lset) throws IOException {
	  LabelSet res=null;
	  switch (vlabel.charAt(0)) {
		case PREFIX_SET:
			res=lset.get(vlabel.substring(1));
			break;
		case PREFIX_LABLE:			
			boolean found =false;
			for(LabelSet s : lset.values()){
			// find the group that contains the label
				if(s.labels.contains(vlabel.substring(1))) {
					//System.err.println(vlabel.substring(1)+" found in "+s.id+" w:"+s.w);
				 res= new LabelSet(""+s.w+" auto_"+s.id+" "+vlabel.substring(1));
				 found=true;
				 continue;
				}
			}
			if(!found){  //throw new IOException("format error reading on triple: "+vlabel+": in "+buff);
				res= new LabelSet("1.0 def "+vlabel.substring(1));
			}
			break;		
		default:
			throw new IOException("format error reading on triple prefix label must be L/S:"+vlabel+": in "+buff);
			
		}
	  return res;
  }
  
  public Tpattern(String buff, final HashMap<String,LabelSet> lset) throws IOException {
	Matcher x=mtriple.matcher(buff);
	if(x.matches()) {
	     
		
		OPERATOR plabel=null;
		String vlabel=x.group(1);
		try {			
		     plabel = OPERATOR.valueOf(vlabel);
		}
		catch(java.lang.IllegalArgumentException e) {
			//e.printStackTrace();
		
		}
		
		/**
		 *  not and operator
		 */
		if(plabel==null) {
			
			// That can be a Sxxx or Sxxx - Sxxx or Lxxx or Lxxx-Lxxx
			
			String vlabels[] = vlabel.split("-");
			
			
			labelSet = readLabel(vlabels[0], buff, lset);
			targetLabelSet = (vlabels.length>1 ? readLabel(vlabels[1], buff, lset) : null);
			label=null;
			
			
			
		}
		else{
			labelSet=null;
			targetLabelSet=null;
			label=plabel;
		}
		
		head= OPERATOR.valueOf(x.group(2));
		mod=OPERATOR.valueOf(x.group(3));
		weight =Double.parseDouble(x.group(4));
	} else {
		System.err.println("Error reading pattern for triples at not Label Set or operatir found in pattern:"+buff);
		throw new RuntimeException("Error reading pattern for triples at:"+buff);
	}
  }

  
  /**
   * using sets the _% will not work
   * _X -> _% (possible)
   * @param x
   * @param y
   * @param label_match
   * @return
   */
  private boolean  matchLabel(final Triples x,  final Triples y, boolean label_match) {
	// System.err.println("label "+label+" labelset "+labelSet+" "+x.label);
	 return  (
			  
			  // check group only for x
			  (label == null  &&   
					  (      
							  (labelSet==null || labelSet.contains(x.label) || checkLabel(labelSet,x.label)) 
							                  && 
							  (targetLabelSet==null || targetLabelSet.contains(y.label) || checkLabel(targetLabelSet,y.label))             
							  
				      )
			  )
					   ||
			  ((label !=null ) && (label_match || label==OPERATOR.O))
			 );
  }
  
  static public boolean checkLabel(LabelSet labelSet, String label) {
	 // System.err.println("CHECK"+labelSet+":"+label+" "+TriplesMatch.getSubLabel(label));
	  return labelSet.contains(label) ||  labelSet.contains(TriplesMatch.getSubLabel(label)+"_%");
  }
/**
 * label match => label are equal or match rules
 * 
 * @param x
 * @param y
 * @param label_match
 * @param source_match
 * @param target_match
 * @return
 */
  public boolean match(final Triples x, final Triples y, boolean label_match, boolean source_match, boolean  target_match){
	 return (
			
				(	 matchLabel(x,y,label_match))
			 && 
			 (head==OPERATOR.O || source_match) 
			 && 
			 (mod==OPERATOR.O || target_match)
			);
  }
  

  public void dump(PrintStream err) {
  	err.println(this.toString());
  }
  
  public String toString() {
	    StringBuffer err = new StringBuffer();
	  	err.append("(");
	  	if(label != null)  err.append(label.name());
	  	if(labelSet!=null) err.append(" "+labelSet.labels+" "+labelSet.id+" setw:"+labelSet.w);
	  	if(targetLabelSet!=null) err.append(" "+targetLabelSet.labels+" labelw:"+targetLabelSet.w);
	  	err.append(",");
	  	err.append(head==null ? "NULL" : head.name());
	  	err.append(",");
	  	err.append(mod==null ? "NULL" : mod.name());
	  	err.append(") : "+weight);
	  	return err.toString();
	  }
  
}