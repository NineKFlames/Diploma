package cheberiak.artem.mastersdiploma;

import org.apache.commons.cli.CommandLine;
import org.apache.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
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
            logger.info("Starting deintrelacing...");

            if (commandLine.hasOption(INTERPOLATOIN_ALGORITHM_OPTION_STRING)) {
                algorithm = DEINTERLACE_LINE_INTERPOLATION;
                logger.info("Line interpolation algorithm is chosen.");
            } else {
                algorithm = DEINTERLACE_LINE_DUPLICATION;
                logger.info("Line duplication algorithm is chosen by default.");
            }

            if (commandLine.hasOption(ODD_FIELD_PATH_OPTION_STRING)) {
                source = loadImage(Main.class.getClassLoader()
                                             .getResource(commandLine.getOptionValue(
                                                     ODD_FIELD_PATH_OPTION_STRING)));
                logger.info("Interpolating odd field...");
            } else if (commandLine.hasOption(EVEN_FIELD_PATH_OPTION_STRING)) {
                source = loadImage(Main.class.getClassLoader()
                                             .getResource(commandLine.getOptionValue(
                                                     EVEN_FIELD_PATH_OPTION_STRING)));
                isFieldEven = true;
                logger.info("Interpolating even field...");
            } else {
                logger.error("No field received! Nothing to convert");
                System.exit(1);
            }

            Path resultPath = Paths.get(commandLine.getOptionValue(RESULT_OPTION_STRING));
            writeImage(algorithm.apply(source, isFieldEven), resultPath);
            logger.info("Image deinterlaced. Path: " + resultPath);
        } catch (Exception e) {
            logger.error(e);
            System.exit(1);
        }
    }

    private static void writeImage(BufferedImage img, Path path) throws IOException {
        String[] split = path.getFileName().toString().split("\\.");
        ImageIO.write(img, split[split.length - 1], path.toFile());
    }

    private static BufferedImage loadImage(URL resource) throws IOException {
        return ImageIO.read(resource);
    }
}
