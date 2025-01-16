package file;

import model.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CSVConverter {

    public static final DateTimeFormatter DATE_TIME_FORMATTER  = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    public static String converTaskToString(Task task) {
        return task.getId() + "," + task.getType() + "," + task.getName() + "," +
                task.getStatus() + "," + task.getDescription() + "," + task.getEpicId() +
                "," + task.getDuration().toMinutes() + "," + (task.getStartTime()==null?null:task.getStartTime().format(DATE_TIME_FORMATTER)) + "\n";
    }

    public static Task getTaskFromString(String inputString) {
        String[] taskStringArray = inputString.split(",");
        int id = Integer.parseInt(taskStringArray[0]);
        TaskType type = TaskType.valueOf(taskStringArray[1]);
        String name = taskStringArray[2];
        Status status = Status.valueOf(taskStringArray[3]);
        String description = taskStringArray[4];
        Integer epicId = taskStringArray[5].equals("null")?null:Integer.parseInt(taskStringArray[5]);
        Duration duration = Duration.ofMinutes(Integer.parseInt(taskStringArray[6]));
        LocalDateTime startTime = taskStringArray[7].equals("null")?null:LocalDateTime.parse(taskStringArray[7],DATE_TIME_FORMATTER);

        switch (type) {
            case TaskType.Task -> {
                Task task = new Task(name,description,status,duration,startTime);
                task.setId(id);
                return task;
            }
            case TaskType.Epic -> {
                Epic epic = new Epic(name,description);
                epic.setDuration(duration);
                epic.setStartTime(startTime);
                epic.setId(id);
                epic.setStatus(status);
                return epic;
            }
            case TaskType.SubTask -> {

                SubTask subTask = new SubTask(name,description,status,duration,startTime);
                subTask.setId(id);
                subTask.setEpicId(epicId);
                return subTask;
            }
        }
        return null;
    }
}
