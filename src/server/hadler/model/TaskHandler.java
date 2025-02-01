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
        //"/tasks"
        if (taskId.isEmpty()) {
            if (isGet(methodType)) {
                sendSuccessfullyDefault(exchange, gson.toJson(manager.getTasks()));
            } else if (isPost(methodType)) {
                InputStream inputStream = exchange.getRequestBody();
                String jsonString = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                if (jsonString.isEmpty()) {
                    sendLengthRequired(exchange);
                }
                Task task = gson.fromJson(jsonString, Task.class);
                int id = task.getId();
                if (id == 0) {
                    Task result = manager.create(task);
                    if (result != null) {
                        sendSuccessfully(exchange, "Задача создана успешно!", 201);
                    } else {
                        sendTimeTaken(exchange);
                    }
                } else {
                    if (manager.getTask(id).isEmpty()) {
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
            } else {
                sendNotImplemented(exchange);
            }
            //"/tasks/{id}"
        } else {
            if (isGet(methodType)) {
                Optional<Task> optionalTask = manager.getTask(taskId.get());
                if (optionalTask.isEmpty()) {
                    sendTaskNotFound(exchange);
                } else {
                    sendSuccessfullyDefault(exchange, gson.toJson(optionalTask.get()));
                }
            } else if (isDelete(methodType)) {
                manager.deleteTask(taskId.get());
                sendSuccessfullyDefault(exchange, "Задача удалена успешно!");
            } else {
                sendNotImplemented(exchange);
            }
        }
    }
}
