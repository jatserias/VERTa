package mt.nlp;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/// Seems to be only used byt the old matching triples
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Triples  implements Comparable<Triples>{
	
	public static final String ID_NAME = "ID";
    public static final String WORD_NAME = "WORD";
	
	/// label
	private String label;

	/// label
	private int source;
	
	/// head
	private int target;

	@EqualsAndHashCode.Exclude
	private String sourceString;

	@EqualsAndHashCode.Exclude
	private String targetString;




	public Triples(String label, int source, int target) {
		this.source = source;
		this.target = target;
		this.label = label;
	}

	@Override
	public int compareTo(Triples o) {
	  return this.toString().compareTo(o.toString());
	}

}
