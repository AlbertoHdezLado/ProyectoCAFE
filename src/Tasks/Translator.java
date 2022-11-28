package Tasks;

import Utils.Slot;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

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
        Queue<String> BDQueryStringList = new LinkedList<>();
        while (!inputSlot.getQueue().isEmpty()) {
            Document inputDocument = inputSlot.dequeue();
            try {
                // Consulta xPath para extraer una lista de elementos encontrados.
                XPath xPath = XPathFactory.newInstance().newXPath();
                NodeList node = (NodeList) xPath.evaluate(xPathExpression, inputDocument, XPathConstants.NODESET);

                String element = node.item(0).getTextContent();



                // Builder para cargar el documento
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = dbFactory.newDocumentBuilder();

                // Creamos un documento
                Document translatedDocument = builder.newDocument();

                // Añadimos el nodo sql
                Element sqlElement = translatedDocument.createElement("sql");
                sqlElement.setTextContent("select * from " + TableName + " where " + variable + "='" + element + "' " + otherConditions);
                translatedDocument.appendChild(sqlElement);

                outputSlot.enqueue(translatedDocument);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
