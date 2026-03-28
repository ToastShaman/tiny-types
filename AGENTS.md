# AI Agent Instructions for tiny-types

## Project Overview
A Java library providing type-safe value objects (tiny/micro types) with built-in validation. Wraps primitives in domain-specific classes for type safety, validation, and immutability.

## Architecture

### Module Structure
| Module                              | Purpose                                                                                   |
|-------------------------------------|-------------------------------------------------------------------------------------------|
| `core`                              | Base value types (`StringValue`, `UUIDValue`, `NonBlankStringValue`, etc.) and validation |
| `fp`                                | Functional primitives: `Result<T,E>`, `Lens`, `Reader`, `Iso`, `State`, `Converter`       |
| `events`                            | Structured event logging via `Event` interface, `Events` sink, and `RecordingEvents`      |
| `format-jackson`                    | Jackson serialization via `ValueTypeModule` for tiny types                                |
| `events-format-jackson`             | Jackson serialization for events via `EventsModule` and `JacksonEventLogger`              |
| `events-visualiser-mermaid`         | Generate Mermaid diagrams from captured events via `MermaidEventVisualiser`               |
| `time`                              | Testable clocks via `Ticker` interface with `FakeTicker` and `TickingClock`               |
| `fp-database-jooq`                  | Database transactions with `Transaction<R>` monad for jOOQ                                |
| `fp-database-mongodb`               | Database transactions for MongoDB                                                         |
| `fp-database-spring-jdbc`           | Database transactions for Spring JDBC                                                     |
| `http-client-okhttp`                | HTTP actions pattern via `HttpAction<R>` interface with OkHttp                            |
| `aws-sqs-connector`                 | AWS SQS/SNS integration: message handling, tracing, DLQ support                           |
| `testing-core`                      | AssertJ assertions for tiny types via `TinyTypeAssertions`                                |
| `testing-events`                    | AssertJ assertions for events via `RecordingEventsAssertions`                             |
| `testing-fp`                        | AssertJ assertions for `Result<T,E>` via `ResultAssertions`                               |
| `testing-faker`                     | Test data generation via `Faker` with UUID/ULID providers                                 |
| `testing-http-client-okhttp-spring` | Spring MockMvc integration for HTTP client testing                                        |

## Build & Test
```bash
./gradlew build          # Build all modules
./gradlew test           # Run all tests
./gradlew :core:test     # Test specific module
./gradlew spotlessApply  # Format code (Palantir Java style)
```

### Testing Conventions
- JUnit 5 with `@DisplayNameGeneration(ReplaceUnderscores.class)`
- Method names use underscores: `creates_new_value_type()`
- AssertJ for assertions, **no mocking libraries** (no Mockito)

---

# Engineering Philosophy & Working Agreement

## Why We Follow XP and Lean

Software engineering is fundamentally about managing complexity and uncertainty while delivering value. After years of
experience, I've found that XP and Lean provide the most effective framework for this because they:

**Optimize for learning and adaptation** rather than prediction. We acknowledge we can't know everything upfront,
so we build systems that help us learn fast and change direction cheaply. This beats trying to "get it right" the
first time through extensive planning.

**Minimize waste through feedback loops**. The faster we can go from idea → working code → real feedback, the less
time we spend building the wrong thing or over-engineering the right thing. Small batches, continuous integration,
and TDD aren't bureaucracy—they're how we avoid building bridges to nowhere.

**Respect the human element**. Code is read 10x more than it's written. Systems outlive their original authors.
Sustainable pace beats hero sprints. These methodologies put long-term maintainability and team health on equal
footing with delivery speed.

**These are baseline expectations.** When you see references to XP (Extreme Programming) and Lean Software Development
throughout this document, I'm referring to their core values and principles. If you're unfamiliar with these methodologies,
research them – they're fundamental to how we work.

The practices below aren't rules to follow blindly—they're patterns that have emerged from these principles.
When in doubt, return to the principles.

## Working with Skills

This AGENTS.md provides the framework. Skills contain the detailed guidance. Load skills based on what you're doing:

| Situation                                              | Load These Skills                                   |
|--------------------------------------------------------|-----------------------------------------------------|
| Starting any code work                                 | `test-driven-development`, `testing`, `refactoring` |
| Reviewing test quality                                 | `test-design-reviewer`                              |
| Self-reviewing before presenting work as complete      | `code-review`                                       |
| Reviewing a colleague's PR or code changes             | `code-review`                                       |
| Planning safe implementation of a feature or migration | `small-safe-steps`                                  |

**Core workflow skills:**
- `test-driven-development` - Detailed TDD workflow with examples and common pitfalls
- `testing` - Testing patterns, factory functions, and antipatterns
- `test-design-reviewer` - Framework for reviewing test design quality
- `code-review` - Culture-aware code review for self-review and peer PR review
- `refactoring` - Safe refactoring methodology and techniques

Skills contain detailed examples that this document intentionally omits to stay focused on principles.

## Visual Skill Indicators

Each skill defines a `STARTER_CHARACTER` emoji. Always start replies with your active STARTER_CHARACTER(s) followed by
a space. Stack emojis when multiple skills are active — don't replace. The default (no skill loaded) is 🍀,
confirming global rules are active.

Examples: `🍀` (global rules only) · `🍀 🧪` (global + testing skill) · `🔴` (TDD red phase)

## Skill Tool Constraints

Each skill defines `allowed-tools` in its frontmatter — the tools appropriate for that skill's mode. Treat this as binding guidance:

| Skill type                                                  | Typical tools                                     | Rationale                            |
|-------------------------------------------------------------|---------------------------------------------------|--------------------------------------|
| Advisory (small-safe-steps,)                                | `Read`, `AskUserQuestion`                         | Think and question — don't implement |
| Analysis (code-review, test-design-reviewer)                | `Read`, `Glob`, `Grep`, `Bash`, `AskUserQuestion` | Investigate but don't modify         |
| Documentation (planning)                                    | `Read`, `Write`, `Edit`, `AskUserQuestion`        | Produce artefacts, not code          |
| Implementation (tdd, testing, refactoring, language skills) | All tools                                         | Build, test, deploy                  |

When a skill restricts tools, don't reach outside that list without a clear reason. The constraint is intentional — it reflects what the skill is *for*. Advisory skills should be challenging your thinking, not writing code on your behalf.

## How We Work

### Starting Any Task
1. **Understand the "why"** before the "how" – What problem are we actually solving? For whom?
2. **Challenge assumptions** – Is this necessary? Can we solve this with less?
3. **Spike if uncertain** – Limit exploration to 3–5 focused attempts, then report findings and propose next steps
4. **Slice vertically** – Break work into end-to-end deliverable increments

## Core Non-Negotiable

### Test-Driven Development

**TEST-DRIVEN DEVELOPMENT IS NON-NEGOTIABLE.** Every line of production code is written in response to a failing test. No exceptions.

**Why this matters:** TDD embeds feedback into the development process. A failing test forces you to understand the requirement before writing code, prevents over-engineering, and creates a safety net for refactoring. It's how we "make the change easy" before making it.

**What must be true:**
- Every production code change begins with a failing test
- Private helpers with branching logic are unit tested
- User-facing features have E2E tests before marking complete
- Tests verify behavior, not implementation details

**How:** Load the `test-driven-development` skill when starting any coding work. It contains workflow, examples, common pitfalls, RED-GREEN-REFACTOR cycle details, and recovery strategies.

### Working with Uncertainty

Don't guess or make assumptions:
- **Known unknowns** - Spike and research (time-box to 3-5 attempts), then report findings
- **When stuck** – State what's blocking you, show what you tried, propose 2–3 paths forward
- **Never proceed on assumptions** – Ask clarifying questions

#### Requirement Ambiguity Protocol

When you're uncertain about what the user wants:
1. **Present options, don't assume it.** If you can see 2+ valid interpretations, present them as numbered options with a one-line description of each. Let the user choose.
2. **"I think you mean X" followed by proceeding is an anti-pattern.** If you feel the need to say "I think" or "I assume," that's your signal to ask instead.
3. **Distinguish requirement uncertainty from implementation uncertainty.** Requirement ambiguity (what to build) → always ask the user. Implementation ambiguity (how to build it) → spike/research, then propose options if multiple approaches exist.

- ❌ Expressing uncertainty then proceeding on an assumption instead of asking

Load the `expectations` skill for detailed problem-solving approaches.

### Documentation & Communication

**Keep documentation current.** After significant changes:
- Update relevant docs (AGENTS.md, READMEs, etc.)
- Document learning and decisions
- Ask: "What do I wish I'd known at the start?"

**Surface trade-offs explicitly.** Explain reasoning and alternatives considered.

Load the `expectations` skill for documentation frameworks and formats.

## Decision-Making Framework

When facing technical decisions:
1. **Does it solve the actual problem?** (not a hypothetical future one)
2. **Is it the simplest thing that could work?**
3. **Can we test it easily?**
4. **Can we reverse it if we're wrong?**

If 1 & 2 are yes and 3-4 don't raise red flags, proceed.

## Summary

Write clean, testable code that evolves through small, safe increments. Every change should be driven by a test, and the implementation should be the simplest thing that makes that test pass. When in doubt, favor simplicity and readability over cleverness.

---

*"Make it work, make it right, make it fast" – in that order, and stop when you get to "good enough".*

*"Make the change easy, then make the easy change" – if the change is hard, refactor until it is no longer hard, then make the change.*