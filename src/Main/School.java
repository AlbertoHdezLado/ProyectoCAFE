package Main;

import DB.ConectorCafeDB;
import Tasks.*;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

import static Main.Main.printXmlDocument;

public class School {
    public School() {
        Slot splitterInput = new Slot();
        Slot splitterOutput = new Slot();
        Slot replicatorOutput1;
        Slot replicatorOutput2;
        Slot translatorOuput;
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
        List<Slot> mergerInputList = new LinkedList<>();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
            /*Scanner in = new Scanner(System.in);
            System.out.print("Introduce un numero del 1 al 1: ");
            int order = in.nextInt();

            Document documento = builder.parse(new File("src/Students/student"+order+".xml"));*/
            Document documento = builder.parse(new File("src/Students/student1.xml"));

            splitterInput.enqueue(documento);

            Splitter splitter = new Splitter(splitterInput,splitterOutput, "//subject");
            splitter.Split();

            Distributor distributor = new Distributor(splitterOutput, distributorOutputList, "//evaluation");
            List<String> types = distributor.Distribute();

            for (int i = 0; i < distributorOutputList.size(); i++) {
                // Replicator
                replicatorOutputList = new LinkedList<>();
                replicatorOutput1 = new Slot();
                replicatorOutput2 = new Slot();
                replicatorOutputList.add(replicatorOutput1);
                replicatorOutputList.add(replicatorOutput2);

                Replicator replicatorHot = new Replicator(distributorOutputList.get(i), replicatorOutputList);
                replicatorHot.Replicate();

                // Translator
                translatorOuput = new Slot();
                Translator translator = new Translator(replicatorOutputList.get(0), translatorOuput,"//name");
                translator.TranslateSQL("Nombre", "dbo.BEBIDAS_FRIAS", "and stock>0");

                System.out.println(types.get(i));

                // Conector
                conectorOutput = new Slot();
                ConectorCafeDB conector = new ConectorCafeDB(translatorOuput, conectorOutput);
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

            // Merger input list


            // Merger
            Merger merger = new Merger(mergerInputList, mergerOutput);
            merger.Merge();

            // Aggregator
            Aggregator aggregatorHot = new Aggregator(mergerOutput, agreggatorOutput, "//drink");
            aggregatorHot.Aggregate();

            printXmlDocument(agreggatorOutput.getQueue().element());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}