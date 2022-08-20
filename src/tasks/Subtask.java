package tasks;

import managers.CSVSerializator;
import managers.TypeTask;

import java.time.LocalDateTime;
import java.util.Objects;

public class Subtask extends Task {
    private int epicId;

    public Subtask(TypeTask type, String name, String description, LocalDateTime startTime, Long duration, int epicId) {
        super(type, name, description, startTime, duration);
        this.epicId = epicId;
    }

    public int getIdEpic() {
        return epicId;
    }

    @Override
    public String toString() {
        String result = "(id = " + id + ", тип задачи = " + type + ", статус= " + status + ", "
                + "название= " + name;
        result = result + ", содержание: " + description;

        if (duration == 0) {
            result = result + ", сроки задачи не определены " + ") " + '\n';
        } else {
            result = result + ", дата начала: "
                    + startTime.format(CSVSerializator.DATE_TIME_FORMATTER);
            result = result + ", дата окончания: "
                    + startTime.plusMinutes(this.duration).format(CSVSerializator.DATE_TIME_FORMATTER);
            result = result + ", номер эпика: "
                    + epicId;
            result = result + ") " + '\n';
        }
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (this.getClass() != obj.getClass()) return false;
        Subtask otherTask = (Subtask) obj;
        return (id == otherTask.id) &&
                Objects.equals(type, otherTask.type) &&
                Objects.equals(name, otherTask.name) &&
                Objects.equals(description, otherTask.description) &&
                Objects.equals(status, otherTask.status) &&
                Objects.equals(startTime, otherTask.startTime) &&
                Objects.equals(duration, otherTask.duration) &&
                (epicId == otherTask.epicId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.type, this.name, this.description, this.status,
                this.startTime, this.duration, this.epicId);
    }

}