package model;

import java.time.Duration;
import java.time.LocalDateTime;

public class SubTask extends Task {
    private int epicId;//"список задач", к которому относится подзадачи

    public SubTask(String name, String description, Status status, Duration duration, LocalDateTime startTime) {
        super(name, description, status, duration, startTime);
    }

    public SubTask(String name, String description, Status status) {
        super(name, description, status);
    }

    public Integer getEpicId() {
        return epicId;
    }

    @Override
    public TaskType getType() {
        return TaskType.SubTask;
    }

    public void setEpicId(int id) {
        this.epicId = id;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "epic=" + epicId +
                "id=" + this.getId() +
                ", name='" + this.getName() + '\'' +
                ", description='" + this.getDescription() + '\'' +
                ", status=" + this.getStatus() +
                "} ";
    }
}

