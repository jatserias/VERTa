package mt;

public class TimeExpressions {

	public int start;
   	public int end;
   	public String date;
   	public String type;
   	
	public TimeExpressions(int start, int end, String date, String type) {
		this.start=start;
		this.end=end;
		this.date=date;
		this.type=type;
	}
	
	public String toString() {
		return date+":"+type+" "+start+"-"+end;
	}

}
