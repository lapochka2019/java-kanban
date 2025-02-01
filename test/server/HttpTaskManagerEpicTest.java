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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("Путь /epics")
public class HttpTaskManagerEpicTest {

    // создаём экземпляр InMemoryTaskManager
    TaskManager manager = Managers.getDefault();
    // передаём его в качестве аргумента в конструктор HttpTaskServer
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = HttpTaskServer.getGson();

    public HttpTaskManagerEpicTest() throws IOException {
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

    @DisplayName("Получить эпики")
    @Test
    public void testGettingEpicListUsingHttp() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Epic1", "Description1");
        Epic epic2 = new Epic("Epic2", "Description2");
        manager.create(epic1);
        manager.create(epic2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonArray jsonArray = jsonElement.getAsJsonArray();
        List<Epic> parsed = gson.fromJson(jsonArray, new EpicTypeToken().getType());
        assertEquals(200, response.statusCode());

        assertEquals(2, parsed.size(), "Некорректный размер списка");
    }

    @DisplayName("Получить один эпик")
    @Test
    public void testGettingEpicUsingHttp() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Epic1", "Description1");
        Epic epic2 = new Epic("Epic2", "Description2");
        manager.create(epic1);
        manager.create(epic2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        Epic newTask = gson.fromJson(jsonObject, Epic.class);
        assertEquals(200, response.statusCode());

        assertEquals("Epic1", newTask.getName(), "Некорректное имя эпика");
    }

    @DisplayName("Получить подзадачи эпика")
    @Test
    public void testGettingEpicSubtaskListUsingHttp() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Epic1", "Description1");
        Epic epic2 = new Epic("Epic2", "Description2");

        SubTask subTask1 = new SubTask("Subtask1", "Description1", Status.IN_PROGRESS, Duration.ofMinutes(10), LocalDateTime.of(2022, JANUARY, 1, 0, 40));
        SubTask subTask2 = new SubTask("Subtask2", "Description2", Status.DONE, Duration.ofMinutes(10), LocalDateTime.of(2022, JANUARY, 1, 1, 0));
        SubTask subTask3 = new SubTask("Subtask3", "Description3", Status.NEW, Duration.ofMinutes(10), LocalDateTime.of(2022, JANUARY, 1, 1, 20));
        SubTask subTask4 = new SubTask("Subtask4", "Description4", Status.NEW, Duration.ofMinutes(10), LocalDateTime.of(2022, JANUARY, 1, 2, 0));
        SubTask subTask5 = new SubTask("Subtask5", "Description5", Status.IN_PROGRESS, Duration.ofMinutes(10),LocalDateTime.of(2022, JANUARY, 1, 2, 30));

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

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonArray jsonArray = jsonElement.getAsJsonArray();
        List<SubTask> parsed = gson.fromJson(jsonArray, new SubTaskTypeToken().getType());

        assertEquals(200, response.statusCode());

        assertEquals(3, parsed.size(), "Некорректное имя эпика");
    }

    @DisplayName("Добавить эпик")
    @Test
    public void testAddingEpicUsingHttp() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Epic1", "Description1");
        String taskJson = gson.toJson(epic1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<Epic> tasksFromManager = manager.getEpics();

        assertNotNull(tasksFromManager, "Эпики не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество эпиков");
        assertEquals("Epic1", tasksFromManager.get(0).getName(), "Некорректное имя эпика");
    }


    @DisplayName("Удалить эпик")
    @Test
    public void testDeletingEpicUsingHttp() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Epic1", "Description1");
        Epic epic2 = new Epic("Epic2", "Description2");
        manager.create(epic1);
        manager.create(epic2);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<Epic> tasksFromManager = manager.getEpics();

        assertEquals(1, tasksFromManager.size(), "Некорректное количество эпиков");
    }

    @DisplayName("Некорректные запросы")
    @Test
    public void testUsingUnsupportedHttpRequests() throws IOException, InterruptedException {
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString("")).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode(), "Обработан некорректный путь");

        url = URI.create("http://localhost:8080/epics");
        request = HttpRequest.newBuilder().uri(url).DELETE().build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(501, response.statusCode(), "Данный метод не предусмотрен для запроса");

    }
}

class EpicTypeToken extends TypeToken<List<Epic>> {
    // здесь ничего не нужно реализовывать
}