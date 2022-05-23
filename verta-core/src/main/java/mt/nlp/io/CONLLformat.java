package mt.nlp.io;

import java.io.BufferedReader;
import java.util.logging.Logger;

public class CONLLformat {

	private static Logger LOGGER = Logger.getLogger(CONLLformat.class.getName());

	private String formatFilename;
	public String[] featureNames;

	public CONLLformat() {
	}

	public CONLLformat(String filename) throws Exception {
		loadFormat(filename);
	}
	
	public void loadFormat(String formatFilename) throws Exception {
		try {
			BufferedReader ftin = FileManager.get_file_content(formatFilename);
			String buff = ftin.readLine();
			featureNames = buff.split("\t");
		} catch (Exception e) {
			LOGGER.info("Error Loading CONLL format");
			e.printStackTrace();
			throw (e);
		}
	}

	public String getFormatFilename() {
		return formatFilename;
	}

	public void setFormatFilename(String formatFilename) {
		this.formatFilename = formatFilename;
	}
}
