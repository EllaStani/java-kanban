package managers;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FileBackedTasksManager extends InMemoryTaskManager {

    private String fileName;
    private final CSVSerializator serializator = new CSVSerializator();

    public FileBackedTasksManager(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void createSubTask(Subtask subtask) {
        super.createSubTask(subtask);
        save();
    }

    @Override
    public void updatedTask(Task task) {
        super.updatedTask(task);
        save();
    }

    @Override
    public void updatedEpic(Epic epic) {
        super.updatedEpic(epic);
        save();
    }

    @Override
    public void updatedSubTask(Subtask subTask) {
        super.updatedSubTask(subTask);
        save();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = super.getTaskById(id);
        save();
        return task;
    }

    @Override
    public Subtask getSubTaskById(int id) {
        Subtask subtask = super.getSubTaskById(id);
        save();
        return subtask;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = super.getEpicById(id);
        save();
        return epic;
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteSubTaskById(int id) {
        super.deleteSubTaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteAllTask() {
        super.deleteAllTask();
        save();
    }

    @Override
    public List<Subtask> getListSubTasks(int id) {
        return super.getListSubTasks(id);
    }

    @Override
    public StatusTask getStatusById(int id) {
        return super.getStatusById(id);
    }

    @Override
    public Map<Integer, Task> getTask() {
        return super.getTask();
    }

    @Override
    public Map<Integer, Epic> getEpic() {
        return super.getEpic();
    }

    @Override
    public Map<Integer, Subtask> getSubTask() {
        return super.getSubTask();
    }

    @Override
    public List<Task> getAllTasks() {
        return super.getAllTasks();
    }

    @Override
    public List<Epic> getAllEpics() {
        return super.getAllEpics();
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return super.getAllSubtasks();
    }

    @Override
    public List<Task> getHistory() {
        return super.getHistory();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return super.getPrioritizedTasks();
    }

    public static FileBackedTasksManager loadFromFile(String file) {
        final FileBackedTasksManager manager = new FileBackedTasksManager("resources/tasks.csv");
        manager.load();
        return manager;
    }

    private void load() {
        int nextId = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(fileName, StandardCharsets.UTF_8))) {
            br.readLine();
            while (br.ready()) {
                String line = br.readLine();

                if (line.isEmpty() || line.isBlank()) {
                    break;
                }
                Task task = serializator.fromString(line);
                int taskId = task.getId();

                switch (task.getType()) {
                    case TASK:
                        tasks.put(taskId, task);
                        break;
                    case EPIC:
                        Epic epic = (Epic) task;
                        epics.put(taskId, epic);
                        break;
                    case SUBTASK:
                        Subtask subtask = (Subtask) task;
                        subtasks.put(taskId, subtask);
                        Epic epicOfSubtask = getEpicById(subtask.getIdEpic());
                        epicOfSubtask.setListSubTask(subtask);
                        break;
                    default:
                        break;
                }

                if (nextId < taskId) {
                    nextId = taskId;
                }
            }

            setId(nextId);

            String lineHistory = br.readLine();
            List<Integer> history = CSVSerializator.fromStringHistory(lineHistory);
            for (int taskId : history) {
                if (tasks.containsKey(taskId)) {
                    historyManager.add(getTaskById(taskId));
                } else if (epics.containsKey(taskId)) {
                    historyManager.add(getEpicById(taskId));
                } else {
                    historyManager.add(getSubTaskById(taskId));
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException(e.getMessage());
        }
    }

    private void save() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(this.fileName, StandardCharsets.UTF_8))) {
            bw.write("id,type,name,status,description,epic" + "\n");
            for (Task task : getAllTasks()) {
                bw.write(serializator.toString(task));
            }
            for (Epic epic : getAllEpics()) {
                bw.write(serializator.toString(epic));
            }
            for (Subtask subtask : getAllSubtasks()) {
                bw.write(serializator.toString(subtask));
            }
            bw.write("\n");
            bw.write(CSVSerializator.toStringHistory(historyManager));
        } catch (IOException e) {
            throw new ManagerSaveException(e.getMessage());
        }
    }

    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();

        Task task1 = new Task(TypeTask.TASK, "Переезд", "Собрать коробки; Упаковать кошку;"
                + " Сказать слова прощания",
                LocalDateTime.of(2022, 6, 1, 0, 0), (long) (60 * 24 * 3));
        manager.createTask(task1);
        Task task2 = new Task(TypeTask.TASK, "Спортивный корпоративный праздник",
                "Создать команду; Купить форму; Выбрать место; Купить призы",
                LocalDateTime.of(2022, 7, 1, 0, 0), (long) (60 * 24 * 5));
        manager.createTask(task2);

        Epic task3 = new Epic(TypeTask.EPIC, "Юбилей бабушки",
                "Заказать ресторан; Купить подарок; Подготовить сценарий",
                LocalDateTime.now(), 0L);
        manager.createEpic(task3);

        Subtask task4 = new Subtask(TypeTask.SUBTASK, "Заказать ресторан",
                "Украсить зал; Придумать меню",
                LocalDateTime.of(2022, 8, 1, 0, 0), (long) (60 * 24 * 5),3);
        manager.createSubTask(task4);
        Subtask task5 = new Subtask(TypeTask.SUBTASK, "Подготовить сценарий",
                "Найти аниматора; Согласовать конкурсы",
                LocalDateTime.of(2022, 8, 10, 0, 0), (long) (60 * 24 * 5),3);
        manager.createSubTask(task5);

        Epic task6 = new Epic(TypeTask.EPIC, "Поездка в отпуск",
                "Выбрать место отдыха; Собрать вещи",
                LocalDateTime.of(2022, 9, 20, 0, 0), 0l);
        manager.createEpic(task6);

        System.out.println("Задача № " + 3 + ": " + manager.getEpicById(3));
        System.out.println("Задача № " + 2 + ": " + manager.getTaskById(2));
        System.out.println("Задача № " + 4 + ": " + manager.getSubTaskById(4));
        System.out.println("Задача № " + 4 + ": " + manager.getSubTaskById(4));
        System.out.println("Задача № " + 5 + ": " + manager.getSubTaskById(5));
        System.out.println("Задача № " + 1 + ": " + manager.getTaskById(1));
        System.out.println("Задача № " + 2 + ": " + manager.getTaskById(2));
        System.out.println("Задача № " + 6 + ": " + manager.getSubTaskById(5));
        System.out.println("История запросов: " + manager.getHistory());

        // Далее восстанавливаем задачи из файла "resources/tasks.csv":

        TaskManager managerFile = FileBackedTasksManager.loadFromFile("resources/tasks.csv");

        System.out.println("Восстановили таски: " + managerFile.getAllTasks());
        System.out.println("Восстановили эпики: " + managerFile.getAllEpics());
        System.out.println("Восстановили субтаски: " + managerFile.getAllSubtasks());
        System.out.println("Восстановили историю запросов: " + managerFile.getHistory());

        managerFile.deleteTaskById(1);
        System.out.println("У Эпика 3 следующие подзадачи:" + '\n' + managerFile.getListSubTasks(3));
        managerFile.deleteSubTaskById(4);
        System.out.println("У Эпика 3 следующие подзадачи:" + '\n' + managerFile.getListSubTasks(3));
        managerFile.deleteEpicById(3);

        System.out.println("Новая история: " + managerFile.getHistory());

    }
}
