package mt.nlp;

public class TimeExpressions {

	public int start;
	public int end;
	public String date;
	public String type;

	public TimeExpressions(int start, int end, String date, String type) {
		this.start = start;
		this.end = end;
		this.date = date;
		this.type = type;
	}

	public String toString() {
		return date + ":" + type + " " + start + "-" + end;
	}

	@Override
    public boolean equals(Object obj)
    {
          
		if(this == obj) return true;
          
        if(obj == null || obj.getClass()!= this.getClass())
            return false;
          
        TimeExpressions timex = (TimeExpressions) obj;
          
        return (timex.start == this.start && timex.end == timex.end && timex.date.equals(date) && timex.type.equals(type));
    }
	
	public int hashCode() {
		return this.toString().hashCode();
	}
	
}
