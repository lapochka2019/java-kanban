package service;

import file.FileBackedTaskManager;
import file.ManagerSaveException;
import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static file.FileBackedTaskManager.loadFromFile;
import static java.time.Month.*;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Класс FileBackedTaskManager")
public class FileBackedTaskManagerTest {
    FileBackedTaskManager manager;
    InMemoryHistoryManager history = new InMemoryHistoryManager();
    //Пустое сохранение
    @DisplayName("Тест. Сохранить пустой менеджер")
    @Test
    public void shouldReturnTrueIfResultFileContainsOnlyOneString() throws IOException {
        Path file = Paths.get("test1.csv");
        manager = new FileBackedTaskManager(new InMemoryHistoryManager(), file);
        manager.clearTasks();
        List<String> lines = Files.readAllLines(file);
        assertEquals(1, lines.size());
    }
    //Пустое чтение
    @DisplayName("Тест. Считать данные из пустого файла (без первой строки)")
    @Test
    public void shouldReturnTrueIfManagerListsIsEmpty() {
        Path file = Paths.get("test2.csv");
        manager = loadFromFile(file);

        assertEquals(0, manager.getTasks().size());
        assertEquals(0, manager.getEpics().size());
        assertEquals(0, manager.getSubTasks().size());
    }
    //Сохранение нескольких задач
    @DisplayName("Тест. Сохранить заполненный менеджер")
    @Test
    public void shouldReturnTrueIfResultFileIsNotEmpty() throws IOException {
        Path file = Paths.get("test3.csv");
        manager = new FileBackedTaskManager(history,file);

        Task task1 = new Task("Task1","Description1", Status.NEW, Duration.ofMinutes(10), LocalDateTime.of(2022, JANUARY, 1, 0, 0));
        Task task2 = new Task("Task2","Description2", Status.DONE, Duration.ofMinutes(10), LocalDateTime.of(2022, JANUARY, 1, 0, 20));
        Epic epic1 = new Epic("Epic1", "Description1");
        Epic epic2 = new Epic("Epic2", "Description2");
        SubTask subTask1 = new SubTask("Subtask1", "Description1", Status.IN_PROGRESS, Duration.ofMinutes(10), LocalDateTime.of(2022, JANUARY, 1, 0, 40));
        SubTask subTask2 = new SubTask("Subtask2", "Description2", Status.DONE, Duration.ofMinutes(10), LocalDateTime.of(2022, JANUARY, 1, 1, 0));
        SubTask subTask3 = new SubTask("Subtask3", "Description3", Status.NEW, Duration.ofMinutes(10), LocalDateTime.of(2022, JANUARY, 1, 1, 20));
        SubTask subTask4 = new SubTask("Subtask4", "Description4", Status.NEW, Duration.ofMinutes(10), LocalDateTime.of(2022, JANUARY, 1, 2, 0));
        SubTask subTask5 = new SubTask("Subtask5", "Description5", Status.IN_PROGRESS, Duration.ofMinutes(10),LocalDateTime.of(2022, JANUARY, 1, 2, 30));

        manager.create(task1);
        manager.create(task2);

        manager.create(epic1);
        manager.create(epic2);

        subTask1.setEpicId(epic1.getId());
        subTask2.setEpicId(epic1.getId());
        subTask3.setEpicId(epic1.getId());

        subTask4.setEpicId(epic2.getId());
        subTask5.setEpicId(epic2.getId());

        manager.create(subTask1);
        manager.create(subTask2);
        manager.create(subTask3);
        manager.create(subTask4);
        manager.create(subTask5);

        List<String> lines = Files.readAllLines(file);
        assertEquals(10, lines.size());
    }
    //Загрузка нескольких задач
    @DisplayName("Тест. Считать данные из заполненного файла")
    @Test
    public void shouldReturnTrueIfManagerListsIsNotEmpty() {
        Path file = Paths.get("test4.csv");
        manager = loadFromFile(file);

        assertEquals(2, manager.getTasks().size());
        assertEquals(2, manager.getEpics().size());
        assertEquals(5, manager.getSubTasks().size());
    }

    //Обработка ошибки
    //Наверное, лучше было бы вызвать метод с ошибкой, но я не придумала, как вызвать жту ошибку
    @DisplayName("Тест. Обработка ошибки")
    @Test
    public void shouldReturnManagerSaveException() {
        Path file = Paths.get("test4.csv");

        assertDoesNotThrow(()->loadFromFile(file));
    }
}
