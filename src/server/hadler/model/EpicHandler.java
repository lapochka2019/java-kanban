package server.hadler.model;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import model.Epic;
import model.Task;
import server.hadler.BaseHttpHandler;
import service.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Predicate;

public class EpicHandler extends BaseHttpHandler {

    public EpicHandler(Gson gson, TaskManager manager) {
        super(gson, manager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = Arrays.stream(exchange.getRequestURI().getPath().split("/")).filter(Predicate.not(String::isEmpty)).toArray(String[]::new)[0];
        if (!path.equals("epics")) {
            sendError(exchange, "Некорректный запрос!", 400);
            return;
        }
        String methodType = exchange.getRequestMethod();
        Optional<Integer> epicId = getId(exchange);
        //"/epics"
        if (epicId.isEmpty()) {
            if (isGet(methodType)) {
                sendSuccessfullyDefault(exchange, gson.toJson(manager.getEpics()));
            } else if (isPost(methodType)) {
                InputStream inputStream = exchange.getRequestBody();
                String jsonString = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                if (jsonString.isEmpty()) {
                    sendLengthRequired(exchange);
                }
                Epic epic = gson.fromJson(jsonString, Epic.class);
                int id = epic.getId();
                if (id == 0) {
                    Epic result = manager.create(epic);
                    if (result != null) {
                        sendSuccessfully(exchange, "Эпик создан успешно!", 201);
                    } else {
                        sendTimeTaken(exchange);
                    }
                } else {
                    if (manager.getEpic(id).isEmpty()) {
                        sendTaskNotFound(exchange);
                        return;
                    }
                    Task result = manager.update(epic);
                    if (result != null) {
                        sendSuccessfully(exchange, "Эпик обновлен успешно!", 201);
                    } else {
                        sendTimeTaken(exchange);
                    }
                }
            } else {
                sendNotImplemented(exchange);
            }
            //"/epics/{id}"
        } else {
            if (isGetSubtasks(exchange)) {
                Optional<Epic> optionalEpic = manager.getEpic(epicId.get());
                //если эпика нет
                if (optionalEpic.isEmpty()) {
                    sendTaskNotFound(exchange);
                } else {
                    sendSuccessfullyDefault(exchange, gson.toJson(manager.getEpicSubTasks(epicId.get())));
                }
            } else if (isGet(methodType)) {
                Optional<Epic> optionalEpic = manager.getEpic(epicId.get());
                if (optionalEpic.isEmpty()) {
                    sendTaskNotFound(exchange);
                } else {
                    sendSuccessfullyDefault(exchange, gson.toJson(optionalEpic.get()));
                }
            } else if (isDelete(methodType)) {
                manager.deleteEpic(epicId.get());
                sendSuccessfullyDefault(exchange, "Эпик удален успешно!");
            } else {
                sendNotImplemented(exchange);
            }
        }
    }

    private boolean isGetSubtasks(HttpExchange exchange) {
        String[] pathParts = Arrays.stream(exchange.getRequestURI().getPath().split("/")).filter(Predicate.not(String::isEmpty)).toArray(String[]::new);
        try {
            if (pathParts[2].equals("subtasks"))
                return true;
        } catch (ArrayIndexOutOfBoundsException e) {
            return false;
        }
        return false;
    }
}
