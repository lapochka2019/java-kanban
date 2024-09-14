package service;

import model.Task;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final LinkedList<Task> historyList = new LinkedList<>();
    private static final int MAX_HISTORY_SIZE = 10;
    //Почему мы храним Таски, а не их индексы?
    @Override
    public ArrayList<Task> getHistory() {
        //Подозреваю, что опять перемудрила
        //и достаточно было ы просто вернуть лист
        return new ArrayList<>(historyList);
    }

    @Override
    public void add(Task task) {
        if (task==null){
            return;
        }
        if (historyList.size()==MAX_HISTORY_SIZE){
            //Первый элемент - самый "старый"
            historyList.removeFirst();
        }
        historyList.add(task);
    }
}