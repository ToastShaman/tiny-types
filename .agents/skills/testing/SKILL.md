---
name: testing
description: Testing patterns for behavior-driven tests. Use when writing tests or test factories.
allowed-tools:
  - Bash
  - Read
  - Write
  - Edit
  - Glob
  - Grep
  - AskUserQuestion
---

# Testing Patterns

STARTER_CHARACTER = 🧪

**This skill focuses on test patterns and anti-patterns. For the TDD workflow (RED-GREEN-REFACTOR cycle), load the `tdd` skill.**

## Core Principle

**Test behavior, not implementation.** 100% coverage through business behavior, not implementation details.

**Example:** Validation code in `payment-validator.ts` gets 100% coverage by testing `processPayment()` behavior, NOT by directly testing validator functions.

---

## Test Naming

Test names should describe the **business requirement being protected**, not the test data or implementation detail being exercised.

When a test fails, its name should immediately tell you which requirement broke — not which fixture was used or which function was called.

❌ **WRONG — named after test data:**
```
'accepts a real-world shapefile (HAGFISH)'
'processes payment with card 4111111111111111'
'validates user with email test@example.com'
```

❌ **WRONG — named after implementation:**
```
'calls validateAmount'
'multipart handler returns 200'
'processFiles returns Html<String>'
```

✅ **CORRECT — named after the business requirement:**
```
'accepts a shapefile upload where the geometry file exceeds 2MB'
'rejects payments where the amount exceeds the daily limit'
'rejects users with an invalid email format'
```

**Ask: if this test fails, what requirement broke?** The answer to that question is the test name. If the answer is "I don't know — it depends on the fixture", the name is describing data, not behaviour.

A useful heuristic: if someone who has never seen the codebase reads the test name, can they understand what the system is supposed to do? If the name requires knowledge of the fixture, it is describing data, not behaviour.

---

## One Test Per Scenario

A test represents one user-visible scenario, not one assertion. When multiple tests share identical setup and differ only in what they assert about the same outcome, they are **fragmented** — all the assertions belong in one test.

**Fragmentation smell:** identical `setup()` calls across tests, each checking one structural detail of the same state.

❌ **WRONG — one scenario fragmented across four tests:**
```tsx
test('renders result-page container', () => {
  setup()
  expect(document.querySelector('.result-page')).toBeInTheDocument()
})
test('result-page has a floating toolbar', () => {
  setup()
  expect(document.querySelector('.result-page .floating-toolbar')).toBeInTheDocument()
})
test('the floating toolbar contains the submission list', async () => {
  setup()
  await waitFor(() =>
    expect(document.querySelector('.floating-toolbar .submissions-list')).toBeInTheDocument()
  )
})
test('the floating toolbar contains the shape sidebar', () => {
  setup()
  expect(document.querySelector('.floating-toolbar .shape-sidebar')).toBeInTheDocument()
})
```

✅ **CORRECT — one test for the complete business value of that scenario:**
```tsx
test('shows collection navigation and shape details side by side with the map', async () => {
  setup()
  await waitFor(() => {
    expect(screen.getByRole('link', { name: /all collections/i })).toBeInTheDocument()
    expect(screen.getByRole('heading', { name: 'survey' })).toBeInTheDocument()
  })
  expect(document.querySelector('.shape-map')).toBeInTheDocument()
})
```

**Tests split on scenario boundaries** (different user paths, different interactions) — not assertion boundaries (different structural details of the same outcome).

A useful check: can you write a meaningful scenario description for each separate test that differs from all the others? If two tests would share the same description, they should be one test.

---

## Test Through Public API Only

Never test implementation details. Test behavior through public APIs.

**Why this matters:**
- Tests remain valid when refactoring
- Tests document intended behavior
- Tests catch real bugs, not implementation changes

### Examples

❌ **WRONG - Testing implementation:**
```typescript
// ❌ Testing HOW (implementation detail)
it('should call validateAmount', () => {
  const spy = jest.spyOn(validator, 'validateAmount');
  processPayment(payment);
  expect(spy).toHaveBeenCalled(); // Tests HOW, not WHAT
});

// ❌ Testing private methods
it('should validate CVV format', () => {
  const result = validator._validateCVV('123'); // Private method!
  expect(result).toBe(true);
});

// ❌ Testing internal state
it('should set isValidated flag', () => {
  processPayment(payment);
  expect(processor.isValidated).toBe(true); // Internal state
});
```

✅ **CORRECT - Testing behavior through public API:**
```typescript
it('should reject negative amounts', () => {
  const payment = getMockPayment({ amount: -100 });
  const result = processPayment(payment);
  expect(result.success).toBe(false);
  expect(result.error).toContain('Amount must be positive');
});

it('should reject invalid CVV', () => {
  const payment = getMockPayment({ cvv: '12' }); // Only 2 digits
  const result = processPayment(payment);
  expect(result.success).toBe(false);
  expect(result.error).toContain('Invalid CVV');
});

it('should process valid payments', () => {
  const payment = getMockPayment({ amount: 100, cvv: '123' });
  const result = processPayment(payment);
  expect(result.success).toBe(true);
  expect(result.data.transactionId).toBeDefined();
});
```

### In UI Tests: Prefer Semantic Queries Over CSS Selectors

CSS class names and DOM structure are implementation details of how a component is styled and organised — they can change during refactoring without any change to the user's experience.

❌ **WRONG — asserting CSS class names and DOM structure:**
```tsx
test('clicking collapse adds is-collapsed class to toolbar', async () => {
  setup()
  await userEvent.click(screen.getByRole('button', { name: /collapse/i }))
  expect(document.querySelector('.floating-toolbar.is-collapsed')).toBeInTheDocument()
})
```

✅ **CORRECT — asserting what the user perceives:**
```tsx
test('the user can collapse the panel to focus on the map, and expand it again', async () => {
  setup()
  await userEvent.click(screen.getByRole('button', { name: /collapse/i }))
  // panel is collapsed — the user can now expand it
  expect(screen.getByRole('button', { name: /expand/i })).toBeInTheDocument()
})
```

**Prefer:**
- `screen.getByRole()`, `screen.getByText()`, `screen.getByLabelText()` — queries that reflect how the user perceives content
- Checking visible labels, headings, and accessible names — things the user reads
- Checking what the user *can do next* after an interaction (affordances change)

**Avoid:**
- `document.querySelector('.css-class')` — a styling/structural detail
- Asserting that a CSS class was added or removed to verify state changes
- Asserting DOM nesting (`.toolbar .sidebar .card`) — tests the component hierarchy, not the behaviour

**Exception:** when an element genuinely has no semantic role and no accessible name (e.g. a canvas, a map tile layer), a CSS class selector may be the only practical option. Note it as a known limitation in the test.

---

## Coverage Through Behavior

Validation code gets 100% coverage by testing the behavior it protects:

```typescript
// Tests covering validation WITHOUT testing validator directly
describe('processPayment', () => {
  it('should reject negative amounts', () => {
    const payment = getMockPayment({ amount: -100 });
    const result = processPayment(payment);
    expect(result.success).toBe(false);
  });

  it('should reject amounts over 10000', () => {
    const payment = getMockPayment({ amount: 15000 });
    const result = processPayment(payment);
    expect(result.success).toBe(false);
  });

  it('should reject invalid CVV', () => {
    const payment = getMockPayment({ cvv: '12' });
    const result = processPayment(payment);
    expect(result.success).toBe(false);
  });

  it('should process valid payments', () => {
    const payment = getMockPayment({ amount: 100, cvv: '123' });
    const result = processPayment(payment);
    expect(result.success).toBe(true);
  });
});

// ✅ Result: payment-validator.ts has 100% coverage through behavior
```

**Key insight:** When coverage drops, ask **"What business behavior am I not testing?"** not "What line am I missing?"

---

## Test Factory Pattern

For test data, use factory functions with optional overrides.

### Core Principles

1. Return complete objects with sensible defaults
2. Accept `Partial<T>` overrides for customization
3. Validate with real schemas (don't redefine)
4. NO `let`/`beforeEach` - use factories for fresh state

### Basic Pattern

```typescript
const getMockUser = (overrides?: Partial<User>): User => {
  return UserSchema.parse({
    id: 'user-123',
    name: 'Test User',
    email: 'test@example.com',
    role: 'user',
    ...overrides,
  });
};

// Usage
it('creates user with custom email', () => {
  const user = getMockUser({ email: 'custom@example.com' });
  const result = createUser(user);
  expect(result.success).toBe(true);
});
```

### Complete Factory Example

```typescript
import { UserSchema } from '@/schemas'; // Import real schema

const getMockUser = (overrides?: Partial<User>): User => {
  return UserSchema.parse({
    id: 'user-123',
    name: 'Test User',
    email: 'test@example.com',
    role: 'user',
    isActive: true,
    createdAt: new Date('2024-01-01'),
    ...overrides,
  });
};
```

**Why validate with schema?**
- Ensures test data is valid according to production schema
- Catches breaking changes early (schema changes fail tests)
- Single source of truth (no schema redefinition)

### Factory Composition

For nested objects, compose factories:

```typescript
const getMockItem = (overrides?: Partial<Item>): Item => {
  return ItemSchema.parse({
    id: 'item-1',
    name: 'Test Item',
    price: 100,
    ...overrides,
  });
};

const getMockOrder = (overrides?: Partial<Order>): Order => {
  return OrderSchema.parse({
    id: 'order-1',
    items: [getMockItem()],      // ✅ Compose factories
    customer: getMockCustomer(),  // ✅ Compose factories
    payment: getMockPayment(),    // ✅ Compose factories
    ...overrides,
  });
};

// Usage - override nested objects
it('calculates total with multiple items', () => {
  const order = getMockOrder({
    items: [
      getMockItem({ price: 100 }),
      getMockItem({ price: 200 }),
    ],
  });
  expect(calculateTotal(order)).toBe(300);
});
```

### Anti-Patterns

❌ **WRONG: Using `let` and `beforeEach`**
```typescript
let user: User;
beforeEach(() => {
  user = { id: 'user-123', name: 'Test User', ... };  // Shared mutable state!
});

it('test 1', () => {
  user.name = 'Modified User';  // Mutates shared state
});

it('test 2', () => {
  expect(user.name).toBe('Test User');  // Fails! Modified by test 1
});
```

✅ **CORRECT: Factory per test**
```typescript
it('test 1', () => {
  const user = getMockUser({ name: 'Modified User' });  // Fresh state
  // ...
});

it('test 2', () => {
  const user = getMockUser();  // Fresh state, not affected by test 1
  expect(user.name).toBe('Test User');  // ✅ Passes
});
```

❌ **WRONG: Incomplete objects**
```typescript
const getMockUser = () => ({
  id: 'user-123',  // Missing name, email, role!
});
```

✅ **CORRECT: Complete objects**
```typescript
const getMockUser = (overrides?: Partial<User>): User => {
  return UserSchema.parse({
    id: 'user-123',
    name: 'Test User',
    email: 'test@example.com',
    role: 'user',
    ...overrides,  // All required fields present
  });
};
```

❌ **WRONG: Redefining schemas in tests**
```typescript
// ❌ Schema already defined in src/schemas/user.ts!
const UserSchema = z.object({ ... });
const getMockUser = () => UserSchema.parse({ ... });
```

✅ **CORRECT: Import real schema**
```typescript
import { UserSchema } from '@/schemas/user';

const getMockUser = (overrides?: Partial<User>): User => {
  return UserSchema.parse({
    id: 'user-123',
    name: 'Test User',
    email: 'test@example.com',
    ...overrides,
  });
};
```

---

## Use Domain APIs to Build Test Fixtures, Not Internal Mechanics

When a library or domain module provides a way to construct a valid entity, use it in test fixtures — don't hand-craft the raw internal representation.

Fixtures built from raw internals require the reader to understand format mechanics to verify the fixture is correct. Fixtures built from the domain API are self-explanatory: the fixture says what the thing *is*, not how bytes happen to be laid out.

❌ **WRONG — raw byte manipulation; opaque without format knowledge:**
```rust
fn valid_shp_bytes() -> Vec<u8> {
    let mut bytes = vec![0u8; 100];
    bytes[0..4].copy_from_slice(&9994i32.to_be_bytes());   // file code?
    bytes[28..32].copy_from_slice(&1000i32.to_le_bytes()); // version?
    bytes[32..36].copy_from_slice(&1i32.to_le_bytes());    // shape type 1?
    bytes
}
```

✅ **CORRECT — uses the library's own writer; intent is immediately clear:**
```rust
fn valid_shp_bytes() -> Vec<u8> {
    let mut buf = Cursor::new(Vec::new());
    {
        let mut writer = ShapeWriter::new(&mut buf);
        writer.write_shape(&Point { x: 0.0, y: 0.0 }).expect("write shape");
        writer.finalize().expect("finalize");
    }
    buf.into_inner()
}
```

This principle extends beyond binary formats:
- Use ORM/query builders to seed database rows, not raw SQL strings
- Use schema factories (Zod, etc.) to construct valid objects, not plain object literals
- Use the public constructor of a domain type, not `Default::default()` followed by field mutation

The rule of thumb: **if understanding the fixture requires knowing how the format works internally, the fixture is wrong.**

---

## Coverage Theater Detection

Watch for these patterns that give fake 100% coverage:

### Pattern 1: Mock the function being tested

❌ **WRONG** - Gives 100% coverage but tests nothing:
```typescript
it('calls validator', () => {
  const spy = jest.spyOn(validator, 'validate');
  validate(payment);
  expect(spy).toHaveBeenCalled(); // Meaningless assertion
});
```

✅ **CORRECT** - Test actual behavior:
```typescript
it('should reject invalid payment', () => {
  const payment = getMockPayment({ amount: -100 });
  const result = validate(payment);
  expect(result.success).toBe(false);
  expect(result.error).toContain('Amount must be positive');
});
```

### Pattern 2: Test only that function was called

❌ **WRONG** - No behavior validation:
```typescript
it('processes payment', () => {
  const spy = jest.spyOn(processor, 'process');
  handlePayment(payment);
  expect(spy).toHaveBeenCalledWith(payment); // So what?
});
```

✅ **CORRECT** - Verify the outcome:
```typescript
it('should process payment and return transaction ID', () => {
  const payment = getMockPayment();
  const result = handlePayment(payment);
  expect(result.success).toBe(true);
  expect(result.transactionId).toBeDefined();
});
```

### Pattern 3: Test trivial getters/setters

❌ **WRONG** - Testing implementation, not behavior:
```typescript
it('sets amount', () => {
  payment.setAmount(100);
  expect(payment.getAmount()).toBe(100); // Trivial
});
```

✅ **CORRECT** - Test meaningful behavior:
```typescript
it('should calculate total with tax', () => {
  const order = createOrder({ items: [item1, item2] });
  const total = order.calculateTotal();
  expect(total).toBe(230); // 200 + 15% tax
});
```

### Pattern 4: 100% line coverage, 0% branch coverage

❌ **WRONG** - Missing edge cases:
```typescript
it('validates payment', () => {
  const result = validate(getMockPayment());
  expect(result.success).toBe(true); // Only happy path!
});
// Missing: negative amounts, invalid CVV, missing fields, etc.
```

✅ **CORRECT** - Test all branches:
```typescript
describe('validate payment', () => {
  it('should reject negative amounts', () => {
    const payment = getMockPayment({ amount: -100 });
    expect(validate(payment).success).toBe(false);
  });

  it('should reject amounts over limit', () => {
    const payment = getMockPayment({ amount: 15000 });
    expect(validate(payment).success).toBe(false);
  });

  it('should reject invalid CVV', () => {
    const payment = getMockPayment({ cvv: '12' });
    expect(validate(payment).success).toBe(false);
  });

  it('should accept valid payments', () => {
    const payment = getMockPayment();
    expect(validate(payment).success).toBe(true);
  });
});
```

---

## No 1:1 Mapping Between Tests and Implementation

Don't create test files that mirror implementation files.

❌ **WRONG:**
```
src/
  payment-validator.ts
  payment-processor.ts
  payment-formatter.ts
tests/
  payment-validator.test.ts  ← 1:1 mapping
  payment-processor.test.ts  ← 1:1 mapping
  payment-formatter.test.ts  ← 1:1 mapping
```

✅ **CORRECT:**
```
src/
  payment-validator.ts
  payment-processor.ts
  payment-formatter.ts
tests/
  process-payment.test.ts  ← Tests behavior, not implementation files
```

**Why:** Implementation details can be refactored without changing tests. Tests verify behavior remains correct regardless of how code is organized internally.

---

## Summary Checklist

When writing tests, verify:

- [ ] Test names describe business requirements, not test data or implementation details
- [ ] A failing test name alone communicates which requirement broke
- [ ] Each test represents one scenario — tests with identical setup and different single assertions are merged
- [ ] Testing behavior through public API (not implementation details)
- [ ] In UI tests, using semantic queries (`getByRole`, `getByText`) rather than CSS class selectors
- [ ] No mocks of the function being tested
- [ ] No tests of private methods or internal state
- [ ] Factory functions return complete, valid objects
- [ ] Factories validate with real schemas (not redefined in tests)
- [ ] Using Partial<T> for type-safe overrides
- [ ] No `let`/`beforeEach` - use factories for fresh state
- [ ] Edge cases covered (not just happy path)
- [ ] Tests would pass even if implementation is refactored
- [ ] No 1:1 mapping between test files and implementation files
