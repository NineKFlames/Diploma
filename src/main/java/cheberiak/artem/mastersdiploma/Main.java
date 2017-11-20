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
            
            transform(commandLine.getOptionValue(CliHandler.ODD_FIELD_PATH_OPTION_STRING),
                      commandLine.getOptionValue(CliHandler.RESULT_OPTION_STRING));
        } catch (Exception e) {
            logger.error(e);
        }
    }

    private static void transform(String oddFieldPath, String deinterlacedPath) throws IOException {
        BufferedImage oddField = loadImage(Main.class.getClassLoader().getResource(oddFieldPath));
        BufferedImage deinterlaceLineDuplication = deinterlaceLineDuplication(oddField);
        writeImage(deinterlaceLineDuplication, deinterlacedPath);
    }

    private static BufferedImage deinterlaceLineDuplication(BufferedImage interlacedImage) {
        BufferedImage returnValue =
                new BufferedImage(interlacedImage.getWidth(), interlacedImage.getHeight(), interlacedImage.getType());
        Graphics returnValueGraphics = returnValue.getGraphics();

        for (int lineIndex = 0; lineIndex < interlacedImage.getHeight(); lineIndex += 2) {
            BufferedImage oddLine = interlacedImage.getSubimage(0, lineIndex, interlacedImage.getWidth(), 1);
            returnValueGraphics.drawImage(oddLine, 0, lineIndex, null);
            returnValueGraphics.drawImage(oddLine, 0, lineIndex + 1, null);
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
