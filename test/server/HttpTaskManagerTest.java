package server;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.InMemoryTaskManager;
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

@DisplayName("Пути /history и /prioritized")
public class HttpTaskManagerTest {
    // создаём экземпляр InMemoryTaskManager
    TaskManager taskManager = Managers.getDefault();
    // передаём его в качестве аргумента в конструктор HttpTaskServer
    HttpTaskServer taskServer = new HttpTaskServer(taskManager);
    Gson gson = HttpTaskServer.getGson();

    public HttpTaskManagerTest() throws IOException {
    }

    @BeforeEach
    public void init() {
        taskManager.clearTasks();
        taskManager.clearSubTusks();
        taskManager.clearEpics();

        Task task1 = new Task("Task1", "Description1", Status.NEW, Duration.ofMinutes(10), LocalDateTime.of(2022, JANUARY, 1, 0, 20));
        Task task2 = new Task("Task2", "Description2", Status.DONE, Duration.ofMinutes(10), LocalDateTime.of(2022, JANUARY, 1, 0, 0));
        Epic epic1 = new Epic("Epic1", "Description1");
        Epic epic2 = new Epic("Epic2", "Description2");
        SubTask subTask1 = new SubTask("Subtask1", "Description1", Status.IN_PROGRESS, Duration.ofMinutes(10), LocalDateTime.of(2022, JANUARY, 1, 0, 40));
        SubTask subTask2 = new SubTask("Subtask2", "Description2", Status.DONE, Duration.ofMinutes(10), LocalDateTime.of(2022, JANUARY, 1, 1, 0));
        SubTask subTask3 = new SubTask("Subtask3", "Description3", Status.NEW, Duration.ofMinutes(10), LocalDateTime.of(2022, JANUARY, 1, 1, 20));
        SubTask subTask4 = new SubTask("Subtask4", "Description4", Status.NEW, Duration.ofMinutes(10), LocalDateTime.of(2022, JANUARY, 1, 2, 0));
        SubTask subTask5 = new SubTask("Subtask5", "Description5", Status.IN_PROGRESS, Duration.ofMinutes(10), LocalDateTime.of(2022, JANUARY, 1, 2, 30));


        taskManager.create(task1);
        taskManager.create(task2);

        taskManager.create(epic1);
        taskManager.create(epic2);

        subTask1.setEpicId(epic1.getId());
        subTask2.setEpicId(epic1.getId());
        subTask3.setEpicId(epic1.getId());

        subTask4.setEpicId(epic2.getId());
        subTask5.setEpicId(epic2.getId());

        taskManager.create(subTask1);
        taskManager.create(subTask2);
        taskManager.create(subTask3);
        taskManager.create(subTask4);
        taskManager.create(subTask5);

        taskServer.startServer();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stopServer();
    }

    @DisplayName("Сохраняется ли история при просмотре через http и получение истории через http")
    @Test
    public void testHistoryManagerInHttpServer() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url;
        HttpRequest request;
        //Получаем разные задачи через запросы
        String path = "http://localhost:8080/tasks/";
        //первая в списке истории должна быть задача 3
        for(int i=1;i<3;i++){
            url = URI.create(path + i);
            request = HttpRequest.newBuilder().uri(url).GET().build();
            client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        url = URI.create("http://localhost:8080/history");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonArray jsonArray = jsonElement.getAsJsonArray();
        List<Task> parsed = gson.fromJson(jsonArray, new TaskTypeToken().getType());

        assertEquals(200, response.statusCode());
        assertEquals(2, parsed.size(), "Некорректный размер списка");
        assertEquals("Task1", parsed.get(0).getName(), "Некорректный  первый элемент списка");
    }


    @DisplayName("Получить задачи в порядке приоритета через http ")
    @Test
    public void testPrioritizeSortingInHttpServer() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonArray jsonArray = jsonElement.getAsJsonArray();
        List<Task> parsed = gson.fromJson(jsonArray, new TaskTypeToken().getType());

        assertEquals(200, response.statusCode());
        assertEquals("Task2", parsed.get(0).getName(), "Некорректный первый элемент списка");
    }

}
