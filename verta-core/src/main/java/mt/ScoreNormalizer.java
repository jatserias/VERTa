package mt;

public class ScoreNormalizer {

	public static double sigmoid(double x)
	{
	    return 1 / (1 + Math.exp(-x));
	}
}
