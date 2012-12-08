package org.aimas.craftingquest.core;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSParser;
import org.w3c.dom.ls.LSParserFilter;


public class GameUtils {
	
	/** Reads an XML from a file and validates it using the schema reference
     * defined in the file.
     * This function ignores <i>ignorable white spaces</i>
     * @param fileName the name of the file to read.
     * @return an {@link org.w3c.dom.Document} containing the parsed content
     * of the XML
     */
    public static Document readXMLDocument(String fileName) {
        DOMImplementationRegistry registry;

        try {
            registry = DOMImplementationRegistry.newInstance();
            DOMImplementationLS impl = 
                (DOMImplementationLS)registry.getDOMImplementation("LS");
            LSParser builder = impl.createLSParser(
                                DOMImplementationLS.MODE_SYNCHRONOUS,
                                null);
            builder.setFilter(new NoWhitespaceFilter());
            Document document = builder.parseURI(fileName);   
            return document;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

/** This class is a filter that rejects any <i>ignorable whitespaces</i>. It
 * is used by {@link AIWOGPUtils}
 */
class NoWhitespaceFilter implements LSParserFilter{

    /**
     * @param elementArg
     * @return always <b>LSParserFilter.FILTER_ACCEPT</b>
     */
    public short startElement(Element elementArg) {
        return LSParserFilter.FILTER_ACCEPT;
    }

    /**
     * @param nodeArg
     * @return <b>LSParserFilter.FILTER_REJECT</b> if the given argument is
     * a whitespace as defined by <a href="http://www.w3.org/DOM/">DOM Standard
     * </a>, <b>LSParserFilter.FILTER_ACCEPT</b> otherwise
     */
    public short acceptNode(Node nodeArg) {
        String content = nodeArg.getTextContent();
        if(content.contains(System.getProperty("line.separator")) ||
           content.matches("\\s+")) {
            return LSParserFilter.FILTER_REJECT;
        } else {
            return LSParserFilter.FILTER_ACCEPT;
        }
    }

    /**
     * @return always <b>Node.NOTATION_NODE</b>
     */
    public int getWhatToShow() {
        return Node.NOTATION_NODE;
    }
}
