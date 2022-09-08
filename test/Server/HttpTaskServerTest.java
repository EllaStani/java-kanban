package Server;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import managers.*;
import tasks.Task;
import tasks.Epic;
import tasks.Subtask;
import utility.TypeTask;

import static org.junit.jupiter.api.Assertions.*;
import static java.nio.charset.StandardCharsets.UTF_8;

class HttpTaskServerTest {

    private HttpTaskServer server;
    private TaskManager manager;
    private Gson gson = Managers.getGson();

    private Task task1;
    private Epic epic1;
    private Subtask subtask1;

    @BeforeEach
    void init() throws IOException {
        manager = new FileBackedTasksManager("resources/tasks.csv");
        server = new HttpTaskServer(manager);

        task1 = new Task(TypeTask.TASK, "Задача1", "Задача1.Описание",
                LocalDateTime.of(2022, 8, 1, 0, 0),
                (long) (60 * 24 * 3));
        manager.createTask(task1);

        epic1 = new Epic(TypeTask.EPIC, "Эпик1", "Эпик1.Описание", LocalDateTime.now(), 0L);
        manager.createEpic(epic1);

        subtask1 = new Subtask(TypeTask.SUBTASK, "Подзадача1", "Подзадача1.Описание",
                LocalDateTime.of(2022, 8, 5, 0, 0),
                (long) (60 * 24 * 5), epic1.getId());
        manager.createSubTask(subtask1);

        server.startServer();
    }

    @AfterEach
    void stop() {
        server.stopServer();
    }

    @Test
    void testGetPrioritized() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Type taskType = new TypeToken<ArrayList<Task>>() {
        }.getType();

        final List<Task> tasks = gson.fromJson(response.body(), taskType);

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(2, tasks.size(), "Неверное количество задач.");
        assertEquals(task1.getId(), tasks.get(0).getId(), "Задачи не совпадают.");
        assertEquals(subtask1.getId(), tasks.get(1).getId(), "Задачи не совпадают.");
    }

    @Test
    void testGetHistory() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/history");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Type taskType = new TypeToken<ArrayList<Task>>() {
        }.getType();

        final List<Task> tasks = gson.fromJson(response.body(), taskType);

        assertNotNull(tasks, "История не возвращается.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
    }

    @Test
    void testGetAllTasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Type taskType = new TypeToken<ArrayList<Task>>() {
        }.getType();

        final List<Task> tasks = gson.fromJson(response.body(), taskType);

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task1, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void testGetTaskById() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task/?id=1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Type taskType = new TypeToken<Task>() {
        }.getType();

        final Task getTask = gson.fromJson(response.body(), taskType);

        assertNotNull(getTask, "Задачи не возвращаются.");
        assertEquals(task1, getTask, "Задачи не совпадают.");
    }

    @Test
    void testGetTaskByIdForNotExistID() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task/?id=2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
        assertEquals("Not Found", response.body());
    }

    @Test
    void testGetAllSubtasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/subtask/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Type taskType = new TypeToken<ArrayList<Subtask>>() {
        }.getType();

        final List<Subtask> subtasks = gson.fromJson(response.body(), taskType);

        assertNotNull(subtasks, "Подзадачи не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество подзадач.");
        assertEquals(subtask1, subtasks.get(0), "Подзадачи не совпадают.");
    }

    @Test
    void testGetSubtaskById() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/subtask/?id=3");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Type taskType = new TypeToken<Subtask>() {
        }.getType();

        final Subtask getSubtask = gson.fromJson(response.body(), taskType);

        assertNotNull(getSubtask, "Подзадачи не возвращаются.");
        assertEquals(subtask1, getSubtask, "Подзадачи не совпадают.");
    }

    @Test
    void testGetSubtaskByIdForNotExistID() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/subtask/?id=1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
        assertEquals("Not Found", response.body());
    }

    @Test
    void testGetAllEpics() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Type taskType = new TypeToken<ArrayList<Epic>>() {
        }.getType();

        final List<Epic> epics = gson.fromJson(response.body(), taskType);

        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(epic1, epics.get(0), "Эпики не совпадают.");
    }

    @Test
    void testGetEpicById() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic/?id=2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Type taskType = new TypeToken<Epic>() {
        }.getType();

        final Epic getEpic = gson.fromJson(response.body(), taskType);

        assertNotNull(getEpic, "Эпики не возвращаются.");
        assertEquals(epic1, getEpic, "Эпики не совпадают.");
    }

    @Test
    void testGetEpicByIdForNotExistID() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic/?id=1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
        assertEquals("Not Found", response.body());
    }

    @Test
    void testGetListSubtasksForEpicById() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/subtask/epic/?id=2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());


        Type taskType = new TypeToken<ArrayList<Subtask>>() {
        }.getType();

        final List<Subtask> subtasks = gson.fromJson(response.body(), taskType);

        assertNotNull(subtasks, "Подзадачи не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество подзадач.");
        assertEquals(subtask1, subtasks.get(0), "Подзадачи не совпадают.");
    }

    @Test
    void testDeleteTaskById() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task/?id=1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        final List<Task> tasks = manager.getAllTasks();

        assertTrue(tasks.isEmpty(), "Задача не удалена.");
        assertEquals(0, tasks.size(), "Неверное количество задач.");
        assertEquals("task deleted", response.body());
    }

    @Test
    void testDeleteTaskByIdForNotExistID() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task/?id=2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
        assertEquals("Not Found", response.body());
    }

    @Test
    void testDeleteSubtaskById() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/subtask/?id=3");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        final List<Subtask> allSubtasks = manager.getAllSubtasks();
        final List<Subtask> subtasksEpic = manager.getListSubTasks(2);

        assertTrue(allSubtasks.isEmpty(), "Подзадача не удалена.");
        assertEquals(0, allSubtasks.size(), "Неверное количество подзадач.");
        assertEquals(0, subtasksEpic.size(), "Неверное количество подзадач у эпика.");
        assertEquals("subtask deleted", response.body());
    }

    @Test
    void testDeleteSubtaskByIdForNotExistID() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/subtask/?id=2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
        assertEquals("Not Found", response.body());
    }

    @Test
    void testDeleteEpicById() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic/?id=2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        final List<Epic> allEpics = manager.getAllEpics();
        final List<Subtask> allSubtasks = manager.getAllSubtasks();

        assertTrue(allEpics.isEmpty(), "Эпик не удалена.");
        assertEquals(0, allSubtasks.size(), "Неверное количество подзадач.");
        assertEquals("epic deleted", response.body());
    }

    @Test
    void testDeleteEpicByIdForNotExistID() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/subtask/?id=2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
        assertEquals("Not Found", response.body());
    }

    @Test
    void testCreateTask() throws IOException, InterruptedException {
        Task task2 = new Task(TypeTask.TASK, "Задача2", "Задача2.Описание",
                LocalDateTime.of(2022, 9, 1, 0, 0),
                (long) (60 * 24 * 5));

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task/");
        String jsonTask = gson.toJson(task2);

        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(jsonTask, UTF_8);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        final List<Task> tasks = manager.getAllTasks();

        assertEquals(200, response.statusCode());
        assertEquals(2, tasks.size(), "Неверное количество задач.");

    }

    @Test
    void testUpdateTask() throws IOException, InterruptedException {
        Task task2 = new Task(TypeTask.TASK, "Задача1", "Задача1.Обновление",
                LocalDateTime.of(2022, 9, 1, 0, 0),
                (long) (60 * 24 * 5));
        task2.setId(1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task/");
        String jsonTask = gson.toJson(task2);

        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(jsonTask, UTF_8);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        final List<Task> tasks = manager.getAllTasks();

        assertEquals(200, response.statusCode());
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task2, tasks.get(0), "Задачи не совпадают");
    }

    @Test
    void testCreateSubtask() throws IOException, InterruptedException {
        Subtask subtask2 = new Subtask(TypeTask.SUBTASK, "Подзадача2", "Подзадача2.Описание",
                LocalDateTime.of(2022, 8, 25, 0, 0),
                (long) (60 * 24 * 3), epic1.getId());

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/subtask/");
        String jsonTask = gson.toJson(subtask2);

        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(jsonTask, UTF_8);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        final List<Subtask> subtasks = manager.getAllSubtasks();
        final List<Subtask> listsubtasksEpic = manager.getListSubTasks(epic1.getId());

        assertEquals(200, response.statusCode());
        assertEquals(2, subtasks.size(), "Неверное количество Подзадач.");
        assertEquals(2, listsubtasksEpic.size(), "Неверное количество Подзадач у Эпика.");
    }

    @Test
    void testUpdateSubtask() throws IOException, InterruptedException {
        Subtask subtask2 = new Subtask(TypeTask.SUBTASK, "Подзадача1", "Подзадача1.Обновление",
                LocalDateTime.of(2022, 8, 25, 0, 0),
                (long) (60 * 24 * 3), epic1.getId());
        subtask2.setId(3);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/subtask/");
        String jsonTask = gson.toJson(subtask2);

        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(jsonTask, UTF_8);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        final List<Task> tasks = manager.getAllTasks();

        final List<Subtask> subtasks = manager.getAllSubtasks();
        final List<Subtask> listsubtasksEpic = manager.getListSubTasks(epic1.getId());

        assertEquals(200, response.statusCode());
        assertEquals(1, subtasks.size(), "Неверное количество Подзадач.");
        assertEquals(1, listsubtasksEpic.size(), "Неверное количество Подзадач у Эпика.");
        assertEquals(subtask2, subtasks.get(0), "Задачи не совпадают");
    }

    @Test
    void testCreateEpic() throws IOException, InterruptedException {
        Epic epic2 = new Epic(TypeTask.EPIC, "Эпик2", "Эпик2.Описание", LocalDateTime.now(), 0L);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        String jsonTask = gson.toJson(epic2);

        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(jsonTask, UTF_8);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        final List<Epic> epics = manager.getAllEpics();

        assertEquals(200, response.statusCode());
        assertEquals(2, epics.size(), "Неверное количество эпиков.");

    }

    @Test
    void testUpdateEpic() throws IOException, InterruptedException {
        Epic epic2 = new Epic(TypeTask.EPIC, "Эпик1", "Эпик1.Обновление", LocalDateTime.now(), 0L);
        epic2.setId(2);
        System.out.println("epics = " + epic2);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        String jsonTask = gson.toJson(epic2);

        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(jsonTask, UTF_8);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        final List<Epic> epics = manager.getAllEpics();
        System.out.println("epics = " + epics.get(0));

        assertEquals(200, response.statusCode());
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
    }
}