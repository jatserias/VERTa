package mt.core;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MatchResult {

	private double score;

	private Object prov;

	public String toString() {
		return "" + score + ":" + prov;
	}
}