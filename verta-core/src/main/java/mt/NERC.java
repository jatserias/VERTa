package mt;
/**
 * 
 * A class to hold a Named Entity
 * 
 * @author jordi
 *
 */
public class NERC {
	
  public NERC(int nsen, int pos, String mention, String type) {
		this.mention=mention;
		this.type=type;
		start=pos;
		end=pos+mention.length();
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
	  return mention+" type:"+type+" ("+start+","+end+")";
  }
}
