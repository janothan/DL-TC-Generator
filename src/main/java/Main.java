import org.apache.commons.cli.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Main class for command line.
 */
public class Main {


    public static final String DEFAULT_RESULT_DIR = "./results";

    public static void main(String[] args) {
        Options options = new Options();
        options.addOption("d", "directory", true,
                "The test directory that shall be written. The directory must not exist yet.");
        options.addOption(
                Option.builder("q")
                        .longOpt("queries")
                        .desc("Time out in seconds for queries.")
                        .numberOfArgs(1)
                        .required()
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
                .desc("The test case collection such as 'tc1'; space separated.")
                .hasArgs()
                .valueSeparator(' ')
                .build()
        );
        options.addOption(Option.builder("tc")
                .longOpt("tc_group")
                .desc("The test case group such as 'cities'; space separated.")
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

            String queryDirectory;
            if (cmd.hasOption("q")) {
                queryDirectory = cmd.getOptionValue("q");
            } else {
                System.out.println("Missing mandatory option -q for query directory. Call help via -h." +
                        "ABORTING program.");
                return;
            }

            String resultDirectory = cmd.getOptionValue("d", DEFAULT_RESULT_DIR);
            Generator generator = new Generator(queryDirectory, resultDirectory);

            if(cmd.hasOption("s")){
                String[] sValues = cmd.getOptionValues("s");
                List<Integer> sizeList = new ArrayList<>();
                if(sValues != null){
                    for(String sValue : sValues) {
                        try {
                            sizeList.add(Integer.parseInt(sValue));
                        } catch (NumberFormatException nfe) {
                            System.out.println("A number format exception occurred while parsing the values for -s. " +
                                    "ABORTING program.");
                        }
                    }
                    int[] sizeArray = sizeList.stream().mapToInt(i->i).toArray();
                    generator.setSizes(sizeArray);
                }
            }

            if(cmd.hasOption("t")){
                try {
                    int timeoutInSeconds = Integer.parseInt(cmd.getOptionValue("t"));
                    generator.setTimeoutInSeconds(timeoutInSeconds);
                } catch (NumberFormatException nfe){
                    System.out.println("A number format exception occurred while parsing the values for -t. " +
                            "ABORTING program.");
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
