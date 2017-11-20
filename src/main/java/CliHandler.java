import org.apache.commons.cli.*;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

class CliHandler {
    static final String EVEN_FIELD_PATH_OPTION_STRING = "e";
    static final String ODD_FIELD_PATH_OPTION_STRING = "o";
    static final String RESULT_OPTION_STRING = "r";
    private static final String HELP_OPTION_STRING = "h";
    private static final String VERSION_OPTION_STRING = "v";
    private static final Logger log = Logger.getLogger(CliHandler.class.getName());
    private final Options infoOptions = new Options();
    private final Options generationOptions = new Options();
    private String[] args = null;
    private CommandLine cmd;

    public CliHandler(String[] appArgs) {
        args = appArgs;

        OptionGroup inputGroup = new OptionGroup();
        inputGroup.addOption(Option.builder(EVEN_FIELD_PATH_OPTION_STRING)
                                   .longOpt("even")
                                   .desc("path to even field picture")
                                   .hasArg()
                                   .build());
        inputGroup.addOption(Option.builder(ODD_FIELD_PATH_OPTION_STRING)
                                   .longOpt("odd")
                                   .desc("path to odd field picture")
                                   .hasArg()
                                   .build());
        inputGroup.addOption(Option.builder(RESULT_OPTION_STRING)
                                   .longOpt("result")
                                   .desc("path to write the result")
                                   .hasArg()
                                   .required()
                                   .build());
        inputGroup.setRequired(true);
        generationOptions.addOptionGroup(inputGroup);

        infoOptions.addOption(Option.builder(VERSION_OPTION_STRING).longOpt("version")
                                    .desc("show current tool version.").build());
        infoOptions.addOption(Option.builder(HELP_OPTION_STRING).longOpt("help").desc("show this help.").build());

    }

    public CommandLine getCmd() {
        return cmd;
    }

    public void parse() {
        CommandLineParser parser = new DefaultParser();
        cmd = null;

        try {
            cmd = parser.parse(infoOptions, args, true);

            if (cmd.getOptions().length != 0) {
                if (cmd.hasOption(HELP_OPTION_STRING)) {
                    help();
                } else if (cmd.hasOption(VERSION_OPTION_STRING)) {
                    version();
                }

                System.exit(0);
            } else {
                cmd = parser.parse(generationOptions, args);
            }

        } catch (Exception e) {
            log.error("Exception during parsing command line properties. If you are lost in options, try -h",
                      e);
            System.exit(1);
        }
    }

    private void help() {
        System.out.println("Factory programming tool help:");
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("generation", generationOptions);
        formatter.printHelp("info", infoOptions);
    }

    private void version() throws IOException {
        final Properties properties = new Properties();
        InputStream propertiesStream = getClass().getClassLoader().getResourceAsStream("application.properties");
        properties.load(propertiesStream);
        propertiesStream.close();

        System.out.println("Factory programming C++ tool.");
        System.out.println("Version: " + properties.getProperty("version"));
    }
}
