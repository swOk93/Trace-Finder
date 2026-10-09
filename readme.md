# Trace Finder

This program reads a log file, a rulebook, and the path of the report file, which will be created if it does not exist.
Then the program analyzes the logs and creates a report containing activity counts, 
flagged entries with a severity level of 3 or higher, suspicious activity by IP address, 
unknown patterns, and malformed lines.

## What it does

- Reads a log file
- Reads a severity rulebook
- Analizes log entries
- detects flagged entries by rulebook and suspicious Ip addresses 
- reports unknown patterns and malformed lines
- writes the result to a report file

## Requirements

- Java 21
- Maven

## Build

mvn clean package
java -jar tracefinder.jar logs.txt rules.csv report.txt

## Arguments 

1. Path to the log file
2. Path to the rulebook csv file
3. Path to the output report file

## Log format

Each log line must contain:
timestamp | level | source IP | target | action
Example:
2024-03-15 02:14:08 | INFO | 192.168.1.45 | /login | success

## Rulebook format

level,severity_score
INFO,1
WARN,3
ERROR,5
ALERT,9



