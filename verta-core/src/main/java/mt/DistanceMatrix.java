package mt;

import java.io.PrintStream;
/**
 * 
 *  A direct implementation  of a sentenceAligment using the whole matrix
 *  
 * @author jordi
 *
 */
public class DistanceMatrix  implements SentenceAlignment {
  private static final String NO_PROVENANCE = "NULL";
/// distance from source to target words
  private double[][] dist;
  private Object[][] prov;
  /// source Sentence
  Sentence source;
  
  /// target Sentence
  Sentence target;
	  
  final int source_size;
  final int target_size;
  
  // for testing pouposes
  public DistanceMatrix(double[][] m) {
	  dist=m;
	  this.source_size=m.length;
	  this.target_size=m[0].length;
	  prov=new Object[source_size][target_size];
  }
  public DistanceMatrix(int source_size, int target_size) {
	  dist = new double[source_size][target_size];
	  prov = new Object[source_size][target_size];
	  this.source_size=source_size;
	  this.target_size=target_size;
  }
  
  public DistanceMatrix(Sentence source, Sentence target) {
	  dist = new double[source.size()][target.size()];
	  this.source_size=source.size();
	  this.target_size=target.size();
	  prov = new Object[source_size][target_size];
	  this.source=source;
	  this.target=target;
  }
         
  private void setDistance(int n1,int n2, double d, Object provenence) {
	  //if(d>0) dist[n1][n2]=dist[n1][n2]+d; 
	  dist[n1][n2]=d;
	  prov[n1][n2]=provenence;
  }
  
  public void setDistance(boolean reversed, int n1,int n2, double d, Object o) {
	 if(reversed) setDistance(n2,n1,d,o); else  setDistance(n1,n2,d,o);
  }
  
  private double getDistance(int n1,int n2) {
	  return dist[n1][n2];
  }
  
  private String getProvenance(int n1,int n2) {
	  return prov[n1][n2]==null ? NO_PROVENANCE : prov[n1][n2].toString();
  }
  public double getDistance(boolean reversed, int n1, int n2){
	 return  reversed ? getDistance(n2,n1) :getDistance(n1,n2);
  }
  public String getProvenance(boolean reversed, int n1, int n2){
		 return  reversed ? getProvenance(n2,n1) :getProvenance(n1,n2);
	  }
  /**
   * this is the default alignment function
   * @TODO it should take into consideration that the node has not been used before
   */
//  private boolean aligned(int n1,int n2) {
//	  return getDistance(n1,n2)>0;
//  }
  
  /**
   * Default strategy is free alignment
   */
  public boolean isAligned(boolean reversed, int n1,int n2) {
	  return reversed ? getDistance(n2,n1)>0 : getDistance(n1,n2)>0;
  }
  
  public void dump(PrintStream s) {
	  
	  
	  s.println("Distances S2T:");
	  int wid=0;
	  for(Word w:source) {
		  s.print(w.getFeature("WORD")+"_"+wid+": ");
		  int tid =0;
		  for(Word t:target) {
			  s.print(" ");
			  if(isAligned(false,wid,tid)) {
			    s.print(t.getFeature("WORD")+"_"+tid);
			   }
			  s.print(dist[wid][tid]);
			  ++tid;
			  }
		  s.println();
		  ++wid;
		  }
	  
	  s.println("Distances T2S:");
	   wid=0;
	  for(Word w:target) {
		  s.print(w.getFeature("WORD")+"_"+wid+": ");
		  int tid =0;
		  for(Word t:source) {
			  if(isAligned(true,wid,tid)) {
			    s.print(" "+t.getFeature("WORD")+"_"+tid);
			   }
			  ++tid;
			  }
		  s.println();
		  ++wid;
		  }
	  }
  


 public int bestMatchH(int c){
	double max=-1;
	int maxp=-1;
	for(int i=0;i<dist[c].length;++i) 
		if(dist[c][i]>max){ max=dist[c][i]; maxp=i;}
   return maxp;
}

 public int  bestMatchV(int c){
	double max=-1;
	int maxp=-1;
	for(int i=0;i<dist.length;++i) 
		if(dist[i][c]>max){ max=dist[i][c]; maxp=i;}
   return maxp;
}

public int getRowSize(boolean reversed) {
	return reversed ? target_size : source_size;
}
public int getColumnSize(boolean reversed) {
	return reversed ?  source_size : target_size;
}

@Override
public int[] aligned(boolean reversed) {
	throw new RuntimeException("NOT IMPLEMENTED!");
	// TODO Auto-generated method stub
	
}

@Override
public void setAligned(boolean reversed, int i, int j,String provenence) {
	// TODO Auto-generated method stub
	throw new RuntimeException("NOT IMPLEMENTED!");
}


@Override
public String provenence(boolean reversed, int i, int j) {
	return reversed ? prov[i][j].toString() : prov[j][i].toString();
}

}
