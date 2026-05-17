package com.github.toastshaman.tinytypes.test.approvals;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

@DisplayNameGeneration(ReplaceUnderscores.class)
class ApprovalsTest {

    @RegisterExtension
    ApprovalsExtension approvals = Approvals.extension();

    @Test
    void can_approve_plain_text(Approver approver) {
        approver.assertApproved("Hello, World!");
    }

    @Test
    void can_approve_multi_line_output(Approver approver) {
        var output = """
                line one
                line two
                line three
                """;
        approver.assertApproved(output);
    }
}
