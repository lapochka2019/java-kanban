package service;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

public class InMemoryTaskManager implements TaskManager {
    protected HashMap<Integer, Task> tasks;
    protected HashMap<Integer, Epic> epics;
    protected HashMap<Integer, SubTask> subTasks;
    protected TreeSet<Task> sortedTasksByTime;
    protected int idCount = 0;
    //пришлось убрать приватность для теста
    InMemoryHistoryManager history;

    public InMemoryTaskManager(InMemoryHistoryManager history) {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subTasks = new HashMap<>();
        this.history = history;
        sortedTasksByTime = new TreeSet<Task>((t1,t2) -> t1.getStartTime().compareTo(t2.getStartTime()));
    }

    @Override
    public int generateId() {
        return ++idCount;
    }

    public ArrayList<Task> getPrioritizedTasks() {
        return new ArrayList<>(sortedTasksByTime);
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
        for (Integer id : tasks.keySet()) {
            history.remove(id);
            sortedTasksByTime.remove(tasks.get(id));
        }
        tasks.clear();
    }

    @Override
    public void clearEpics() {
        for (Integer id : epics.keySet()) {
            history.remove(id);
        }
        epics.clear();
        for (Integer id : subTasks.keySet()) {
            history.remove(id);
            sortedTasksByTime.remove(subTasks.get(id));
        }
        subTasks.clear();
    }

    @Override
    public void clearSubTusks() {
        for (Integer id : subTasks.keySet()) {
            history.remove(id);
            sortedTasksByTime.remove(subTasks.get(id));
        }
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
        if (checkTaskTime(task)) {
            task.setId(generateId());
            tasks.put(task.getId(),task);
            if (task.getStartTime() != null) {
                sortedTasksByTime.add(task);
            }
            return task;
        } else {
            return null;
        }

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
        if (checkTaskTime(subTask)) {
            int epicId = subTask.getEpicId();
            if (!epics.containsKey(epicId)) {
                return null;
            }
            subTask.setId(generateId());
            subTasks.put(subTask.getId(), subTask);
            Epic epic = epics.get(epicId);
            epic.addSubTask(subTask);
            calculateEpicStatus(epicId);
            calculateEpicDuration(epicId, subTask.getDuration());
            setEpicStartTime(epicId,subTask.getStartTime());
            setEpicEndTime(epicId,subTask.getEndTime());
            if (subTask.getStartTime() != null) {
                sortedTasksByTime.add(subTask);
            }
            return subTask;
        } else {
            return null;
        }
    }

    /**Обновление.**/
    //не уверена, что тут нужно возвращать какое-то значение
    @Override
    public Task update(Task task) {
        Task savedTask = tasks.get(task.getId());
        if (savedTask == null) {
            return null;
        }
        if (checkTaskTime(task)) {
            savedTask.setName(task.getName());
            savedTask.setDescription(task.getDescription());
            savedTask.setStatus(task.getStatus());
            if (task.getStartTime() != null) {
                sortedTasksByTime.add(task);
            }
            return savedTask;
        } else {
            return null;
        }
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
        if (checkTaskTime(subTask)) {
            savedSubTask.setName(subTask.getName());
            savedSubTask.setDescription(subTask.getDescription());
            savedSubTask.setStatus(subTask.getStatus());
            if (subTask.getStartTime() != null) {
                sortedTasksByTime.add(subTask);
            }
            calculateEpicStatus(savedSubTask.getEpicId());
            return savedSubTask;
        } else {
            return null;
        }
    }

    /**Удаление по идентификатору.**/
    @Override
    public void deleteTask(int id) {
        sortedTasksByTime.remove(tasks.get(id));
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
        for (int subTusk : epicSubTasks) {
            sortedTasksByTime.remove(subTasks.get(subTusk));
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
        sortedTasksByTime.remove(subTasks.get(id));
        subTasks.remove(id);
        history.remove(id);
    }

    /**Получение списка всех подзадач определённого эпика.**/
    public ArrayList<SubTask> getEpicSubTasks(int id) {
        ArrayList<SubTask> epicSubTasks = new ArrayList<>();
        epics.get(id).getSubTusks().stream()
                .forEach(s -> epicSubTasks.add(subTasks.get(s)));
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

    private void calculateEpicDuration(int epicId, Duration duration) {
        Epic epic = epics.get(epicId);
        epic.setDuration(epic.getDuration().plus(duration));
    }

    private void setEpicStartTime(int epicId, LocalDateTime startTime) {
        Epic epic = epics.get(epicId);
        LocalDateTime epicStartTime = epic.getStartTime();
        if (epicStartTime == null || startTime.isBefore(epicStartTime)) {
            epicStartTime = startTime;
        }
    }

    private void setEpicEndTime(int epicId, LocalDateTime endTime) {
        Epic epic = epics.get(epicId);
        LocalDateTime epicEndTime = epic.getEndTime();
        if (epicEndTime == null || endTime.isAfter(epicEndTime)) {
            epicEndTime = endTime;
        }
    }

    private boolean checkTaskTime(Task task) {
        return sortedTasksByTime.stream()
                .filter(t -> t.getId() != task.getId())
                .noneMatch(t ->
                        t.getEndTime().isAfter(task.getStartTime()) && t.getStartTime().isBefore(task.getEndTime())
                );
    }
}