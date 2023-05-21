package mt.nlp;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/// A class to hold a Named Entity
@Setter
@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class NERC {

	/// start token
	private int start;

	/// End token
	private int end;

	/// String of the NE
	public String mention;

	/// Type of the NE
	public String type;

}
