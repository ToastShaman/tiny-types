---
name: mutation-testing
description: Finds weak or missing tests by analyzing if code changes would be caught. Use when verifying test effectiveness, strengthening test suites, or validating TDD workflows.
allowed-tools:
  - Bash
  - Read
  - Glob
  - Grep
  - AskUserQuestion
---

# Mutation Testing

STARTER_CHARACTER = 🧬🔬

Mutation testing answers the question: **"Are my tests actually catching bugs?"**

Code coverage tells you what code your tests execute. Mutation testing tells you if your tests would **detect changes** to that code. A test suite with 100% coverage can still miss 40% of potential bugs.

---

## Core Concept

**The Mutation Testing Process:**

1. **Generate mutants**: Introduce small bugs (mutations) into production code
2. **Run tests**: Execute your test suite against each mutant
3. **Evaluate results**: If tests fail, the mutant is "killed" (good). If tests pass, the mutant "survived" (bad — your tests missed the bug)

**The Insight**: A surviving mutant represents a bug your tests wouldn't catch.

---

## When to Use

Use mutation testing analysis when:

- Reviewing code changes on a branch
- Verifying test effectiveness after TDD
- Identifying weak tests that appear to have coverage
- Finding missing edge case tests
- Validating that refactoring didn't weaken test suite

**Integration with TDD:**

```
TDD Workflow                    Mutation Testing Validation
┌─────────────────┐             ┌─────────────────────────────┐
│ RED: Write test │             │                             │
│ GREEN: Pass it  │──────────►  │ After GREEN: Verify tests   │
│ REFACTOR        │             │ would kill relevant mutants │
└─────────────────┘             └─────────────────────────────┘
```

---

## Systematic Branch Analysis Process

Follow this systematic process when analysing code on a branch:

### Step 1: Identify Changed Code

```bash
git diff main...HEAD --name-only | grep -E '\.(ts|js|tsx|jsx|rs|go)$' | grep -v '\.test\.'
git diff main...HEAD -- src/
```

### Step 2: Generate Mental Mutants

For each changed function/method, mentally apply mutation operators (see sections below).

### Step 3: Verify Test Coverage

For each potential mutant, ask:

1. **Is there a test that exercises this code path?**
2. **Would that test FAIL if this mutation were applied?**
3. **Is the assertion specific enough to catch this change?**

### Step 4: Document Findings

| Category | Description | Action Required |
|----------|-------------|-----------------|
| Killed | Test would fail if mutant applied | None — tests are effective |
| Survived | Test would pass with mutant | Add/strengthen test |
| No Coverage | No test exercises this code | Add behavior test |
| Equivalent | Mutant produces same behavior | None — not a real bug |

---

## Universal Mutation Operators

These mutation operators apply across all languages:

### Arithmetic Operators

| Original | Mutated | Test Should Verify |
|----------|---------|-------------------|
| `a + b` | `a - b` | Addition behavior |
| `a - b` | `a + b` | Subtraction behavior |
| `a * b` | `a / b` | Multiplication behavior |
| `a / b` | `a * b` | Division behavior |
| `a % b` | `a * b` | Modulo behavior |

**Key Pattern:**

```
// ❌ WEAK TEST — Would NOT catch mutant
calculate(10, 1)  // 10 * 1 = 10, 10 / 1 = 10 (SAME!)

// ✅ STRONG TEST — Would catch mutant
calculate(10, 3)  // 10 * 3 = 30, 10 / 3 = 3.33 (DIFFERENT!)
```

### Conditional Expressions

| Original | Mutated | Test Should Verify |
|----------|---------|-------------------|
| `a < b` | `a <= b` | Boundary value at equality |
| `a < b` | `a >= b` | Both sides of condition |
| `a <= b` | `a < b` | Boundary value at equality |
| `a > b` | `a >= b` | Boundary value at equality |
| `a >= b` | `a > b` | Boundary value at equality |

**Key Pattern:**

```
// ❌ WEAK TEST — Would NOT catch boundary mutant
isAdult(25)  // 25 >= 18 = true, 25 > 18 = true (SAME!)

// ✅ STRONG TEST — Would catch boundary mutant
isAdult(18)  // 18 >= 18 = true, 18 > 18 = false (DIFFERENT!)
```

### Equality Operators

| Original | Mutated | Test Should Verify |
|----------|---------|-------------------|
| `a == b` | `a != b` | Both equal and not equal cases |
| `a != b` | `a == b` | Both equal and not equal cases |

### Logical Operators

| Original | Mutated | Test Should Verify |
|----------|---------|-------------------|
| `a AND b` | `a OR b` | Case where one is true, other is false |
| `a OR b` | `a AND b` | Case where one is true, other is false |
| `NOT a` | `a` | Negation is necessary |

**Key Pattern:**

```
// ❌ WEAK TEST — Would NOT catch mutant
canAccess(true, true)  // true OR true = true AND true (SAME!)

// ✅ STRONG TEST — Would catch mutant
canAccess(true, false)  // true OR false = true, true AND false = false (DIFFERENT!)
```

### Boolean Literals

| Original | Mutated | Test Should Verify |
|----------|---------|-------------------|
| `true` | `false` | Both true and false outcomes |
| `false` | `true` | Both true and false outcomes |

### Block Statements

| Original | Mutated | Test Should Verify |
|----------|---------|-------------------|
| Function body | Empty function | Side effects of the function |

**Key Pattern:**

```
// ❌ WEAK TEST — Would NOT catch mutant
processOrder(order)  // No assertions — empty function also doesn't throw!

// ✅ STRONG TEST — Would catch mutant
processOrder(order)
verifyOrderWasSaved(order)  // Verifies side effect
```

### String Literals

| Original | Mutated | Test Should Verify |
|----------|---------|-------------------|
| `"text"` | `""` | Non-empty string behavior |
| `""` | `"XX"` | Empty string behavior |

### Collection Literals

| Original | Mutated | Test Should Verify |
|----------|---------|-------------------|
| `[1, 2, 3]` | `[]` | Non-empty collection behavior |
| `{}` | Empty or mutated | Empty collection behavior |

---

## TypeScript/JavaScript Mutation Operators

These operators apply specifically to TypeScript and JavaScript code.

### Optional Chaining Mutations

| Original | Mutated | Test Should Verify |
|----------|---------|-------------------|
| `foo?.bar` | `foo.bar` | Null/undefined handling |
| `foo?.[i]` | `foo[i]` | Null/undefined handling |
| `foo?.()` | `foo()` | Null/undefined handling |

```typescript
// ❌ WEAK TEST — Would NOT catch mutant
it('returns name for valid user', () => {
  expect(getUserName({ name: "Alice" })).toBe("Alice");
});

// ✅ STRONG TEST — Would catch mutant
it('returns Anonymous for null user', () => {
  expect(getUserName(null)).toBe("Anonymous");  // Would crash without ?.
});
```

### Nullish Coalescing Mutations

| Original | Mutated | Test Should Verify |
|----------|---------|-------------------|
| `a ?? b` | `a && b` | Nullish coalescing behavior |
| `a ?? b` | `a \|\| b` | Difference between nullish and falsy |

```typescript
// ❌ WEAK TEST — Would NOT catch mutant
it('returns provided port', () => {
  expect(getPort(8080)).toBe(8080);  // Works for both ?? and ||
});

// ✅ STRONG TEST — Would catch mutant
it('returns default for 0', () => {
  expect(getPort(0)).toBe(0);  // 0 ?? 3000 = 0, but 0 || 3000 = 3000 (DIFFERENT!)
});
```

### Method Expression Mutations

| Original | Mutated | Test Should Verify |
|----------|---------|-------------------|
| `startsWith()` | `endsWith()` | Correct string position |
| `endsWith()` | `startsWith()` | Correct string position |
| `toUpperCase()` | `toLowerCase()` | Case transformation |
| `toLowerCase()` | `toUpperCase()` | Case transformation |
| `some()` | `every()` | Partial vs full match |
| `every()` | `some()` | Full vs partial match |
| `filter()` | (removed) | Filtering is necessary |
| `min()` | `max()` | Correct extremum |
| `max()` | `min()` | Correct extremum |

```typescript
// ❌ WEAK TEST — Would NOT catch some→every mutant
it('returns true when all active', () => {
  const users = [{ isActive: true }, { isActive: true }];
  expect(hasActiveUser(users)).toBe(true);  // some = true, every = true (SAME!)
});

// ✅ STRONG TEST — Would catch mutant
it('returns true when one active', () => {
  const users = [{ isActive: true }, { isActive: false }];
  expect(hasActiveUser(users)).toBe(true);  // some = true, every = false (DIFFERENT!)
});
```

### Unary Operator Mutations

| Original | Mutated | Test Should Verify |
|----------|---------|-------------------|
| `+a` | `-a` | Sign matters |
| `-a` | `+a` | Sign matters |
| `++a` | `--a` | Increment vs decrement |

### TypeScript-Specific Patterns

```typescript
// Nullish coalescing — test 0, "", false explicitly
it('returns default for null only, not for falsy', () => {
  expect(getValue(null)).toBe("default");
  expect(getValue(undefined)).toBe("default");
  expect(getValue(0)).toBe(0);      // Not nullish
  expect(getValue("")).toBe("");    // Not nullish
});

// Enum mutations — always test both values
it('returns true for Active', () => {
  expect(isActive(Status.Active)).toBe(true);
});
it('returns false for Inactive', () => {
  expect(isActive(Status.Inactive)).toBe(false);
});
```

**TypeScript Red Flags:**
- Tests don't verify optional chaining with null/undefined
- Tests use only truthy/falsy without testing actual null/undefined
- Tests don't distinguish between `??` and `||`
- Tests use `0`, `""`, `false` when verifying defaults (makes `??` and `||` equivalent)
- Array tests only check length, not contents
- Tests don't verify correct array method (`some` vs `every`, `filter` vs `map`)

---

## Mutant States and Metrics

### Mutant States

| State | Meaning | Action |
|-------|---------|--------|
| **Killed** | Test failed when mutant applied | Good — tests are effective |
| **Survived** | Tests passed with mutant active | Bad — add/strengthen test |
| **No Coverage** | No test exercises this code | Add behavior test |
| **Timeout** | Tests timed out (infinite loop) | Counted as detected |
| **Equivalent** | Mutant produces same behavior | No action — not a real bug |

### Metrics

- **Mutation Score**: `killed / valid * 100` — the higher, the better
- **Undetected**: `survived + no coverage`

### Target Mutation Score

| Score | Quality |
|-------|---------|
| < 60% | Weak test suite — significant gaps |
| 60-80% | Moderate — many improvements possible |
| 80-90% | Good — but still gaps to address |
| > 90% | Strong — but watch for equivalent mutants |

---

## Equivalent Mutants

Equivalent mutants produce the same behavior as the original code and cannot be killed.

**Common patterns:**
- Operations with identity elements (`+= 0`, `*= 1`)
- Boundary conditions that don't affect the outcome
- Dead code paths

When you find one: identify it, document why it's equivalent, and accept it. Sometimes equivalent mutants indicate unclear code worth refactoring.

---

## Branch Analysis Checklist

### For Each Function/Method Changed:

- [ ] **Arithmetic operators**: Would changing +, -, *, / be detected?
- [ ] **Conditionals**: Are boundary values tested (>=, <=)?
- [ ] **Boolean logic**: Are all branches of AND, OR tested?
- [ ] **Return statements**: Would changing return value be detected?
- [ ] **Method calls**: Would removing or swapping methods be detected?
- [ ] **String literals**: Would empty strings be detected?
- [ ] **Collections**: Would empty collections be detected?

### Red Flags (Likely Surviving Mutants):

- [ ] Tests only verify "no error thrown"
- [ ] Tests only check one side of a condition
- [ ] Tests use identity values (0, 1, empty string)
- [ ] Tests only verify function was called, not with what
- [ ] Tests don't verify return values
- [ ] Boundary values not tested

### Questions to Ask:

1. "If I changed this operator, would a test fail?"
2. "If I negated this condition, would a test fail?"
3. "If I removed this line, would a test fail?"
4. "If I returned early here, would a test fail?"

---

## Strengthening Weak Tests

### Add Boundary Value Tests

```
// Strengthened with boundary values
test('validates age at boundary', () => {
  assert(isAdult(17) === false)  // Just below
  assert(isAdult(18) === true)   // Exactly at boundary
  assert(isAdult(19) === true)   // Just above
})
```

### Test Both Branches of Conditions

```
test('grants access when admin', () => {
  assert(canAccess(true, false) === true)
})
test('grants access when owner', () => {
  assert(canAccess(false, true) === true)
})
test('denies access when neither', () => {
  assert(canAccess(false, false) === false)
})
```

### Avoid Identity Values

```
// Strong — uses values that reveal operator differences
test('calculates', () => {
  assert(multiply(10, 3) === 30)  // 10 * 3 != 10 / 3
  assert(add(5, 3) === 8)         // 5 + 3 != 5 - 3
})
```

### Verify Side Effects

```
// Strong — verifies observable outcomes
test('processes order', () => {
  processOrder(order)
  verifyOrderSaved(order)
  verifyEmailSent(order.customerEmail)
})
```

---

## Tool

**Stryker** is the mutation testing tool for TypeScript/JavaScript. It automates mutant generation and test execution. Run it after TDD to verify your test suite would catch real bugs.

---

## Summary: Mutation Testing Mindset

**The key question for every line of code:**

> "If I introduced a bug here, would my tests catch it?"

**Remember:**
- Coverage measures execution; mutation testing measures detection
- A test that doesn't make assertions can't kill mutants
- Boundary values are critical for conditional mutations
- Avoid identity values that make operators interchangeable

### Operators Most Likely to Have Surviving Mutants

1. `>=` vs `>` (boundary not tested)
2. `AND` vs `OR` (only tested when both true/false)
3. `+` vs `-` (only tested with 0)
4. `*` vs `/` (only tested with 1)
5. `??` vs `||` (only tested with undefined, not 0/""/false)

### Test Values That Kill Mutants

| Avoid | Use Instead |
|-------|-------------|
| 0 (for +/-) | Non-zero values |
| 1 (for */) | Values > 1 |
| Empty collections | Collections with multiple items |
| Identical values for comparisons | Distinct values |
| All true/false for logical ops | Mixed true/false |
| undefined only for `??` | Also test 0, "", false |

---

*Adapted from [Skill Factory](https://github.com/lada-k/skill-factory) by Lada Kesseler (Apache 2.0)*
