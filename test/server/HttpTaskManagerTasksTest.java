package server;

import com.google.gson.*;

import com.google.gson.reflect.TypeToken;
import model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import service.Managers;
import service.TaskManager;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static java.time.Month.JANUARY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("Путь /tasks")
public class HttpTaskManagerTasksTest {

    // создаём экземпляр InMemoryTaskManager
    TaskManager manager = Managers.getDefault();
    // передаём его в качестве аргумента в конструктор HttpTaskServer
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = HttpTaskServer.getGson();

    public HttpTaskManagerTasksTest() throws IOException {
    }

    @BeforeEach
    public void setUp() {
        manager.clearTasks();
        manager.clearSubTusks();
        manager.clearEpics();
        taskServer.startServer();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stopServer();
    }

    @DisplayName("Добавить задачу успешно")
    @Test
    public void testAddingTaskUsingHttpSuccessfully() throws IOException, InterruptedException {
        Task task = new Task("Test 2", "Testing task 2",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<Task> tasksFromManager = manager.getTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 2", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @DisplayName("Добавить задачу неуспешно (пересечение по времени)")
    @Test
    public void testAddingTaskUsingHttpUnsuccessfully() throws IOException, InterruptedException {
        Task task1 = new Task("Task1", "Testing task 1",
                Status.NEW, Duration.ofMinutes(10), LocalDateTime.of(2025, JANUARY, 31, 12, 0));
        Task task2 = new Task("Task2", "Testing task 2",
                Status.NEW, Duration.ofMinutes(10), LocalDateTime.of(2025, JANUARY, 31, 12, 15));
        manager.create(task1);
        manager.create(task2);

        Task updateTask = new Task("Task2", "Testing task 2",
                Status.NEW, Duration.ofMinutes(10), LocalDateTime.of(2025, JANUARY, 31, 12, 10));
        String taskJson = gson.toJson(updateTask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode());

        List<Task> tasksFromManager = manager.getTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(2, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Task1", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @DisplayName("Обновить задачу успешно")
    @Test
    public void testUpdatingTaskUsingHttpSuccessfully() throws IOException, InterruptedException {
        Task task1 = new Task("Task1", "Testing task 1",
                Status.NEW, Duration.ofMinutes(10), LocalDateTime.of(2025, JANUARY, 31, 12, 0));
        Task task2 = new Task("Task2", "Testing task 2",
                Status.NEW, Duration.ofMinutes(10), LocalDateTime.of(2025, JANUARY, 31, 12, 15));
        manager.create(task1);
        manager.create(task2);

        Task updateTask = new Task("Task2", "Testing task 2",
                Status.NEW, Duration.ofMinutes(10), LocalDateTime.of(2025, JANUARY, 31, 12, 30));
        updateTask.setId(1);
        String taskJson = gson.toJson(updateTask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<Task> tasksFromManager = manager.getTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(2, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Task2", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @DisplayName("Обновить задачу неуспешно(задача не найдена)")
    @Test
    public void testUpdatingTaskUsingHttpUnsuccessfully() throws IOException, InterruptedException {
        Task task1 = new Task("Task1", "Testing task 1",
                Status.NEW, Duration.ofMinutes(10), LocalDateTime.of(2025, JANUARY, 31, 12, 0));
        Task task2 = new Task("Task2", "Testing task 2",
                Status.NEW, Duration.ofMinutes(10), LocalDateTime.of(2025, JANUARY, 31, 12, 15));
        manager.create(task1);
        manager.create(task2);

        Task updateTask = new Task("Task2", "Testing task 2",
                Status.NEW, Duration.ofMinutes(10), LocalDateTime.of(2025, JANUARY, 31, 12, 10));
        updateTask.setId(3);
        String taskJson = gson.toJson(updateTask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());

        List<Task> tasksFromManager = manager.getTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(2, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Task1", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @DisplayName("Удалить задачу")
    @Test
    public void testDeletingTaskUsingHttp() throws IOException, InterruptedException {
        Task task1 = new Task("Task1", "Testing task 1",
                Status.NEW, Duration.ofMinutes(10), LocalDateTime.of(2025, JANUARY, 31, 12, 0));
        Task task2 = new Task("Task2", "Testing task 2",
                Status.NEW, Duration.ofMinutes(10), LocalDateTime.of(2025, JANUARY, 31, 12, 15));
        manager.create(task1);
        manager.create(task2);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<Task> tasksFromManager = manager.getTasks();

        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
    }

    @DisplayName("Получить задачу успешно")
    @Test
    public void testGettingTaskUsingHttpSuccessfully() throws IOException, InterruptedException {
        Task task = new Task("Test 2", "Testing task 2",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        manager.create(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        Task newTask = gson.fromJson(jsonObject, Task.class);
        assertEquals(200, response.statusCode());

        assertEquals("Test 2", newTask.getName(), "Некорректное имя задачи");
    }

    @DisplayName("Получить задачи успешно")
    @Test
    public void testGettingTaskListUsingHttpSuccessfully() throws IOException, InterruptedException {
        Task task1 = new Task("Task1","Description1", Status.NEW, Duration.ofMinutes(10), LocalDateTime.of(2022, JANUARY, 1, 0, 0));
        Task task2 = new Task("Task2","Description2", Status.DONE, Duration.ofMinutes(10), LocalDateTime.of(2022, JANUARY, 1, 0, 20));
        manager.create(task1);
        manager.create(task2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonArray jsonArray = jsonElement.getAsJsonArray();
        List<Task> parsed = gson.fromJson(jsonArray, new EpicTypeToken().getType());
        assertEquals(200, response.statusCode());

        assertEquals(2, parsed.size(), "Некорректный размер списка");
    }

    @DisplayName("Некорректные запросы")
    @Test
    public void testUsingUnsupportedHttpRequests() throws IOException, InterruptedException {
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString("")).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode(), "Обработан некорректный путь");

        url = URI.create("http://localhost:8080/tasks");
        request = HttpRequest.newBuilder().uri(url).DELETE().build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(501, response.statusCode(), "Данный метод не предусмотрен для запроса");

    }
}

class TaskTypeToken extends TypeToken<List<Task>> {
    // здесь ничего не нужно реализовывать
}