package mt.core;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.HashMap;
import org.junit.jupiter.api.Test;
import mt.nlp.Triples;

public class TpatternTest {

	@Test
	public void old_main_test() throws IOException {

		HashMap<String, LabelSet> p = new HashMap<String, LabelSet>();
		p.put("top", new LabelSet("1.0	top	sbj patient nsubj"));
		p.put("down", new LabelSet("1.0 down pp"));
		// (Lamod-Lprep_of,X,X)

		System.err.println("TESTing");
		System.err.println(p);

		System.err.println("starting ...");
		/// L-L test
		Tpattern r = new Tpattern("(Lagent-Lnsubj,X,X) : 1.0", p);
		Triples x = new Triples("agent", 1, 2);
		Triples y = new Triples("nsubj", 1, 2);

		// label_match / source_match / target_match
		assertEquals(true, r.match(x, y, false, true, true), "Match Lagent-Lnsubj,X,X\n");

		// try the same with _%

		/// test L-L with _%
		r = new Tpattern("(Lagent-Lnsubj_%,X,X) : 1.0", p);
		System.err.println(r);
		Triples y2 = new Triples("nsubj_by", 1, 2);
		assertEquals(true, r.match(x, y2, false, true, true), "Match Lagent-Lnsubj_%,X,X\n");

		/// test X X X same label
		x = new Triples("nsubj", 1, 2);
		r = new Tpattern("(X,X,X): 1.0", p);
		assertEquals(true, r.match(x, y, true, true, true), "Match X X X sbj:\n");

		// Test L-L match with _
		x = new Triples("amod", 1, 2);
		y = new Triples("prep_of", 1, 2);
		r = new Tpattern("(Lamod-Lprep_of,X,X): 1.0", p);
		assertEquals(true, r.match(x, y, false, true, true), "Match Lamod-Lprep_of,X,X:\n");

		// Test X ,X,O
		r = new Tpattern("(X,X,O) : 1.0", p);
		x = new Triples("pmod", 1, 2);
		y = new Triples("pmod", 1, 3);
		r.dump(System.err);
		assertEquals(true, r.match(x, y, true, true, false), "Match (X,X,O) pmod\n");

		// Test S-S match
		r = new Tpattern("(Stop-Sdown,X,O) : 1.0", p);
		x = new Triples("agent", 1, 2);
		y = new Triples("pp", 1, 3);
		r.dump(System.err);
		assertEquals(false, r.match(x, y, true, true, false), "Match S-S\n");
		x = new Triples("patient", 1, 2);
		assertEquals(true, r.match(x, y, true, true, false), "Match S-S\n");

		// Test S-L
		r = new Tpattern("(Stop-Lpp,X,O) : 1.0", p);
		assertEquals(true, r.match(x, y, true, true, false), "Match S-S\n");
		Triples x2 = new Triples("kk", 1, 2);
		assertEquals(false, r.match(x2, y, true, true, false), "Match S-S\n");
		assertEquals(false, r.match(x, x2, true, true, false), "Match S-S\n");

		// Test L-S
		r = new Tpattern("(Lpp-Stop,X,O) : 1.0", p);
		assertEquals(true, r.match(y, x, true, true, false), "Match S-S\n");
		assertEquals(false, r.match(y, x2, true, true, false), "Match S-S\n");
		assertEquals(false, r.match(x2, x, true, true, false), "Match S-S\n");
	}

}
