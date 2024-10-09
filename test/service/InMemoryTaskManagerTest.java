package service;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Класс InMemoryTaskManager")
class InMemoryTaskManagerTest {
    InMemoryTaskManager taskManager;
    @BeforeEach
    public void init(){
        taskManager = (InMemoryTaskManager) Managers.getDefault();
        Task task1 = new Task("Task1","Description1", Status.NEW);
        Task task2 = new Task("Task2","Description2", Status.DONE);
        Epic epic1 = new Epic("Epic1", "Description1");
        Epic epic2 = new Epic("Epic2", "Description2");
        SubTask subTask1 = new SubTask("Subtusk1", "Description1", Status.IN_PROGRESS);
        SubTask subTask2 = new SubTask("Subtusk2", "Description2", Status.DONE);
        SubTask subTask3 = new SubTask("Subtusk3", "Description3", Status.NEW);
        SubTask subTask4 = new SubTask("Subtusk4", "Description4", Status.NEW);
        SubTask subTask5 = new SubTask("Subtusk5", "Description5", Status.IN_PROGRESS);

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
    public void shouldReturnTwoIfRealContainsTasks(){
        int count = taskManager.getTasks().size();
        Assertions.assertEquals(2,count);
    }
    @DisplayName("Тест. Хранятся ли Эпики")
    @Test
    public void shouldReturnTwoIfRealContainsEpics(){
        int count = taskManager.getEpics().size();
        Assertions.assertEquals(2,count);
    }
    @DisplayName("Тест. Хранятся ли подзадачи")
    @Test
    public void shouldReturnFiveIfRealContainsSubtasks(){
        int count = taskManager.getSubTasks().size();
        Assertions.assertEquals(5,count);
    }
//Сильно не уверена, что это корректная проверка
    @DisplayName("Тест. Получаем Task по id")
    @Test
    public void shouldReturnTaskIfRealContainsTasks(){
        Task task1 = new Task("Task1","Description1", Status.NEW);
        task1.setId(1);
        Task resultTusk = taskManager.getTask(1);
        Assertions.assertEquals(task1,resultTusk);
    }

    @DisplayName("Тест. Получаем Epic по id")
    @Test
    public void shouldReturnEpicIfRealContainsEpics(){
        Epic epic1 = new Epic("Epic1", "Description1");
        epic1.setId(3);
        Epic resultEpic = taskManager.getEpic(3);
        Assertions.assertEquals(epic1,resultEpic);
    }

    @DisplayName("Тест. Получаем SubTask по id")
    @Test
    public void shouldReturnSubtaskIfRealContainsSubtasks(){
        SubTask subTask1 = new SubTask("Subtusk1", "Description1", Status.IN_PROGRESS);
        subTask1.setId(5);
        SubTask resultTusk = taskManager.getSubTask(5);
        Assertions.assertEquals(subTask1,resultTusk);
    }

    @DisplayName("Тест. Проверка неизменности полей при добавлении в taskManager (кроме id)")
    @Test
    public void shouldReturnTrueIfTaskBeforeSaveEqualsSavedTask(){
        Task task3 = new Task("Task3","Description1", Status.NEW);
        taskManager.create(task3);
        Task savedTask = taskManager.getTask(task3.getId());
        assertEquals(task3.getName(), savedTask.getName());
        assertEquals(task3.getDescription(),savedTask.getDescription());
        assertEquals(task3.getStatus(),savedTask.getStatus());
    }
    //Удаляемые подзадачи не должны хранить внутри себя старые id???

    @DisplayName("Тест. Внутри эпиков не должно оставаться неактуальных id подзадач")
    @Test
    public void shouldReturnTrueIfEpicContainsOnlyActualTasks(){
        ArrayList<SubTask> oldTasks = taskManager.getEpicSubTasks(3);
        SubTask subTask1 = new SubTask("Subtusk1", "Description1", Status.IN_PROGRESS);
        oldTasks.remove(subTask1);
        taskManager.deleteSubTusk(5);
        ArrayList<SubTask> newTasks = taskManager.getEpicSubTasks(3);
        assertEquals(oldTasks.size(),newTasks.size()+1);
    }
//С помощью сеттеров экземпляры задач позволяют изменить
// любое своё поле, но это может повлиять на данные внутри менеджера. Протестируйте эти кейсы
}