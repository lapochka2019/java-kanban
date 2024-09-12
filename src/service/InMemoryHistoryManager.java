package service;

import model.Task;

import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final LinkedList<Task> historyList = new LinkedList<>();

    //Почему мы храним Таски, а не их индексы?
    @Override
    public List<Task> getHistory() {
        //Подозреваю, что опять перемудрила
        //и достаточно было ы просто вернуть лист
        return new LinkedList<>(historyList);
    }

    @Override
    public void add(Task task) {
        if (task==null){
            return;
        }
        if (historyList.size()>9){
            //Первый элемент - самый "старый"
            historyList.removeFirst();
        }
        historyList.add(task);
    }
}