package managers;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected int id = 0;
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();
    protected final HistoryManager historyManager = Managers.getDefaultHistory();
    protected final Map<LocalDateTime, Task> prioritizedTasks = new TreeMap<>();


    public Map<Integer, Task> getTask() {
        return tasks;
    }

    public Map<Integer, Epic> getEpic() {
        return epics;
    }

    public Map<Integer, Subtask> getSubTask() {
        return subtasks;
    }

    public int getId() {
        return ++this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        List<Task> tasksByPriority = new ArrayList<>();
        List<Task> startTimeNotSetTasks = new ArrayList<>();
        for (Task prioritizedTask : prioritizedTasks.values()) {
            if (prioritizedTask.getDuration() == 0) {
                startTimeNotSetTasks.add(prioritizedTask);
            } else {
                tasksByPriority.add(prioritizedTask);
            }
        }
        tasksByPriority.addAll(startTimeNotSetTasks);
        return tasksByPriority;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public List<Task> getAllTasks() {
        List<Task> allTasks = new ArrayList<>();
        for (Task task : tasks.values()) {
            allTasks.add(task);
        }
        return allTasks;
    }

    @Override
    public List<Epic> getAllEpics() {
        List<Epic> allEpics = new ArrayList<>();
        for (Epic epic : epics.values()) {
            allEpics.add(epic);
        }
        return allEpics;
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        List<Subtask> allSubtasks = new ArrayList<>();
        for (Subtask subtask : subtasks.values()) {
            allSubtasks.add(subtask);
        }
        return allSubtasks;
    }

    @Override
    public void createTask(Task task) {
        try {
            checkingOverlaysOfPeriods(task.getStartTime(), task.getEndTime(), null);
            int id = getId();
            task.setId(id);
            this.tasks.put(id, task);
            prioritizedTasks.put(task.getStartTime(), task);
        } catch (UnsupportedOperationException exception) {
            System.out.println(exception.getMessage() + "Задача <" + task.getName() + "> не добавлена!\n");
        }
    }

    @Override
    public void createSubTask(Subtask subtask) {
        try {
            checkingOverlaysOfPeriods(subtask.getStartTime(), subtask.getEndTime(), null);
            Epic epic = getEpicById(subtask.getIdEpic());

            int idSubtask = getId();
            subtask.setId(idSubtask);
            this.subtasks.put(idSubtask, subtask);

            epic.setListSubTask(subtask);
            setStatusEpic(subtask.getIdEpic());
            setStartEndEpic(subtask.getIdEpic());

            prioritizedTasks.put(subtask.getStartTime(), subtask);

        } catch (UnsupportedOperationException exception) {
            System.out.println(exception.getMessage() + "Подзадача <" + subtask.getName() + "> не добавлена!\n");
        } catch (NullPointerException exception) {
            System.out.println(exception.getMessage() + "Подзадача <" + subtask.getName() + "> не добавлена!\n");
        }
    }

    @Override
    public void createEpic(Epic epic) {
        int id = getId();
        epic.setId(id);
        this.epics.put(id, epic);
    }

    @Override
    public void updatedTask(Task task) {
        try {
            checkingOverlaysOfPeriods(task.getStartTime(), task.getEndTime(), task.getId());
            this.tasks.put(task.getId(), task);
            System.out.println("task = " + task);
            prioritizedTasks.put(task.getStartTime(), task);
        } catch (UnsupportedOperationException exception) {
            System.out.println("Задача <" + task.getName() + "> не обновлена!\n");
        }
    }

    @Override
    public void updatedEpic(Epic epic) {
        this.epics.put(epic.getId(), epic);
        setStatusEpic(epic.getId());
    }

    @Override
    public void updatedSubTask(Subtask subTask) {
        try {
            checkingOverlaysOfPeriods(subTask.getStartTime(), subTask.getEndTime(), subTask.getId());
            this.subtasks.put(subTask.getId(), subTask);
            setStatusEpic(subTask.getIdEpic());
            setStartEndEpic(subTask.getIdEpic());
            prioritizedTasks.put(subTask.getStartTime(), subTask);
        } catch (UnsupportedOperationException exception) {
            System.out.println("Подзадача <" + subTask.getName() + "> не обновлена!\n");
        }
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    @Override
    public Subtask getSubTaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            historyManager.add(subtask);
        }
        return subtask;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.add(epic);
            return epic;
        } else {
            throw new NullPointerException(String.format("Эпик с номером [%d] не существует!", id));
        }
    }

    @Override
    public List<Subtask> getListSubTasks(int id) {
        if (getEpicById(id) != null) {
            return getEpicById(id).getListSubTask();
        } else {
            System.out.println("Эпика с номером " + id + " не существует!");
            return null;
        }
    }

    @Override
    public StatusTask getStatusById(int id) {
        if (tasks.containsKey(id)) {
            return tasks.get(id).getStatus();
        } else if (epics.containsKey(id)) {
            return epics.get(id).getStatus();
        } else if (subtasks.containsKey(id)) {
            return subtasks.get(id).getStatus();
        } else {
            System.out.println("Задачи с номером " + id + " нет в списке!");
            return null;
        }
    }

    @Override
    public void deleteTaskById(int id) {
        Task removedTask = tasks.remove(id);
        if (removedTask == null) {
            System.out.println("Задачи с номером " + id + " нет в списке!");
        } else {
            LocalDateTime keyRemovedTask = removedTask.getStartTime();
            historyManager.remove(id);
            prioritizedTasks.remove(keyRemovedTask);
        }
    }

    @Override
    public void deleteSubTaskById(int id) {
        Subtask removedSubtask = subtasks.remove(id);
        if (removedSubtask == null) {
            System.out.println("Подзадачи с номером " + id + " нет в списке!");
        } else {
            LocalDateTime keyRemovedSubtask = removedSubtask.getStartTime();
            historyManager.remove(id);
            prioritizedTasks.remove(keyRemovedSubtask);
            int idEpic = removedSubtask.getIdEpic();
            epics.get(idEpic).deleteSubTask(removedSubtask);
            setStatusEpic(idEpic);
            setStartEndEpic(idEpic);
        }
    }

    @Override
    public void deleteEpicById(int id) {
        Epic removedEpic = epics.remove(id);
        if (removedEpic == null) {
            System.out.println("Эпика с номером " + id + " нет в списке!");
        } else {
            historyManager.remove(id);
            for (Subtask subTaskEpic : removedEpic.getListSubTask()) {
                int idSubtask = subTaskEpic.getId();
                subtasks.remove(idSubtask);
                historyManager.remove(idSubtask);
                LocalDateTime keyRemovedSubtask = subTaskEpic.getStartTime();
                prioritizedTasks.remove(keyRemovedSubtask);
            }
        }
    }

    @Override
    public void deleteAllTask() {
        if (!tasks.isEmpty()) {
            tasks.clear();
        }
        if (!subtasks.isEmpty()) {
            subtasks.clear();
        }
        if (!epics.isEmpty()) {
            epics.clear();
        }
        this.id = 0;
        prioritizedTasks.clear();
    }

    private void checkingOverlaysOfPeriods(LocalDateTime startTime, LocalDateTime endTime, Integer idForUpdateTask) {
        for (Task prioritizedTask : getPrioritizedTasks()) {
            boolean isСondition;
            if (idForUpdateTask != null) {
                isСondition = (prioritizedTask.getDuration() != 0)
                        && (prioritizedTask.getId() != idForUpdateTask);
            } else {
                isСondition = (prioritizedTask.getDuration() != 0);
            }
            if (isСondition) {
                if (endTime.isAfter(prioritizedTask.getStartTime())
                        && startTime.isBefore(prioritizedTask.getEndTime())) {
                    throw new UnsupportedOperationException("Обнаружено пересечение периодов!");
                }
            }
        }
    }

    private void setStartEndEpic(int idEpic) {
        SortedSet<LocalDateTime> startTime = new TreeSet<>();
        SortedSet<LocalDateTime> endTime = new TreeSet<>();
        List<Subtask> listSubTask = getListSubTasks(idEpic);

        for (Subtask subtask : listSubTask) {
            if (subtask.getIdEpic() == idEpic) {
                startTime.add(subtask.getStartTime());
                endTime.add(subtask.getEndTime());
            }
        }
        epics.get(idEpic).setStartTime(startTime.first());
        epics.get(idEpic).setEndTimeEpic(endTime.last());
    }

    private void setStatusEpic(int idEpic) {
        List<Subtask> listSubTask = getListSubTasks(idEpic);
        boolean isStatusNew = false;
        boolean isStatusInProgess = false;
        boolean isStatusDone = false;

        for (Subtask subtask : listSubTask) {
            switch (subtask.getStatus().toString()) {
                case ("NEW"):
                    isStatusNew = true;
                    break;
                case ("IN_PROGRESS"):
                    isStatusInProgess = true;
                    break;
                case ("DONE"):
                    isStatusDone = true;
                    break;
            }
        }

        if (!isStatusNew && !isStatusInProgess && isStatusDone) {
            epics.get(idEpic).setStatus(StatusTask.DONE);
        } else if (isStatusNew && !isStatusInProgess && !isStatusDone) {
            epics.get(idEpic).setStatus(StatusTask.NEW);
        } else {
            epics.get(idEpic).setStatus(StatusTask.IN_PROGRESS);
        }
    }
}
