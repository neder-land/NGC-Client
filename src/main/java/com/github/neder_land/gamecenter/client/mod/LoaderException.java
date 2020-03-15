package com.github.neder_land.gamecenter.client.mod;

public class LoaderException extends RuntimeException {
    public LoaderException() {
        super();
    }

    public LoaderException(String desc) {
        super(desc);
    }

    public LoaderException(Throwable wrapped) {
        super(wrapped);
    }

    public LoaderException(String desc, Throwable wrapped) {
        super(desc, wrapped);
    }

    public static class ModCrash extends LoaderException {
        public ModCrash() {
        }

        public ModCrash(String desc) {
            super(desc);
        }

        public ModCrash(Throwable wrapped) {
            super(wrapped);
        }

        public ModCrash(String desc, Throwable wrapped) {
            super(desc, wrapped);
        }
    }
}
