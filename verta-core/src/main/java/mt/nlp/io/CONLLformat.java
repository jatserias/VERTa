package mt.nlp.io;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;

@Slf4j
public class CONLLformat {

    public String[] featureNames;
    private String formatFilename;

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
            log.info("Error Loading CONLL format " + formatFilename);
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
