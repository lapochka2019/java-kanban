package service;

import model.Epic;
import model.SubTask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, Epic> epics;
    private HashMap<Integer, SubTask> subTasks;
    int sequence=0;

    public TaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subTasks = new HashMap<>();
    }

    public int generateId(){
        return ++sequence;
    }

    /**Получение списка всех задач.**/
    public HashMap<Integer, Task> getTasks() {
        return tasks;
    }

    public HashMap<Integer, Epic> getEpics() {
        return epics;
    }

    public HashMap<Integer, SubTask> getSubTasks() {
        return subTasks;
    }

    /**Удаление всех задач.**/
    public HashMap<Integer, Task> clearTasks (){
        tasks.clear();
        return tasks;
    }
    public HashMap<Integer, Epic> clearEpics (){
        //Для каждого эпика нужно удалить задачи
        for(Integer id:epics.keySet()){
            //можно ли здесь вызывать метод deleteSubTask?
            epics.get(id).clearSubTasks();
            epics.remove(id);
        }
        return epics;
    }
    public HashMap<Integer, SubTask> clearSubTusks (){
        //Для каждой подзадачи нужно пересчитывать Эпик
        for(Integer id:subTasks.keySet()){
            //можно ли здесь вызывать метод deleteSubTask?
            SubTask subTask = subTasks.get(id);
            Epic epic = epics.get(subTask.getEpic().getId());
            epic.deleteSubTask(subTask);
            epic.calculateEpicStatus();
            subTasks.remove(id);
        }
        return subTasks;
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
        //Если id уже задан, то есть элемент уже существует в таблице
        if(task.getId()!=0){
            return task;
        }
        task.setId(generateId());
        tasks.put(task.getId(),task);
        return task;
    }
    public Epic create (Epic epic){
        //Если id уже задан, то есть элемент уже существует в таблице
        if(epic.getId()!=0){
            return epic;
        }
        epic.setId(generateId());
        epics.put(epic.getId(),epic);
        return epic;
    }
    public SubTask create (SubTask subTask){
        //Если id уже задан, то есть элемент уже существует в таблице
        if(subTask.getId()!=0){
            return subTask;
        }
        subTask.setId(generateId());
        subTasks.put(subTask.getId(), subTask);
        Epic epic = epics.get(subTask.getEpic().getId());
        epic.addSubTask(subTask);
        epic.calculateEpicStatus();
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
        Epic epic = epics.get(savedSubTask.getEpic().getId());
        epic.calculateEpicStatus();
        return savedSubTask;
    }
    /**Удаление по идентификатору.**/
    public HashMap<Integer, Task> deleteTask (int id){
        tasks.remove(id);
        return tasks;
    }
    public HashMap<Integer, Epic> deleteEpic (int id){
        epics.get(id).clearSubTasks();
        epics.remove(id);
        return epics;
    }
    public HashMap<Integer, SubTask> deleteSubTusk (int id){
        SubTask subTask = subTasks.get(id);
        Epic epic = epics.get(subTask.getEpic().getId());
        epic.deleteSubTask(subTask);
        epic.calculateEpicStatus();
        subTasks.remove(id);
        return subTasks;
    }

    /**Получение списка всех подзадач определённого эпика.**/
    public ArrayList<SubTask> getSubTusks(int id) {
        return epics.get(id).getSubTusks();
    }
}