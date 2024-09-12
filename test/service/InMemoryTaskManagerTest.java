package service;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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

        subTask1.setEpicId(epic1);
        subTask2.setEpicId(epic1);
        subTask3.setEpicId(epic1);

        subTask4.setEpicId(epic2);
        subTask5.setEpicId(epic2);

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
        SubTask resultTusk = taskManager.getSubTusk(5);
        Assertions.assertEquals(subTask1,resultTusk);
    }

    /**проверьте, что задачи с заданным id и сгенерированным id не конфликтуют внутри менеджера**/
    //Что значит "не конфликтуют"?
    //Как можно им самостоятельно задать id, если он потом все равно будет заменен
    //на сгенерированный при добавлении в менеджер?

    /**создайте тест, в котором проверяется неизменность задачи (по всем полям) при добавлении задачи в менеджер**/
    //Но id же будет изменен (на сгенерированный)


}