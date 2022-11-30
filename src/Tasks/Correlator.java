package Tasks;

import Cafe.Slot;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.util.List;

public class Correlator {

    List<Slot> inputSlotList;
    List<Slot> outputSlotList;

    public Correlator(List<Slot> inputSlotList, List<Slot> outputSlotList) {
        this.inputSlotList = inputSlotList;
        this.outputSlotList = outputSlotList;
    }

    public void Correlate() {
        // Mínimo dos entradas
        try {
            boolean found, foundAllSlots = true;
            int i = 0;
            XPath xPath = XPathFactory.newInstance().newXPath();
            while (i < inputSlotList.get(0).getQueue().size()) {
                Document inputDocument = inputSlotList.get(0).dequeue();
                NodeList replicatorIDNode = (NodeList) xPath.evaluate("//replicator_id", inputDocument, XPathConstants.NODESET);
                String replicatorID = replicatorIDNode.item(0).getTextContent();
                int j = 1;
                // Comprobamos si todos los slot de entrada tienen un documento con el replicator id
                while (j < inputSlotList.size()) {
                    int documentPos = 0;
                    found = false;
                    while (documentPos < inputSlotList.get(j).getQueue().size() && !found) {
                        Document comparedDocument = inputSlotList.get(j).dequeue();
                        NodeList comparedIDNode = (NodeList) xPath.evaluate("//replicator_id", comparedDocument, XPathConstants.NODESET);
                        String comparedID = comparedIDNode.item(0).getTextContent();
                        if (replicatorID.equals(comparedID))
                            found = true;
                        documentPos++;
                        inputSlotList.get(j).enqueue(comparedDocument);
                    }
                    if (!found) foundAllSlots = false;
                    j++;
                }

                // En el caso de que todos los slot tienen un documento con el id deseado, los sacamos al mismo tiempo por la salida
                if (foundAllSlots) {
                    outputSlotList.get(0).enqueue(inputDocument);
                    for (int k = 1; k < inputSlotList.size(); k++) {
                        found = false;
                        int l = 0;
                        while (!found && l < inputSlotList.get(k).getQueue().size() ) {
                            Document aux = inputSlotList.get(k).dequeue();
                            NodeList auxIDNode = (NodeList) xPath.evaluate("//replicator_id", aux, XPathConstants.NODESET);
                            String auxID = auxIDNode.item(0).getTextContent();
                            if (replicatorID.equals(auxID)) {
                                outputSlotList.get(k).enqueue(aux);
                                found = true;
                            }
                            else {
                                inputSlotList.get(k).enqueue(aux);
                                l++;
                            }
                        }
                    }
                }
                else {
                    inputSlotList.get(0).enqueue(inputDocument);
                    i++;
                }
            }
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }

    }
}
