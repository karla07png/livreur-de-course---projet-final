package Engine;

import java.awt.event.KeyEvent;

public class GamePad extends MovementController {
    private int quitKey = KeyEvent.VK_ESCAPE;
    private int captureKey = KeyEvent.VK_E;


    public GamePad() {
        bindKey(quitKey);
        bindKey(captureKey);
    }

    public boolean isQuitPressed() {
        return isKeyPressed(quitKey);
    }
    public boolean isCapturePressed() {
        return isKeyPressed(captureKey);
    }
}
