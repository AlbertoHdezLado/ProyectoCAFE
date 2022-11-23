/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Tasks;

import Utils.Slot;

import java.util.List;

public class Replicator {

    Slot inputSlot;
    List<Slot> outputSlotList;

    public Replicator(Slot inputSlot, List<Slot> outputSlotList) {
        this.inputSlot = inputSlot;
        this.outputSlotList = outputSlotList;
    }

    public void Replicate() {
        int i = 0;
        while (!inputSlot.getQueue().isEmpty()) // Esta condición está mal si queremos hacerlo concurrente
                outputSlotList.get(i++%outputSlotList.size()).enqueue(inputSlot.dequeue());
    }
}