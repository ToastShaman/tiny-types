---
name: expectations
description: Working expectations and documentation practices. Load this to understand how to work effectively with this codebase, agent guidance, code quality principles, and collaboration practices.
allowed-tools:
  - Read
  - AskUserQuestion
---

# Expectations

STARTER_CHARACTER = 📐

## Core Non-Negotiables

1. **TEST-DRIVEN DEVELOPMENT IS MANDATORY**
   - Every production code line needs a failing test first
   - No exceptions, no "I'll add tests later"
   - Load `tdd` skill for detailed workflow

2. **Think Deeply Before Acting**
   - Understand full context of code and requirements
   - Challenge assumptions and think from first principles
   - Ask clarifying questions when requirements are ambiguous

3. **Work in Small, Known-Good Increments**
   - Each change leaves system in working state
   - All tests pass after every commit
   - Load `planning` skill for complex work

## What I Expect from Agents

**Core principle**: Think deeply, follow TDD strictly, capture learnings while context is fresh.

### When Writing Code

- **ALWAYS follow TDD** - no production code without failing test first (load the `tdd` and `testing` skills)
- Make small, incremental changes that leave the system in a working state
- Assess refactoring after every green (but only if it adds clear value)
- Update change logs when introducing any change
- Update documentation when introducing meaningful patterns or changes
- After significant changes, document: gotchas discovered, patterns that emerged, decisions made and why
- Ask "What do I wish I'd known at the start?" after significant changes
- Explain trade-offs you're making
- Flag areas of uncertainty or technical debt being introduced

### When Problem-Solving

- Ask clarifying questions if the requirement is ambiguous
- Propose the simplest solution first
- Suggest alternatives if you see them
- Challenge the premise if something seems overcomplicated
- Time-box exploratory work and report findings

### When Stuck

- State explicitly what's blocking you
- Show what you've tried (list specific approaches)
- Propose 2-3 alternative paths forward
- Ask specific questions about trade-offs or unknowns

### Anti-Patterns to Avoid

- ❌ Writing production code before writing a failing test
- ❌ Premature abstraction (wait for the Rule of Three)
- ❌ Over-engineering "for future flexibility"
- ❌ Skipping tests "to move faster"
- ❌ Large, monolithic changes
- ❌ Clever code that's hard to understand
- ❌ Copy-pasting without understanding
- ❌ Adding dependencies without evaluating trade-offs
- ❌ Expressing uncertainty about a requirement then proceeding on an assumption instead of asking

## Code Quality Principles

- **Readability**: Code should read like prose. Names matter immensely.
- **Minimalism**: Fewer lines, fewer files, fewer abstractions when possible
- **Locality**: Related things should be near each other
- **Reversibility**: Avoid decisions that are hard to undo
- **Convention**: Follow existing patterns in the codebase unless there's a compelling reason to change

## Code Style Guidelines

**Functional programming with immutable data. Self-documenting code.**

**Core practices:**
- No data mutation - immutable data structures only
- Pure functions wherever possible
- No nested if/else - use early returns or composition
- Avoid comments - code should be self-documenting through clear naming
- Prefer options objects over positional parameters
- Use array methods (`map`, `filter`, `reduce`) over loops

## Incremental Design

- Start with the simplest design that works
- Let patterns emerge from real needs, don't impose them upfront
- Refactor continuously as understanding grows (but only when value is clear)
- Code duplication is acceptable initially - wait for the third instance before abstracting (Rule of Three)
- Prefer composition over inheritance, functions over frameworks

## Continuous Integration Practices

- Integrate to main frequently (multiple times per day if possible)
- Keep the build green - broken builds are stop-the-line events
- Run tests locally before committing and pushing
- Small commits with clear messages explaining "why" not just "what"

## Working with Uncertainty

- **Known knowns**: Execute with confidence
- **Known unknowns**: Spike, research, ask questions - limit to 3-5 focused attempts
- **Unknown unknowns**: Admit them. Build in feedback loops to surface them early
- When stuck: Pause, explain the problem out loud (rubber duck), ask for help

## Code Review Mindset

- Self-review your own work before presenting it as complete
- When reviewing a colleague's PR, lead with curiosity and empathy
- Focus on: correctness, design, testability, security, readability — in that order
- Label comments clearly: blocking, suggestion, nitpick, question, praise
- "Ask, don't tell" - questions invite dialogue; statements close it
- Every review is a learning opportunity in both directions
- Load the `code-review` skill for detailed review process and communication principles

## Communication Style

- **Keep project docs current** - Update docs when introducing changes
- **Surface trade-offs** explicitly - there are no perfect solutions
- **Admit uncertainty** - "I don't know" is a valid and respected answer
- **Show, don't just tell** - code examples, diagrams, prototypes
- **Document decisions**, especially the ones we chose NOT to make

## Documentation Framework

**At the end of every significant change, ask: "What do I wish I'd known at the start?"**

Document if ANY of these are true:
- Would save future developers >30 minutes
- Prevents a class of bugs or errors
- Reveals non-obvious behavior or constraints
- Captures architectural rationale or trade-offs
- Documents domain-specific knowledge
- Identifies effective patterns or anti-patterns
- Clarifies tool setup or configuration gotchas

## Types of Learnings to Capture

- **Gotchas**: Unexpected behavior discovered (e.g., "API returns null instead of empty array")
- **Patterns**: Approaches that worked particularly well
- **Anti-patterns**: Approaches that seemed good but caused problems
- **Decisions**: Architectural choices with rationale and trade-offs
- **Edge cases**: Non-obvious scenarios that required special handling
- **Tool knowledge**: Setup, configuration, or usage insights

## Documentation Format

```markdown
#### Gotcha: [Descriptive Title]

**Context**: When this occurs
**Issue**: What goes wrong
**Solution**: How to handle it

// CORRECT - Solution
const example = "correct approach";

// WRONG - What causes the problem
const wrong = "incorrect approach";
```

## Code Change Principles

- **Start with a failing test** - always. No exceptions. (See `tdd` skill)
- After making tests pass, always assess refactoring opportunities (See `refactoring` skill)
- After refactoring, verify all tests and static analysis pass, then commit
- Respect the existing patterns and conventions
- Maintain test coverage for all behavior changes (See `testing` skill)
- Keep changes small and incremental (See `planning` skill for complex work)
- Ensure all TypeScript strict mode requirements are met (See `typescript-strict` skill)
- Provide rationale for significant design decisions

**If you find yourself writing production code without a failing test, STOP immediately and write the test first.**
