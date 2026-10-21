package vrms;

import javax.swing.JFrame;
import java.awt.Frame;

public final class WindowState {
    private static int mode = Frame.NORMAL;

    private WindowState() {
    }

    public static void apply(JFrame frame) {
        frame.setExtendedState(mode);

        frame.addWindowStateListener(event -> {
            int newState = event.getNewState();

            if ((newState & Frame.ICONIFIED) != 0) {
                return;
            }

            if ((newState & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH) {
                mode = Frame.MAXIMIZED_BOTH;
            } else {
                mode = Frame.NORMAL;
            }
        });
    }
}
