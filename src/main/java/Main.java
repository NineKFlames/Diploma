import org.apache.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class Main {
    private Logger logger = Logger.getLogger(Main.class);

    public static void main(String[] args) throws IOException {
        BufferedImage interlacedImage = loadImage(Main.class.getClassLoader().getResource("example_odd_field.png"));
        BufferedImage deinterlaceLineDuplication = deinterlaceLineDuplication(interlacedImage);
        writeImage(deinterlaceLineDuplication);
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

    private static void writeImage(BufferedImage img) throws IOException {
        ImageIO.write(img, "png", new File("test.png"));
    }

    private static BufferedImage loadImage(URL resource) throws IOException {
        return ImageIO.read(resource);
    }
}
