package managers;

import Server.KVServer;
import client.KVTaskClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpTaskManager extends FileBackedTasksManager {
    private KVTaskClient client = new KVTaskClient(8078);
    private Gson gson;

    public HttpTaskManager(String fileName) {
        super(fileName);
    }

    public HttpTaskManager(int port) {
        super(null);
        gson = Managers.getGson();
    }

    @Override
    protected void save() {
        String jsonTasks = gson.toJson(getTask());
        client.put("tasks/", jsonTasks);

        String jsonEpics = gson.toJson(getEpic());
        client.put("epics/", jsonEpics);

        String jsonSubtasks = gson.toJson(getSubTask());
        client.put("subtasks/", jsonSubtasks);

        List<Integer> history = getHistory().stream()
                .map(Task::getId)
                .collect(Collectors.toList());
        String jsonHistory = gson.toJson(history);
        client.put("history/", jsonHistory);

    }

    public static HttpTaskManager loadFromServer(int port) {
        final HttpTaskManager manager = new HttpTaskManager(KVServer.PORT);
        manager.load();
        return manager;
    }

    protected void load() {
        Type taskType = new TypeToken<Map<Integer, Task>>() {
        }.getType();
        final Map<Integer, Task> tasksFromServer = gson.fromJson(client.load("tasks/"), taskType);

        for (Map.Entry<Integer, Task> task : tasksFromServer.entrySet()) {
            this.tasks.put(task.getKey(), task.getValue());
            this.prioritizedTasks.put(task.getValue().getStartTime(), task.getValue());
            if (id > getId()) {
                setId(id);
            }
        }

        Type epicType = new TypeToken<Map<Integer, Epic>>() {
        }.getType();
        final Map<Integer, Epic> epicsFromServer = gson.fromJson(client.load("epics/"), epicType);

        for (Map.Entry<Integer, Epic> epic : epicsFromServer.entrySet()) {
            this.epics.put(epic.getKey(), epic.getValue());
            if (id > getId()) {
                setId(id);
            }
        }

        Type subtaskType = new TypeToken<Map<Integer, Subtask>>() {
        }.getType();
        final Map<Integer, Subtask> subtasksFromServer = gson.fromJson(client.load("subtasks/"), subtaskType);

        for (Map.Entry<Integer, Subtask> subtask : subtasksFromServer.entrySet()) {
            this.subtasks.put(subtask.getKey(), subtask.getValue());
            this.prioritizedTasks.put(subtask.getValue().getStartTime(), subtask.getValue());
            if (id > getId()) {
                setId(id);
            }
        }

        Type historyType = new TypeToken<List<Integer>>() {
        }.getType();
        final List<Integer> history = gson.fromJson(client.load("history/"), historyType);

        for (Integer id : history) {
            if (this.tasks.containsKey(id)) {
                historyManager.add(getTaskById(id));
            } else if (this.epics.containsKey(id)) {
                historyManager.add(getEpicById(id));
            } else {
                historyManager.add(getSubTaskById(id));
            }
        }
    }
}
