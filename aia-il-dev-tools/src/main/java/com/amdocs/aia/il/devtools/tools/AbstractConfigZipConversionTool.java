package com.amdocs.aia.il.devtools.tools;

import com.amdocs.aia.common.model.ProjectElement;
import com.amdocs.aia.repo.client.ElementsProvider;
import com.amdocs.aia.repo.client.local.LocalFileSystemElementsProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public abstract class AbstractConfigZipConversionTool {
    private final static Logger LOGGER = LoggerFactory.getLogger(AbstractConfigZipConversionTool.class);

    private String sourceZip;
    private String targetZip;

    private String workDir;
    private ElementsProvider elementsProvider;

    private ObjectWriter writer;

    protected AbstractConfigZipConversionTool(String sourceZip, String targetZip) {
        this.sourceZip = sourceZip;
        this.targetZip = targetZip;
        try {
            this.workDir = Files.createTempDirectory("aia-il-" + getClass().getSimpleName()).toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.writer = new ObjectMapper().writerWithDefaultPrettyPrinter();
    }

    protected String getWorkDir() {
        return workDir;
    }

    protected ObjectWriter getWriter() {
        return writer;
    }

    protected ElementsProvider getElementsProvider() {
        return elementsProvider;
    }

    public final void convertSingleZip() {
        try {
            extractSourceToWorkDir();
            initElementsProvider();
            doConversions();
            packTarget();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected abstract void doConversions();

    private void initElementsProvider() {
        this.elementsProvider = new LocalFileSystemElementsProvider(workDir);
    }

    private void extractSourceToWorkDir() throws ZipException {
        new ZipFile(sourceZip).extractAll(workDir);
    }

    private void packTarget() throws ZipException {
        final File targetFile = new File(targetZip);
        if (targetFile.isFile()) {
            Boolean status = targetFile.delete();
            LOGGER.debug("Status of file deletion {}", status);
        }
        targetFile.getParentFile().mkdirs();
        final ZipFile targetZipFile = new ZipFile(targetZip);

        final File[] newContentFolders = new File(workDir).listFiles(f -> f.isDirectory());
        final File[] newContentFiles = new File(workDir).listFiles(f -> !f.isDirectory());
        for (File folder : newContentFolders) {
            if (folder.getName().equals("metadata") && !shouldPackMetadata()) {
                continue;
            }
            targetZipFile.addFolder(folder);
        }

        if (newContentFiles.length > 0) {
            targetZipFile.addFiles(Arrays.asList(newContentFiles));
        }
    }

    protected abstract boolean shouldPackMetadata();

    protected void saveElement(ProjectElement element) {
        File path = getWorkDataFile(element);
        path.getParentFile().mkdirs();
        try {
            writer.writeValue(path, element);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void deleteElement(ProjectElement element) {
        if (element != null) {
            boolean delete1 = false;
            final File workFile = getWorkDataFile(element);
            boolean delete = workFile.delete();//NOSONAR
            final File folder = workFile.getParentFile();
            if (isFolderEmpty(folder)) {
                delete1 = folder.delete();//NOSONAR
            }
            LOGGER.debug("Status of deleted elements workFile {} folder {}", delete, delete1);
        }
    }

    protected File getWorkDataFile(ProjectElement element) {
        return new File(workDir, String.format("data/%s/%s/%s", element.getProductKey(), element.getElementType(), element.getId()));
    }

    protected boolean isFolderEmpty(File dir) {
        try (Stream<Path> entries = Files.list(dir.toPath())) {
            return !entries.findFirst().isPresent();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
