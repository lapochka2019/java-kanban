package service;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryTaskManager implements TaskManager {
    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, Epic> epics;
    private HashMap<Integer, SubTask> subTasks;
    private int idCount = 0;
    //пришлось убрать приватность для теста
    InMemoryHistoryManager history;

    public InMemoryTaskManager(InMemoryHistoryManager history) {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subTasks = new HashMap<>();
        this.history = history;
    }

    @Override
    public int generateId() {
        return ++idCount;
    }

    /**Получение списка всех задач.**/
    @Override
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<SubTask> getSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    /**Удаление всех задач.**/
    @Override
    public void clearTasks() {
        tasks.clear();
    }

    @Override
    public void clearEpics() {
        epics.clear();
        subTasks.clear();
    }

    @Override
    public void clearSubTusks() {
        subTasks.clear();
        for (Epic epic: epics.values()) {
            epic.clearSubTasks();
            epic.setStatus(Status.NEW);
        }
    }

    /**Получение по идентификатору**/
    @Override
    public Task getTask(int id) {
        if (tasks.containsKey(id)) {
            Task task = tasks.get(id);
            history.add(task);
            return task;
        }
        return null;
    }

    @Override
    public Epic getEpic(int id) {
        if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            history.add(epic);
            return epic;
        }
        return null;
    }

    @Override
    public SubTask getSubTask(int id) {
        if (subTasks.containsKey(id)) {
            SubTask subTask = subTasks.get(id);
            history.add(subTask);
            return subTask;
        }
        return null;
    }

    /**Создание.**/
    @Override
    public Task create(Task task) {
        if (task == null) {
            return null;
        }
        task.setId(generateId());
        tasks.put(task.getId(),task);
        return task;
    }

    @Override
    public Epic create(Epic epic) {
        if (epic == null) {
            return null;
        }
        epic.setId(generateId());
        epic.setStatus(Status.NEW);
        epics.put(epic.getId(),epic);
        return epic;
    }

    @Override
    public SubTask create(SubTask subTask) {
        if (subTask == null) {
            return null;
        }
        int epicId = subTask.getEpicId();
        if (!epics.containsKey(epicId)) {
            return null;
        }
        subTask.setId(generateId());
        subTasks.put(subTask.getId(), subTask);
        Epic epic = epics.get(epicId);
        epic.addSubTask(subTask);
        calculateEpicStatus(epicId);
        return subTask;
    }

    /**Обновление.**/
    //не уверена, что тут нужно возвращать какое-то значение
    @Override
    public Task update(Task task) {
        Task savedTask = tasks.get(task.getId());
        if (savedTask == null) {
            return null;
        }
        savedTask.setName(task.getName());
        savedTask.setDescription(task.getDescription());
        savedTask.setStatus(task.getStatus());
        return savedTask;
    }

    @Override
    public Epic update(Epic epic) {
        Epic savedEpic = epics.get(epic.getId());
        if (savedEpic == null) {
            return null;
        }
        savedEpic.setName(epic.getName());
        savedEpic.setDescription(epic.getDescription());
        return savedEpic;
    }

    @Override
    public SubTask update(SubTask subTask) {
        SubTask savedSubTask = subTasks.get(subTask.getId());
        if (savedSubTask == null) {
            return null;
        }
        savedSubTask.setName(subTask.getName());
        savedSubTask.setDescription(subTask.getDescription());
        savedSubTask.setStatus(subTask.getStatus());
        //нужно ли обновлять Эпик, к которому относится подзадача, например теперь не первый, а второй
        calculateEpicStatus(savedSubTask.getEpicId());
        return savedSubTask;
    }

    /**Удаление по идентификатору.**/
    @Override
    public void deleteTask(int id) {
        tasks.remove(id);
        history.remove(id);
    }

    @Override
    public void deleteEpic(int id) {
        //получили Эпик
        Epic epic = epics.get(id);
        //получили подзадачи
        ArrayList<Integer> epicSubTasks = epic.getSubTusks();
        //удаляем каждую подзадачу из HashMap
        for (Integer subTusk:epicSubTasks) {
            subTasks.remove(subTusk);
            history.remove(subTusk);
        }
        //удаляем Эпик
        epics.remove(id);
        history.remove(id);
    }

    @Override
    public void deleteSubTusk(int id) {
        SubTask subTask = subTasks.get(id);
        Epic epic = epics.get(subTask.getEpicId());
        epic.deleteSubTask(id);
        calculateEpicStatus(epic.getId());
        subTasks.remove(id);
        history.remove(id);
    }

    /**Получение списка всех подзадач определённого эпика.**/
    public ArrayList<SubTask> getEpicSubTasks(int id) {
        ArrayList<SubTask> epicSubTasks = new ArrayList<>();
        for (Integer subTuskId:epics.get(id).getSubTusks()) {
            epicSubTasks.add(subTasks.get(subTuskId));
        }
        return epicSubTasks;
    }

    //Нужен ли этот метод в интерфейсе?
    //Раз он приватный, я полагаю, что не нужен.
    private void calculateEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        int newCounter = 0;
        int doneCounter = 0;
        ArrayList<Integer> epicSubTusks = epic.getSubTusks();
        int totalCount = epicSubTusks.size();
        if (epicSubTusks.isEmpty()) {
            epic.setStatus(Status.NEW);
        } else {
            for (Integer id:epicSubTusks) {
                if (subTasks.get(id).getStatus() == Status.NEW) {
                    newCounter++;
                }
                if (subTasks.get(id).getStatus() == Status.DONE) {
                    doneCounter++;
                }
                if (subTasks.get(id).getStatus() == Status.IN_PROGRESS) {
                    epic.setStatus(Status.IN_PROGRESS);
                    return;
                }
            }
            if (newCounter == totalCount) {
                epic.setStatus(Status.NEW);
            } else if (doneCounter == totalCount)  {
                epic.setStatus(Status.DONE);
            } else {
                epic.setStatus(Status.IN_PROGRESS);
            }
        }
    }
}