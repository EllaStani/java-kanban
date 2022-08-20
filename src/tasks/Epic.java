package tasks;

import managers.CSVSerializator;
import managers.TypeTask;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {
    private LocalDateTime endTimeEpic = LocalDateTime.now();
    private List<Subtask> subtasks;

    public Epic(TypeTask type, String name, String description, LocalDateTime startTime, Long duration) {
        super(type, name, description, startTime, duration);
        this.subtasks = new ArrayList<>();
    }

    public LocalDateTime getEndTimeEpic() {
        return this.endTimeEpic;
    }

    public void setEndTimeEpic(LocalDateTime endTime) {
        this.endTimeEpic = endTime;
    }

    public void setListSubTask(Subtask subTask) {
        this.subtasks.add(subTask);
    }

    public List<Subtask> getListSubTask() {
        return subtasks;
    }

    public void deleteSubTask(Subtask subTask) {
        this.subtasks.remove(subTask);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (this.getClass() != obj.getClass()) return false;
        Epic otherTask = (Epic) obj;
        return (id == otherTask.id) &&
                Objects.equals(type, otherTask.type) &&
                Objects.equals(name, otherTask.name) &&
                Objects.equals(description, otherTask.description) &&
                Objects.equals(status, otherTask.status) &&
                Objects.equals(startTime, otherTask.startTime) &&
                Objects.equals(duration, otherTask.duration) &&
                Objects.equals(endTimeEpic, otherTask.endTimeEpic) &&
                Objects.equals(subtasks, otherTask.subtasks);
    }

    @Override
    public String toString() {
        String result = "(номер Эпика = " + id + ", тип задачи = " + type + ", статус= " + status + ", "
                + "название= " + name;
        result = result + ", содержание: " + description;
        result = result + ", дата начала: "
                + startTime.format(CSVSerializator.DATE_TIME_FORMATTER);
        result = result + ", дата окончания: "
                + endTimeEpic.format(CSVSerializator.DATE_TIME_FORMATTER);
        result = result + ") " + '\n';
        return result;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.type, this.name, this.description, this.status,
                this.startTime, this.duration, this.endTimeEpic, this.subtasks);
    }
}
