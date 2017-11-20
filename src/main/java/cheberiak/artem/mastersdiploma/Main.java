package cheberiak.artem.mastersdiploma;

import org.apache.commons.cli.CommandLine;
import org.apache.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class Main {
    private static Logger logger = Logger.getLogger(Main.class);

    public static void main(String[] args) throws IOException {
        try {
            CliHandler handler = new CliHandler(args);
            handler.parse();
            CommandLine commandLine = handler.getCmd();
            BufferedImage result = null;

            if (commandLine.hasOption(CliHandler.ODD_FIELD_PATH_OPTION_STRING)) {
                URL oddFieldURL = Main.class.getClassLoader()
                                            .getResource(commandLine.getOptionValue(
                                                    CliHandler.ODD_FIELD_PATH_OPTION_STRING));
                result = deinterlaceLineDuplication(loadImage(oddFieldURL), false);
            } else if (commandLine.hasOption(CliHandler.EVEN_FIELD_PATH_OPTION_STRING)) {
                URL oddFieldURL = Main.class.getClassLoader()
                                            .getResource(commandLine.getOptionValue(
                                                    CliHandler.EVEN_FIELD_PATH_OPTION_STRING));
                result = deinterlaceLineDuplication(loadImage(oddFieldURL), true);
            }
            
            writeImage(result, commandLine.getOptionValue(CliHandler.RESULT_OPTION_STRING));
        } catch (Exception e) {
            logger.error(e);
        }
    }

    private static BufferedImage deinterlaceLineDuplication(BufferedImage field, boolean isFieldEven) {
        BufferedImage returnValue =
                new BufferedImage(field.getWidth(), field.getHeight(), field.getType());
        Graphics returnValueGraphics = returnValue.getGraphics();

        if (isFieldEven) {
            for (int lineIndex = 1; lineIndex < field.getHeight(); lineIndex += 2) {
                BufferedImage oddLine = field.getSubimage(0, lineIndex, field.getWidth(), 1);
                returnValueGraphics.drawImage(oddLine, 0, lineIndex - 1, null);
                returnValueGraphics.drawImage(oddLine, 0, lineIndex, null);
            }
        } else {
            for (int lineIndex = 0; lineIndex < field.getHeight(); lineIndex += 2) {
                BufferedImage oddLine = field.getSubimage(0, lineIndex, field.getWidth(), 1);
                returnValueGraphics.drawImage(oddLine, 0, lineIndex, null);
                returnValueGraphics.drawImage(oddLine, 0, lineIndex + 1, null);
            }
        }

        returnValueGraphics.dispose();

        return returnValue;
    }

    private static void writeImage(BufferedImage img, String pathname) throws IOException {
        String[] split = pathname.split("\\.");
        ImageIO.write(img, split[split.length - 1], new File(pathname));
    }

    private static BufferedImage loadImage(URL resource) throws IOException {
        return ImageIO.read(resource);
    }
}
