package file;

import model.*;
import service.InMemoryHistoryManager;
import service.InMemoryTaskManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private Path file;
    public FileBackedTaskManager (InMemoryHistoryManager history, Path file) {
        super(history);
        this.file = file;
    }

    @Override
    public void clearTasks() {
        super.clearTasks();
        save();
    }

    @Override
    public void clearEpics() {
        super.clearEpics();
        save();
    }

    @Override
    public void clearSubTusks() {
        super.clearSubTusks();
        save();
    }

    @Override
    public Task create(Task task) {
        Task task1 = super.create(task);
        save();
        return task1;
    }

    @Override
    public Epic create(Epic epic) {
        Epic epic1 = super.create(epic);
        save();
        return epic1;
    }

    @Override
    public SubTask create(SubTask subTask) {
        SubTask subTusk1 = super.create(subTask);
        save();
        return subTusk1;
    }

    //Может, имеет смысл эти методы в родительском классе сделать void?
    @Override
    public Task update(Task task) {
        Task task1 = super.update(task);
        save();
        return task1;
    }

    @Override
    public Epic update(Epic epic) {
        Epic epic1 = super.update(epic);
        save();
        return epic1;
    }

    @Override
    public SubTask update(SubTask subTask) {
        SubTask subTusk1 = super.update(subTask);
        save();
        return subTusk1;
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public void deleteSubTusk(int id) {
        super.deleteSubTusk(id);
        save();
    }

    public void save() {
        StringBuilder outputString = new StringBuilder("id,type,name,status,description,epic\n");
        for (Task task:tasks.values())
            outputString.append(toString(task));
        for (Epic epic:epics.values())
            outputString.append(toString(epic));
        for (SubTask subTask:subTasks.values())
            outputString.append(toString(subTask));
        try {
            Files.writeString(file, outputString.toString());
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения в файл: ",e);
        }
    }

    //метод для чтения из файла
    public static FileBackedTaskManager loadFromFile(Path file) {
        //Создаем менеджер
        FileBackedTaskManager manager = new FileBackedTaskManager(new InMemoryHistoryManager(),file);
        //Считываем файл
        try {
            List<String> lines = Files.readAllLines(file);
            //Пропускаем первую строку
            if (lines.isEmpty())
                return manager;
            lines.removeFirst();
            //Перебираем строки
            for (String s:lines) {
                manager.getTaskFromString(s);
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка чтения из файла: ",e);
        }

        return manager;
    }
    //метод для записи данных из файла в мапы
    public void getTaskFromString(String inputString) {
        String[] taskStringArray = inputString.split(",");

        int id = Integer.parseInt(taskStringArray[0]);
        TaskType type = TaskType.valueOf(taskStringArray[1]);
        String name = taskStringArray[2];
        Status status = Status.valueOf(taskStringArray[3]);
        String description = taskStringArray[4];

        switch (type) {
            case TaskType.Task -> {
                Task task = new Task(name,description,status);
                task.setId(id);
                tasks.put(id,task);
            }
            case TaskType.Epic -> {
                Epic epic = new Epic(name,description);
                epic.setId(id);
                epic.setStatus(status);
                epics.put(id,epic);
            }
            case TaskType.SubTask -> {
                int epicId = Integer.parseInt(taskStringArray[5]);
                SubTask subTask = new SubTask(name,description,status);
                subTask.setId(id);
                subTask.setEpicId(epicId);
                Epic epic = epics.get(epicId);
                epic.addSubTask(subTask);
                subTasks.put(id,subTask);
            }
        }
        idCount = id;
    }

    public String toString(Task task) {
        return task.getId() + "," + task.getType() + "," +task.getName() + "," +
                task.getStatus() + "," +task.getDescription() + "," +task.getEpicId() + "\n";
    }

    public static void main(String[] args) {
        Path file = Paths.get("newFile.csv");
        InMemoryHistoryManager history = new InMemoryHistoryManager();
        FileBackedTaskManager taskManager = new FileBackedTaskManager(history,file);
        Task task1 = new Task("Task1","Description1", Status.NEW);
        Task task2 = new Task("Task2","Description2", Status.DONE);
        Epic epic1 = new Epic("Epic1", "Description1");
        Epic epic2 = new Epic("Epic2", "Description2");
        SubTask subTask1 = new SubTask("Subtask1", "Description1", Status.IN_PROGRESS);
        SubTask subTask2 = new SubTask("Subtask2", "Description2", Status.DONE);
        SubTask subTask3 = new SubTask("Subtask3", "Description3", Status.NEW);
        SubTask subTask4 = new SubTask("Subtask4", "Description4", Status.NEW);
        SubTask subTask5 = new SubTask("Subtask5", "Description5", Status.IN_PROGRESS);

        taskManager.create(task1);
        taskManager.create(task2);

        taskManager.create(epic1);
        taskManager.create(epic2);

        subTask1.setEpicId(epic1.getId());
        subTask2.setEpicId(epic1.getId());
        subTask3.setEpicId(epic1.getId());

        subTask4.setEpicId(epic2.getId());
        subTask5.setEpicId(epic2.getId());

        taskManager.create(subTask1);
        taskManager.create(subTask2);
        taskManager.create(subTask3);
        taskManager.create(subTask4);
        taskManager.create(subTask5);

        FileBackedTaskManager manager = loadFromFile(file);
        manager.create(task1);
    }
}
//ManagerSaveException