package service;

import model.Epic;
import model.SubTask;
import model.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    int generateId();

    ArrayList<Task> getTasks();

    ArrayList<Epic> getEpics();

    ArrayList<SubTask> getSubTasks();

    void clearTasks();

    void clearEpics();

    void clearSubTusks();

    Task getTask(int id);

    Epic getEpic(int id);

    SubTask getSubTusk(int id);

    Task create(Task task);

    Epic create(Epic epic);

    SubTask create(SubTask subTask);

    //не уверена, что тут нужно возвращать какое-то значение
    Task update(Task task);

    Epic update(Epic epic);

    SubTask update(SubTask subTask);

    void deleteTask(int id);

    void deleteEpic(int id);

    void deleteSubTusk(int id);

    ArrayList<SubTask> getSubTusks(int id);


}