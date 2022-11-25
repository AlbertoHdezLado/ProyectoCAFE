import Tasks.Aggregator;
import Tasks.Distributor;
import Tasks.Replicator;
import Tasks.Splitter;
import Utils.Slot;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.*;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class Cafe {
    public static void main(String[] args) {
        new Cafe();
    }

    public Cafe() {
        Slot entrada = new Slot();
        Slot s1 = new Slot();
        Slot s2 = new Slot();
        List<Slot> salida = new LinkedList<>();

        List<String> criteriaList = new LinkedList<>();
        criteriaList.add("//drink[type='hot']");
        criteriaList.add("//drink[type='cold']");

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
            Document documento = builder.parse(new File("src/order2.xml"));
            entrada.enqueue(documento);
            printXmlDocument(documento);
            salida.add(s1);
            Replicator replicator = new Replicator(entrada,salida);
            replicator.Replicate();
            printXmlDocument(salida.get(0).dequeue());
            /*System.out.println("Separando: ");
            Splitter splitter = new Splitter(entrada, s1, "//drink");
            splitter.SplitRemovingInputQueue();
            System.out.println("Juntando: ");
            Aggregator aggregator = new Aggregator(s1, s2, "//drink");
            aggregator.Aggregate();
            System.out.println("Separando por tipo: ");
            Distributor distributor = new Distributor(s2, salida, criteriaList);
            distributor.Distribute();*/
        } catch (Exception e) {
            e.printStackTrace();
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
