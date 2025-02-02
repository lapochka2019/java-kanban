package server.hadler.model;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import model.SubTask;
import model.Task;
import server.hadler.BaseHttpHandler;
import service.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Predicate;

public class SubtaskHandler extends BaseHttpHandler {

    public SubtaskHandler(Gson gson, TaskManager manager) {
        super(gson, manager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = Arrays.stream(exchange.getRequestURI().getPath().split("/")).filter(Predicate.not(String::isEmpty)).toArray(String[]::new)[0];
        if (!path.equals("subtasks")) {
            sendError(exchange, "Некорректный запрос!", 400);
            return;
        }
        String methodType = exchange.getRequestMethod();
        Optional<Integer> subtaskId = getId(exchange);
        if (subtaskId.isEmpty()) { //"/subtasks"
            if (isGet(methodType)) {
                sendSuccessfullyDefault(exchange, gson.toJson(manager.getSubTasks()));
            } else if (isPost(methodType)) {
                parseSubtaskPostRequest(exchange);
            } else {
                sendNotImplemented(exchange);
            }
        } else { //"/subtasks/{id}"
            if (isGet(methodType)) {
                getSubtaskById(exchange, subtaskId);
            } else if (isDelete(methodType)) {
                deleteSubtaskById(exchange, subtaskId);
            } else {
                sendNotImplemented(exchange);
            }
        }
    }

    //удалить подзадачу
    private void deleteSubtaskById(HttpExchange exchange, Optional<Integer> subtaskId) throws IOException {
        manager.deleteSubTusk(subtaskId.get());
        sendSuccessfullyDefault(exchange, "Подзадача удалена успешно!");
    }

    //получить подзадачу по id
    private void getSubtaskById(HttpExchange exchange, Optional<Integer> subtaskId) throws IOException {
        Optional<SubTask> optionalSubTask = manager.getSubTask(subtaskId.get());
        if (optionalSubTask.isEmpty()) {
            sendTaskNotFound(exchange);
        } else {
            sendSuccessfullyDefault(exchange, gson.toJson(optionalSubTask.get()));
        }
    }

    //разобрать POST запрос
    private void parseSubtaskPostRequest(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        String jsonString = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        if (jsonString.isEmpty()) {
            sendLengthRequired(exchange);
            return;
        }
        SubTask subTask = gson.fromJson(jsonString, SubTask.class);
        if (subTask.getId() == 0) {
            createSubtask(exchange, subTask);
        } else {
            updateSubtask(exchange, subTask);
        }
    }

    //создать задачу
    private void createSubtask(HttpExchange exchange, SubTask subTask) throws IOException {
        if (manager.getEpic(subTask.getEpicId()).isEmpty()) {
            sendTaskNotFound(exchange);
            return;
        }
        SubTask result = manager.create(subTask);
        if (result != null) {
            sendSuccessfully(exchange, "Подзадача создана успешно!", 201);
        } else {
            sendTimeTaken(exchange);
        }
    }

    //обновить задачу
    private void updateSubtask(HttpExchange exchange, SubTask subTask) throws IOException {
        if (manager.getEpic(subTask.getEpicId()).isEmpty()) {
            sendTaskNotFound(exchange);
            return;
        }
        SubTask result = manager.update(subTask);
        if (result != null) {
            sendSuccessfully(exchange, "Подзадача обновлена успешно!", 201);
        } else {
            sendTimeTaken(exchange);
        }
    }
}
