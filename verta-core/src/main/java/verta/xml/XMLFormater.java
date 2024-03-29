package verta.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XMLFormater {
	Document document;
	Element sentencesRoot;

	public void addTag(String tagName) {
		Element root = document.createElement("s");
		sentencesRoot.appendChild(root);
	}

	public void setAttribute(String attributeName, String attributeValue) {
		sentencesRoot.setAttribute(attributeName, attributeValue);
	}

	public static String encodeXMLString(String str) {
		String result = str;
		result = result.replace("&", "&amp;");
		result = result.replace("<", "&lt;");
		result = result.replace(">", "&gt;");
		result = result.replace("\"", "&quot;");
		result = result.replace("'", "&apos;");
		return result;
	}

	public static String header() {
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
	}
}
