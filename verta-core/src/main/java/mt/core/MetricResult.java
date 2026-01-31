package mt.core;

import java.io.PrintStream;
import java.text.NumberFormat;
import java.util.Vector;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

/// representation of the whole result
@Getter
@Setter
public class MetricResult {

    private double precision;
    private double recall;
    private Vector<Double> moduleWeigths;
    private double totalWeighs;
    private Vector<Double> modulePresion;
    private Vector<Double> moduleRecall;
    private Vector<String> moduleName;

    public MetricResult() {
        init(0, 0);
    }

    public void init(final double prec, final double rec) {
        assert (!Double.isNaN(prec));
        assert (!Double.isNaN(rec));
        assert (!Double.isInfinite(prec));
        assert (!Double.isInfinite(rec));
        totalWeighs = 0;
        this.precision = prec;
        this.recall = rec;
        moduleWeigths = new Vector<>();
        modulePresion = new Vector<>();
        moduleRecall = new Vector<>();
        moduleName = new Vector<>();
    }

    @JsonIgnore
    public double getOverallPrec() {
        return (precision > 0 && totalWeighs > 0) ? precision / totalWeighs : 0;
    }

    @JsonIgnore
    public double getOverallRec() {
        return (recall > 0 && totalWeighs > 0) ? recall / totalWeighs : 0;
    }

    public double getModulePrec(int i) {
        return modulePresion.get(i);
    }

    public double getModuleRec(int i) {
        return moduleRecall.get(i);
    }

    public double getModuleF1(int i) {
        return (getModulePrec(i) > 0 && getModuleRec(i) > 0) ? ((2 * getModulePrec(i) * getModuleRec(i)) / (getModulePrec(i) + getModuleRec(i))) : 0;
    }

    public double getOverallWF1() {
        double sumf1 = 0;
        double sumw = 0;
        for (int i = 0; i < modulePresion.size(); ++i) {
            sumf1 += moduleWeigths.get(i) * getModuleF1(i);
            sumw += moduleWeigths.get(i);
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

        precision += pPrec * weight;
        recall += pRec * weight;
        totalWeighs += weight;

        modulePresion.add(pPrec);
        moduleRecall.add(pRec);
        moduleWeigths.add(weight);

        moduleName.add(name);
    }

    public String toString() {
        return getOverallWF1() + "\t" + getOverallPrec() + "\t" + getOverallRec();
    }

    public void dump(PrintStream strace) {

        for (int i = 0; i < modulePresion.size(); ++i) {
            String name = moduleName.get(i);
            double prec = getModulePrec(i);
            double rec = getModuleRec(i);
            double f1 = getModuleF1(i);
            double wprec = moduleWeigths.get(i) * prec;
            double wrec = moduleWeigths.get(i) * rec;
            double wf1 = moduleWeigths.get(i) * f1;

            strace.println("<wmetric name='" + name + "'>");
            strace.println("<f>" + f1 + "</f>");
            strace.println("<p>" + prec + "</p>");
            strace.println("<r>" + rec + "</r>");
            strace.println("<wf>" + wf1 + "</wf>");
            strace.println("<wp>" + wprec + "</wp>");
            strace.println("<wr>" + wrec + "</wr>");
            strace.println("</wmetric>");
        }

        strace.println("<f>" + getOverallWF1() + "</f>");
        strace.println("<prec>" + getOverallPrec() + "</prec>");
        strace.println("<rec>" + getOverallRec() + "</rec>");
    }

    /**
     * output format
     * <p>
     * F / P / R #MODULS MOD-NAME MOD-F1 MOD-PRE MOD-RECALL WMOD-F1 WMOD-PREC
     * WMOD-REC ...
	 * </p>
     */
    public void textdump(PrintStream strace, NumberFormat nf) {
        strace.print(nf.format(getOverallWF1()));
        strace.print("\t" + nf.format(getOverallPrec()));
        strace.print("\t" + nf.format(getOverallRec()));
        strace.print("\t" + modulePresion.size());
        for (int i = 0; i < modulePresion.size(); ++i) {
            String name = moduleName.get(i);
            double prec = getModulePrec(i);
            double f1 = getModuleF1(i);
            double rec = getModuleRec(i);
            double wprec = moduleWeigths.get(i) * prec;
            double wrec = moduleWeigths.get(i) * rec;
            double wf1 = moduleWeigths.get(i) * f1;
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