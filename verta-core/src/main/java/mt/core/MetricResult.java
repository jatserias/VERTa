package mt.core;

import java.io.PrintStream;
import java.text.NumberFormat;
import java.util.Vector;

/// representation of the whole result
public class MetricResult {

    private double prec;
    private double rec;
    private Vector<Double> v_w;
    private double sumpes;
    private Vector<Double> v_pres;
    private Vector<Double> v_rec;
    private Vector<String> v_name;

    public MetricResult() {
        init(0, 0);
    }

    public void init(final double prec, final double rec) {
        assert (!Double.isNaN(prec));
        assert (!Double.isNaN(rec));
        assert (!Double.isInfinite(prec));
        assert (!Double.isInfinite(rec));
        sumpes = 0;
        this.prec = prec;
        this.rec = rec;
        v_w = new Vector<>();
        v_pres = new Vector<>();
        v_rec = new Vector<>();
        v_name = new Vector<>();
    }

    public double getPrec() {
        return (prec > 0 && sumpes > 0) ? prec / sumpes : 0;
    }

    public double getRec() {
        return (rec > 0 && sumpes > 0) ? rec / sumpes : 0;
    }

    public double getPrec(int i) {
        return v_pres.get(i);
    }

    public double getRec(int i) {
        return v_rec.get(i);
    }

    public double getWF1() {
        return getSF1();
    }

    public double getF1(int i) {
        return (getPrec(i) > 0 && getRec(i) > 0) ? ((2 * getPrec(i) * getRec(i)) / (getPrec(i) + getRec(i))) : 0;
    }

    private double getSF1() {
        double sumf1 = 0;
        double sumw = 0;
        for (int i = 0; i < v_pres.size(); ++i) {
            sumf1 += v_w.get(i) * getF1(i);
            sumw += v_w.get(i);
        }
        return (sumf1 > 0 ? (sumf1 / sumw) : 0.0);
    }

    /*
     * SUM(W * F1) =?? F1(SUM(P * w) + SUM (F * w))
     */
    public void add(String name, double weight, double pPrec, double pRec) {
        assert (!Double.isNaN(pPrec));
        assert (!Double.isNaN(pRec));
        assert (!Double.isNaN(weight));
        assert (!Double.isInfinite(pPrec));
        assert (!Double.isInfinite(pRec));
        assert (weight >= 0);

        prec += pPrec * weight;
        rec += pRec * weight;
        sumpes += weight;

        v_pres.add(pPrec);
        v_rec.add(pRec);
        v_w.add(weight);

        v_name.add(name);
    }

    public String toString() {
        return getSF1() + "\t" + getPrec() + "\t" + getRec();
    }

    public void dump(PrintStream strace) {

        for (int i = 0; i < v_pres.size(); ++i) {
            String name = v_name.get(i);
            double prec = getPrec(i);
            double rec = getRec(i);
            double f1 = getF1(i);
            double wprec = v_w.get(i) * prec;
            double wrec = v_w.get(i) * rec;
            double wf1 = v_w.get(i) * f1;

            strace.println("<wmetric name='" + name + "'>");
            strace.println("<f>" + f1 + "</f>");
            strace.println("<p>" + prec + "</p>");
            strace.println("<r>" + rec + "</r>");
            strace.println("<wf>" + wf1 + "</wf>");
            strace.println("<wp>" + wprec + "</wp>");
            strace.println("<wr>" + wrec + "</wr>");
            strace.println("</wmetric>");
        }

        strace.println("<f>" + getSF1() + "</f>");
        strace.println("<prec>" + getPrec() + "</prec>");
        strace.println("<rec>" + getRec() + "</rec>");
    }

    /**
     * output format
     * <p>
     * F / P / R #MODULS MOD-NAME MOD-F1 MOD-PRE MOD-RECALL WMOD-F1 WMOD-PREC
     * WMOD-REC ...
	 * </p>
     */
    public void textdump(PrintStream strace, NumberFormat nf) {
        strace.print(nf.format(getSF1()));
        strace.print("\t" + nf.format(getPrec()));
        strace.print("\t" + nf.format(getRec()));
        strace.print("\t" + v_pres.size());
        for (int i = 0; i < v_pres.size(); ++i) {
            String name = v_name.get(i);
            double prec = getPrec(i);
            double f1 = getF1(i);
            double rec = getRec(i);
            double wprec = v_w.get(i) * prec;
            double wrec = v_w.get(i) * rec;
            double wf1 = v_w.get(i) * f1;
            strace.print("\t" + name);
            strace.print("\t" + nf.format(f1));
            strace.print("\t" + nf.format(prec));
            strace.print("\t" + nf.format(rec));
            strace.print("\t" + nf.format(wf1));
            strace.print("\t" + nf.format(wprec));
            strace.print("\t" + nf.format(wrec));
        }

        strace.println();
    }

}