package managers;

public class Managers {
    public static TaskManager getDefault() {
        return new FileBackedTasksManager("resources/tasks.csv");
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
