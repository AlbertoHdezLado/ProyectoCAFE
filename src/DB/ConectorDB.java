package DB;

import Cafe.Slot;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

public class ConectorDB {
    Slot inputSlot;
    Slot outputSlot;

    public ConectorDB(Slot inputSlot, Slot outputSlot) {
        this.inputSlot = inputSlot;
        this.outputSlot = outputSlot;
    }

    public void Conect() {
        XPath xPath = XPathFactory.newInstance().newXPath();
        while (!inputSlot.getQueue().isEmpty()) {
            Document inputDocument = inputSlot.dequeue();
            try {
                // Consulta xPath para extraer una lista de elementos encontrados.
                NodeList node = (NodeList) xPath.evaluate("/sql", inputDocument, XPathConstants.NODESET);
                String sqlQuery = node.item(0).getTextContent();
                String id_replicator = node.item(0).getAttributes().item(0).getTextContent();

                String[] parts = sqlQuery.split("'");
                System.out.println(parts[1]);
                System.out.println(id_replicator);

                // Builder para cargar el documento
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = dbFactory.newDocumentBuilder();

                // Creamos un documento
                Document reponseDocument = builder.newDocument();

                // Creamos el nodo sql
                Element resultsetElement = reponseDocument.createElement("resultset");
                Element fileElement = reponseDocument.createElement("file");
                Element nameElement = reponseDocument.createElement("name");
                Element replicatorIDElement = reponseDocument.createElement("replicator_id");
                Element resultElement = reponseDocument.createElement("resut");

                nameElement.setTextContent(parts[1]);
                replicatorIDElement.setTextContent(id_replicator);
                resultElement.setTextContent("true");

                reponseDocument.appendChild(resultsetElement);
                resultsetElement.appendChild(fileElement);
                fileElement.appendChild(nameElement);
                fileElement.appendChild(replicatorIDElement);
                fileElement.appendChild(resultElement);

                outputSlot.enqueue(reponseDocument);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
