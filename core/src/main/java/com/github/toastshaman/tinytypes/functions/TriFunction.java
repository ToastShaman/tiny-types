package com.github.toastshaman.tinytypes.functions;

public interface TriFunction<FIRST, SECOND, THIRD, RESULT> {
    RESULT apply(FIRST first, SECOND second, THIRD third);
}
