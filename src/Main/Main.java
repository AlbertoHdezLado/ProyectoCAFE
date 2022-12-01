package Main;

import org.w3c.dom.Document;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        System.out.print("Introduce cafe o uni: ");
        String dsl = in.nextLine();
        if (dsl.equalsIgnoreCase("cafe"))
            new Cafe();
        else if (dsl.equalsIgnoreCase("uni"))
            new Uni();
        else
            System.out.println("Fallo al cargar DSL. Finalizando programa...");
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

    public static void printSlot(Slot slot) {
        for (int i = 0; i < slot.getQueue().size(); i++) {
            Document doc = slot.dequeue();
            printXmlDocument(doc);
            slot.enqueue(doc);
        }
    }
}
