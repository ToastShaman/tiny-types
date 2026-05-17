package com.github.toastshaman.tinytypes.test.approvals;

/**
 * Entry point for approval testing. Register the extension on your test class and inject an
 * {@link Approver} parameter into each test method.
 *
 * <pre>{@code
 * @RegisterExtension
 * ApprovalsExtension approvals = Approvals.extension();
 *
 * @Test
 * void my_test(Approver approver) {
 *     approver.assertApproved(someOutput());
 * }
 * }</pre>
 *
 * <p>On the first run the test fails and writes a {@code .actual} file next to the test source.
 * Rename it to {@code .approved} to establish the baseline; subsequent runs compare against it.
 */
public final class Approvals {

    private Approvals() {}

    public static ApprovalsExtension extension() {
        return new ApprovalsExtension();
    }

    public static ApprovalsExtension extension(ApproverFactory factory) {
        return new ApprovalsExtension(factory);
    }
}
