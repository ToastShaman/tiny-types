---
name: planning
description: Planning work in small, known-good increments. Use when starting significant work or breaking down complex tasks.
allowed-tools:
  - Read
  - Write
  - Edit
  - AskUserQuestion
---

# Planning in Small Increments

STARTER_CHARACTER = 📋

**All work must be done in small, known-good increments.** Each increment leaves the codebase in a working state where all tests pass.

## Documents

| Document | Purpose | Lifecycle |
|----------|---------|-----------|
| **PLAN.md** | User story, acceptance criteria, and steps | Created at start, changes need approval, deleted when complete |
| **LEARNINGS.md** | What we discovered along the way | Temporary, merged into permanent docs then deleted |
| **CHANGELOG.md** | Record of meaningful changes | Persistent, updated with every significant change |

PLAN.md and LEARNINGS.md are ephemeral — they exist for the duration of the work and are deleted when done. CHANGELOG.md is permanent and lives in the project root.

## Planning Phase: User Story Discovery

Planning begins with a conversation, not a document. Before writing PLAN.md, the agent and the user must discuss the business requirements and arrive at a shared understanding.

### Getting to a Real User Story

The goal of planning is to arrive at a clear, outcome-focused user story. The story must describe the **user's role**, **what they'll be able to do**, and **their motivation** — not a system feature or a developer task.

"As a user, I want the system to use OAuth2" is a technical task wearing a story's clothes. The user cares about keeping their details safe, not about the authentication protocol. Push until the story reflects the user's actual need.

### 1. Understand the Business Need

Start with a conversation. Tease out the business need by asking:
- **Who is this for?** — which user, in which role, in which context?
- **What outcome do they need?** — what capability or result matters to them?
- **Why does this matter?** — what's the motivation or value behind this?

Don't accept a solution as a requirement. "Add a caching layer" is an implementation. "Users should see results within 200ms" is a requirement. Push for the requirement.

Shape the answers into a Connextra story: "As a [role], I want to [outcome] so that [motivation]." If the story can't be expressed this way without describing system internals, it's likely not a user story yet — keep discussing.

### 2. Analyse the Existing System

Before proposing how to build, understand what exists. Trace through the codebase to map:

- **Entry points** — which endpoints, routes, handlers, or UI components are involved
- **Business logic** — which services, domain models, or use cases are relevant
- **Data access** — which database queries, repositories, schemas, or migrations support this area
- **External integrations** — third-party APIs, message queues, caching layers
- **Architectural patterns** — how the existing code is structured, what conventions are followed

This analysis informs the story by revealing what already exists, what's missing, and where the gaps are. Report findings to the user before proposing the plan.

### 3. Define Acceptance Criteria

Work with the user to define concrete scenarios that confirm the scope of the story. Each criterion should describe a specific situation and expected outcome. Together, they define the boundary of the story: what's in scope and what's not. Express them as Given/When/Then scenarios where this adds clarity.

Document the story, context, and acceptance criteria in PLAN.md. See the PLAN.md structure below.

### 4. Break Down into Steps

Decompose the user story into small, known-good increments. Each step should be informed by the system analysis — you know what exists, so you know what needs to change.

Each step must:
- Leave all tests passing
- Be independently deployable
- Have clear done criteria
- Fit in a single commit
- Be describable in one sentence

**If you can't describe a step in one sentence, break it down further.**

## Step Size Heuristics

**Too big if:**
- Takes more than one session
- Requires multiple commits to complete
- Has multiple "and"s in description
- You're unsure how to test it
- Involves more than 3 files

**Right size if:**
- One clear test case
- One logical change
- Can explain to someone in 30 seconds
- Obvious when done
- Single responsibility

## TDD Integration

**Every step follows RED-GREEN-REFACTOR.** Load the `tdd` skill for detailed workflow.

**For each step:**
1. RED: Write failing test first
2. GREEN: Write minimum code to pass
3. REFACTOR: Assess improvements (load `refactoring` skill)
4. STOP: Wait for commit approval

**No exceptions. No "I'll add tests later."**

## Commit Discipline

**NEVER commit without user approval.**

After completing a step (RED-GREEN-REFACTOR):

1. Verify all tests pass
2. Verify static analysis passes
3. Update CHANGELOG.md if the change is meaningful
4. Capture any learnings in LEARNINGS.md
5. **STOP and ask**: "Ready to commit [description]. Approve?"

Only proceed with commit after explicit approval.

## PLAN.md Structure

```markdown
# Plan: [Feature Name]

## User Story

As a [role],
I want to [capability],
so that [motivation].

## Context

[Brief analysis of relevant existing system — what's already in place,
what patterns are used, what gaps exist. This is the output of the
system analysis phase, summarised for reference.]

## Acceptance Criteria

- [ ] **Given** [situation], **when** [action], **then** [expected outcome]
- [ ] **Given** [situation], **when** [action], **then** [expected outcome]
- [ ] **Given** [edge case], **when** [action], **then** [expected outcome]

## Steps

### Step 1: [One sentence description]

**Test**: What failing test will we write?
**Implementation**: What code will we write?
**Done when**: How do we know it's complete?

### Step 2: [One sentence description]

**Test**: ...
**Implementation**: ...
**Done when**: ...
```

### Plan Changes Require Approval

If the plan needs to change:

1. Explain what changed and why
2. Propose updated steps
3. **Wait for approval** before proceeding

Plans are not immutable, but changes must be explicit and approved.

## LEARNINGS.md Structure

```markdown
# Learnings: [Feature Name]

## Gotchas

### [Title]
- **Context**: When this occurs
- **Issue**: What goes wrong
- **Solution**: How to handle it

## Patterns That Worked

### [Title]
- **What**: Description
- **Why it works**: Rationale

## Decisions Made

### [Title]
- **Options considered**: What we evaluated
- **Decision**: What we chose
- **Rationale**: Why
- **Trade-offs**: What we gained/lost

## Edge Cases

- [Edge case 1]: How we handled it
- [Edge case 2]: How we handled it
```

### Capture Learnings As They Occur

Don't wait until the end. When you discover something, add it to LEARNINGS.md immediately.

## CHANGELOG.md

CHANGELOG.md is a persistent project file. It records meaningful changes so that the team (and future contributors) can understand what changed and why, without reading every commit.

### What Belongs in the Changelog

- API changes — new endpoints, changed contracts, removed capabilities
- New concepts or domain models introduced
- Significant behavioural changes — changes to how existing features work
- Operational changes — caching strategies, database migrations, infrastructure changes
- Breaking changes — anything that requires downstream consumers to adapt

### What Does NOT Belong

- Internal refactoring that doesn't change behaviour
- Test additions or changes (unless they reveal a behaviour change)
- Dependency updates (unless they change behaviour or capability)
- Code style changes

### Format

```markdown
# Changelog

## [Unreleased]

### Added
- [What was added and why]

### Changed
- [What changed and why — including operational changes like caching strategy updates]

### Removed
- [What was removed and why]

### Fixed
- [What was fixed and what was the symptom]
```

Move entries from `[Unreleased]` to a versioned section when releasing. Follow [Keep a Changelog](https://keepachangelog.com/) conventions.

## End of Feature

When all steps are complete:

### 1. Verify Completion

- All acceptance criteria met
- All tests passing

### 2. Offer to Create a PR

Extract the user story and acceptance criteria from PLAN.md and offer to create a pull request with these as the PR description. The PR description should make it clear what was built and how to verify it, using the story and criteria directly — they were written for exactly this purpose.

### 3. Merge Learnings

Review LEARNINGS.md and determine where each learning belongs. There are two categories:

**Project-specific learnings** — gotchas, domain knowledge, edge cases, and architectural decisions about this project. These go into **project documentation**. If the project has an established docs location (e.g. `docs/`, a wiki, ADR directory), merge learnings there. If there is no clear location, **ask the user** where they'd like them captured before proceeding.

**Ways-of-working learnings** — insights about best practices, development workflow, or engineering approach that apply across projects. These belong in **AGENTS.md** or the relevant **skill file**. For example, a discovery about a better TDD pattern goes into the `tdd` skill; a new coding standard goes into AGENTS.md.

| Learning Type | Destination |
|---------------|-------------|
| Project gotchas | Project docs |
| Domain knowledge | Project docs |
| Architectural decisions | ADR |
| Best practices / workflow improvements | AGENTS.md or relevant skill |

### 4. Delete Planning Documents

After learnings are merged:

```bash
rm PLAN.md LEARNINGS.md
```

**The knowledge lives on in:**
- CHANGELOG.md (what changed)
- Project docs (project-specific gotchas, domain knowledge)
- ADRs (architectural decisions)
- AGENTS.md / skills (ways-of-working improvements)
- PR description (user story and acceptance criteria)
- Git history (what was done)

## Anti-Patterns

- **Committing without approval** — always wait for explicit "yes"
- **Steps that span multiple commits** — break down further until one step = one commit
- **Writing code before tests** — RED comes first, always
- **Waiting until end to capture learnings** — add to LEARNINGS.md as discoveries occur
- **Plans that change silently** — all plan changes require discussion and approval
- **Keeping planning docs after feature complete** — delete them; knowledge is in permanent locations
- **Skipping system analysis** — understand what exists before proposing what to build
- **Accepting implementation as requirement** — push for the business outcome
- **Dressing tasks as user stories** — "As a user, I want OAuth2" is a task in a template, not a story. If the card describes a system feature rather than a user capability, it's not a story.

## Quick Reference

```
START FEATURE
│
├─► Discuss business requirements with user
├─► Analyse existing system (architecture, data, patterns)
├─► Shape user story (who, what, why) and confirm with scenarios
├─► Create PLAN.md (get approval)
├─► Create LEARNINGS.md
│
│   FOR EACH STEP:
│   │
│   ├─► RED: Failing test
│   ├─► GREEN: Make it pass
│   ├─► REFACTOR: If valuable
│   ├─► Update CHANGELOG.md (if meaningful change)
│   ├─► Capture learnings
│   └─► **WAIT FOR COMMIT APPROVAL**
│
END FEATURE
│
├─► Verify all criteria met
├─► Offer to create PR (story + criteria as description)
├─► Merge learnings into project docs (ask user for location if unclear)
└─► Delete PLAN.md, LEARNINGS.md
```
