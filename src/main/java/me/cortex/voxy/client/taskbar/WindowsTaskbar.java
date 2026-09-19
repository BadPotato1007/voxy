package me.cortex.voxy.client.taskbar;

public class WindowsTaskbar implements Taskbar.ITaskbar {

    WindowsTaskbar(long windowId) {
        // Disabled for 26.3: windowId is an SDL handle, not GLFW.
    }

    public void close() {}

    @Override
    public void setIsNone() {}

    @Override
    public void setProgress(long count, long outOf) {}

    @Override
    public void setIsPaused() {}

    @Override
    public void setIsProgression() {}

    @Override
    public void setIsError() {}
}