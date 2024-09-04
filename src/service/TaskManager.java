package service;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;

import java.beans.Introspector;
import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, Epic> epics;
    private HashMap<Integer, SubTask> subTasks;
    private int idCount =0;

    public TaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subTasks = new HashMap<>();
    }

    public int generateId(){
        return ++idCount;
    }

    /**Получение списка всех задач.**/
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    public ArrayList<SubTask> getSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    /**Удаление всех задач.**/
    public void clearTasks (){
        tasks.clear();
    }
    public void clearEpics (){
        epics.clear();
        subTasks.clear();
    }
    public void clearSubTusks (){
        subTasks.clear();
        for(Epic epic: epics.values()){
            epic.clearSubTasks();
            epic.setStatus(Status.NEW);
        }
    }

    /**Получение по идентификатору**/
    public Task getTask (int id){
        return tasks.get(id);
    }
    public Epic getEpic (int id){
        return epics.get(id);
    }
    public SubTask getSubTusk (int id){
        return subTasks.get(id);
    }

    /**Создание.**/
    public Task create (Task task){
        if(task==null){
            return null;
        }
        task.setId(generateId());
        tasks.put(task.getId(),task);
        return task;
    }
    public Epic create (Epic epic){
        if(epic==null){
            return null;
        }
        epic.setId(generateId());
        epic.setStatus(Status.NEW);
        epics.put(epic.getId(),epic);
        return epic;
    }
    public SubTask create (SubTask subTask){
        if(subTask==null){
            return null;
        }
        int epicId = subTask.getEpicId();
        if(!epics.containsKey(epicId)){
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
    public Task update (Task task){
        Task savedTask = tasks.get(task.getId());
        if(savedTask==null){
            return null;
        }
        savedTask.setName(task.getName());
        savedTask.setDescription(task.getDescription());
        savedTask.setStatus(task.getStatus());
        return savedTask;
    }
    public Epic update (Epic epic){
        Epic savedEpic = epics.get(epic.getId());
        if(savedEpic==null){
            return null;
        }
        savedEpic.setName(epic.getName());
        savedEpic.setDescription(epic.getDescription());
        return savedEpic;
    }
    public SubTask update (SubTask subTask){
        SubTask savedSubTask = subTasks.get(subTask.getId());
        if(savedSubTask==null){
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
    public void deleteTask (int id){
        tasks.remove(id);
    }
    public void deleteEpic (int id){
        //получили Эпик
        Epic epic = epics.get(id);
        //получили подзадачи
        ArrayList<Integer> epicSubTasks = epic.getSubTusks();
        //удаляем каждую подзадачу из HashMap
        for(Integer subTusk:epicSubTasks){
            subTasks.remove(subTusk);
        }
        //удаляем Эпик
        epics.remove(id);
    }
    public void deleteSubTusk (int id){
        SubTask subTask = subTasks.get(id);
        Epic epic = epics.get(subTask.getEpicId());
        epic.deleteSubTask(id);
        calculateEpicStatus(epic.getId());
        subTasks.remove(id);
    }

    /**Получение списка всех подзадач определённого эпика.**/
    public ArrayList<SubTask> getSubTusks(int id) {
        ArrayList<SubTask> epicSubTasks = new ArrayList<>();
        for(Integer subTuskId:epics.get(id).getSubTusks())
        {
            epicSubTasks.add(subTasks.get(subTuskId));
        }
        return epicSubTasks;
    }

    private void calculateEpicStatus(int epicId){
        Epic epic = epics.get(epicId);
        int newCounter = 0;
        int doneCounter = 0;
        ArrayList <Integer> epicSubTusks = epic.getSubTusks();
        int totalCount = epicSubTusks.size();
        if(epicSubTusks.isEmpty()) {
            epic.setStatus(Status.NEW);
        } else{
            for(Integer id:epicSubTusks){
                if(subTasks.get(id).getStatus()==Status.NEW){
                    newCounter++;
                }
                if(subTasks.get(id).getStatus()==Status.DONE){
                    doneCounter++;
                }
                if(subTasks.get(id).getStatus()==Status.IN_PROGRESS){
                    epic.setStatus(Status.IN_PROGRESS);
                    return;
                }
            }
            if(newCounter==totalCount){
                epic.setStatus(Status.NEW);
            }else if(doneCounter==totalCount) {
                epic.setStatus(Status.DONE);
            }else{
                epic.setStatus(Status.IN_PROGRESS);
            }
        }
    }
}