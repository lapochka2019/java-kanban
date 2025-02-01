package server.hadler.model;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import model.SubTask;
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
        //"/subtasks"
        if (subtaskId.isEmpty()) {
            if (isGet(methodType)) {
                sendSuccessfullyDefault(exchange, gson.toJson(manager.getSubTasks()));
            } else if (isPost(methodType)) {
                InputStream inputStream = exchange.getRequestBody();
                String jsonString = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                if (jsonString.isEmpty()) {
                    sendLengthRequired(exchange);
                }
                SubTask subTask = gson.fromJson(jsonString, SubTask.class);
                int id = subTask.getId();
                //создать SubTask
                if (id == 0) {
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
                } else {
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
            } else {
                sendNotImplemented(exchange);
            }
            //"/subtasks/{id}"
        } else {
            if (isGet(methodType)) {
                Optional<SubTask> optionalSubTask = manager.getSubTask(subtaskId.get());
                if (optionalSubTask.isEmpty()) {
                    sendTaskNotFound(exchange);
                } else {
                    sendSuccessfullyDefault(exchange, gson.toJson(optionalSubTask.get()));
                }
            } else if (isDelete(methodType)) {
                manager.deleteSubTusk(subtaskId.get());
                sendSuccessfullyDefault(exchange, "Подзадача удалена успешно!");
            } else {
                sendNotImplemented(exchange);
            }
        }
    }
}
