---
name: test-driven-development
description: Test-driven development (TDD) process used when writing code. Use whenever you are adding any new code, unless the user explicitly asks to skip TDD or the code is exploratory/spike.
allowed-tools:
  - Bash
  - Read
  - Write
  - Edit
  - Glob
  - Grep
  - AskUserQuestion
---

# Test-Driven Development Process

TDD is a design technique that uses tests as a tool. Design emerges from usage, not speculation. Short feedback loops let you course-correct immediately. The resulting architecture is testable by design, not retrofitted. We are not trying to rush towards a feature completion, it's important that the code is correct and well-designed, it's crucial to be thorough and only add what tests demand. 

When starting, announce: "Using TDD skill in mode: [auto|human]"

MODE (user specifies, default: auto)
- auto: Proceed through TDD steps (red/green/refactor) without stopping for approval.
  This does NOT skip requirement clarification. If the requirement is ambiguous, always stop and ask — regardless of mode.
- human: wait for confirmation at key points

STARTER_CHARACTER = 🔴 for red test, 🌱 for green, 🌀 when refactoring, always followed by a space

## Core Rules

1. ALL code changes follow TDD - Feature requests mid-stream are NOT exceptions. Write test first, then code.
2. Write only one test at a time - focus on the simplest, lowest-hanging fruit test
3. Predict failures - State what we expect to fail before running tests
4. Two-step red phase:
   - First: Make it fail to compile (class/method doesn't exist)
   - Second: Make it compile but fail the assertion (return wrong value)
5. Minimal code to pass - Just enough to make the test green. If no test requires it, don't write it.
6. No comments in production code - Keep it clean unless specifically asked
7. Run all tests every time - Not just the one you're working on
8. Refactor at the first opportunity when the tests are green
9. Test behavior, not implementation - check responses or state, not method calls
10. Push back when something seems wrong or unclear

## Test Planning

0. **Verify you understand the requirement.** Before writing any `[TEST]` stubs, check: can you identify 2 or more valid interpretations of what the user wants? If yes, STOP. Present the interpretations as numbered options and let the user choose. Do not pick one and proceed — that is an anti-pattern.
1. Think about what the code you want to write should do
2. Plan tests as single-line `[TEST]` comments. Example:
   ```
   [TEST] Zero plus a number is equal to that number
   [TEST] Add two positive numbers
   [TEST] Add two negative numbers
   [TEST] Adds a negative and a positive number
   [TEST] Division by zero is not allowed
   ...
   ```
3. **Scenarios, not assertions.** Each `[TEST]` stub represents a distinct user scenario or interaction path — not a single assertion about the same outcome. If several stubs share the same setup and only differ in which structural detail they check, they are asserting one scenario from multiple angles. Collapse them into one stub that names the scenario.

   ❌ One scenario fragmented into assertion-per-test stubs:
   ```
   [TEST] page has a sidebar
   [TEST] sidebar contains the navigation list
   [TEST] sidebar contains the shape panel
   [TEST] collapse button is present
   ```

   ✅ One stub per scenario:
   ```
   [TEST] shows navigation and shape details alongside the map
   [TEST] the user can collapse the panel to focus on the map, and expand it again
   [TEST] dragging the panel edge adjusts its width
   ```

   A useful check: *"If I had to describe what user need each test protects, would any two tests give the same answer?"* If yes, merge them.

4. Check completeness - walk through [ZOMBIES](references/zombies.md) explicitly:
   - Zero/empty cases covered?
   - One item cases covered?
   - Many items cases covered?
   - Boundary transitions covered?
   - Interface clarity verified?
   - Exceptions/errors covered?
5. If MODE is human, wait for confirmation after test planning

## Implementation Phase

1. Replace the next [TEST] comment directly with a failing test. No intermediate markers.
2. Test should be in format given-when-then (do not add as comments), with empty line separating them
3. Think through the expected value BEFORE writing the assertion. Trace the logic step by step.
4. Predict what will fail
5. Run tests, see compilation error (if testing something new)
6. Add minimal code to compile
7. Predict assertion failure
8. Run tests, see assertion failure
9. Add minimal code to pass
10. Predict whether the tests will pass and why. Run tests, see green
11. Simplify. For each line/expression you just added, ask: "Does a failing test require this?"
    - If no test requires it, delete it or if it's necessary, add a test comment to write that test
    - Run tests after each simplification
    - Repeat until every line is justified by a test
12. Refactor.
    - Reflect on the domain: Is there a missing concept that would make the code more expressive? An object waiting to be extracted? A better way to model the problem?
    - You may introduce domain concepts (new abstractions) as long as you add NO new behavior. Tests must still pass, and there should be no new code added that doesn't have tests.
    - Think about improvements to expressiveness, clarity, simplicity
    - Say `🧹 Starting refactoring stage` and list planned refactorings
    - Implement one at a time, run tests after each
    - When done (or if none needed), say "🧹 Refactoring complete"
13. Go to step 1 for the next [TEST] comment. Repeat until all planned tests are passing.

## When Unit Tests Are Sufficient — Ask About E2E

When unit tests fully express a new behaviour, pause and ask:

> *"If all unit tests were deleted, would an e2e test catch a regression in this behaviour?"*

If yes, an e2e test is missing. Unit tests prove the logic is correct; e2e tests prove the behaviour reaches the user. Both are necessary — they are not substitutes for each other.

**The trigger is user-visible behaviour.** Ask this whenever:
- A new response path is added (success message, error message, redirect)
- A new user interaction is handled (form submission, file upload, button click)
- An existing response is meaningfully changed (new field shown, wording changed)

**Do not duplicate edge cases in e2e.** Unit tests own the edge cases. E2E tests own at minimum one representative of each distinct outcome the user can see.

| Behaviour | Unit tests own | E2E tests own |
|-----------|---------------|---------------|
| Validation logic | All variants | One representative per error type |
| Success path | All variants | At least one confirming the full outcome reaches the user |
| Error path | All variants | At least one per distinct error the user sees |

---

## Final Evaluation

1. Analyze the code written and think about the tests that we might have missed.
2. If there are any gaps in the tests, start the process for the missing tests from the beginning, starting from test comments then following the process flow until done
3. Is anything still hardcoded in the code that shouldn't be? Fix it, analyze test gaps and go back to previous stages if needed.
4. Analyze code expressiveness and quality. If there's anything you can see to improve, go to refactoring phase.
5. Re-run the "when unit tests are sufficient" check — does any new user-visible behaviour lack an e2e test?

---

## Anti-Patterns

- ❌ Writing production code without a failing test
- ❌ Testing implementation details (spies on internal methods)
- ❌ Speculative code — logic without a test demanding it
- ❌ Trusting coverage claims without verification
- ❌ Mocking the function being tested
- ❌ Collapsing distinct user scenarios into assertion-level stubs
- ❌ Skipping the e2e question after new user-visible behaviour
- ❌ Expressing uncertainty about a requirement then proceeding on an assumption instead of asking

**For detailed testing anti-patterns and factory patterns**, load the `testing` skill.
