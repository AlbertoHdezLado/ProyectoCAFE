import Tasks.Aggregator;
import Tasks.Splitter;
import Utils.Slot;
import com.sun.jdi.PathSearchingVirtualMachine;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

public class Cafe {
    public static void main(String[] args) {
        Cafe cafe = new Cafe();
    }

    public Cafe() {
        Slot entrada = new Slot();
        Slot intermedio = new Slot();
        Slot salida = new Slot();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
            Document documento = builder.parse(new File("src/order2.xml"));
            entrada.enqueue(documento);
            System.out.println("Separando: ");
            Splitter splitter = new Splitter(entrada, intermedio, "//drink");
            splitter.Split();
            System.out.println("Juntando: ");
            Aggregator aggregator = new Aggregator(intermedio, salida, "//drink");
            aggregator.Aggregate();

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
