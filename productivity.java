import java.time.*;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JFrame;
import static java.time.temporal.ChronoUnit.MINUTES;
import java.io.File;

public class productivity extends JFrame {
    static LocalTime startTime = LocalTime.now();
    static final int minutesToAlarm = 60;
    static String currentDir = System.getProperty("user.dir");
    static final File soundFile = new File("alert.wav");
    public static void main(String[] args) {
        Duration amountToAdd = Duration.parse("PT60M");
        LocalTime currentTime = startTime.plus(amountToAdd);
        Alarm(getTimeDifference(startTime, currentTime));
    }

    public static void Alarm(long d) {
        if (d % minutesToAlarm == 0) {
            try {
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile.toURI().toURL());  
                Clip clip = AudioSystem.getClip();
                clip.open(audioIn);
                clip.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static long getTimeDifference(LocalTime st, LocalTime ct) {
        return Math.abs(MINUTES.between(st, ct));
    }
}
