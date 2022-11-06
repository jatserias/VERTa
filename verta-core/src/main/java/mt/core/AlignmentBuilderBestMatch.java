package mt.core;

import java.util.Comparator;
import java.util.PriorityQueue;

/// A class to do a better alignment 
public class AlignmentBuilderBestMatch implements AlignmentBuilder {
    @Override
    public void build(ISentenceAlignment res, final DistanceMatrix distances_source_target) {
        int source_size = distances_source_target.getRowSize();
        int target_size = distances_source_target.getColumnSize();

        PriorityQueue<Align> sorted = new PriorityQueue<>(10, (arg0, arg1) -> {
			if (arg0.w == arg1.w) {
				int d0 = Math.abs(arg0.source - arg0.target);
				int d1 = Math.abs(arg1.source - arg1.target);
				if (d0 == d1) {
					if (arg0 == arg1) return 0;
					return arg0.source > arg1.source ? 1 : -1;
				} else return d0 > d1 ? 1 : -1;
			}
			return arg0.w < arg1.w ? 1 : -1;
		});

        for (int i = 0; i < source_size; ++i)
            for (int j = 0; j < target_size; ++j) {
                double dd = distances_source_target.getDistance(i, j);
                if (dd > 0) sorted.add(new Align(i, j, dd, distances_source_target.getProvenance(i, j)));
            }
        // arrays initilized to false
        boolean[] taken = new boolean[target_size];

        boolean[] staken = new boolean[source_size];

        while (!sorted.isEmpty()) {
            Align a = sorted.poll();
            if (!staken[a.source] && !taken[a.target]) {
                res.setAligned(a.source, a.target, a.prov.toString());

                staken[a.source] = true;
                taken[a.target] = true;
            }
        }

    }

    static class Align implements Comparator<Align> {
        /// provenance
        public Object prov;
        /// word position in the source sentence
        int source;

        // word position in the target sentence
        int target;

        /// weight
        double w;

        public Align(int i, int j, double dd, Object prov) {
            source = i;
            target = j;
            w = dd;
            this.prov = prov;
        }

        @Override
        public int compare(Align arg0, Align arg1) {
            if (arg0.w == arg1.w) return 0;
            return arg0.w > arg1.w ? -1 : 1;
        }

        public String toString() {
            return "" + source + " x " + target + ":" + w;
        }

    }
}
