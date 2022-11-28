package Cafe;

import Tasks.*;
import Utils.Slot;
import org.w3c.dom.Document;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Cafe {
    public static void main(String[] args) {
        new Cafe();
    }

    public Cafe() {
        Slot slot1 = new Slot();
        Slot slot2 = new Slot();
        Slot slot3 = new Slot();
        Slot slot4 = new Slot();
        Slot slot5 = new Slot();
        Slot slot6 = new Slot();
        List<Slot> slotList1 = new LinkedList<>();
        List<Slot> slotList2 = new LinkedList<>();
        List<Slot> slotList3 = new LinkedList<>();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
            Document documento = builder.parse(new File("src/order2.xml"));

            slot1.enqueue(documento);

            Splitter splitter = new Splitter(slot1,slot2, "//drink");
            splitter.Split();

            Distributor distributor = new Distributor(slot2, slotList1, "//type");
            distributor.Distribute();

            slotList2.add(slot3);
            slotList2.add(slot4);
            slotList3.add(slot5);
            slotList3.add(slot6);

            Replicator replicatorHot = new Replicator(slotList1.get(0), slotList2);
            replicatorHot.Replicate();
            Replicator replicatorCold = new Replicator(slotList1.get(1), slotList3);
            replicatorCold.Replicate();

            Translator translatorHot = new Translator(slotList2.get(0),"//drink/name");
            Translator translatorCold = new Translator(slotList3.get(0),"//drink/name");

            Queue<String> queryListHot = translatorHot.TranslateSQL("Nombre", "dbo.BEBIDAS_CALIENTES", "and stock>0");
            Queue<String> queryListCold = translatorCold.TranslateSQL("Nombre", "dbo.BEBIDAS_FRIAS", "and stock>0");







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
