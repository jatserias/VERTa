package mt;

import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * 
 * A class to do a better aligment 
 * 
 * @author jordi
 *
 */
public class AlignmentBuilderBestMatch implements AlignmentBuilder{
    class Align implements Comparator<Align>
    {
    	public Align(int i, int j, double dd, Object prov) {
			source=i;
			target=j;
			w=dd;
			this.prov=prov;
		}
    	
    	/// word position in the source sentence
		int source;
		
		// word position in the target sentence
    	int target;
    	
    	/// weigth
    	double w;
    	
    	/// provenance
    	public Object prov;
    	
		@Override
		public int compare(Align arg0, Align arg1) {
			if(arg0.w == arg1.w) return 0;
			return arg0.w > arg1.w ? -1 : 1;
		}
		
		public String toString(){return ""+source+" x "+target+":"+w;}
		
    }

    
	//@Override
	public void build(SentenceAlignment res, final DistanceMatrix d) {
		build(true,res,d);
		build(false,res,d);
	}
	@Override
	public void build(boolean reversed,SentenceAlignment res, final DistanceMatrix d) {
		int source_size =  d.getRowSize(reversed);
		int target_size =  d.getColumnSize(reversed);
		
		PriorityQueue<Align> sorted = new PriorityQueue<Align>(10, new Comparator<Align>() {
		public int compare(Align arg0, Align arg1) {
			if(arg0.w == arg1.w) { 
				int d0 =Math.abs(arg0.source-arg0.target);
				int d1 =Math.abs(arg1.source-arg1.target);
				if(d0==d1) return arg0.source>arg1.source ? 1 : -1;
				else return d0>d1 ? 1 : -1;
			}
			return arg0.w < arg1.w ? 1 : -1;
		}});
		
		
		for(int i=0;i<source_size;++i)
			for(int j=0;j<target_size;++j) {
				 double dd =d.getDistance(reversed, i, j);
				 if(dd>0) sorted.add(new Align(i,j,dd,d.getProvenance(reversed, i, j)));
				 }
		//
		boolean taken[]= new boolean[target_size];
		for(int i=0;i<taken.length;i++) taken[i]=false;
		boolean staken[]= new boolean[source_size];
		for(int i=0;i<staken.length;i++) staken[i]=false;
	
		//SentenceAlignment res = new AlignmentImpl(d.source.size(), d.target.size());
		while(!sorted.isEmpty()) {
			Align a =sorted.poll();
			//if(reversed)System.err.println(a+" "+d.target.get(a.source).getFeature("WORD")+"-"+d.source.get(a.target).getFeature("WORD"));
			//else System.err.println(a+" "+d.source.get(a.source).getFeature("WORD")+"-"+d.target.get(a.target).getFeature("WORD"));
			if(!staken[a.source] &&  !taken[a.target]) {
				res.setAligned(reversed, a.source, a.target, a.prov.toString());
				staken[a.source]=true;
				taken[a.target]=true;
				//System.err.println("ALIGN TAKEN");
			}else {
				//System.err.println("ALIGN SKIP");
			}
		}
		
	}

}
