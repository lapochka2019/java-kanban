package model;

import java.util.ArrayList;

public class Epic extends Task {
    ArrayList<SubTask> subTasks = new ArrayList<>();//список подзадач, относящихся к данному "списку"

    public Epic(String name, String description, Status status) {
        super(name, description, status);
    }

    public ArrayList<SubTask> getSubTusks() {
        return subTasks;
    }

    public void addSubTask(SubTask subTask){
        subTasks.add(subTask);
    }

    public void deleteSubTask(SubTask subTask){
        subTasks.remove(subTask);
    }
    public void clearSubTasks(){
        subTasks.clear();
    }

    public Status calculateEpicStatus(){
        Status status = Status.NEW;
        if(subTasks.isEmpty()){
            this.setStatus(Status.NEW);
            return Status.NEW;
        }

        int doneCount=0;
        for(SubTask subTask : subTasks) {
            if(Status.DONE.equals(subTask.getStatus())) {
                doneCount++;
            }
        }
        if(doneCount== subTasks.size()){
            status = Status.DONE;
        }else if(doneCount==0){
            status = Status.NEW;
        }else{
            status = Status.IN_PROGRESS;
        }
        this.setStatus(status);
        return status;
    }

    @Override
    public String toString() {
        String string = "Epic{"+"id=" + this.getId() +
                ", name='" + this.getName() + '\'' +
                ", description='" + this.getDescription() + '\'' +
                ", status=" + this.getStatus()+ '\'' +
                ", SubTasks[";
        for(SubTask subTask:subTasks){
            string+=subTask.toString()+"\n";
        }
        string+="]}";
        return string;
    }
}
