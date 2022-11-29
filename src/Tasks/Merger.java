package Tasks;

import Cafe.Slot;

import java.util.List;

public class Merger { //Por comprobar

    List<Slot> inputSlotList;
    Slot outputSlot;

    public Merger(List<Slot> inputSlotQueue, Slot outputSlot) {
        this.inputSlotList = inputSlotQueue;
        this.outputSlot = outputSlot;
    }

    public void Merge() {
        int i = 0;
        while (!inputSlotList.isEmpty()) {
            if (!inputSlotList.get(i%inputSlotList.size()).getQueue().isEmpty()) {
                outputSlot.enqueue(inputSlotList.get(i%inputSlotList.size()).dequeue());
                i++;
            }
            else {
                inputSlotList.get(i).dequeue();
            }
        }
    }
}