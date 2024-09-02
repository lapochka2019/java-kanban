import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import service.TaskManager;

import java.util.HashMap;

public class Main {

    public static void main(String[] args) {
        TaskManager manager = new TaskManager();
        //Создать задачу 1
        Task task1 = new Task("Task1", "Update Task1", Status.NEW);//1
        manager.create(task1);
        //Создать задачу 2
        Task task2 = new Task("Task2","Delete Task2", Status.IN_PROGRESS);//2
        manager.create(task2);
        printTasks(manager);

        //Обновить задачу 1
        Task task1Updated = new Task("Task1Updated", "Update Task1", Status.DONE);
        task1Updated.setId(1);
        manager.update(task1Updated);
        //Удалить задачу 2
        manager.deleteTask(task2.getId());
        printTasks(manager);
        System.out.println("---------------------------------------------------");

        Epic epic1 = new Epic("Epic1", "Update Epic1", Status.NEW);//3
        SubTask subTask1 = new SubTask("SubTask1", "Delete this task", Status.NEW);//4
        SubTask subTask2 = new SubTask("SubTask2", "Update this tusk", Status.DONE);//5
        SubTask subTask3 = new SubTask("SubTask3", "Delete this tusk", Status.IN_PROGRESS);//6
        SubTask subTask4 = new SubTask("SubTask4", "Delete this tusk", Status.NEW);//7

        Epic epic2 = new Epic("Epic2", "None", Status.NEW);//8
        SubTask subTask5 = new SubTask("SubTask5", "None", Status.DONE);//9
        SubTask subTask6 = new SubTask("SubTask6", "None", Status.NEW);//10

        manager.create(epic1);
        System.out.println("Добавили эпик1");
        printEpic(manager);

        subTask1.setEpic(epic1);
        subTask2.setEpic(epic1);
        subTask3.setEpic(epic1);
        //Попытка добавить один элемент второй раз
        manager.create(epic1);
        System.out.println("Еще раз добавили эпик1");
        printEpic(manager);

        //subTask4 никому не принадлежит - ошибка при добавлении его в менеджер
        manager.create(subTask1);
        manager.create(subTask2);
        manager.create(subTask3);
        System.out.println("Добавили подзадачи в эпик");
        printEpic(manager);
        printSubTasks(manager);

        manager.create(epic2);
        System.out.println("Добавили эпик2");
        printEpic(manager);
        printSubTasks(manager);

        subTask5.setEpic(epic2);
        subTask6.setEpic(epic2);
        manager.create(subTask5);
        manager.create(subTask6);
        System.out.println("Добавили подзадачи в эпик2");
        printEpic(manager);
        printSubTasks(manager);

        //Удаляем Epic2
        manager.deleteEpic(epic2.getId());
        System.out.println("Удалили эпик2");
        printEpic(manager);
        printSubTasks(manager);

        //Удаляем subTask1
        System.out.println("Удалили подзадачу1");
        manager.deleteSubTusk(subTask1.getId());
        printEpic(manager);
        printSubTasks(manager);

        //Обновляем subTask2
        System.out.println("Обновили подзадачу2");
        subTask2.setName("SubTask2UPD");
        manager.update(subTask2);
        printEpic(manager);
        printSubTasks(manager);
    }

    public static void printTasks(TaskManager manager){
        HashMap<Integer, Task> tasks = manager.getTasks();
        for(Integer id:tasks.keySet()){
            Task task = tasks.get(id);
            System.out.println(task.toString());
        }
    }

    public static void printEpic(TaskManager manager){
        HashMap<Integer, Epic> epic = manager.getEpics();
        for(Integer id:epic.keySet()){
            Task task = epic.get(id);
            System.out.println(task.toString());
        }
    }
    public static void printSubTasks(TaskManager manager){
        HashMap<Integer, SubTask> epic = manager.getSubTasks();
        for(Integer id:epic.keySet()){
            Task task = epic.get(id);
            System.out.println(task.toString());
        }
    }
}
