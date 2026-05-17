package com.github.toastshaman.tinytypes.test.approvals;

@FunctionalInterface
public interface ApproverFactory {

    Approver create(Class<?> testClass, String methodName);
}
