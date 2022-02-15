package de.uni_mannheim.informatik.dws.dl_tc_generator;

import de.uni_mannheim.informatik.dws.dl_tc_generator.by_query.GeneratorQuery;
import de.uni_mannheim.informatik.dws.dl_tc_generator.synthetic.GeneratorSynthetic;
import org.apache.commons.cli.*;
import org.apache.jena.reasoner.rulesys.impl.Generator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * de.uni_mannheim.informatik.dws.dl_tc_generator.Main class for command line.
 */
public class Main {


    public static final String DEFAULT_RESULT_DIR = "./results";

    /**
     * Generator (static variable for testing).
     */
    private static IGenerator generator = null;

    public static void main(String[] args) {
        Options options = new Options();
        options.addOption("d", "directory", true,
                "The test directory that shall be written or analyzed (-a). " +
                        "If the directory shall be written, it must not exist yet.");

        options.addOption(
                Option.builder("q")
                        .longOpt("queries")
                        .desc("The directory where the queries reside. If parameter -q is not specified, the " +
                                "synthetic query module is used.")
                        .numberOfArgs(1)
                        .build()
        );

        options.addOption(Option.builder("a")
                .longOpt("analyze")
                .hasArg(false)
                .build());

        options.addOption(Option.builder("e")
                .longOpt("edges")
                .desc("Only valid for the synthetic generator. The parameter specifies " +
                        "the number of edges to be used.")
                .numberOfArgs(1)
                .build());
        options.addOption(Option.builder("n")
                .longOpt("nodes")
                .desc("Only valid for the synthetic generator. The total nodes factor determines " +
                        "the maximum number of nodes in a graph (totalNodesFactor * nodesOfInterest).")
                .numberOfArgs(1)
                .build()
        );
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
                .desc("The test case collection such as 'tc01'; space separated.")
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

            if (cmd.hasOption("a")) {
                if (cmd.hasOption("d")) {
                    String directory = cmd.getOptionValue("d");
                    ResultValidator rv = new ResultValidator(new File(directory));
                    if (sizeArray != null) {
                        rv.setSizeRestriction(sizeArray);
                    }
                    rv.validate();
                } else {
                    System.out.println("If you run the analyze function, you must also provide a directory " +
                            "(-d <directory_to_be_analyzed>). ABORTING program.");
                }
                return;
            }

            String resultDirectory = cmd.getOptionValue("d", DEFAULT_RESULT_DIR);
            if (resultDirectory.equals(DEFAULT_RESULT_DIR)) {
                System.out.println("Using default result directory for the query results: " + DEFAULT_RESULT_DIR);
            }

            String queryDirectory = null;


            if (cmd.hasOption("q")) {
                queryDirectory = cmd.getOptionValue("q");
                System.out.println("Query directory provided. Using query-based test case generator.");
                generator = new GeneratorQuery(queryDirectory, resultDirectory);
            } else {
                System.out.println("Missing option -q for query directory. Therefore, synthetic datasets will be " +
                        "calculated.");
                generator = new GeneratorSynthetic(resultDirectory);
            }

            if (sizeArray != null) {
                generator.setSizes(sizeArray);
            }

            if (cmd.hasOption("t")) {
                if (generator instanceof GeneratorQuery) {
                    try {
                        int timeoutInSeconds = Integer.parseInt(cmd.getOptionValue("t"));
                        ((GeneratorQuery) generator).setTimeoutInSeconds(timeoutInSeconds);
                    } catch (NumberFormatException nfe) {
                        System.out.println("A number format exception occurred while parsing the values for -t. " +
                                "ABORTING program.");
                        return;
                    }
                } else {
                    System.out.println("Timeout can only be set of GeneratorQuery. ABORTING program.");
                    return;
                }
            }

            if (cmd.hasOption("tcc")) {
                String[] tccValues = cmd.getOptionValues("tcc");
                if (tccValues != null && tccValues.length > 0) {
                    generator.setIncludeOnlyCollection(tccValues);
                }
            }

            if (cmd.hasOption("tc")) {
                if (generator instanceof GeneratorQuery) {
                    String[] tcgValues = cmd.getOptionValues("tc");
                    if (tcgValues != null && tcgValues.length > 0) {
                        ((GeneratorQuery) generator).setIncludeOnlyTestCase(tcgValues);
                    }
                } else {
                    System.out.println("tc can only be set for GeneratorQuery. ABORTING program.");
                    return;
                }
            }

            if (cmd.hasOption("e")) {
                if (generator instanceof GeneratorSynthetic) {
                    try {
                        int numberEdges = Integer.parseInt(cmd.getOptionValue("e"));
                        ((GeneratorSynthetic) generator).setNumberOfEdges(numberEdges);
                    } catch (NumberFormatException nfe) {
                        System.out.println("A number format exception occurred while parsing the values for -e. " +
                                "ABORTING program.");
                        return;
                    }
                } else {
                    System.out.println("The parameter -e is only valid for the synthetic generator. ABORTING " +
                            "program.");
                    return;
                }
            }

            if (cmd.hasOption("n")) {
                if (generator instanceof GeneratorSynthetic) {
                    try {
                        int nodesFactor = Integer.parseInt(cmd.getOptionValue("n"));
                        ((GeneratorSynthetic) generator).setNodesFactor(nodesFactor);
                    } catch (NumberFormatException nfe) {
                        System.out.println("A number format exception occurred while parsing the values for -n. " +
                                "ABORTING program.");
                        return;
                    }
                } else {
                    System.out.println("The parameter -n is only valid for the synthetic generator. ABORTING " +
                            "program.");
                    return;
                }
            }

            generator.generateTestCases();
            System.out.println("Generation completed.");
        } catch (ParseException e) {
            System.out.println("A parse error occurred.");
            e.printStackTrace();
        }

    }

    public static IGenerator getGenerator() {
        return generator;
    }
}
