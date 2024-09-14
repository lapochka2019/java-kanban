package model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
@DisplayName("Класс Epic")
class EpicTest {
    @DisplayName("Тест. Если id равны, то и epic-и равны")
    @Test
    public void shouldReturnTrueIfIdEqual(){
        //Получается, что equals должен смотреть только на id???
        //(Как-то странно, но ладно)
        Epic epic1 = new Epic("Epic1","Description1");
        epic1.setId(2);
        Epic epic2 = new Epic("Epic2","Description2");
        epic2.setId(2);
        Assertions.assertEquals(epic1,epic2);
    }
}