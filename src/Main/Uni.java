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

import static Main.Main.*;

public class Uni {
    public Uni() {
        Slot splitterInput = new Slot();
        Slot splitterOutput = new Slot();
        Slot filterOutput = new Slot();
        Slot replicatorOutput1 = new Slot();
        Slot replicatorOutput2 = new Slot();
        Slot translatorSQLOutput = new Slot();
        Slot conectorOutput = new Slot();
        Slot correlatorOutput1 = new Slot();
        Slot correlatorOutput2 = new Slot();
        Slot contextEnricherOutput = new Slot();
        Slot translatorEmailGatewayOutput = new Slot();

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

            Document documento = builder.parse(new File("Actas/acta" + acta + ".xml"));

            System.out.println("Principio: ");
            printXmlDocument(documento);

            splitterInput.enqueue(documento);

            Splitter splitter = new Splitter(splitterInput,splitterOutput, "//alumno");
            splitter.Split();

            System.out.println("Despues del spliter: ");
            printSlot(splitterOutput);

            Filter filter = new Filter(splitterOutput,filterOutput, "//alumnos/alumno[calificacion!=\"No presentado\"]");
            filter.Filt();

            System.out.println("Despues del filter: ");
            printSlot(filterOutput);

            replicatorOutputList.add(replicatorOutput1);
            replicatorOutputList.add(replicatorOutput2);

            Replicator replicator = new Replicator(filterOutput, replicatorOutputList);
            replicator.Replicate();

            System.out.println("Despues del replicator: ");
            for (int slot = 0; slot<replicatorOutputList.size(); slot++) {
                System.out.println("-Slot " + slot + ": ");
                printSlot(replicatorOutputList.get(slot));
            }

            Translator translatorSQL = new Translator(replicatorOutputList.get(0), translatorSQLOutput, "//alumno/dni");
            translatorSQL.TranslateSQL("email", "dbo.ALUMNOS", "dni", "");

            System.out.println("Despues del translator: ");
            printSlot(translatorSQLOutput);

            ConectorUniDB conector = new ConectorUniDB(translatorSQLOutput, conectorOutput);
            conector.Conect();

            System.out.println("Despues del conector: ");
            printSlot(conectorOutput);

            correlatorInputList.add(replicatorOutputList.get(1));
            correlatorInputList.add(conectorOutput);

            correlatorOutputList.add(correlatorOutput1);
            correlatorOutputList.add(correlatorOutput2);

            Correlator correlator = new Correlator(correlatorInputList, correlatorOutputList);
            correlator.Correlate();

            System.out.println("Despues del correlator: ");
            for (int slot = 0; slot<correlatorOutputList.size(); slot++) {
                System.out.println("-Slot " + slot + ": ");
                printSlot(correlatorOutputList.get(slot));
            }

            ContextEnricher contextEnricher = new ContextEnricher(correlatorOutputList.get(0),correlatorOutputList.get(1),contextEnricherOutput, "//alumno[1]");
            contextEnricher.Enrich();

            System.out.println("Despues del context enricher: ");
            printSlot(contextEnricherOutput);

            Translator translatorEmailGateway = new Translator(contextEnricherOutput, translatorEmailGatewayOutput, "//*");
            String subject = "Calificaciones ? convocatoria ?.";
            String[] subjectVariables = {"id_asignatura","convocatoria"};
            String content = "El alumno ? ha obtenido una calificación de ? en la convocatoria ? de ?.";
            String[] contentVariables = {"nombreCompleto","calificacion","convocatoria","id_asignatura"};
            translatorEmailGateway.TranslateEmailGateway("no-reply@uhu.es", "email", subject, subjectVariables, content, contentVariables);

            System.out.println("Despues del translator: ");
            printSlot(translatorEmailGatewayOutput);

            Document outputDoc = translatorEmailGatewayOutput.getQueue().element();
            //String pathArchivo = "Orders/Entregas/entrega"+ordersList[n]+".xml";
            //xmlDocumentToFile(outputDoc, pathArchivo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}