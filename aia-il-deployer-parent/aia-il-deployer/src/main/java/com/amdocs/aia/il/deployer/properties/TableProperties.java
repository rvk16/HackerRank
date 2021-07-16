package com.amdocs.aia.il.deployer.properties;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "aia.il.deployer.properties")
public class TableProperties {


    private Caching caching;
    private String  comment;
    private Compaction compaction;
    private Compression compression;
    private Double crcCheckChance;
    private Integer defaultTimeToLive;
    private Integer gcGraceSeconds;
    private Integer maxIndexInterval;
    private Integer memtableFlushPeriodInMs;
    private Integer minIndexInterval;
    private String speculativeRetry;
    private Double readRepairChance;
    private Double bloomFilterFpChance;
    private Double dclocalReadRepairChance;


    public Caching getCaching() {
        return caching;
    }

    public void setCaching(Caching caching) {
        this.caching = caching;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Compaction getCompaction() {
        return compaction;
    }

    public void setCompaction(Compaction compaction) {
        this.compaction = compaction;
    }

    public Compression getCompression() {
        return compression;
    }

    public void setCompression(Compression compression) {
        this.compression = compression;
    }

    public Double getCrcCheckChance() {
        return crcCheckChance;
    }

    public void setCrcCheckChance(Double crcCheckChance) {
        this.crcCheckChance = crcCheckChance;
    }

    public Integer getDefaultTimeToLive() {
        return defaultTimeToLive;
    }

    public void setDefaultTimeToLive(Integer defaultTimeToLive) {
        this.defaultTimeToLive = defaultTimeToLive;
    }

    public Integer getGcGraceSeconds() {
        return gcGraceSeconds;
    }

    public void setGcGraceSeconds(Integer gcGraceSeconds) {
        this.gcGraceSeconds = gcGraceSeconds;
    }

    public Integer getMaxIndexInterval() {
        return maxIndexInterval;
    }

    public void setMaxIndexInterval(Integer maxIndexInterval) {
        this.maxIndexInterval = maxIndexInterval;
    }

    public Integer getMemtableFlushPeriodInMs() {
        return memtableFlushPeriodInMs;
    }

    public void setMemtableFlushPeriodInMs(Integer memtableFlushPeriodInMs) {
        this.memtableFlushPeriodInMs = memtableFlushPeriodInMs;
    }

    public Integer getMinIndexInterval() {
        return minIndexInterval;
    }

    public void setMinIndexInterval(Integer minIndexInterval) {
        this.minIndexInterval = minIndexInterval;
    }

    public String getSpeculativeRetry() {
        return speculativeRetry;
    }

    public void setSpeculativeRetry(String speculativeRetry) {
        this.speculativeRetry = speculativeRetry;
    }

    public Double getReadRepairChance() {
        return readRepairChance;
    }

    public void setReadRepairChance(Double readRepairChance) {
        this.readRepairChance = readRepairChance;
    }

    public Double getBloomFilterFpChance() {
        return bloomFilterFpChance;
    }

    public void setBloomFilterFpChance(Double bloomFilterFpChance) {
        this.bloomFilterFpChance = bloomFilterFpChance;
    }

    public Double getDclocalReadRepairChance() {
        return dclocalReadRepairChance;
    }

    public void setDclocalReadRepairChance(Double dclocalReadRepairChance) {
        this.dclocalReadRepairChance = dclocalReadRepairChance;
    }


    @JsonPropertyOrder({"compactionClass","max_threshold","min_threshold"})
    public static class Compaction{
        @JsonProperty("class")
        private String compactionClass;
        @JsonProperty("max_threshold")
        private String maxThreshold;
        @JsonProperty("min_threshold")
        private String minThreshold;

        public String getCompactionClass() {
            return compactionClass;
        }

        public void setCompactionClass(String compactionClass) {
            this.compactionClass = compactionClass;
        }

        public String getMinThreshold() {
            return minThreshold;
        }

        public void setMinThreshold(String minThreshold) {
            this.minThreshold = minThreshold;
        }

        public String getMaxThreshold() {
            return maxThreshold;
        }

        public void setMaxThreshold(String maxThreshold) {
            this.maxThreshold = maxThreshold;
        }

        @Override
        public String toString() {
            return "{" +
                    "class=" + compactionClass +
                    ", max_threshold=" + maxThreshold +
                    ", min_threshold=" + minThreshold +
                    '}';
        }
    }

    @JsonPropertyOrder({"keys","rows_per_partition"})
    public static class Caching{
        private String keys;
        @JsonProperty("rows_per_partition")
        private String rowsPerPartition;

        public String getKeys() {
            return keys;
        }

        public void setKeys(String keys) {
            this.keys = keys;
        }

        public String getRowsPerPartition() {
            return rowsPerPartition;
        }

        public void setRowsPerPartition(String rowsPerPartition) {
            this.rowsPerPartition = rowsPerPartition;
        }

        @Override
        public String toString() {
            return "{" +
                    "keys=" + keys +
                    ", rows_per_partition=" + rowsPerPartition +
                    '}';
        }
    }

    @JsonPropertyOrder({"chunk_length_kb","sstable_compression"})
    public static class Compression{
        @JsonProperty("chunk_length_kb")
        private String chunkLengthKb;
        @JsonProperty("sstable_compression")
        private String sstableCompression;

        public String getSstableCompression() {
            return sstableCompression;
        }

        public void setSstableCompression(String sstableCompression) {
            this.sstableCompression = sstableCompression;
        }

        public String getChunkLengthKb() {
            return chunkLengthKb;
        }

        public void setChunkLengthKb(String chunkLengthKb) {
            this.chunkLengthKb = chunkLengthKb;
        }


        @Override
        public String toString() {
            return "{" +
                    "chunk_length_kb=" + chunkLengthKb +
                    ", sstable_compression=" + sstableCompression +
                    '}';
        }
    }
}
