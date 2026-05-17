package com.github.toastshaman.tinytypes.test.approvals;

public interface Approver {

    /**
     * Asserts that {@code actual} matches the approved baseline for this test.
     *
     * <p>On the first call, or whenever no {@code .approved} file exists, the test fails and a
     * {@code .actual} file is written next to the test source. Rename it to {@code .approved} to
     * establish the baseline.
     */
    void assertApproved(String actual);
}
