package Server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.LocalDateTime;

import managers.*;
import tasks.Task;
import tasks.Epic;
import tasks.Subtask;
import utility.TypeTask;

import static java.nio.charset.StandardCharsets.UTF_8;

public class HttpTaskServer {
    public static final int PORT = 8080;
    private HttpServer server;
    private Gson gson;
    private TaskManager manager;

    public HttpTaskServer() throws IOException {
        this(Managers.getDefault());
    }

    public HttpTaskServer(TaskManager manager) throws IOException {
        this.manager = manager;
        gson = Managers.getGson();
        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        server.createContext("/tasks/", this::taskHandler);
    }

    public static void main(String[] args) throws IOException {
        final HttpTaskServer server = new HttpTaskServer();
        server.setUpTasks();
        server.startServer();
    }

    public void startServer() {
        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
        System.out.println("http://localhost:" + PORT + "/tasks/");
        server.start();
    }

    public void stopServer() {
        server.stop(0);
        System.out.println("На " + PORT + "-порту сервер остановлен!");
    }

    public void taskHandler(HttpExchange h) {
        try {
            String requestMethod = h.getRequestMethod();
            String requestURI = h.getRequestURI().toString();
            String path = h.getRequestURI().getPath().replaceFirst("/tasks/", "");
            String query = h.getRequestURI().getQuery();

            System.out.println("Поступил запрос: " + requestMethod + ", path: " + path + ", query:" + query);

            switch (requestMethod) {
                case "GET": {
                    if (path.equals("")) {
                        System.out.println("Получаем приоритеты ");
                        final String response = gson.toJson(manager.getPrioritizedTasks());
                        sendText(h, response, 200);
                        return;
                    }

                    if (path.equals("history")) {
                        System.out.println("Получаем историю ");
                        final String response = gson.toJson(manager.getHistory());
                        sendText(h, response, 200);
                        return;
                    }

                    if (path.equals("task/")) {
                        if (query == null) {
                            System.out.println("Получаем все Задачи ");
                            final String response = gson.toJson(manager.getAllTasks());
                            sendText(h, response, 200);
                            return;
                        }
                        if (query.contains("=")) {
                            System.out.println("Получаем Задачу по Id ");
                            int id = parseId(query.split("=")[1]);
                            Task task = manager.getTaskById(id);
                            if (task == null) {
                                System.out.println("Неверный id");
                                sendText(h, "Not Found", 404);
                            } else {
                                final String response = gson.toJson(task);
                                sendText(h, response, 200);
                            }
                            return;
                        }
                    }

                    if (path.equals("subtask/")) {
                        if (query == null) {
                            System.out.println("Получаем все Подзадачи ");
                            final String response = gson.toJson(manager.getAllSubtasks());
                            sendText(h, response, 200);
                            return;
                        }
                        if (query.contains("=")) {
                            System.out.println("Получаем Подзадачу по Id ");
                            int id = parseId(query.split("=")[1]);
                            Subtask subtask = manager.getSubTaskById(id);
                            if (subtask == null) {
                                System.out.println("Неверный id");
                                sendText(h, "Not Found", 404);
                            } else {
                                final String response = gson.toJson(subtask);
                                sendText(h, response, 200);
                            }
                            return;
                        }
                    }

                    if (path.equals("epic/")) {
                        if (query == null) {
                            System.out.println("Получаем все Эпики ");
                            final String response = gson.toJson(manager.getAllEpics());
                            sendText(h, response, 200);
                            return;
                        }
                        if (query.contains("=")) {
                            System.out.println("Получаем Эпик по Id ");
                            int id = parseId(query.split("=")[1]);
                            Epic epic = manager.getEpicById(id);
                            if (epic == null) {
                                System.out.println("Неверный id");
                                sendText(h, "Not Found", 404);
                            } else {
                                final String response = gson.toJson(epic);
                                sendText(h, response, 200);
                            }
                            return;
                        }
                    }

                    if (path.equals("subtask/epic/")) {
                        if (query.contains("=")) {
                            System.out.println("Получаем все подзадачи Эпика по Id.");
                            int id = parseId(query.split("=")[1]);
                            Epic epic = manager.getEpicById(id);
                            if (epic == null) {
                                System.out.println("Неверный id");
                                sendText(h, "Not Found", 404);
                            } else {
                                final String response = gson.toJson(manager.getListSubTasks(id));
                                sendText(h, response, 200);
                            }
                            return;
                        }
                    }
                    break;
                }

                case "DELETE": {
                    if (path.equals("task/")) {
                        if (query == null) {
                            manager.deleteAllTasks();
                            System.out.println("Все задачи удалены! ");
                            final String response = "all tasks have been deleted";
                            sendText(h, response, 200);
                            return;
                        } else {
                            if (query.contains("=")) {
                                System.out.println("Удаляем Задачу по Id ");
                                int id = parseId(query.split("=")[1]);
                                Task task = manager.getTaskById(id);
                                if (task == null) {
                                    System.out.println("Неверный id");
                                    sendText(h, "Not Found", 404);
                                } else {
                                    manager.deleteTaskById(id);
                                    final String response = "task deleted";
                                    sendText(h, response, 200);
                                }
                                return;
                            }
                        }
                    }

                    if (path.equals("subtask/")) {
                        System.out.println("Удаляем Подзадачу по Id ");
                        int id = parseId(query.split("=")[1]);
                        Subtask subtask = manager.getSubTaskById(id);

                        if (subtask == null) {
                            System.out.println("Неверный id");
                            sendText(h, "Not Found", 404);
                        } else {
                            manager.deleteSubTaskById(id);
                            final String response = "subtask deleted";
                            sendText(h, response, 200);
                        }
                        return;
                    }

                    if (path.equals("epic/")) {
                        System.out.println("Удаляем Эпик по Id ");
                        int id = parseId(query.split("=")[1]);
                        Epic epic = manager.getEpicById(id);
                        if (epic == null) {
                            System.out.println("Неверный id");
                            sendText(h, "Not Found", 404);
                        } else {
                            manager.deleteEpicById(id);
                            final String response = "epic deleted";
                            sendText(h, response, 200);
                        }
                        return;
                    }
                    break;
                }

                case "POST": {
                    if (path.equals("task/")) {
                        String jsonBody = readText(h);
                        if (jsonBody.isEmpty()) {
                            System.out.println("Недостаточно данных для выполнения запроса");
                            sendText(h, "insufficient data", 400);
                            return;
                        }
                        final Task task = gson.fromJson(jsonBody, Task.class);
                        final Integer id = task.getId();

                        if (id == null) {
                            System.out.println("Недостаточно данных для выполнения запроса");
                            sendText(h, "insufficient data", 400);
                            return;
                        }
                        if (id == 0) {
                            manager.createTask(task);
                            System.out.println("Создана задача: " + task);
                            final String response = gson.toJson(task);
                            sendText(h, response, 200);
                        } else {
                            manager.updatedTask(task);
                            System.out.println("Задача обновлена.");
                            final String response = gson.toJson(task);
                            sendText(h, response, 200);
                        }
                        return;
                    }

                    if (path.equals("subtask/")) {
                        final String jsonBody = readText(h);
                        if (jsonBody.isEmpty()) {
                            System.out.println("Недостаточно данных для выполнения запроса");
                            sendText(h, "insufficient data", 400);
                            return;
                        }

                        final Subtask subtask = gson.fromJson(jsonBody, Subtask.class);
                        final Integer id = subtask.getId();
                        if (id == null) {
                            System.out.println("Недостаточно данных для выполнения запроса");
                            sendText(h, "insufficient data", 400);
                            return;
                        }
                        if (id == 0) {
                            manager.createSubTask(subtask);
                            System.out.println("Создана подзадача: " + subtask);
                            sendText(h, gson.toJson(subtask), 200);
                        } else {
                            manager.updatedSubTask(subtask);
                            System.out.println("Подзадача обновлена.");
                            sendText(h, gson.toJson(subtask), 200);
                        }
                        return;
                    }

                    if (path.equals("epic/")) {
                        String jsonBody = readText(h);
                        if (jsonBody.isEmpty()) {
                            System.out.println("Недостаточно данных для выполнения запроса");
                            sendText(h, "insufficient data", 400);
                            return;
                        }

                        final Epic epic = gson.fromJson(jsonBody, Epic.class);
                        final Integer id = epic.getId();
                        if (id == null) {
                            System.out.println("Недостаточно данных для выполнения запроса");
                            sendText(h, "insufficient data", 400);
                            return;
                        }
                        if (id == 0) {
                            manager.createEpic(epic);
                            System.out.println("Создан эпик: " + epic);
                            sendText(h, gson.toJson(epic), 200);
                            return;
                        } else {
                            manager.updatedEpic(epic);
                            System.out.println("Эпик обновлен! ");
                            sendText(h, gson.toJson(epic), 200);
                            return;
                        }
                    }
                    break;
                }

                default: {
                    System.out.println("/ запрос " + requestMethod + " не обработан!");
                    h.sendResponseHeaders(400, 0);
                }
            }
        } catch (Exception exception) {
            System.out.println("Ошибка при обработка запроса");
        } finally {
            h.close();
        }
    }

    private int parseId(String idString) {
        try {
            return Integer.parseInt(idString);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private String readText(HttpExchange h) throws IOException {
        return new String(h.getRequestBody().readAllBytes(), UTF_8);
    }

    private void sendText(HttpExchange h, String responseText, int responseCode) throws IOException {
        byte[] resp = responseText.getBytes(UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(responseCode, resp.length);
        h.getResponseBody().write(resp);
    }

    private void setUpTasks() {
        Task task10 = new Task(TypeTask.TASK, "Задача10", "Задача10.Описание",
                LocalDateTime.now(), (long) 0);
        manager.createTask(task10);

        Task task1 = new Task(TypeTask.TASK, "Задача1",
                "Задача1.Описание",
                LocalDateTime.of(2022, 7, 1, 0, 0, 0),
                (long) (60 * 24 * 14));
        manager.createTask(task1);
        Task task2 = new Task(TypeTask.TASK, "Задача2",
                "Задача2.Описание",
                LocalDateTime.of(2022, 6, 1, 0, 0, 0), (long) (60 * 24 * 7));
        manager.createTask(task2);
    }
}
