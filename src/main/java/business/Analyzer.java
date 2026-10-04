package business;

import java.net.Inet4Address;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;

import model.Findings;
import model.FlaggedEntry;
import model.Log;
import model.Rulebook;

public class Analyzer {
    public static Findings analyzeLog(final Log log, final Rulebook rulebook) {
        final Findings findings = new Findings();
        final Map<String, Integer> rules = rulebook.getLevelToSeverity();
        final Map<Inet4Address, Integer> ipCounts = new HashMap<>();
        final Set<Inet4Address> suspiciousIps = new HashSet<>();

        for (final Log.Entry entry : log.getEntries()) {
            final Inet4Address ip = entry.getAddress();
            final String level = entry.getLevel().getValue();
            final Integer severity = rules.get(level);
            
            ipCounts.merge(ip, 1, Integer::sum);

            if (severity == null) {
                findings.getUnknownPatterns().add(new Findings.UnknownPattern(entry.getLine(), entry.getRaw()));
            } else {
                findings.getLevelToOccurrences().merge(level, 1, Integer::sum);
                if (severity >= 3) {
                    findings.getFlaggedEntries().add(new FlaggedEntry(entry.getRaw(), severity));
                    suspiciousIps.add(entry.getAddress());
                }
            }

        }
        
        for (final Inet4Address ip : suspiciousIps) {
            findings.getIpToOccurrences().put(ip, ipCounts.get(ip));
        }

        findings.getMalformedEntries().addAll(log.getMalformedEntries());
        return findings;
    }
}
