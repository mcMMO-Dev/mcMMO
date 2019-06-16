package com.gmail.nossr50.config.hocon.skills.repair;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RepairWildcard {

    private String wildcardName;
    private ArrayList<String> matchCandidates;

    public RepairWildcard(String wildcardName) {
        this.wildcardName = wildcardName;
        matchCandidates = new ArrayList<>();
    }

    public void addMatchCandidates(List<String> arrayList) {
        matchCandidates.addAll(arrayList);
    }

    public ArrayList<String> getMatchCandidates() {
        return matchCandidates;
    }

    public String getWildcardName() {
        return wildcardName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RepairWildcard)) return false;
        RepairWildcard that = (RepairWildcard) o;
        return getWildcardName().equals(that.getWildcardName()) &&
                Objects.equals(getMatchCandidates(), that.getMatchCandidates());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getWildcardName(), getMatchCandidates());
    }
}
