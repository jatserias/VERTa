package mt.nlp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.EqualsAndHashCode;

@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Ngram {
	public static final Ngram[] EMPTY_NGRAM = new Ngram[0];

	private int start;
	private int size;

}
