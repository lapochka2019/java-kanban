package service;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static java.time.Month.JANUARY;
import static org.junit.jupiter.api.Assertions.*;
@DisplayName("Класс InMemoryHistoryManager")
class InMemoryHistoryManagerTest {
    InMemoryTaskManager taskManager;
    @BeforeEach
    public void init(){
        taskManager = (InMemoryTaskManager) Managers.getDefault();
        Task task1 = new Task("Task1","Description1", Status.NEW, Duration.ofMinutes(10), LocalDateTime.of(2022, JANUARY, 1, 0, 0));
        Task task2 = new Task("Task2","Description2", Status.DONE, Duration.ofMinutes(10), LocalDateTime.of(2022, JANUARY, 1, 0, 20));
        Epic epic1 = new Epic("Epic1", "Description1");
        Epic epic2 = new Epic("Epic2", "Description2");
        SubTask subTask1 = new SubTask("Subtask1", "Description1", Status.IN_PROGRESS, Duration.ofMinutes(10), LocalDateTime.of(2022, JANUARY, 1, 0, 40));
        SubTask subTask2 = new SubTask("Subtask2", "Description2", Status.DONE, Duration.ofMinutes(10), LocalDateTime.of(2022, JANUARY, 1, 1, 0));
        SubTask subTask3 = new SubTask("Subtask3", "Description3", Status.NEW, Duration.ofMinutes(10), LocalDateTime.of(2022, JANUARY, 1, 1, 20));
        SubTask subTask4 = new SubTask("Subtask4", "Description4", Status.NEW, Duration.ofMinutes(10), LocalDateTime.of(2022, JANUARY, 1, 2, 0));
        SubTask subTask5 = new SubTask("Subtask5", "Description5", Status.IN_PROGRESS, Duration.ofMinutes(10),LocalDateTime.of(2022, JANUARY, 1, 2, 30));


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
        Task task1 = taskManager.getTask(1).get();
        Task task1Update = new Task("Task1Update","Description1Update", Status.NEW);
        task1Update.setId(1);
        taskManager.update(task1Update);
        Task savedTask = taskManager.history.getHistory().get(0);
        assertEquals(task1.getId(),savedTask.getId());
        assertEquals(task1.getName(), savedTask.getName());
        assertEquals(task1.getDescription(),savedTask.getDescription());
        assertEquals(task1.getStatus(),savedTask.getStatus());
    }
    @DisplayName("Тест. История не хранит повторные просмотры задач")
    @Test
    public void shouldReturnTrueIfHistoryNotContainsDuplicates(){
        for(int i=0;i<2;i++){
            //получается 12 добавлений
            taskManager.getTask(1);
            taskManager.getEpic(3);
            taskManager.getSubTask(5);
        }
        ArrayList<Task> savedList = taskManager.history.getHistory();
        assertEquals(3, savedList.size());
    }
    @DisplayName("Тест. История сохраняет порядок просмотра задач")
    @Test
    public void shouldReturnTrueIfHistorySaveViewingOrder(){
        ArrayList<Task> sampleList = new ArrayList<>();
        sampleList.add(taskManager.getTask(1).get());
        sampleList.add(taskManager.getEpic(3).get());
        sampleList.add(taskManager.getSubTask(5).get());
        taskManager.getTask(1);
        taskManager.getEpic(3);
        taskManager.getSubTask(5);
        ArrayList<Task> savedList = taskManager.history.getHistory();
        assertIterableEquals(sampleList, savedList);
    }

    @DisplayName("Тест. Удалите задачу, которая есть в истории, и проверьте, что при печати она не будет выводиться")
    @Test
    public void shouldReturnTrueIfHistoryRemoveDeletedTask(){
        taskManager.getTask(1);
        taskManager.getEpic(3);
        taskManager.getSubTask(5);
        taskManager.deleteTask(1);
        ArrayList<Task> savedList = taskManager.history.getHistory();
        assertEquals(2, savedList.size());
    }

    @DisplayName("Тест. Удалите эпик с тремя подзадачами и убедитесь, что из истории удалился как сам эпик, так и все его подзадачи")
    @Test
    public void shouldReturnTrueIfEpicRemoved(){
        taskManager.getTask(1);
        taskManager.getEpic(3);
        taskManager.getSubTask(5);
        taskManager.getSubTask(6);
        taskManager.deleteEpic(3);
        ArrayList<Task> savedList = taskManager.history.getHistory();
        assertEquals(1, savedList.size());
    }

    @DisplayName("Тест. Удаление задач из истории при удалении всех задач из менеджера")
    @Test
    public void shouldReturnTrueIfAllTasksRemoved(){
        taskManager.getTask(1);
        taskManager.getTask(2);
        taskManager.getEpic(3);
        taskManager.getSubTask(5);
        taskManager.getSubTask(6);
        taskManager.clearTasks();
        ArrayList<Task> savedList = taskManager.history.getHistory();
        assertEquals(3, savedList.size());
    }

    @DisplayName("Тест. Удаление подзадач из истории при удалении их из менеджера")
    @Test
    public void shouldReturnTrueIfAllSubtasksRemoved(){
        taskManager.getTask(1);
        taskManager.getTask(2);
        taskManager.getEpic(3);
        taskManager.getEpic(4);
        taskManager.getSubTask(5);
        taskManager.getSubTask(6);
        taskManager.clearSubTusks();
        ArrayList<Task> savedList = taskManager.history.getHistory();
        assertEquals(4, savedList.size());
    }

    @DisplayName("Тест. Удаление подзадач и эпиков из истории при удалении их из менеджера")
    @Test
    public void shouldReturnTrueIfAllEpicsRemoved(){
        taskManager.getTask(1);
        taskManager.getTask(2);
        taskManager.getEpic(3);
        taskManager.getEpic(4);
        taskManager.getSubTask(5);
        taskManager.getSubTask(6);
        taskManager.clearEpics();
        ArrayList<Task> savedList = taskManager.history.getHistory();
        assertEquals(2, savedList.size());
    }
}