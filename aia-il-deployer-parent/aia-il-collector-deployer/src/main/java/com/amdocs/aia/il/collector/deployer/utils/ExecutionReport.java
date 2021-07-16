package com.amdocs.aia.il.collector.deployer.utils;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Component
public class ExecutionReport {
    private List<String> createdTables = new ArrayList<>();
    private List<String> failedTables = new ArrayList<>();
    private List<String> unchangedTables = new ArrayList<>();
    private List<String> alteredTables = new ArrayList<>();
    private int totalTableCount;

    @PostConstruct
    public void clear() {
        createdTables.clear();
        failedTables.clear();
        unchangedTables.clear();
        alteredTables.clear();
        totalTableCount = 0;
    }

    public void addCreatedTable(String name) {
        createdTables.add(name);
        totalTableCount++;
    }

    public void addFailedTable(String name) {
        failedTables.add(name);
        totalTableCount++;
    }

    public void addUnchangedTable(String name) {
        unchangedTables.add(name);
        totalTableCount++;
    }

    public void addAlteredTable(String name) {
        alteredTables.add(name);
        totalTableCount++;
    }

    public List<String> getCreatedTables() {
        return createdTables;
    }

    public List<String> getFailedTables() {
        return failedTables;
    }

    public List<String> getUnchangedTables() {
        return unchangedTables;
    }

    public List<String> getAlteredTables() {
        return alteredTables;
    }

    public int getTotalTableCount() {
        return totalTableCount;
    }
}