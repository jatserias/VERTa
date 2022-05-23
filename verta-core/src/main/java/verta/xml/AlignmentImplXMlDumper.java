package verta.xml;

import java.io.PrintStream;

import mt.core.AlignmentImplSingle;

public class AlignmentImplXMlDumper {

	public static void dump(AlignmentImplSingle a, AlignmentImplSingle a_rev, PrintStream strace) {
		strace.print("<align>");
		strace.print("<st>");
		int i = 0;
		for (int i_a: a.getAlignment()) {
			strace.println("<s  s=\"" + i + "\" t=\"" + i_a + "\"/>");
			++i;
		}
		strace.print("</st>");

		strace.print("<ts>");
		int j = 0;
		for(int j_a : a_rev.getAlignment()) {
			strace.println("<s  s=\"" + j + "\" t=\"" + j_a + "\"/>");
			++j;		}
		strace.print("</ts>");
		strace.print("</align>");
	}

}
