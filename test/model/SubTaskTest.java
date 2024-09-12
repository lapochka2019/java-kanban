package model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
@DisplayName("Класс SubTask")
class SubTaskTest {
    @DisplayName("Тест. Если id равны, то и subTask-и равны")
    @Test
    public void shouldReturnTrueIfIdEqual(){
        //Получается, что equals должен смотреть только на id???
        //(Как-то странно, но ладно)
        SubTask subTask1 = new SubTask("Epic1","Description1",Status.IN_PROGRESS);
        subTask1.setId(3);
        SubTask subTask2 = new SubTask("Epic2","Description2",Status.NEW);
        subTask2.setId(3);
        Assertions.assertEquals(subTask1,subTask2);
    }
}