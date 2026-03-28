---
name: code-review
description: Culture-aware code review for self-review and peer PR review. Use when reviewing your own work before presenting it, or when reviewing a colleague's PR.
allowed-tools:
  - Bash
  - Read
  - Glob
  - Grep
  - AskUserQuestion
---

# Code Review

STARTER_CHARACTER = 👁️

## When to Use This Skill

**Self-review**: Before presenting code as complete, review your own changes through this lens. This catches issues early and models the standards you hold for all code.

**Peer review**: When asked to review a colleague's PR or code changes. The same evaluation criteria apply, with additional attention to communication, empathy, and cultural sensitivity.

## Review Philosophy

Code review is a collaborative act, not an evaluative one. The goal is shared understanding and collective improvement — not gatekeeping or proving expertise.

**From XP:**
- **Collective code ownership** — the code belongs to the team. A review is not an audit of "their" code; it's a conversation about "our" code.
- **Continuous feedback** — reviews are feedback loops. The tighter and more respectful these loops are, the faster the team learns.
- **Communication** — XP values direct, honest, kind communication. Reviews are one of the primary channels where this value is practiced.

**From Lean:**
- **Respect for people** — the author invested thought and effort. Honor that by engaging thoughtfully with their work.
- **Build quality in** — review is a collaborative step that builds quality into the process, not a gate that catches defects after the fact.
- **Eliminate waste** — bike-shedding, unnecessary style debates, and blocking on cosmetic issues are waste. Focus energy where it creates genuine value.
- **Flow** — reviews should not be bottlenecks. Keep feedback focused and aim to move work forward.

## The Review Mindset

### Assume Positive Intent

The author made deliberate choices. Before suggesting a change, consider that they may have context you lack. Ask about their reasoning before proposing alternatives.

### Know Your Audience

Different people communicate differently, have different levels of experience, and come from different cultural contexts. What reads as "direct feedback" to one person may feel dismissive to another. When you don't know the author well, err on the side of being more explicit, more encouraging, and more curious.

### Written Text Carries No Tone

In spoken conversation, tone does the heavy lifting. In a review comment, the words are all there is. Choose them carefully.

- Use plain, straightforward language — avoid idioms, slang, and phrasal verbs that may confuse non-native English speakers
- Never use words like "obvious," "trivially," "simply," or "just" — they imply the author should have already known something
- Avoid absolutes and generalizations — "This is wrong" lands differently from "I think there might be an issue here"
- When in doubt, add a question mark — questions invite dialogue; statements close it

### Acknowledge Good Work

When something is well-designed, clearly written, or teaches you something, say so. Positive feedback sets a constructive tone, reinforces good patterns, and signals genuine engagement with the code. This is not flattery — it is honest acknowledgment.

## What to Evaluate

Whether self-reviewing or reviewing a peer's PR, evaluate in this priority order:

1. **Correctness** — Does the code do what was asked? Are edge cases handled? Are error paths covered, not just the happy path?
2. **Design** — Does the approach fit the codebase? Is the change appropriately scoped? Are there decisions that would be hard to reverse?
3. **Testability** — Was code driven by tests? Do tests describe behavior, not implementation? Would a stranger understand intent from test names alone?
4. **Security** — Are there injection risks, data exposure, or trust boundary issues?
5. **Readability** — Would a teammate understand this code without explanation? Are names clear and descriptive? Is this the simplest version that solves the problem?

Do not review for style — let automated tools handle formatting, linting, and style consistency.

## Self-Review Process

Before presenting your work as ready, run through the evaluation criteria above against your own changes. Additionally check:

- [ ] No leftover debugging code, commented-out blocks, or TODOs without context
- [ ] No unintended file changes or formatting noise in the diff
- [ ] Documentation updated if behavior or API has changed

Apply the same rigor you'd want from a peer reviewer. Do not skip self-review because the change feels small or obvious.

## Peer Review Process

### Step 1: Understand Before Evaluating

Read the PR description and any linked context first. Understand:
- What problem is being solved?
- What approach did the author choose, and why?
- What constraints were they working within?

Read the code with curiosity, not suspicion. If something looks unusual, your first instinct should be "I wonder why they did it this way" — not "this is wrong."

### Step 2: Evaluate

Apply the evaluation criteria above. For large changes, break your review into phases rather than writing dozens of comments at once:

1. **First pass**: Architecture, approach, and high-level design. If this needs rethinking, the author shouldn't also be processing many line-level comments.
2. **Second pass**: Logic, correctness, edge cases, and test coverage.
3. **Final pass**: Naming, readability, minor improvements.

When the same issue appears multiple times, leave one detailed comment and note that it applies in other places. Repeating the same feedback on every occurrence overwhelms the author without adding information.

### Step 3: Write Comments with Care

Frame every comment using the communication principles below. Before finalizing your review, re-read it as if you were the author receiving it. Adjust anything that could land poorly.

## Communication Principles

### Provide Suggestions, Not Just Criticism

When you believe something could be better, offer a concrete alternative. Saying "this function is in the wrong place" gives the author a problem without a path forward. Instead:

> This function seems to be handling concerns specific to [X]. Because of [reasoning], I think it might fit better in [specific location]. What do you think?

The structure is: **observation** + **reasoning** + **suggestion** + **invitation to discuss**.

### Ask Before You Assert

Questions are almost always better than statements. They invite dialogue and acknowledge that you may be missing context.

| Instead of | Try |
|------------|-----|
| "This should use X instead" | "Have you considered using X here? It might help with [reason]" |
| "This doesn't handle the error case" | "What happens if this fails? Should we handle that case?" |
| "This is too complex" | "I'm finding this hard to follow — could we simplify this path?" |
| "Wrong approach" | "I'm curious about the approach here — what led you to this design?" |

### Label Your Comments

Not all comments carry the same weight. Make expectations explicit so the author knows what must change and what's optional.

- **blocking:** — Must be addressed before merging. Use sparingly.
- **suggestion:** — Would improve things, but it's the author's call.
- **nitpick:** — Minor preference, totally fine to ignore.
- **question:** — Seeking understanding, no change necessarily needed.
- **praise:** — Good work worth acknowledging.

### Be Concise

Long comments lose their audience. Say what you need to say in as few words as possible, then stop. When suggesting an alternative approach, a brief code example often communicates more clearly than a written explanation — especially when working across language or cultural barriers.

### Good Enough Is Good Enough

Seeking perfection in review does more harm to the contributor, the team, and the project than "imperfect" code does. The standard is "good enough for the health of the project," not "exactly how I would have written it." Focus on the problem at hand. Forward-looking suggestions are valuable, but they should not block the current change.

## Anti-Patterns

### In Self-Review
- Skipping self-review because "it's obvious"
- Reviewing only the code you just wrote while ignoring how it integrates with what exists
- Rubber-stamping your own work — apply the same rigor you'd want from a peer

### In Peer Review
- **Rubber-stamping** — approving without genuinely engaging with the code
- **Bike-shedding** — spending review energy on trivial naming or formatting while missing design issues
- **Tone-deaf phrasing** — "Why would you do it this way?" vs "I'm curious about the reasoning here"
- **Scope creep** — requesting changes unrelated to the PR's purpose. If you spot improvements elsewhere, note them separately.
- **Perfectionism** — blocking a PR for issues that don't affect correctness, security, or maintainability
- **Repeating feedback** — if an issue appears multiple times, say it once and note the scope
- **Ignoring cultural context** — not everyone communicates or interprets feedback the same way. Be aware of this and adapt.

## What a Good Review Looks Like

A good review:
- Has **few, focused comments** — enough to guide the change forward, not so many as to overwhelm
- **Acknowledges strengths** alongside areas for improvement
- Makes **expectations clear** — the author knows exactly what needs to change and what is optional
- **Invites dialogue** — the author feels safe to push back, ask questions, or propose alternatives
- **Respects the author's autonomy** — suggestions are offered, not imposed
- Moves the work **toward completion**, not toward the reviewer's personal preferences

A good review does not need many comments. It needs the right comments.

## Summary

Code review exists to make the code better and the team stronger. It is a conversation, not an inspection. Lead with curiosity, communicate with care, and remember that the person on the other side of the PR is a collaborator working toward the same goal.

---

*"Find that common ground, find that middle-ground, find the right compromise. It's OK to disagree and to have good enough code as long as the health of the project is not harmed by it."*
