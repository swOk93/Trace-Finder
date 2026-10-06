package business;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.InvalidPathException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import exception.LogUnreadableException;
import exception.RulebookFormatException;
import exception.RulebookReadException;
import model.Action;
import model.Level;
import model.Log;
import model.Rulebook;
import model.MalformedEntry;

public class Parser {
    public static Log parseLog(final String filepath) throws LogUnreadableException {
        final java.nio.file.Path path;
        try {
            path = java.nio.file.Path.of(filepath);
        } catch (final InvalidPathException e) {
            throw new LogUnreadableException(e, filepath);
        }
        final Log log = new Log(); // empty container where will store good strings and malformed
        try (final LineNumberReader lineNumberReader = 
                    new LineNumberReader(new FileReader(path.toFile()))) {
            while (true) {
                final String line = lineNumberReader.readLine();
                if (line == null) break; // end of file
                final int lineNumber = lineNumberReader.getLineNumber();

                parseLogLine(line, lineNumber, log);
            }
        } catch (final FileNotFoundException e) {
            throw new LogUnreadableException(e, filepath);
        } catch (final IOException e) {
            throw new LogUnreadableException(e, filepath);
        }

        return log;
    }

    static void parseLogLine(String line, int lineNumber, Log log) {
        String[] parts = line.split("\\|", -1); // "-1" for not cut the last empty part

        if (line.isBlank() || parts.length != 5) { // if there is empty line or not 5 parts, sent to malformed
            log.getMalformedEntries().add(new MalformedEntry(lineNumber, line)); 
            return;
        }

        String timestampText = parts[0].trim();
        String levelText = parts[1].trim();
        String ipText = parts[2].trim();
        String pathText = parts[3].trim();
        String actionText = parts[4].trim();

        try {
            final LocalDateTime timestamp = 
                    LocalDateTime.parse(
                        timestampText, DateTimeFormatter
                        .ofPattern("yyyy-MM-dd HH:mm:ss")); // which format we expect
            final Level level = new Level(levelText);
            final InetAddress inetAddress = InetAddress.getByName(ipText); // return common type of InetAddress

            if (!(inetAddress instanceof Inet4Address)) { // check if it exactly Inet4Address and
                log.getMalformedEntries().add(new MalformedEntry(lineNumber, line)); // sent to malformed if not
                return;
            }

            final Inet4Address address = (Inet4Address) inetAddress;
            // model.Path because of conflict with name of java class java.nio.file.Path
            final model.Path targetPath = new model.Path(pathText);
            final Action action = new Action(actionText);
            final Log.Entry entry =
                    new Log.Entry(
                            lineNumber,
                            line,
                            timestamp,
                            level,
                            address,
                            targetPath,
                            action
                    );
            log.getEntries().add(entry);
        } catch (final DateTimeParseException | UnknownHostException e) {
            log.getMalformedEntries().add(new MalformedEntry(lineNumber, line));
        }
    }

    public static Rulebook parseRulebook(final String filepath) throws RulebookReadException, RulebookFormatException {
        final java.nio.file.Path path;
        try {
            path = java.nio.file.Path.of(filepath);
        } catch (final InvalidPathException e) {
            throw new RulebookReadException(e, filepath); 
        }
        final Rulebook rulebook = new Rulebook();
        try (final LineNumberReader lineNumberReader = 
                    new LineNumberReader(new FileReader(path.toFile()))) {
            if (!"level,severity_score".equals(lineNumberReader.readLine())) { // first line is a header
                throw new RulebookFormatException("wrong header");
            }
            while (true) {
                final String line = lineNumberReader.readLine();
                if (line == null) break; // end of file
                parseRulebookLine(line, rulebook);
            }
        } catch (final IOException e) {
            throw new RulebookReadException(e, filepath);
        }
        return rulebook;
    }

    static void parseRulebookLine(String line, Rulebook rulebook) throws RulebookFormatException {
        if (line.isBlank()) throw new RulebookFormatException("The line is blank");
        String[] parts = line.split(",", -1); // "-1" for not cut the last empty part
        if (parts.length != 2) throw new RulebookFormatException("More, or less then 2 parts");
        parts[0] = parts[0].trim();
        parts[1] = parts[1].trim();
        if (parts[0].isEmpty()) throw new RulebookFormatException("Level is missing");
        try {
            rulebook.getLevelToSeverity().put(parts[0], Integer.parseInt(parts[1]));
        } catch (NumberFormatException e) {
            throw new RulebookFormatException(e, "Severity is not a number");
        }
    }
}
