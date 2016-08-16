package mt;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintStream;
import java.io.StringReader;

public class FreelingTagging {
	private static final String FREELING_SERVER = "192.168.99.100";
	
	public static void main(String[] args) throws Exception {
		String sfin=args[0];
		String out=args[1];
		CONLLformat fmt = new CONLLformat("conf/conllFreeling.fmt");
		
		PrintStream outStream = new PrintStream(out,"UTF-8");
		FreelingSocketClient server = new FreelingSocketClient(FREELING_SERVER, 50005);
		
		BufferedReader fin =new BufferedReader(new FileReader(sfin));
		String text;
		while((text=fin.readLine()).trim()!=null){
			System.err.println("IN:"+text);
			String s = server.processSegment(text);	
			System.err.println("OUT:"+s);
			System.err.println("Freeling calls done ...");
		    BufferedReader bs = new BufferedReader(new StringReader(s));
		    Sentence ss=ReaderCONLL.read(bs,fmt);
		    System.err.println(ss.toString());
		}
		
		fin.close();
	    outStream.close();
	    server.close();
	   	
	}
}
