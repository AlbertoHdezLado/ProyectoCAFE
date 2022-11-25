package Tasks;

import Utils.Slot;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ContextEnricher {
    Slot inputSlot;
    Slot contextSlot;
    Slot outputSlot;
    String xPathExpression;

    public ContextEnricher(Slot input, Slot context, Slot output, String xPathExpression) {
        this.inputSlot = input;
        this.contextSlot = context;
        this.outputSlot = output;
        this.xPathExpression = xPathExpression; // "/*[result = 'true']"
    }

    public void Enrich() {
        if (!inputSlot.getQueue().isEmpty() && !contextSlot.getQueue().isEmpty()) {
            // Desencolamos el documento del slot de entrada
            Document inputDocument = inputSlot.dequeue();
            try {
                // Consulta xPath para extraer una lista de elementos encontrados.
                XPath xPath = XPathFactory.newInstance().newXPath();
                NodeList splitNodes = (NodeList) xPath.evaluate(xPathExpression, contextSlot.dequeue(), XPathConstants.NODESET);

                // Si hemos encontrado el elemento
                if (splitNodes.getLength() > 0) {
                    outputSlot.enqueue(inputDocument);
                }
            } catch (XPathExpressionException ex) {
                Logger.getLogger(Splitter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}