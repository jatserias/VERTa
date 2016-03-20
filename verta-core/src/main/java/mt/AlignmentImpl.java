package mt;

import java.io.PrintStream;

/**
 * 
 * This keeps an aligment in both directions
 * 
 * @author jordi
 *
 */
public class AlignmentImpl implements SentenceAlignment {

	int[] alignS2T;
	int[] alignT2S;
	Object[] provS2T;
	Object[] provT2S;
	       
	public AlignmentImpl(int rowsize, int colsize) {
		 alignS2T =  new int[rowsize];
		 alignT2S =  new int[colsize];
		 provS2T =  new Object[rowsize];
		 provT2S =  new Object[colsize];;
		 for(int i=0;i<rowsize;++i) {alignS2T[i]=-1;}
		 for(int i=0;i<colsize;++i) {alignT2S[i]=-1;}
	}
	
	
	
	@Override
	public void setAligned(boolean reversed, int i, int j, String provenence) {
		if(reversed) {alignT2S[i]=j; provT2S[i]=provenence;}
		else {alignS2T[i]=j;provS2T[i]=provenence;}
	}
	

	
	
	@Override
	public boolean isAligned(boolean reversed, int i, int j) {		
		return reversed ? alignT2S[j]==i : alignS2T[j]==i;
	}
	
	@Override
	public int[] aligned(boolean reversed) {
		return reversed ? alignT2S : alignS2T;
	}
	
	public void dump(PrintStream strace){
			strace.print("<align>");
			strace.print("<st>"); 
			for(int i=0;i<alignS2T.length;++i) strace.println("<s  s=\""+i+"\" t=\""+alignS2T[i]+"\"/>");
			strace.print("</st>");
		
			strace.print("<ts>"); 
			for(int i=0;i<alignT2S.length;++i) strace.println("<s  s=\""+i+"\" t=\""+alignT2S[i]+"\"/>");
			strace.print("</ts>");
			strace.print("</align>");
	}


	public String toString() {
		StringBuffer strace= new StringBuffer();
		for(int i:alignS2T) strace.append("\t"+i);
		strace.append("\n");
	 
		for(int i:alignT2S) strace.append("\t"+i);
		strace.append("\n");
		return strace.toString();
	}

	@Override
	public String provenence(boolean reversed, int i, int j) {
		return reversed ?provT2S[i].toString() : provS2T[i].toString();
	}
}
