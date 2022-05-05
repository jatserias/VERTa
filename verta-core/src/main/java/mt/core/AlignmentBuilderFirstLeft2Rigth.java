package mt.core;

import java.util.HashSet;
import java.util.Set;

public class AlignmentBuilderFirstLeft2Rigth implements AlignmentBuilder {

	@Override
	public void build(boolean reversed, SentenceAlignment align, final DistanceMatrix d) {

		Set<Integer> taken = new HashSet<Integer>();

		double x;
		String prov = "null";

		for (int i = 0; i < d.getRowSize(reversed); ++i) {
			int pos = -1;
			;
			double max = -1;

			for (int j = 0; j < d.getColumnSize(reversed); ++j) {
				x = d.getDistance(reversed, i, j);
				if (x > max && !taken.contains(j)) {
					max = x;
					pos = j;
					prov = d.getProvenance(reversed, i, j);
				}
			}
			if (max >= 0) {
				align.setAligned(reversed, i, pos, prov);
				taken.add(pos);
			}
		}

	}

}
