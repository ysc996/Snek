import java.io.File;
import java.io.IOException;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class AudioPlayer implements LineListener {
    boolean playCompleted;
    Clip audioClip;
    public AudioPlayer(String audioFilePath) {
        File audiofile = new File(audioFilePath);

        try{
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audiofile);
            AudioFormat format = audioStream.getFormat();
            DataLine.Info info = new DataLine.Info(Clip.class, format);
            //control chunk size w buffer size arg; smaller buff size should equal less latency, but didn't rly notice
            audioClip = (Clip) AudioSystem.getLine(info);
            audioClip.open(audioStream);
        } catch (UnsupportedAudioFileException ex) {
            System.out.println("The specified audio file is not supported.");
            ex.printStackTrace();
        } catch (LineUnavailableException ex) {
            System.out.println("Audio line for playing back is unavailable");
            ex.printStackTrace();
        } catch (IOException ex) {
            System.out.println("Error playing the audio file.");
            ex.printStackTrace();
        }
    }

    public void play() {
        if(audioClip.getMicrosecondLength() != 0) {
            audioClip.setMicrosecondPosition(0);
        }
        //audioClip.setMicrosecondPosition(10000);
        audioClip.start();
        //System.out.println(Thread.currentThread().getPriority());
        //System.out.println("sound is playing on EDT: " + javax.swing.SwingUtilities.isEventDispatchThread()); //check if on EDT
    }

    /*public void playOnSeparateThread() { //problem: 2nd sound eaten if eat two apples to quickly
        Runnable runner = new Runnable() {
            public void run() {
                play();
            }
        };
        Thread t = new Thread(runner);
        t.setPriority(8);   //apparently priority only takes effect if max thread capacity reached
        t.start();
        //(new Thread(runner)).start(); //alt way if don't need to call other methods on thread
    }*/

    public boolean isRunning() {
        return audioClip.isRunning();
    }

    public void resume() {
        audioClip.start();
    }

    public void loop() {
        audioClip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    public void stop() {
        audioClip.stop();
    }

    public boolean hasClip() {
        if(audioClip != null)
            return true;
        return false;
    }

    @Override
    public void update(LineEvent event) {
        LineEvent.Type type = event.getType();

        if(type == LineEvent.Type.START) {
            System.out.println("Playback started");
        } else if (type == LineEvent.Type.STOP) {
            playCompleted = true;
            System.out.println("Playback completed.");
        }
    }
}