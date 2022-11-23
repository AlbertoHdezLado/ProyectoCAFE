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
        Slot salida = new Slot();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
            Document documento = builder.parse(new File("src/order2.xml"));
            entrada.enqueue(documento);
            Splitter splitter = new Splitter(entrada, salida, "//drink");
            splitter.Split();
            for (int i = 0;i < salida.getQueue().size();i++) {
                System.out.println(salida.dequeue());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
