package mt.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.inject.Inject;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import verta.wn.IWordNet;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public abstract class WnBaseSimilarity extends BaseSimilarity {
	@JsonIgnore
	@Inject
	public IWordNet wn;

	@Override
	public void compile(Object wn) {
		this.setWn((IWordNet) wn);
	}
}
