package org.revo.domain;

import java.util.ArrayList;
import java.util.List;

public class Calls {
    private List<Call> calls = new ArrayList<>();

    public Calls() {
    }

    public Calls(List<Call> calls) {
        this.calls = calls;
    }

    public List<Call> getCalls() {
        return calls;
    }

    public void setCalls(List<Call> calls) {
        this.calls = calls;
    }
}
