package Tasks;

import Utils.Slot;

import java.util.List;

public class Distributor {

    Slot inputSlot;
    List<Slot> outputSlotList;
    List<String> criteriaList;

    public Distributor(Slot inputSlot, List<Slot> outputSlotList, List<String> criteriaList) {
        this.inputSlot = inputSlot;
        this.outputSlotList = outputSlotList;
        this.criteriaList = criteriaList; // "//drink[type='hot']"
    }

    public void Distribute() {
        Splitter splitter;
        Slot outputSlot = new Slot();
        for (int i = 0; i < criteriaList.size(); i++) {
            splitter = new Splitter(inputSlot, outputSlot, criteriaList.get(i));
            splitter.SplitWithoutRemovingInputQueue();
            outputSlotList.add(outputSlot);
        }
        inputSlot.dequeue();
    }
}
