package mt;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;

//import com.sun.org.apache.xml.internal.serialize.OutputFormat;
//import com.sun.org.apache.xml.internal.serialize.XMLSerializer;


public class XMLFormater {
	Document document;
	Element sentencesRoot;
	
	public void addTag(String tagName){
		Element root = (Element) document.createElement("s");
		sentencesRoot.appendChild(root);
	}
	
	public void setAttribute(String attributeName, String attributeValue){
		 sentencesRoot.setAttribute(attributeName, attributeValue);		
	}
	
	
	 public static String encodeXMLString(String str) {
	    	String result = new String(str);
	    	result = result.replace("&", "&amp;");
	    	result = result.replace("<", "&lt;");
	    	result = result.replace(">", "&gt;");
	    	result = result.replace("\"", "&quot;");
	    	result = result.replace("'", "&apos;");
	    	return result;
	 }

	public static String  header() {
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
	}
}
