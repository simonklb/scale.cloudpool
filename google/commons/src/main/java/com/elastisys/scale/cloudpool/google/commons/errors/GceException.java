package com.elastisys.scale.cloudpool.google.commons.errors;

import com.elastisys.scale.cloudpool.commons.basepool.driver.CloudPoolDriverException;

/**
 * Thrown by a {@link ComputeClient} to indicate an API error.
 */
public class GceException extends CloudPoolDriverException {

    private static final long serialVersionUID = 1L;

    public GceException() {
        super();
    }

    public GceException(String message, Throwable cause) {
        super(message, cause);
    }

    public GceException(String message) {
        super(message);
    }

    public GceException(Throwable cause) {
        super(cause);
    }
}
