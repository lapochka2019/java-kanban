package model;

public class SubTask extends Task{
    private int epicId;//"список задач", к которому относится подзадачи

    public SubTask(String name, String description, Status status) {
        super(name, description, status);
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(Epic epic) {
        this.epicId = epic.getId();
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "epic=" + epicId +
                "id=" + this.getId() +
                ", name='" + this.getName() + '\'' +
                ", description='" + this.getDescription() + '\'' +
                ", status=" + this.getStatus()+
                "} ";
    }
}
