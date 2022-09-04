package utility;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;


public class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
    private DateTimeFormatter myFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
//    ISO_LOCAL_DATE_TIME
    @Override
    public void write(JsonWriter jsonWriter, LocalDateTime localDate) throws IOException {
        LocalDateTime value = Objects.nonNull(localDate) ? localDate : LocalDateTime.now();
//        jsonWriter.value(value.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        jsonWriter.value(localDate.format(myFormatter));
    }

    @Override
    public LocalDateTime read(final JsonReader jsonReader) throws IOException {
//        return LocalDateTime.parse(jsonReader.nextString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        return LocalDateTime.parse(jsonReader.nextString(), myFormatter);
    }

}
