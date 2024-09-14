package service;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
@DisplayName("Класс InMemoryHistoryManager")
class InMemoryHistoryManagerTest {
    InMemoryTaskManager taskManager;
    @BeforeEach
    public void init(){
        taskManager = (InMemoryTaskManager) Managers.getDefault();
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
    }
    @DisplayName("Тест. Сохраняется ли состояние \"сохраненной\" задачи")
    @Test
    public void shouldReturnTrueIfSavedTaskState(){
        Task task1 = taskManager.getTask(1);
        Task task1Update = new Task("Task1Update","Description1Update", Status.NEW);
        task1Update.setId(1);
        taskManager.update(task1Update);
        Task savedTask = taskManager.history.getHistory().get(0);
        assertEquals(task1.getId(),savedTask.getId());
        assertEquals(task1.getName(), savedTask.getName());
        assertEquals(task1.getDescription(),savedTask.getDescription());
        assertEquals(task1.getStatus(),savedTask.getStatus());
    }
    @DisplayName("Тест. История хранит не больше 10 задач")
    @Test
    public void shouldReturnTrueIfTaskListSizeEquals10(){
        Random r = new Random();
        for(int i=0;i<4;i++){
            //получается 12 добавлений
            taskManager.getTask(r.nextInt(2)+1);//(0,1)+1=(1,2)
            taskManager.getEpic(r.nextInt(2)+3);//(0,1)+3=(3,4)
            taskManager.getSubTusk(r.nextInt(5)+5);//(0,1,2,3,4) + 5 = (5,6,7,8,9)
        }
        ArrayList<Task> savedList = taskManager.history.getHistory();
        assertEquals(10, savedList.size());
    }
}