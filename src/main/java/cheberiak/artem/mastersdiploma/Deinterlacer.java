package cheberiak.artem.mastersdiploma;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.awt.image.BufferedImage;

public interface Deinterlacer {
    static BufferedImage deinterlace(BufferedImage field, boolean isFieldEven) {
        throw new NotImplementedException();
    }
}
