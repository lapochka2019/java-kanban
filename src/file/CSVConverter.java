package file;

import model.*;

public class CSVConverter {

    public static String converTaskToString(Task task) {
        if (TaskType.SubTask.equals(task.getType())) {
            SubTask subTask = (SubTask) task;
            return subTask.getId() + "," + subTask.getType() + "," + subTask.getName() + "," +
                    subTask.getStatus() + "," + subTask.getDescription() + "," + subTask.getEpicId() + "\n";
        } else {
            return task.getId() + "," + task.getType() + "," + task.getName() + "," +
                    task.getStatus() + "," + task.getDescription() + "\n";
        }
    }

    public static Task getTaskFromString(String inputString) {
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
                return task;
            }
            case TaskType.Epic -> {
                Epic epic = new Epic(name,description);
                epic.setId(id);
                epic.setStatus(status);
                return epic;
            }
            case TaskType.SubTask -> {
                int epicId = Integer.parseInt(taskStringArray[5]);
                SubTask subTask = new SubTask(name,description,status);
                subTask.setId(id);
                subTask.setEpicId(epicId);
                return subTask;
            }
        }
        return null;
    }
}
