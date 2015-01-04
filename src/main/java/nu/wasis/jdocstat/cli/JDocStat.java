package nu.wasis.jdocstat.cli;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;
import java.util.stream.Collectors;

import nu.wasis.jdocstat.domain.ApiDescriptor;
import nu.wasis.jdocstat.domain.JavaVersion;
import nu.wasis.jdocstat.domain._Method;
import nu.wasis.jdocstat.exception.ArgumentParsingException;
import nu.wasis.jdocstat.parser.Java102DocTreeHtmlParser;
import nu.wasis.jdocstat.parser.Java118DocTreeHtmlParser;
import nu.wasis.jdocstat.parser.Java12DocTreeHtmlParser;
import nu.wasis.jdocstat.parser.Java17DocTreeHtmlParser;
import nu.wasis.jdocstat.parser.Java18DocTreeHtmlParser;
import nu.wasis.jdocstat.parser.JavaDocTreeHtmlParser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

public class JDocStat {

    private static final Logger LOG = LogManager.getLogger();
    private static final PrintStream ERR = System.err;
    private static final PrintStream OUT = System.out;

    public static void main(final String... args) throws IOException {
        try {
            final JDocStatConfig config = parseArgs(args);
            final JavaDocTreeHtmlParser parser = createParser(config);
            final ApiDescriptor apiDescriptor = parser.parseHtml();
            if (null != apiDescriptor) {
                final List<String> deprecatedClasses = apiDescriptor.getClasses()
                        .stream()
                        .filter(c -> c.isDeprecated())
                        .map(c -> c.getClassName())
                        .collect(Collectors.toList());
                final List<_Method> deprecatedMethods = apiDescriptor.getClasses()
                        .stream()
                        .flatMap(c -> c.getMethods().stream())
                        .filter(m -> m.isDeprecated())
                        .collect(Collectors.toList());
                final int numClasses = apiDescriptor.getClasses().size();
                final int numMethods = apiDescriptor.getClasses()
                        .stream()
                        .mapToInt(c -> c.getMethods().size())
                        .sum();
                final String version = apiDescriptor.getJavaVersion().toString().replaceAll("V_", "").replaceAll("_", ".");
                OUT.println(version + ", " + numClasses + ", " + numMethods + ", " + deprecatedClasses.size() + ", " + deprecatedMethods.size());
            }
        } catch (final IllegalArgumentException e) {
            if (!(e instanceof ArgumentParsingException) && null != e.getMessage()) {
                ERR.println(e.getMessage());
                printHelp(new CmdLineParser(new Options()));
            }
            return;
        }
    }

    private static JavaDocTreeHtmlParser createParser(final JDocStatConfig config) {
        switch (config.getJavaVersion()) {
            case V_1_0_2: {
                return new Java102DocTreeHtmlParser(config);
            }
            case V_1_1_8: {
                return new Java118DocTreeHtmlParser(config);
            }
            case V_1_2:
            case V_1_3_1:
            case V_1_4_2:
            case V_1_5:
            case V_1_6:
                return new Java12DocTreeHtmlParser(config);
            case V_1_7:
                return new Java17DocTreeHtmlParser(config);
            case V_1_8:
                return new Java18DocTreeHtmlParser(config);
            default:
                throw new RuntimeException("Unimplemented Java version: " + config.getJavaVersion() + ".");
        }
    }

    private static JDocStatConfig parseArgs(final String... args) throws IOException {
        final Options options = new Options();
        final CmdLineParser parser = new CmdLineParser(options);
        try {
            parser.parseArgument(args);
        } catch (final CmdLineException e) {
            LOG.error(e);
            throw new ArgumentParsingException();
        }

        if (null == options.getJavaVersion()) {
            bailOut(parser, "No Java version specified.");
        }

        if (null == options.getTreeFile()) {
            bailOut(parser, "No tree file specified.");
        }

        final JavaVersion javaVersion = JavaVersion.valueOf(options.getJavaVersion());
        final File treeFile = options.getTreeFile();
        if (!treeFile.exists()) {
            bailOut(parser, "Tree file not found.");
        }
        if (!treeFile.isFile()) {
            bailOut(parser, "Tree file not a regular file.");
        }
        if (!treeFile.canRead()) {
            bailOut(parser, "Tree file not readable.");
        }

        return new JDocStatConfig(javaVersion, treeFile);
    }

    private static void bailOut(final CmdLineParser parser, final String message) throws IOException {
        ERR.println(message);
        printHelp(parser);
        throw new ArgumentParsingException();
    }

    private static void printHelp(final CmdLineParser parser) throws IOException {
        final Writer stringWriter = new StringWriter();
        stringWriter.append("Usage: [progname] [options]\n");
        parser.printUsage(stringWriter, null);
        OUT.println(stringWriter.toString());
    }

}
