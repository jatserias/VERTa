package mt;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.StringReader;

public class CONLLformat {
    String formatFilename;
	String[]  featureNames;
	
//	public CONLLformat() throws Exception {
//		loadFormat("conf/conll08.fmt");
//	}
	
	public CONLLformat(String filename) throws Exception {
		loadFormat(filename);
	}

    //static private int offset=0;
    
	public void loadFormat(String formatFilename) throws Exception {
		try {
		BufferedReader ftin=null;
			try{	
				ftin=new BufferedReader(new FileReader(formatFilename));
			}catch(Exception e) {
				ftin=new BufferedReader(new InputStreamReader(ReaderCONLL.class.getResourceAsStream("/"+formatFilename)));
			}
		
		String buff=ftin.readLine();
		featureNames= buff.split("\t");
		}
		catch(Exception e){
			System.err.println("Error Loading CONLL format");
			e.printStackTrace();
			throw(e);
		}
	}
	
	public static void main(String args[]) throws Exception{
		CONLLformat fmt =  new CONLLformat("conf/conllFreeling.fmt");
		BufferedReader in = new BufferedReader( new StringReader("1 2 3 4 5 6 7 8 9 10 11 12 13"));
		ReaderCONLL.readSegment(in, fmt);
	}
}
