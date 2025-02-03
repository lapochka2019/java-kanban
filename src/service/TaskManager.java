package service;

import model.Epic;
import model.SubTask;
import model.Task;

import java.util.ArrayList;
import java.util.Optional;
import java.util.TreeSet;

public interface TaskManager {
    int generateId();

    TreeSet<Task> sortedTasksByTime = null;
    InMemoryHistoryManager history = null;


    ArrayList<Task> getHistory();

    ArrayList<Task> getPrioritizedTasks();

    ArrayList<Task> getTasks();

    ArrayList<Epic> getEpics();

    ArrayList<SubTask> getSubTasks();

    void clearTasks();

    void clearEpics();

    void clearSubTusks();

    Optional<Task> getTask(int id);

    Optional<Epic> getEpic(int id);

    Optional<SubTask> getSubTask(int id);

    Task create(Task task);

    Epic create(Epic epic);

    SubTask create(SubTask subTask);

    Task update(Task task);

    Epic update(Epic epic);

    SubTask update(SubTask subTask);

    void deleteTask(int id);

    void deleteEpic(int id);

    void deleteSubTusk(int id);

    ArrayList<SubTask> getEpicSubTasks(int id);


}