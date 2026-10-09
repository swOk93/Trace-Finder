import business.Analyzer;
import business.Parser;
import business.Reporter;
import exception.InvalidOutputPathException;
import exception.LogUnreadableException;
import exception.OutputWriteException;
import exception.RulebookFormatException;
import exception.RulebookReadException;
import model.Findings;
import model.Log;
import model.Rulebook;

class TraceFinder {
    public static void main(final String[] args) {
        if (args.length != 3) {
            System.out.println("Wrong number of arguments. Please provide log file, rulebook and report name.");
            System.exit(1);
        }
        try {
            final Log log = Parser.parseLog(args[0]); // parses the log file into valid and malformed log entries
            final Rulebook rulebook = Parser.parseRulebook(args[1]); // converts file into hashmap Level -> severity
            final Findings findings = Analyzer.analyzeLog(log, rulebook); // analyzes and prepares data
            Reporter.writeReportToFile(args[2], findings); // formats the findings and writes the report to the output file
        } catch (final LogUnreadableException |
                       RulebookFormatException |
                       RulebookReadException |
                       InvalidOutputPathException |
                       OutputWriteException e) {
            System.out.println(e);
            System.exit(1);
        }
    }
}
