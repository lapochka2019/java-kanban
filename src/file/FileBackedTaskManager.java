package file;

import model.*;
import service.InMemoryHistoryManager;
import service.InMemoryTaskManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static file.CSVConverter.getTaskFromString;


public class FileBackedTaskManager extends InMemoryTaskManager {
    private Path file;
    private static final String HEADER = "id,type,name,status,description,epic\n";

    public FileBackedTaskManager(InMemoryHistoryManager history, Path file) {
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
        StringBuilder outputString = new StringBuilder(HEADER);
        for (Task task : tasks.values())
            outputString.append(CSVConverter.converTaskToString(task));
        for (Epic epic : epics.values())
            outputString.append(CSVConverter.converTaskToString(epic));
        for (SubTask subTask : subTasks.values())
            outputString.append(CSVConverter.converTaskToString(subTask));
        try {
            Files.writeString(file, outputString.toString());
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения в файл: ", e);
        }
    }

    //метод для чтения из файла
    public static FileBackedTaskManager loadFromFile(Path file) {
        //Создаем менеджер
        FileBackedTaskManager manager = new FileBackedTaskManager(new InMemoryHistoryManager(), file);
        //Считываем файл
        try {
            List<String> lines = Files.readAllLines(file);
            //Пропускаем первую строку
            if (lines.isEmpty())
                return manager;
            lines.removeFirst();
            //Перебираем строки
            for (String s : lines) {
                Task task = getTaskFromString(s);
                switch (task.getType()) {
                    case TaskType.Task -> {
                        manager.tasks.put(task.getId(), task);
                    }
                    case TaskType.Epic -> {
                        Epic epic = (Epic) task;
                        manager.epics.put(epic.getId(), epic);
                    }
                    case TaskType.SubTask -> {
                        SubTask subTask = (SubTask) task;
                        Epic epic = manager.epics.get(subTask.getEpicId());
                        epic.addSubTask(subTask);
                        manager.subTasks.put(subTask.getId(), subTask);
                    }
                }
                if (task.getId() > manager.idCount) {
                    manager.idCount = task.getId();
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка чтения из файла: ", e);
        }

        return manager;
    }
}