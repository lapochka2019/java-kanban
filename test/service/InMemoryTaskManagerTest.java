package service;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static java.time.Month.JANUARY;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Класс InMemoryTaskManager")
//Если честно, вообще не поняла смысл абстрактного класса для тестов.
class InMemoryTaskManagerTest {

    InMemoryTaskManager taskManager;

    @BeforeEach
    public void init() {
        taskManager = (InMemoryTaskManager) Managers.getDefault();
        Task task1 = new Task("Task1", "Description1", Status.NEW, Duration.ofMinutes(10), LocalDateTime.of(2022, JANUARY, 1, 0, 0));
        Task task2 = new Task("Task2", "Description2", Status.DONE, Duration.ofMinutes(10), LocalDateTime.of(2022, JANUARY, 1, 0, 20));
        Epic epic1 = new Epic("Epic1", "Description1");
        Epic epic2 = new Epic("Epic2", "Description2");
        SubTask subTask1 = new SubTask("Subtask1", "Description1", Status.IN_PROGRESS, Duration.ofMinutes(10), LocalDateTime.of(2022, JANUARY, 1, 0, 40));
        SubTask subTask2 = new SubTask("Subtask2", "Description2", Status.DONE, Duration.ofMinutes(10), LocalDateTime.of(2022, JANUARY, 1, 1, 0));
        SubTask subTask3 = new SubTask("Subtask3", "Description3", Status.NEW, Duration.ofMinutes(10), LocalDateTime.of(2022, JANUARY, 1, 1, 20));
        SubTask subTask4 = new SubTask("Subtask4", "Description4", Status.NEW, Duration.ofMinutes(10), LocalDateTime.of(2022, JANUARY, 1, 2, 0));
        SubTask subTask5 = new SubTask("Subtask5", "Description5", Status.IN_PROGRESS, Duration.ofMinutes(10), LocalDateTime.of(2022, JANUARY, 1, 2, 30));


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

    @DisplayName("Тест. Хранятся ли Таски")
    @Test
    public void shouldReturnTwoIfRealContainsTasks() {
        int count = taskManager.getTasks().size();
        Assertions.assertEquals(2, count);
    }

    @DisplayName("Тест. Хранятся ли Эпики")
    @Test
    public void shouldReturnTwoIfRealContainsEpics() {
        int count = taskManager.getEpics().size();
        Assertions.assertEquals(2, count);
    }

    @DisplayName("Тест. Хранятся ли подзадачи")
    @Test
    public void shouldReturnFiveIfRealContainsSubtasks() {
        int count = taskManager.getSubTasks().size();
        Assertions.assertEquals(5, count);
    }

    @DisplayName("Тест. Сохранение задачи без эпика")
    @Test
    public void shouldReturnFiveIfDoNotCreateSubtask() {
        SubTask subTask = new SubTask("Subtusk", "Description", Status.IN_PROGRESS);
        taskManager.create(subTask);
        int count = taskManager.getSubTasks().size();
        Assertions.assertEquals(5, count);
    }

    @DisplayName("Тест. Все подзадачи Эпика со статусом NEW")
    @Test
    public void shouldReturnTrueIfStatusIsNew() {
        Epic epic = taskManager.getEpic(3).get();
        for (Integer i : epic.getSubTusks()) {
            SubTask s = taskManager.getSubTask(i).get();
            s.setStatus(Status.NEW);
            taskManager.update(s);
        }
        Assertions.assertEquals(Status.NEW, epic.getStatus());
    }

    @DisplayName("Тест. Все подзадачи Эпика со статусом DONE")
    @Test
    public void shouldReturnTrueIfStatusIsDONE() {
        Epic epic = taskManager.getEpic(3).get();
        for (Integer i : epic.getSubTusks()) {
            SubTask s = taskManager.getSubTask(i).get();
            s.setStatus(Status.DONE);
            taskManager.update(s);
        }
        Assertions.assertEquals(Status.DONE, epic.getStatus());
    }

    @DisplayName("Тест. Подзадачи Эпика со статусами NEW и DONE")
    @Test
    public void shouldReturnTrueIfStatusIsInProgress() {
        Epic epic = taskManager.getEpic(3).get();
        for (Integer i : epic.getSubTusks()) {
            SubTask s = taskManager.getSubTask(i).get();
            if (i % 2 == 0)
                s.setStatus(Status.NEW);
            else
                s.setStatus(Status.DONE);
            taskManager.update(s);
        }
        Assertions.assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }

    @DisplayName("Тест. Все подзадачи Эпика со статусом IN_PROGRESS")
    @Test
    public void shouldReturnTrueIfStatusIsIN_PROGRESS() {
        Epic epic = taskManager.getEpic(3).get();
        for (Integer i : epic.getSubTusks()) {
            SubTask s = taskManager.getSubTask(i).get();
            s.setStatus(Status.IN_PROGRESS);
            taskManager.update(s);
        }
        Assertions.assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }

    @DisplayName("Тест. Получаем Task по id")
    @Test
    public void shouldReturnTaskIfRealContainsTasks() {
        Task task1 = new Task("Task1", "Description1", Status.NEW);
        task1.setId(1);
        Task resultTusk = taskManager.getTask(1).get();
        Assertions.assertEquals(task1, resultTusk);
    }

    @DisplayName("Тест. Получаем Epic по id")
    @Test
    public void shouldReturnEpicIfRealContainsEpics() {
        Epic epic1 = new Epic("Epic1", "Description1");
        epic1.setId(3);
        Epic resultEpic = taskManager.getEpic(3).get();
        Assertions.assertEquals(epic1, resultEpic);
    }

    @DisplayName("Тест. Получаем SubTask по id")
    @Test
    public void shouldReturnSubtaskIfRealContainsSubtasks() {
        SubTask subTask1 = new SubTask("Subtusk1", "Description1", Status.IN_PROGRESS);
        subTask1.setId(5);
        SubTask resultTusk = taskManager.getSubTask(5).get();
        Assertions.assertEquals(subTask1, resultTusk);
    }

    @DisplayName("Тест. Проверка неизменности полей при добавлении в taskManager (кроме id)")
    @Test
    public void shouldReturnTrueIfTaskBeforeSaveEqualsSavedTask() {
        Task task3 = new Task("Task3", "Description1", Status.NEW);
        taskManager.create(task3);
        Task savedTask = taskManager.getTask(task3.getId()).get();
        assertEquals(task3.getName(), savedTask.getName());
        assertEquals(task3.getDescription(), savedTask.getDescription());
        assertEquals(task3.getStatus(), savedTask.getStatus());
    }

    @DisplayName("Тест. Внутри эпиков не должно оставаться неактуальных id подзадач")
    @Test
    public void shouldReturnTrueIfEpicContainsOnlyActualTasks() {
        ArrayList<SubTask> oldTasks = taskManager.getEpicSubTasks(3);
        SubTask subTask1 = new SubTask("Subtusk1", "Description1", Status.IN_PROGRESS);
        oldTasks.remove(subTask1);
        taskManager.deleteSubTusk(5);
        ArrayList<SubTask> newTasks = taskManager.getEpicSubTasks(3);
        assertEquals(oldTasks.size(), newTasks.size() + 1);
    }

    //Удалить задачу -> в менеджере меньше задач
    @DisplayName("Тест. Удалить одну задачу")
    @Test
    public void shouldReturnOneIfRealDeletedTasks() {
        taskManager.deleteTask(1);
        int count = taskManager.getTasks().size();
        Assertions.assertEquals(1, count);
    }

    //Удалить эпик -> в менеджере меньше эпиков и подзадач
    @DisplayName("Тест. Удалить один Эпик и все его подзадачи")
    @Test
    public void shouldReturnOneIfRealDeletedEpics() {
        taskManager.deleteEpic(4);
        int count = taskManager.getEpics().size();
        Assertions.assertEquals(1, count);
        count = taskManager.getSubTasks().size();
        Assertions.assertEquals(3, count);
    }

    //Удалить подзадачу -> в менеджере меньше подзадач
    @DisplayName("Тест. Удалить одну подзадачу")
    @Test
    public void shouldReturnOneIfRealDeletedSubtasks() {
        taskManager.deleteSubTusk(5);
        int count = taskManager.getSubTasks().size();
        Assertions.assertEquals(4, count);
    }

    //Удалить все задачи
    @DisplayName("Тест. Удалить все задачи")
    @Test
    public void shouldReturnNullIfRemoveAllTasks() {
        taskManager.clearTasks();
        int count = taskManager.getTasks().size();
        Assertions.assertEquals(0, count);
    }

    //Удалить все эпики
    @DisplayName("Тест. Удалить все эпики")
    @Test
    public void shouldReturnNullIfRemoveAllEpics() {
        taskManager.clearEpics();
        int count = taskManager.getEpics().size();
        Assertions.assertEquals(0, count);
        count = taskManager.getSubTasks().size();
        Assertions.assertEquals(0, count);
    }

    //Удалить все подзадачи
    @DisplayName("Тест. Удалить все подзадачи")
    @Test
    public void shouldReturnNullIfRemoveAllSubtasks() {
        taskManager.clearSubTusks();
        int count = taskManager.getSubTasks().size();
        Assertions.assertEquals(0, count);
    }

    //Обновить Задачу (текст)
    @DisplayName("Тест. Удалить все подзадачи")
    @Test
    public void shouldReturnTrueIfTaskNameAndTimeChanged() {
        Task task = new Task("UpdatedTask", "Description1", Status.NEW, Duration.ofMinutes(10), LocalDateTime.of(2022, JANUARY, 2, 0, 0));
        task.setId(1);
        taskManager.update(task);
        Assertions.assertEquals("UpdatedTask", taskManager.getTask(1).get().getName());
    }

    //Обновить задачу (время) -> неуспешно
    @DisplayName("Тест. Удалить все подзадачи")
    @Test
    public void shouldReturnFalseIfTaskTimeNotChanged() {
        Task task = new Task("UpdatedTask", "Description1", Status.NEW, Duration.ofMinutes(10), LocalDateTime.of(2022, JANUARY, 1, 0, 15));
        task.setId(1);
        taskManager.update(task);
        Assertions.assertNotEquals("UpdatedTask", taskManager.getTask(1).get().getName());
    }
}