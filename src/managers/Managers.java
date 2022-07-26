package managers;

import Server.KVServer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import utility.LocalDateTimeAdapter;

import java.io.IOException;
import java.time.LocalDateTime;

public class Managers {
    private static KVServer kvServer;

    public static TaskManager getDefault() {
        return new HttpTaskManager(KVServer.PORT);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static KVServer getDefaultKVServer() throws IOException {
        if (kvServer == null) {
            kvServer = new KVServer();
            kvServer.start();
        }
        return kvServer;
    }

    public static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        return gsonBuilder.create();
    }
}
