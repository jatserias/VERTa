package mt.core;

import java.util.HashSet;
import java.util.Set;

public class AlignmentBuilderFirstLeft2Rigth implements AlignmentBuilder {

	@Override
	public void build(ISentenceAlignment align, final DistanceMatrix d) {

		Set<Integer> taken = new HashSet<>();

		double x;
		String prov = "null";

		for (int i = 0; i < d.getRowSize(); ++i) {
			int pos = -1;
			double max = -1;

			for (int j = 0; j < d.getColumnSize(); ++j) {
				x = d.getDistance(i, j);
				if (x > max && !taken.contains(j)) {
					max = x;
					pos = j;
					prov = d.getProvenance(i, j);
				}
			}
			if (max >= 0) {
				align.setAligned(i, pos, prov);
				taken.add(pos);
			}
		}

	}

}
