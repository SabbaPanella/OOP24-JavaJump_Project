package it.unibo.javajump.view.sound.music;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import static it.unibo.javajump.utility.Constants.AUDIO_FRAME_INIT;
import static it.unibo.javajump.utility.Constants.AUDIO_SLEEP;
import static it.unibo.javajump.utility.Constants.AUDIO_STEPS;
import static it.unibo.javajump.utility.Constants.MUSIC_LOOP_END;
import static it.unibo.javajump.utility.Constants.MUSIC_LOOP_START;

/**
 * The type Music manager.
 */
public class MusicManagerImpl implements MusicManager {
    private Clip backgroundClip;
    private FloatControl volumeControl;
    private ScheduledExecutorService fadeExecutor;
    private ScheduledFuture<?> fadeFuture;
    private final float defaultVolume;

    /**
     * Instantiates a new Music manager.
     *
     * @param filePath      the file path
     * @param defaultVolume the default volume
     */
    public MusicManagerImpl(final String filePath, final float defaultVolume) {
        this.defaultVolume = defaultVolume;
        initialize(filePath);
        fadeExecutor = Executors.newSingleThreadScheduledExecutor();
    }

    private void initialize(final String filePath) {
        loadBackgroundMusic(filePath);
    }

    @Override
    public void loadBackgroundMusic(final String filePath) {
        try {
            final File audioFile = new File(filePath);
            final AudioInputStream audioIn = AudioSystem.getAudioInputStream(audioFile);
            backgroundClip = AudioSystem.getClip();
            backgroundClip.open(audioIn);

            if (backgroundClip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                volumeControl = (FloatControl) backgroundClip.getControl(FloatControl.Type.MASTER_GAIN);
            } else {
                volumeControl = null;
            }
        } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
            Logger.getLogger(MusicManagerImpl.class.getName()).log(Level.SEVERE, "Error loading the audio file", e);
        }
    }

    @Override
    public void startMusic() {
        cancelFade();
        if (backgroundClip == null) {
            return;
        }
        if (backgroundClip.isRunning()) {
            return;
        }

        final int totalFrames = backgroundClip.getFrameLength();
        final int loopEnd = (int) (totalFrames * MUSIC_LOOP_END);
        backgroundClip.setLoopPoints(MUSIC_LOOP_START, loopEnd);
        backgroundClip.loop(Clip.LOOP_CONTINUOUSLY);
        setVolume(defaultVolume);
        backgroundClip.start();
    }

    @Override
    public void stopMusic() {
        cancelFade();
        if (backgroundClip != null) {
            backgroundClip.stop();
            backgroundClip.setFramePosition(AUDIO_FRAME_INIT);
        }
    }

    @Override
    public void pauseMusic() {
        cancelFade();
        if (backgroundClip != null && backgroundClip.isRunning()) {
            backgroundClip.stop();
        }
    }

    @Override
    public void resumeMusic() {
        cancelFade();
        if (backgroundClip != null && !backgroundClip.isRunning()) {
            backgroundClip.loop(Clip.LOOP_CONTINUOUSLY);
            backgroundClip.start();
        }
    }

    @Override
    public void setVolume(final float vol) {
        if (volumeControl == null) {
            return;
        }
        final float min = volumeControl.getMinimum();
        final float max = volumeControl.getMaximum();
        final float dB = min + (max - min) * vol;
        volumeControl.setValue(dB);
    }

    @Override
    public void fadeOut(final float durationSeconds) {
        if (backgroundClip == null || volumeControl == null) {
            return;
        }
        cancelFade();

        if (fadeExecutor != null && !fadeExecutor.isShutdown()) {
            fadeExecutor.shutdownNow();
        }
        fadeExecutor = Executors.newSingleThreadScheduledExecutor();

        final int steps = AUDIO_STEPS;
        final float initialVolume = volumeControl.getValue();
        final float finalVolume = volumeControl.getMinimum();
        final float delta = (initialVolume - finalVolume) / steps;
        final long stepTimeMillis = (long) (durationSeconds * AUDIO_SLEEP / steps);

        fadeFuture = fadeExecutor.scheduleAtFixedRate(new Runnable() {
            int currentStep;

            @Override
            public void run() {
                if (currentStep >= steps) {
                    stopMusic();
                    setVolume(defaultVolume);
                    fadeFuture.cancel(false);
                    return;
                }
                final float newVolume = volumeControl.getValue() - delta;
                volumeControl.setValue(newVolume);
                currentStep++;
            }

        }, 0, stepTimeMillis, TimeUnit.MILLISECONDS);
    }

    private void cancelFade() {
        if (fadeFuture != null && !fadeFuture.isDone()) {
            fadeFuture.cancel(true);
        }
    }
}
