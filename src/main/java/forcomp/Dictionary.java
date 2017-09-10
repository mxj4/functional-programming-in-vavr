package forcomp;

import io.vavr.collection.Seq;
import io.vavr.collection.Stream;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Dictionary {
    public static Seq<String> load() {
        try {
            return Stream.ofAll(Files.lines(Paths.get("src", "main", "resources", "forcomp", "linuxwords.txt")));
        } catch (IOException e) {
            throw new RuntimeException("Could not load word list: ", e);
        }
    }
}
