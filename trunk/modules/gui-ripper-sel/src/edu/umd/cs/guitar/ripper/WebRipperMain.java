package edu.umd.cs.guitar.ripper;

import edu.umd.cs.guitar.model.GIDGenerator;


import edu.umd.cs.guitar.model.WebDefaultIDGenerator;

@Deprecated
public class WebRipperMain extends RipperMain {
    public WebRipperMain(NewGRipperConfiguration config) {
        super(config);
    }

    protected GRipperMonitor createMonitor() {
        // TODO: Fix the API
        return new WebRipperMonitor(null);
    }

    protected GIDGenerator getIdGenerator() {
        return WebDefaultIDGenerator.getInstance();
    }
}
