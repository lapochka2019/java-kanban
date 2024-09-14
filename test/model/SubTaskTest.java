package model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.InMemoryTaskManager;
import service.Managers;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
@DisplayName("Класс SubTask")
class SubTaskTest {
    @DisplayName("Тест. Если id равны, то и subTask-и равны")
    @Test
    public void shouldReturnTrueIfIdEqual(){
        //Получается, что equals должен смотреть только на id???
        //(Как-то странно, но ладно)
        SubTask subTask1 = new SubTask("SubTask1","Description1",Status.IN_PROGRESS);
        subTask1.setId(3);
        SubTask subTask2 = new SubTask("SubTask2","Description2",Status.NEW);
        subTask2.setId(3);
        Assertions.assertEquals(subTask1,subTask2);
    }
    @DisplayName("Тест.  Объект Subtask нельзя сделать своим же эпиком")
    @Test
    public void shouldReturnTrueIfSubtaskListIsEmpty(){
        SubTask subTask1 = new SubTask("SubTask1","Description1",Status.IN_PROGRESS);
        subTask1.setId(1);
        SubTask subTask2 = new SubTask("SubTask2","Description2", Status.NEW);
        InMemoryTaskManager manager = (InMemoryTaskManager) Managers.getDefault();
        //Я записала в EpicId Id подзадачи
        subTask2.setEpicId(subTask1.getId());
        //Попыталась добавить в список менеджера
        manager.create(subTask2);
        //Должна получить пустой список, если метод работает правильно и не добавляет
        ArrayList<SubTask> subTasks = manager.getSubTasks();
        Assertions.assertTrue(subTasks.isEmpty());
    }
}