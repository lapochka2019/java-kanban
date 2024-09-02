package model;

public class SubTask extends Task{
    Epic epic;//"список задач", к которому относится подзадачи

    public SubTask(String name, String description, Status status) {
        super(name, description, status);
    }

    public Epic getEpic() {
        return epic;
    }

    public void setEpic(Epic epic) {
        this.epic = epic;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "epic=" + epic.getId() +
                "id=" + this.getId() +
                ", name='" + this.getName() + '\'' +
                ", description='" + this.getDescription() + '\'' +
                ", status=" + this.getStatus()+
                "} ";
    }
}
