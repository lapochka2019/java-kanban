package model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
@DisplayName("Класс Task")
public class TaskTest {
    @DisplayName("Тест. Если id равны, то и task-и равны")
    @Test
    public void shouldReturnTrueIfIdEqual(){
        //Получается, что equals должен смотреть только на id???
        //Из лекции я опять же не совсем поняла, нужно ли "доработать" тест, чтобы он сравнивал все поля
        //или оставить только проверку по id :_(
        Task task1 = new Task("Task1","Description1", Status.NEW);
        task1.setId(1);
        Task task2 = new Task("Task2","Description2", Status.DONE);
        task2.setId(1);
        Assertions.assertEquals(task1,task2);
    }
}
