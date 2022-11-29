package Tasks;

import Cafe.Slot;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

public class Translator {

    Slot inputSlot;
    Slot outputSlot;
    String xPathExpression;

    public Translator(Slot inputSlot, Slot outputSlot, String xPathExpression) {
        this.inputSlot = inputSlot;
        this.outputSlot = outputSlot;
        this.xPathExpression = xPathExpression;
    }

    public void TranslateSQL(String variable, String TableName, String otherConditions) {
        XPath xPath = XPathFactory.newInstance().newXPath();
        while (!inputSlot.getQueue().isEmpty()) {
            Document inputDocument = inputSlot.dequeue();
            try {
                // Consulta xPath para extraer una lista de elementos encontrados.
                NodeList node = (NodeList) xPath.evaluate(xPathExpression, inputDocument, XPathConstants.NODESET);

                String element = node.item(0).getTextContent();

                // Builder para cargar el documento
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = dbFactory.newDocumentBuilder();

                // Creamos un documento
                Document translatedDocument = builder.newDocument();

                // Creamos el nodo sql
                Element sqlElement = translatedDocument.createElement("sql");
                sqlElement.setTextContent("select * from " + TableName + " where " + variable + "='" + element + "' " + otherConditions);

                // Le añadimos el replicator id si tiene
                NodeList replicatorIDNode = (NodeList) xPath.evaluate("//replicator_id", inputDocument, XPathConstants.NODESET);
                if (replicatorIDNode.getLength() > 0) {
                    sqlElement.setAttribute("replicator_id", replicatorIDNode.item(0).getTextContent());
                }


                // Añadimos el nodo sql
                translatedDocument.appendChild(sqlElement);

                outputSlot.enqueue(translatedDocument);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
