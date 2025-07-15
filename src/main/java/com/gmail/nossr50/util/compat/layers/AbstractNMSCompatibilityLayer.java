package com.gmail.nossr50.util.compat.layers;

import com.gmail.nossr50.util.nms.NMSVersion;
import org.jetbrains.annotations.NotNull;

/**
 * These classes are a band-aid solution for adding NMS support into 2.1.XXX In 2.2 we are switching
 * to modules and that will clean things up significantly
 */
public abstract class AbstractNMSCompatibilityLayer extends AbstractCompatibilityLayer {

    protected final @NotNull NMSVersion nmsVersion;

    public AbstractNMSCompatibilityLayer(@NotNull NMSVersion nmsVersion) {
        this.nmsVersion = nmsVersion;
    }

    /**
     * Initialize the CompatibilityLayer
     *
     * @return true if the CompatibilityLayer initialized and should be functional
     */
    public abstract boolean initializeLayer();

    @Override
    public boolean noErrorsOnInitialize() {
        return noErrorsOnInitialize;
    }
}
