package mt;

public class RserverTest {}
/** R java test
import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPGenericVector;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;

public class RserverTest {

	 public static void main(String args[]) throws Exception {
		 RConnection c = new RConnection();
		 REXP x = c.eval("R.version.string");
		 System.out.println(x.asString());
		
		 try { 
		 c.eval("digits=10"); 
		 System.err.println("1");
		 c.eval("old <- read.csv2(file="+"\"/Users/jordi/yprojects/MTmetricsELi/me.txt\""+",head=TRUE,sep=\",\")");
		  System.err.println("2");
		 c.eval("seli <- read.csv2(file="+"\"/Users/jordi/yprojects/MTmetricsELi/exp/expflu_1_precrec.txt\""+",head=FALSE,sep=\"\t\")");
		 System.err.println("3");
		 c.eval("nmax <- max(as.double(old$human))");
		 c.eval("nmin <- min(as.double(old$human))");
		 c.eval("x <- cbind(seli[1])");
		 System.err.println("4");
		 c.eval("r <- x[,1] * (nmax - nmin) + nmin");
		 System.err.println("5");
		 String s =c.eval("paste(capture.output(print(cor.test(r,as.double(old$human)))),collapse='\\n')").asString();
//		 REXPGenericVector xy = (REXPGenericVector)c.eval("cor.test(r,as.double(old$human))");
		 System.err.println("OUTPUT:"+s);
		 c.eval("co=cor.test(r,as.double(old$human))");
		 x=c.eval("co$estimate");
		 System.err.println(x.asDouble());
		 
		 } catch(Exception e) {
			 e.printStackTrace();
		 }
	 }
}
}
**/
