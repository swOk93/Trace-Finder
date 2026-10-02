package business;

import java.io.File;

import exception.LogUnreadableException;
import model.Log;
import model.Rulebook;

public class Parser {
    public static Log parseLog(final String filepath) throws LogUnreadableException {
        final java.nio.file.Path path;
        try {
            path = java.nio.file.Path.of(filepath);
        } catch (final InvalidPathException e) {
            throw new LogUnreadableException(e, filepath);
        }
        final Log log = new Log();
        try (final LineNumberReader lineNumberReader = new LineNumberReader(new FileReader(path.toFile()))) {
            while (true) {
                final String line = lineNumberReader.readLine();
                if (line == null) break;
                final int lineNumber = lineNumberReader.getLineNumber();
                log.getEntries().add(new Log.Entry(lineNumber, line, null, null, null, null, null));
            }
        } catch (final FileNotFoundException e) {
            throw new LogUnreadableException(e, filepath);
        } catch (final IOException e) {
            throw new LogUnreadableException(e, filepath);
        }
        //TODO: implement logic
        return log;
    }

    public static Rulebook parseRulebook(final String filepath) throws LogUnreadableException {
        //TODO: implement logic
    }
}
