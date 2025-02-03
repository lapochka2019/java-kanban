package server.hadler.model;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import model.Task;
import server.hadler.BaseHttpHandler;
import service.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Predicate;

public class TaskHandler extends BaseHttpHandler {

    public TaskHandler(Gson gson, TaskManager manager) {
        super(gson, manager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = Arrays.stream(exchange.getRequestURI().getPath().split("/")).filter(Predicate.not(String::isEmpty)).toArray(String[]::new)[0];
        if (!path.equals("tasks")) {
            sendError(exchange, "Некорректный запрос!", 400);
            return;
        }
        String methodType = exchange.getRequestMethod();
        Optional<Integer> taskId = getId(exchange);
        if (taskId.isEmpty()) { //"/tasks"
            if (isGet(methodType)) {
                sendSuccessfullyDefault(exchange, gson.toJson(manager.getTasks()));
            } else if (isPost(methodType)) {
                parseTaskPostRequest(exchange);
            } else {
                sendNotImplemented(exchange);
            }
        } else { //"/tasks/{id}"
            if (isGet(methodType)) {
                getTaskById(taskId, exchange);
            } else if (isDelete(methodType)) {
                deleteTaskById(taskId, exchange);
            } else {
                sendNotImplemented(exchange);
            }
        }
    }

    //получить задачу по id
    private void getTaskById(Optional<Integer> taskId, HttpExchange exchange) throws IOException {
        Optional<Task> optionalTask = manager.getTask(taskId.get());
        if (optionalTask.isEmpty()) {
            sendTaskNotFound(exchange);
        } else {
            sendSuccessfullyDefault(exchange, gson.toJson(optionalTask.get()));
        }
    }

    //удалить задачу
    private void deleteTaskById(Optional<Integer> taskId, HttpExchange exchange) throws IOException {
        manager.deleteTask(taskId.get());
        sendSuccessfullyDefault(exchange, "Задача удалена успешно!");
    }

    //обработать пост запрос для задачи
    private void parseTaskPostRequest(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        String jsonString = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        if (jsonString.isEmpty()) {
            sendLengthRequired(exchange);
            return;
        }
        Task task = gson.fromJson(jsonString, Task.class);
        if (task.getId() == 0) {
            createTask(exchange, task);
        } else {
            updateTask(exchange, task);
        }
    }

    //создать задачу
    private void createTask(HttpExchange exchange, Task task) throws IOException {
        Task result = manager.create(task);
        if (result != null) {
            sendSuccessfully(exchange, "Задача создана успешно!", 201);
        } else {
            sendTimeTaken(exchange);
        }
    }

    //обновить задачу
    private void updateTask(HttpExchange exchange, Task task) throws IOException {
        if (manager.getTask(task.getId()).isEmpty()) {
            sendTaskNotFound(exchange);
            return;
        }
        Task result = manager.update(task);
        if (result != null) {
            sendSuccessfully(exchange, "Задача обновлена успешно!", 201);
        } else {
            sendTimeTaken(exchange);
        }
    }
}
