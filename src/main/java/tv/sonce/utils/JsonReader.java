package tv.sonce.utils;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class JsonReader <T> {
    private final String pathToFile;
    private final T structure;

    public JsonReader(String pathToFile, T structure) {
        this.pathToFile = pathToFile;
        this.structure = structure;
    }

    public T getContent() {
        Gson gson = new Gson();

        try(FileInputStream fis = new FileInputStream(pathToFile)) {
            Reader reader = new InputStreamReader(fis, StandardCharsets.UTF_8);
            return (T) gson.fromJson(reader, structure.getClass());
        } catch (IOException e) {
            throw new JsonParseException(e.getMessage());
        }
    }
}
