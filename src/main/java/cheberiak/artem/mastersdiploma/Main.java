package cheberiak.artem.mastersdiploma;

import org.apache.commons.cli.CommandLine;
import org.apache.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.function.BiFunction;

import static cheberiak.artem.mastersdiploma.CliHandler.*;

public class Main {
    private static final BiFunction<BufferedImage, Boolean, BufferedImage>
            DEINTERLACE_LINE_INTERPOLATION = LineInterpolation::deinterlaceLineInterpolation;
    private static final BiFunction<BufferedImage, Boolean, BufferedImage>
            DEINTERLACE_LINE_DUPLICATION = LineDuplication::deinterlaceLineDuplication;
    private static Logger logger = Logger.getLogger(Main.class);

    public static void main(String[] args) throws IOException {
        try {
            CliHandler handler = new CliHandler(args);
            handler.parse();
            CommandLine commandLine = handler.getCmd();
            boolean isFieldEven = false;
            BufferedImage source = null;
            BiFunction<BufferedImage, Boolean, BufferedImage> algorithm;

            if (commandLine.hasOption(INTERPOLATOIN_ALGORITHM_OPTION_STRING)) {
                algorithm = DEINTERLACE_LINE_INTERPOLATION;
            } else {
                algorithm = DEINTERLACE_LINE_DUPLICATION;
            }

            if (commandLine.hasOption(ODD_FIELD_PATH_OPTION_STRING)) {
                source = loadImage(Main.class.getClassLoader()
                                             .getResource(commandLine.getOptionValue(
                                                     ODD_FIELD_PATH_OPTION_STRING)));
            } else if (commandLine.hasOption(EVEN_FIELD_PATH_OPTION_STRING)) {
                source = loadImage(Main.class.getClassLoader()
                                             .getResource(commandLine.getOptionValue(
                                                     EVEN_FIELD_PATH_OPTION_STRING)));
                isFieldEven = true;
            } else {
                logger.error("No field received! Nothing to convert");
                System.exit(1);
            }
            
            writeImage(algorithm.apply(source, isFieldEven), commandLine.getOptionValue(RESULT_OPTION_STRING));
        } catch (Exception e) {
            logger.error(e);
            System.exit(1);
        }
    }

    private static void writeImage(BufferedImage img, String pathname) throws IOException {
        String[] split = pathname.split("\\.");
        ImageIO.write(img, split[split.length - 1], new File(pathname));
    }

    private static BufferedImage loadImage(URL resource) throws IOException {
        return ImageIO.read(resource);
    }
}
