package service;

import model.Status;
import model.Task;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;
@DisplayName("Класс Managers")
class ManagersTest {
    /**Надеюсь, я правильно поняла**/
    @DisplayName("Тест. Убедитесь, что утилитарный класс возвращает корректный экземпляр TaskManager")
    @Test
    public void shouldReturnTrueIfTaskManagerNotNull(){
        InMemoryTaskManager manager = (InMemoryTaskManager) Managers.getDefault();
        Assertions.assertNotNull(manager);
    }

    @DisplayName("Тест. Убедитесь, что утилитарный класс возвращает корректный экземпляр HistoryManager")
    @Test
    public void shouldReturnTrueIfHistoryManagerNotNull(){
        InMemoryHistoryManager manager = (InMemoryHistoryManager) Managers.getDefaultHistory();
        Assertions.assertNotNull(manager);
    }
}