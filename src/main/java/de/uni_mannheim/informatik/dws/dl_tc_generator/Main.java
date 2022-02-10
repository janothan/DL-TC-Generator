package de.uni_mannheim.informatik.dws.dl_tc_generator;

import de.uni_mannheim.informatik.dws.dl_tc_generator.by_query.GeneratorQuery;
import org.apache.commons.cli.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * de.uni_mannheim.informatik.dws.dl_tc_generator.Main class for command line.
 */
public class Main {


    public static final String DEFAULT_RESULT_DIR = "./results";

    public static void main(String[] args) {
        Options options = new Options();
        options.addOption("d", "directory", true,
                "The test directory that shall be written or analyzed (-a). " +
                        "If the directory shall be written, it must not exist yet.");

        options.addOption(
                Option.builder("q")
                        .longOpt("queries")
                        .desc("The directory where the queries reside.")
                        .numberOfArgs(1)
                        .build()
        );

        options.addOption(Option.builder("a")
                .longOpt("analyze")
                .hasArg(false)
                .build());

        options.addOption("t", "timeout", true, "Time out in seconds for queries.");
        options.addOption(Option.builder("s")
                .longOpt("sizes")
                .desc("The sizes for the test cases, space separated.")
                .hasArgs() // indicating potentially unlimited arguments
                .valueSeparator(' ') // separator to separate arguments
                .build()
        );
        options.addOption(Option.builder("tcc")
                .longOpt("tc_collection")
                .desc("The test case collection such as 'tc1'; space separated.")
                .hasArgs()
                .valueSeparator(' ')
                .build()
        );
        options.addOption(Option.builder("tc")
                .longOpt("tc_group")
                .desc("The test case group such as 'cities'; space separated.")
                .hasArgs()
                .valueSeparator(' ')
                .build()
        );
        options.addOption(Option.builder("h")
                .longOpt("help")
                .desc("Print this help message.")
                .build());


        try {
            CommandLineParser parser = new DefaultParser();
            CommandLine cmd = parser.parse(options, args);

            if (cmd.hasOption("h")) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("ant", options);
                return;
            }

            int[] sizeArray = null;
            if (cmd.hasOption("s")) {
                String[] sValues = cmd.getOptionValues("s");
                List<Integer> sizeList = new ArrayList<>();
                if (sValues != null) {
                    for (String sValue : sValues) {
                        try {
                            int individualSize = Integer.parseInt(sValue);
                            if (individualSize < 1) {
                                System.out.println("Sizes must be >=1. ABORTING program.");
                                return;
                            }
                            sizeList.add(individualSize);
                        } catch (NumberFormatException nfe) {
                            System.out.println("A number format exception occurred while parsing the values for -s. " +
                                    "ABORTING program.");
                            return;
                        }
                    }
                    sizeArray = sizeList.stream().mapToInt(i -> i).toArray();
                }
            }

            if (cmd.hasOption("a")){
                if(cmd.hasOption("d")){
                    String directory = cmd.getOptionValue("d");
                    ResultValidator rv = new ResultValidator(new File(directory));
                    if(sizeArray != null){
                        rv.setSizeRestriction(sizeArray);
                    }
                    rv.validate();
                } else {
                    System.out.println("If you run the analyze function, you must also provide a directory " +
                            "(-d <directory_to_be_analyzed>). ABORTING program.");
                }
                return;
            }

            String queryDirectory;
            if (cmd.hasOption("q")) {
                queryDirectory = cmd.getOptionValue("q");
            } else {
                System.out.println("Missing mandatory option -q for query directory. Call help via -h." +
                        "ABORTING program.");
                return;
            }

            String resultDirectory = cmd.getOptionValue("d", DEFAULT_RESULT_DIR);
            if (resultDirectory.equals(DEFAULT_RESULT_DIR)) {
                System.out.println("Using default result directory for the query results: " + DEFAULT_RESULT_DIR);
            }
            GeneratorQuery generator = new GeneratorQuery(queryDirectory, resultDirectory);

            if(sizeArray != null) {
                generator.setSizes(sizeArray);
            }

            if (cmd.hasOption("t")) {
                try {
                    int timeoutInSeconds = Integer.parseInt(cmd.getOptionValue("t"));
                    generator.setTimeoutInSeconds(timeoutInSeconds);
                } catch (NumberFormatException nfe) {
                    System.out.println("A number format exception occurred while parsing the values for -t. " +
                            "ABORTING program.");
                    return;
                }
            }

            if (cmd.hasOption("tcc")){
                String[] tccValues = cmd.getOptionValues("tcc");
                if(tccValues != null && tccValues.length > 0){
                    generator.setIncludeOnlyCollection(tccValues);
                }
            }

            if (cmd.hasOption("tc")){
                String [] tcgValues = cmd.getOptionValues("tc");
                if(tcgValues != null && tcgValues.length > 0){
                    generator.setIncludeOnlyTestCase(tcgValues);
                }
            }

            generator.generateTestCases();
            System.out.println("Generation completed.");
        } catch (ParseException e) {
            System.out.println("A parse error occurred.");
            e.printStackTrace();
        }

    }

}
