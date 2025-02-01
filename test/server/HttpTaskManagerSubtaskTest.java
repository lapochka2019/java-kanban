package server;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Путь /subtasks")
public class HttpTaskManagerSubtaskTest {

    // создаём экземпляр InMemoryTaskManager
    TaskManager manager = Managers.getDefault();
    // передаём его в качестве аргумента в конструктор HttpTaskServer
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = HttpTaskServer.getGson();

    public HttpTaskManagerSubtaskTest() throws IOException {
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

    @DisplayName("Добавить подзадачу успешно")
    @Test
    public void testAddingTaskUsingHttpSuccessfully() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Epi description");
        manager.create(epic);
        SubTask subTask = new SubTask("subtask", "description",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        subTask.setEpicId(1);
        String taskJson = gson.toJson(subTask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "При добавлении подзадачи произошла ошибка");

        List<SubTask> subtasksFromManager = manager.getEpicSubTasks(1);

        assertNotNull(subtasksFromManager, "Подзадачи не возвращаются");
        assertEquals(1, subtasksFromManager.size(), "Некорректное количество подзадач");
        assertEquals("subtask", subtasksFromManager.get(0).getName(), "Некорректное имя подзадачи");
    }

    @DisplayName("Добавить подзадачу неуспешно (не найден эпик)")
    @Test
    public void testAddingTaskUsingHttpUnsuccessfully() throws IOException, InterruptedException {
        SubTask subTask = new SubTask("subtask", "description",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        subTask.setEpicId(1);
        String taskJson = gson.toJson(subTask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode(), "Подзадача добавлена успешна");

        List<SubTask> subtasksFromManager = manager.getSubTasks();

        assertEquals(subtasksFromManager.size(), 0, "Подзадачи возвращаются");
    }

    @DisplayName("Обновить подзадачу успешно")
    @Test
    public void testUpdatingTaskUsingHttpSuccessfully() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Epi description");
        SubTask subTask = new SubTask("subtask", "description",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        subTask.setEpicId(1);
        manager.create(epic);
        manager.create(subTask);
        SubTask newSubTask = new SubTask("subtask1", "description1",
                Status.NEW, Duration.ofMinutes(10), LocalDateTime.now());
        newSubTask.setEpicId(1);
        newSubTask.setId(2);
        String taskJson = gson.toJson(newSubTask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "При добавлении подзадачи произошла ошибка");

        List<SubTask> subtasksFromManager = manager.getSubTasks();

        assertEquals(subtasksFromManager.size(), 1, "Подзадачи не возвращаются");
    }

    @DisplayName("Обновить подзадачу неуспешно(пересечение по времени)")
    @Test
    public void testUpdatingTaskUsingHttpUnsuccessfully() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Epi description");
        SubTask subTask1 = new SubTask("subtask1", "description1",
                Status.NEW, Duration.ofMinutes(10), LocalDateTime.of(2025, JANUARY, 31, 12, 0));
        subTask1.setEpicId(1);
        SubTask subTask2 = new SubTask("subtask2", "description2",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.of(2025, JANUARY, 31, 12, 15));
        subTask1.setEpicId(1);
        subTask2.setEpicId(1);
        manager.create(epic);
        manager.create(subTask1);
        manager.create(subTask2);

        SubTask newSubTask = new SubTask("subtask3", "description1",
                Status.NEW, Duration.ofMinutes(10), LocalDateTime.of(2025, JANUARY, 31, 12, 10));
        newSubTask.setEpicId(1);
        newSubTask.setId(1);
        String taskJson = gson.toJson(newSubTask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode(), "При добавлении подзадачи не произошла ошибка");

        assertEquals(manager.getSubTask(2).get().getName(), "subtask1", "Подзадачи обновилась");
    }

    @DisplayName("Удалить подзадачу")
    @Test
    public void testDeletingTaskUsingHttp() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Epi description");
        SubTask subTask1 = new SubTask("subtask1", "description1",
                Status.NEW, Duration.ofMinutes(10), LocalDateTime.of(2025, JANUARY, 31, 12, 0));
        subTask1.setEpicId(1);
        SubTask subTask2 = new SubTask("subtask2", "description2",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.of(2025, JANUARY, 31, 12, 15));
        subTask1.setEpicId(1);
        subTask2.setEpicId(1);
        manager.create(epic);
        manager.create(subTask1);
        manager.create(subTask2);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<SubTask> tasksFromManager = manager.getSubTasks();

        assertEquals(1, tasksFromManager.size(), "Некорректное количество подзадач");
    }

    @DisplayName("Получить задачу успешно")
    @Test
    public void testGettingTaskUsingHttpSuccessfully() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Epi description");
        SubTask subTask1 = new SubTask("subtask1", "description1",
                Status.NEW, Duration.ofMinutes(10), LocalDateTime.of(2025, JANUARY, 31, 12, 0));
        subTask1.setEpicId(1);
        SubTask subTask2 = new SubTask("subtask2", "description2",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.of(2025, JANUARY, 31, 12, 15));
        subTask1.setEpicId(1);
        subTask2.setEpicId(1);
        manager.create(epic);
        manager.create(subTask1);
        manager.create(subTask2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        SubTask newTask = gson.fromJson(jsonObject, SubTask.class);
        assertEquals(200, response.statusCode());

        assertEquals("subtask1", newTask.getName(), "Некорректное имя задачи");
    }

    @DisplayName("Получить задачи успешно")
    @Test
    public void testGettingTaskListUsingHttpSuccessfully() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Epi description");
        SubTask subTask1 = new SubTask("subtask1", "description1",
                Status.NEW, Duration.ofMinutes(10), LocalDateTime.of(2025, JANUARY, 31, 12, 0));
        subTask1.setEpicId(1);
        SubTask subTask2 = new SubTask("subtask2", "description2",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.of(2025, JANUARY, 31, 12, 15));
        subTask1.setEpicId(1);
        subTask2.setEpicId(1);
        manager.create(epic);
        manager.create(subTask1);
        manager.create(subTask2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonArray jsonArray = jsonElement.getAsJsonArray();
        List<SubTask> parsed = gson.fromJson(jsonArray, new SubTaskTypeToken().getType());
        assertEquals(200, response.statusCode());

        assertEquals(2, parsed.size(), "Некорректный размер списка");
    }

    @DisplayName("Некорректные запросы")
    @Test
    public void testUsingUnsupportedHttpRequests() throws IOException, InterruptedException {
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString("")).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode(), "Обработан некорректный путь");

        url = URI.create("http://localhost:8080/subtasks");
        request = HttpRequest.newBuilder().uri(url).DELETE().build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(501, response.statusCode(), "Данный метод не предусмотрен для запроса");

    }
}

class SubTaskTypeToken extends TypeToken<List<SubTask>> {
    // здесь ничего не нужно реализовывать
}