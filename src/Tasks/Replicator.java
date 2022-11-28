/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Tasks;

import Utils.Slot;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.List;
import java.util.UUID;

public class Replicator { // Por comprobar

    Slot inputSlot;
    List<Slot> outputSlotList;

    public Replicator(Slot inputSlot, List<Slot> outputSlotList) {
        this.inputSlot = inputSlot;
        this.outputSlotList = outputSlotList;
    }

    public String Replicate() {
        //Creamos un ID único que necesitaremos para el correlator
        String uniqueID = UUID.randomUUID().toString();
        while (!inputSlot.getQueue().isEmpty()) {
            Document inputDocument = inputSlot.dequeue();

            // Añadimos el ID
            Element root = inputDocument.getDocumentElement();
            Element idElement = inputDocument.createElement("replicator_id");
            idElement.setTextContent(uniqueID);
            root.appendChild(idElement);

            for (int i = 0; i < outputSlotList.size(); i++)
                outputSlotList.get(i).enqueue(inputDocument);
        }

        return uniqueID;
    }
}