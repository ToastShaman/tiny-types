package com.github.toastshaman.tinytypes.test.approvals;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.opentest4j.AssertionFailedError;

public final class FileApprover implements Approver {

    private final Path approvedFile;
    private final Path actualFile;

    public FileApprover(Path approvedFile) {
        this.approvedFile = approvedFile;
        String name = approvedFile.getFileName().toString();
        this.actualFile = approvedFile.resolveSibling(name.replace(".approved", ".actual"));
    }

    @Override
    public void assertApproved(String actual) {
        writeActual(actual);

        if (!Files.exists(approvedFile)) {
            throw new AssertionFailedError(
                    "No approved file found. To approve:\n"
                            + "  mv "
                            + actualFile.toAbsolutePath()
                            + " "
                            + approvedFile.toAbsolutePath(),
                    "",
                    actual);
        }

        String approved = readApproved();
        if (!actual.equals(approved)) {
            throw new AssertionFailedError(
                    "Approval mismatch. To approve:\n"
                            + "  mv "
                            + actualFile.toAbsolutePath()
                            + " "
                            + approvedFile.toAbsolutePath(),
                    approved,
                    actual);
        }

        deleteQuietly(actualFile);
    }

    private void writeActual(String content) {
        try {
            Files.createDirectories(actualFile.getParent());
            Files.writeString(actualFile, content);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private String readApproved() {
        try {
            return Files.readString(approvedFile);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static void deleteQuietly(Path path) {
        try {
            Files.deleteIfExists(path);
        } catch (IOException ignored) {
        }
    }
}
