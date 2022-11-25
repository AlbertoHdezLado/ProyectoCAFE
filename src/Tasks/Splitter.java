package Tasks;

import Utils.Slot;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Splitter { //Comprobado

    Slot inputSlot;
    Slot outputSlot;
    String xPathExpression;

    public Splitter(Slot input, Slot output, String xPathExpression) { // //drink
        this.inputSlot = input;
        this.outputSlot = output;
        this.xPathExpression = xPathExpression;
    }

    public void SplitRemovingInputQueue() {
        // Desencolamos el documento del slot de entrada
        while (!inputSlot.getQueue().isEmpty()) {
            Document inputDocument = inputSlot.dequeue();
            Split(inputDocument);
        }
    }

    public void SplitWithoutRemovingInputQueue() {
        // Desencolamos el documento del slot de entrada
        while (!inputSlot.getQueue().isEmpty()) {
            Document inputDocument = inputSlot.getQueue().element();
            Split(inputDocument);
        }
    }

    public void Split(Document inputDocument) {
            try {
                int nSegmentos = 0;
                // Consulta xPath para extraer una lista de todos los nodos y otra de los elementos que queremos separar.
                XPath xPath = XPathFactory.newInstance().newXPath();
                NodeList root = (NodeList) xPath.evaluate("/*", inputDocument, XPathConstants.NODESET);
                NodeList splitNodes = (NodeList) xPath.evaluate(xPathExpression, inputDocument, XPathConstants.NODESET);

                // Mostramos numero de elementos encontrados
                nSegmentos = splitNodes.getLength();
                System.out.println("Found " + nSegmentos + " elements:");

                // Builder para cargar el documento
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = dbFactory.newDocumentBuilder();

                // Por cada uno de los elementos encontrados
                for (int i = 0; i < nSegmentos; i++) {
                    // Creamos un documento
                    Document splitDocument = builder.newDocument();

                    // Añadimos el nodo raíz
                    Element rootElement = splitDocument.createElement(root.item(0).getNodeName());
                    splitDocument.appendChild(rootElement);

                    // Creamos una NodeList con los hijos de la raíz
                    NodeList rootChild = root.item(0).getChildNodes();

                    // Para cada hijo de la raíz comprobamos si es el padre de los elementos que queremos separar
                    for (int j = 0; j < rootChild.getLength(); j++) {
                        // Si no lo es lo añadimos para conservar el resto de la estructura
                        if (!rootChild.item(j).getNodeName().equals(splitNodes.item(0).getParentNode().getNodeName())) {
                            Node node = splitDocument.importNode(rootChild.item(j), true);
                            rootElement.appendChild(node);
                        }
                        // Si lo es, en vez de añadir este nodo con todos los elementos, solo añadimos la cabecera y el elemento separado
                        else {
                            Element parentElement = splitDocument.createElement(splitNodes.item(0).getParentNode().getNodeName());
                            rootElement.appendChild(parentElement);
                            Node node = splitDocument.importNode(splitNodes.item(i), true);
                            parentElement.appendChild(node);
                        }
                    }

                    // Mostramos el XML
                    printXmlDocument(splitDocument);

                    // Encolamos el documento resultante en el slot de salida
                    outputSlot.enqueue(splitDocument);
                }
            } catch (XPathExpressionException | ParserConfigurationException ex) {
                Logger.getLogger(Splitter.class.getName()).log(Level.SEVERE, null, ex);

        }
    }

    // Método para darle formato al XML al mostrarlo en la consola
    public static void printXmlDocument(Document document) {
        DOMImplementationLS domImplementationLS =
                (DOMImplementationLS) document.getImplementation();
        LSSerializer lsSerializer =
                domImplementationLS.createLSSerializer();
        String string = lsSerializer.writeToString(document);
        System.out.println(string);
    }
}