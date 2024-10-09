package service;
//Это класс для создания других классов???
public class Managers {
    public static TaskManager getDefault(){
        return new InMemoryTaskManager(new InMemoryHistoryManager());
    }

    public static HistoryManager getDefaultHistory(){
        return new InMemoryHistoryManager();
    }
}
