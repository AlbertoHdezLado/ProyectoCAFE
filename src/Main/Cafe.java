package Main;

import DB.ConectorCafeDB;
import Tasks.*;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import static Main.Main.printXmlDocument;
import static Main.Main.xmlDocumentToFile;

public class Cafe {
    public Cafe() {
        Slot splitterInput = new Slot();
        Slot splitterOutput = new Slot();
        Slot replicatorOutput1;
        Slot replicatorOutput2;
        Slot translatorOutput;
        Slot conectorOutput;
        Slot correlatorOutput1;
        Slot correlatorOutput2;
        Slot contextEnricherOutput;
        Slot mergerOutput = new Slot();
        Slot agreggatorOutput = new Slot();

        List<Slot> distributorOutputList = new LinkedList<>();
        List<Slot> replicatorOutputList;
        List<Slot> correlatorInputList;
        List<Slot> correlatorOutputList;
        List<Slot> mergerInputList;

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
            Scanner in = new Scanner(System.in);
            System.out.print("Introduce una secuencia de comandas del 1 al 9 separadas por ',' (ej: 4,7,2,1): ");
            String orders = in.next();

            String[] ordersList = orders.split(",");

            for (int n = 0; n < ordersList.length; n++) {

                Document documento = builder.parse(new File("Orders/order" + ordersList[n] + ".xml"));

                splitterInput = new Slot();
                splitterInput.enqueue(documento);
                splitterOutput = new Slot();
                Splitter splitter = new Splitter(splitterInput, splitterOutput, "//drink");
                splitter.Split();

                distributorOutputList = new LinkedList<>();
                Distributor distributor = new Distributor(splitterOutput, distributorOutputList, "//type");
                List<String> types = distributor.Distribute();

                mergerInputList = new LinkedList<>();
                for (int i = 0; i < distributorOutputList.size(); i++) {
                    // Replicator
                    replicatorOutputList = new LinkedList<>();
                    replicatorOutput1 = new Slot();
                    replicatorOutput2 = new Slot();
                    replicatorOutputList.add(replicatorOutput1);
                    replicatorOutputList.add(replicatorOutput2);

                    Replicator replicator = new Replicator(distributorOutputList.get(i), replicatorOutputList);
                    replicator.Replicate();

                    // Translator
                    translatorOutput = new Slot();
                    Translator translator = new Translator(replicatorOutputList.get(0), translatorOutput, "//drink/name");
                    if (types.get(i).equals("hot"))
                        translator.TranslateSQL("Nombre", "dbo.BEBIDAS_CALIENTES", "and stock>0");
                    else
                        translator.TranslateSQL("Nombre", "dbo.BEBIDAS_FRIAS", "and stock>0");

                    // Conector
                    conectorOutput = new Slot();
                    ConectorCafeDB conector = new ConectorCafeDB(translatorOutput, conectorOutput);
                    conector.Conect();

                    // Entrada correlator
                    correlatorInputList = new LinkedList<>();
                    correlatorInputList.add(replicatorOutputList.get(1));
                    correlatorInputList.add(conectorOutput);

                    // Salida correlator
                    correlatorOutputList = new LinkedList<>();
                    correlatorOutput1 = new Slot();
                    correlatorOutput2 = new Slot();
                    correlatorOutputList.add(correlatorOutput1);
                    correlatorOutputList.add(correlatorOutput2);

                    // Correlator
                    Correlator correlator = new Correlator(correlatorInputList, correlatorOutputList);
                    correlator.Correlate();

                    // Context enricher
                    contextEnricherOutput = new Slot();
                    ContextEnricher contextEnricher = new ContextEnricher(correlatorOutputList.get(0), correlatorOutputList.get(1), contextEnricherOutput, "//drink[1]");
                    contextEnricher.Enrich();
                    mergerInputList.add(contextEnricherOutput);
                }

                // Merger
                mergerOutput = new Slot();
                Merger merger = new Merger(mergerInputList, mergerOutput);
                merger.Merge();

                // Aggregator
                agreggatorOutput = new Slot();
                Aggregator aggregator = new Aggregator(mergerOutput, agreggatorOutput, "//drink");
                aggregator.Aggregate();

                Document outputDoc = agreggatorOutput.getQueue().element();
                xmlDocumentToFile(outputDoc, ordersList[n]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
