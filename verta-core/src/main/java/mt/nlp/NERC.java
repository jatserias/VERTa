package mt.nlp;

/// A class to hold a Named Entity
public class NERC {

	public NERC(int nsen, int start, int end, String mention, String type) {
		this.mention = mention;
		this.type = type;
		this.start = start;
		this.end = end;
	}

	/// String of the NE
	public String mention;

	/// Type of the NE
	public String type;

	/// start token
	public int start;

	/// End token
	public int end;

	public String toString() {
		return mention + " type:" + type + " (" + start + "," + end + ")";
	}
	
	@Override
    public boolean equals(Object obj)
    {
          
		if(this == obj) return true;
          
        if(obj == null || obj.getClass()!= this.getClass())
            return false;
          
        NERC ne = (NERC) obj;
          
        return ((ne.start == this.start) && (ne.end == this.end) && ne.mention.equals(this.mention) && ne.type.equals(this.type));
    }
	
	public int hashCode() {
		return this.toString().hashCode();
	}
	
}
