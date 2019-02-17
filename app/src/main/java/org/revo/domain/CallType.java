package org.revo.domain;

public enum CallType {
    OUTGOING, INCOMING, MISSED;

    public static CallType type(int i) {
        switch (i) {
            case 2:
                return OUTGOING;

            case 1:
                return INCOMING;
            case 3:
                return MISSED;
            default:
                return MISSED;
        }
    }
}
