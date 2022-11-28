package Tasks;

import Utils.Slot;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class Translator {

    Slot inputSlot;
    String xPathExpression;

    public Translator(Slot inputSlot, String xPathExpression) {
        this.inputSlot = inputSlot;
        this.xPathExpression = xPathExpression;
    }

    public Queue<String> TranslateSQL(String variable, String TableName, String otherConditions) {
        Queue<String> BDQueryStringList = new LinkedList<>();
        while (!inputSlot.getQueue().isEmpty()) {
            Document inputDocument = inputSlot.dequeue();
            try {
                // Consulta xPath para extraer una lista de elementos encontrados.
                XPath xPath = XPathFactory.newInstance().newXPath();
                NodeList node = (NodeList) xPath.evaluate(xPathExpression, inputDocument, XPathConstants.NODESET);

                String element = node.item(0).getTextContent();

                BDQueryStringList.add("select * from " + TableName + " where " + variable + "='" + element + "' " + otherConditions);


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return BDQueryStringList;
    }
}
