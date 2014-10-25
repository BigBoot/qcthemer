package de.bigboot.qcthemer;

/**
* Created by Marco Kirchner.
*/
class ImportClockException extends RuntimeException {
    public enum Error {
        NO_CLOCK_XML,
        INVALID_CLOCK_XML,
        READ_ERROR,
        MISSING_FILE
    }

    private Error error;

    @Override
    public String getMessage() {
        switch (error) {
            case NO_CLOCK_XML:
                return "No clock.xml found";
            case INVALID_CLOCK_XML:
                return "clock.xml is not valid";
            case READ_ERROR:
                return "read error";
            case MISSING_FILE:
                return "a file is missing";
            default:
                return super.getMessage();
        }
    }

    ImportClockException(Error error) {
        this.error = error;
    }
}
