package it.unibo.javajump.view.sound.music;

/**
 * The interface Music manager.
 */
public interface MusicManager {
    /**
     * Load background music.
     *
     * @param filePath the file path
     */
    void loadBackgroundMusic(String filePath);

    /**
     * Start music.
     */
    void startMusic();

    /**
     * Stop music.
     */
    void stopMusic();

    /**
     * Pause music.
     */
    void pauseMusic();

    /**
     * Resume music.
     */
    void resumeMusic();

    /**
     * Fade out.
     *
     * @param durationSeconds the duration seconds
     */
    void fadeOut(float durationSeconds);

    /**
     * Sets volume.
     *
     * @param vol the vol
     */
    void setVolume(float vol);
}
