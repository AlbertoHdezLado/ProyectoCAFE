package Main;

import DB.ConectorUniDB;
import Tasks.*;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import static Main.Main.printSlot;

public class Uni {
    public Uni() {
        Slot splitterInput = new Slot();
        Slot splitterOutput = new Slot();
        Slot filterOutput = new Slot();
        Slot replicatorOutput1 = new Slot();
        Slot replicatorOutput2 = new Slot();
        Slot translatorOutput = new Slot();
        Slot conectorOutput = new Slot();
        Slot correlatorOutput1 = new Slot();
        Slot correlatorOutput2 = new Slot();
        Slot contextEnricherOutput = new Slot();

        List<Slot> replicatorOutputList = new LinkedList<>();
        List<Slot> correlatorInputList = new LinkedList<>();
        List<Slot> correlatorOutputList = new LinkedList<>();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
            Scanner in = new Scanner(System.in);
            System.out.print("Introduce un numero del 1 al 4: ");
            int acta = in.nextInt();

            Document documento = builder.parse(new File("src/Actas/acta" + acta + ".xml"));

            splitterInput.enqueue(documento);

            Splitter splitter = new Splitter(splitterInput,splitterOutput, "//alumno");
            splitter.Split();

            Filter filter = new Filter(splitterOutput,filterOutput, "//alumnos/alumno[calificacion!=\"No presentado\"]");
            filter.Filt();

            replicatorOutputList.add(replicatorOutput1);
            replicatorOutputList.add(replicatorOutput2);

            Replicator replicator = new Replicator(filterOutput, replicatorOutputList);
            replicator.Replicate();

            Translator translator = new Translator(replicatorOutputList.get(0), translatorOutput, "//alumno/dni");
            translator.TranslateSQL("dni", "alumnos", "");
            ConectorUniDB conector = new ConectorUniDB(translatorOutput, conectorOutput);
            conector.Conect();

            correlatorInputList.add(replicatorOutputList.get(1));
            correlatorInputList.add(conectorOutput);

            correlatorOutputList.add(correlatorOutput1);
            correlatorOutputList.add(correlatorOutput2);

            Correlator correlator = new Correlator(correlatorInputList, correlatorOutputList);
            correlator.Correlate();

            ContextEnricher contextEnricher = new ContextEnricher(correlatorOutputList.get(0),correlatorOutputList.get(1),contextEnricherOutput, "//alumno[1]");
            contextEnricher.Enrich();

            printSlot(contextEnricherOutput);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}