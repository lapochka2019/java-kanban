package model;

import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subTasksId = new ArrayList<>();//список подзадач, относящихся к данному "списку"

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
    }

    public ArrayList<Integer> getSubTusks() {
        return subTasksId;
    }

    public void addSubTask(SubTask subTask) {
        subTasksId.add(subTask.getId());
    }

    public void deleteSubTask(int subTuskId) {
        subTasksId.remove((Integer) subTuskId);
    }

    public void clearSubTasks() {
        subTasksId.clear();
    }

    @Override
    public String toString() {
        String string = "Epic{" + "id=" + this.getId() +
                ", name='" + this.getName() + '\'' +
                ", description='" + this.getDescription() + '\'' +
                ", status=" + this.getStatus() + '\'' +
                ", SubTasks[";
        for (Integer subTask: subTasksId) {
            string += subTask.toString() + "\n";
        }
        string += "]}";
        return string;
    }
}
