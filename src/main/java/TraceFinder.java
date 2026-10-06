import exception.LogUnreadableException;
import exception.RulebookFormatException;
import exception.RulebookReadException;
import business.Parser;
import business.Analyzer;
import business.Reporter;
import model.Rulebook;
import model.Findings;
import model.Log;

class tracefinder {
    public static void main(final String[] args) {
        if (args.length != 3) {
            System.out.println("Wrong number of arguments. Please provide log file, rulebook and report name.");
            System.exit(1);
        }
        try {
            final Log log = Parser.parseLog(args[0]);
            final Rulebook rulebook = Parser.parseRulebook(args[1]);
            final Findings findings = Analyzer.analyzeLog(log, rulebook);
            Reporter.writeReport(args[2], findings);
        } catch (final LogUnreadableException e) {
            System.out.println(e);
            System.exit(1);
        } catch (final RulebookFormatException | RulebookReadException e) {
            System.out.println(e);
            System.exit(1);
        }
    }
}
