package Cafe;

import DB.ConectorDB;
import Tasks.*;
import org.w3c.dom.Document;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

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
        Slot slot7 = new Slot();
        Slot slot8 = new Slot();
        Slot slot9 = new Slot();
        Slot slot10 = new Slot();
        Slot slot11 = new Slot();
        Slot slot12 = new Slot();
        Slot slot13 = new Slot();
        Slot slot14 = new Slot();
        Slot slot15 = new Slot();
        Slot slot16 = new Slot();

        List<Slot> slotList1 = new LinkedList<>();
        List<Slot> slotList2 = new LinkedList<>();
        List<Slot> slotList3 = new LinkedList<>();
        List<Slot> slotList4 = new LinkedList<>();
        List<Slot> slotList5 = new LinkedList<>();
        List<Slot> slotList6 = new LinkedList<>();
        List<Slot> slotList7 = new LinkedList<>();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
            Document documento = builder.parse(new File("src/Orders/order2.xml"));

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

            Translator translatorHot = new Translator(slotList2.get(0), slot7,"//drink/name");
            Translator translatorCold = new Translator(slotList3.get(0), slot8,"//drink/name");
            translatorHot.TranslateSQL("Nombre", "dbo.BEBIDAS_CALIENTES", "and stock>0");
            translatorCold.TranslateSQL("Nombre", "dbo.BEBIDAS_FRIAS", "and stock>0");

            ConectorDB conectordbhot = new ConectorDB(slot7, slot9);
            ConectorDB conectordbcold = new ConectorDB(slot8, slot10);

            conectordbhot.Conect();
            conectordbcold.Conect();

            // Entrada correlatorHot
            slotList4.add(slotList2.get(1));
            slotList4.add(slot9);

            // Entrada correlatorCold
            slotList5.add(slotList3.get(1));
            slotList5.add(slot10);

            // Salida correlatorHot
            slotList6.add(slot11);
            slotList6.add(slot12);

            // Salida correlatorCold
            slotList7.add(slot13);
            slotList7.add(slot14);

            Correlator correlatorHot = new Correlator(slotList4, slotList6);
            Correlator correlatorCold = new Correlator(slotList5, slotList7);
            correlatorHot.Correlate();
            correlatorCold.Correlate();

            printXmlDocument(slotList6.get(0).getQueue().element());
            printXmlDocument(slotList6.get(1).getQueue().element());

            ContextEnricher contextEnricherHot = new ContextEnricher(slotList6.get(0), slotList6.get(1), slot15, "//drink[1]", "//resultSet/file[1]");
            ContextEnricher contextEnricherCold = new ContextEnricher(slotList7.get(0), slotList7.get(1), slot16, "//drink[1]", "//resultSet/file[1]");
            contextEnricherHot.Enrich();
            contextEnricherCold.Enrich();

            printXmlDocument(slot15.dequeue());

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
