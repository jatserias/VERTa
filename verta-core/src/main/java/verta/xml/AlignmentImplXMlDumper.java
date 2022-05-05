package verta.xml;

import java.io.PrintStream;

import mt.core.AlignmentImpl;

public class AlignmentImplXMlDumper {

	public static void dump(AlignmentImpl a, PrintStream strace) {
		strace.print("<align>");
		strace.print("<st>");
		int i = 0;
		for (int i_a: a.getAlignment(false)) {
			strace.println("<s  s=\"" + i + "\" t=\"" + i_a + "\"/>");
			++i;
		}
		strace.print("</st>");

		strace.print("<ts>");
		int j = 0;
		for(int j_a : a.getAlignment(true)) {
			strace.println("<s  s=\"" + j + "\" t=\"" + j_a + "\"/>");
			++j;		}
		strace.print("</ts>");
		strace.print("</align>");
	}

}
