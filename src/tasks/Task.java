package tasks;

import managers.CSVSerializator;
import managers.StatusTask;
import managers.TypeTask;

import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    protected int id;
    protected TypeTask type;
    protected String name;
    protected String description;
    protected LocalDateTime startTime = LocalDateTime.now();
    protected Long duration = 0L;
    protected StatusTask status = StatusTask.NEW;

    public Task(TypeTask type, String name, String description, LocalDateTime startTime, Long duration) {
        this.type = type;
        this.name = name;
        this.description = description;
        if (startTime != null) {
            this.startTime = startTime;
        }
        if (duration != null) {
            this.duration = duration;
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TypeTask getType() {
        return type;
    }

    public void setType(TypeTask type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public StatusTask getStatus() {
        return this.status;
    }

    public void setStatus(StatusTask status) {
        this.status = status;
    }

    public LocalDateTime getStartTime() {
        return this.startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public LocalDateTime getEndTime() {
        return this.startTime.plusMinutes(this.duration);
    }

    @Override
    public String toString() {
        String result = "(номер задачи= " + id + ", тип задачи = " + type + ", статус= " + status + ", "
                + "название= " + name;
        result = result + ", содержание: " + description;
        if (duration == 0) {
            result = result + ", сроки задачи не определены " + ") " + '\n';
        } else {
            result = result + ", дата начала: "
                    + startTime.format(CSVSerializator.DATE_TIME_FORMATTER);
            result = result + ", дата окончания: "
                    + startTime.plusMinutes(this.duration).format(CSVSerializator.DATE_TIME_FORMATTER);
            result = result + ") " + '\n';
        }
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (this.getClass() != obj.getClass()) return false;
        Task otherTask = (Task) obj;
        return (id == otherTask.id) &&
                Objects.equals(type, otherTask.type) &&
                Objects.equals(name, otherTask.name) &&
                Objects.equals(description, otherTask.description) &&
                Objects.equals(status, otherTask.status) &&
                Objects.equals(startTime, otherTask.startTime) &&
                Objects.equals(duration, otherTask.duration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.type, this.name, this.description, this.status,
                this.startTime, this.duration);
    }

}
