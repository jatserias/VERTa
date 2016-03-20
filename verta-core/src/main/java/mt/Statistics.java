package mt;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;

public class Statistics {

	Collection<Statistics> desc;
	
	/// id
	String id;
	
	/// numbers of calls
	int n_call;
	
	/// times returninig >=0
	int n_pos;
	
	/// times returning nomatch
	int n_neg;
	
	public Statistics(String id) {
		this.id=id;
		n_call=0;
		n_pos=0;
		n_neg=0;
		desc = new ArrayList<Statistics>();
	}
	
	public void dump(PrintStream s) {
		s.println("<sta id=\""+id+"\">");
		for(Statistics d:desc)
			d.dump(s);
		s.println("</sta>");
	}
	
	
}
