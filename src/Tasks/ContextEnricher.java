package Tasks;

import Cafe.Slot;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.sql.SQLOutput;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ContextEnricher {
    Slot inputSlot;
    Slot contextSlot;
    Slot outputSlot;
    String xPathExpressionInput;
    String xPathExpressionContext;

    public ContextEnricher(Slot input, Slot context, Slot output, String xPathExpressionInput, String xPathExpressionContext) {
        this.inputSlot = input;
        this.contextSlot = context;
        this.outputSlot = output;
        this.xPathExpressionInput = xPathExpressionInput;
        this.xPathExpressionContext = xPathExpressionContext;
    }

    public void Enrich() {
        while (!inputSlot.getQueue().isEmpty() && !contextSlot.getQueue().isEmpty()) {
            XPath xPath = XPathFactory.newInstance().newXPath();
            Element element = null;
            // Desencolamos el documento del slot de entrada
            Document inputDocument = inputSlot.dequeue();
            Document contextDocument = contextSlot.dequeue();
            try {
                // Consulta xPath para extraer una lista de elementos encontrados.
                NodeList rootNode = (NodeList) xPath.evaluate("/*", inputDocument, XPathConstants.NODESET);
                NodeList inputNode = (NodeList) xPath.evaluate(xPathExpressionInput, inputDocument, XPathConstants.NODESET);
                NodeList contextNode = (NodeList) xPath.evaluate(xPathExpressionContext, contextDocument, XPathConstants.NODESET);

                NodeList rootNodeChilds = rootNode.item(0).getChildNodes();
                NodeList inputNodeChilds = inputNode.item(0).getChildNodes();
                NodeList contextNodeChilds = contextNode.item(0).getChildNodes();

                // Builder para cargar el documento
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = dbFactory.newDocumentBuilder();

                // Creamos un documento
                Document enrichedDocument = builder.newDocument();

                System.out.println(inputNodeChilds.getLength());
                System.out.println(contextNodeChilds.getLength());

                for (int i = 0; i < contextNodeChilds.getLength(); i++) {
                    System.out.println("a");
                    int j = 0;
                    boolean found = false;
                    while (j < inputNodeChilds.getLength() && !found) {
                        if (inputNodeChilds.item(j).getNodeName().equals(contextNodeChilds.item(i).getNodeName())) {
                            found = true;
                        }
                        else {
                            j++;
                        }
                    }
                    if (!found) {
                        element = enrichedDocument.createElement(contextNodeChilds.item(i).getNodeName());
                        element.setTextContent(contextNodeChilds.item(i).getTextContent());
                    }
                    else {
                        element = enrichedDocument.createElement(inputNodeChilds.item(j).getNodeName());
                        element.setTextContent(inputNodeChilds.item(i).getTextContent());
                    }
                    enrichedDocument.appendChild(element);
                }

                outputSlot.enqueue(inputDocument);

            } catch (XPathExpressionException ex) {
                Logger.getLogger(Splitter.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            }
        }
    }
}