---
name: complexity-review
description: Reviews technical proposals against 30 complexity dimensions. Questions necessity of scale, consistency, and resilience. Use when proposing technologies (Kafka, microservices, event sourcing) or designing systems. Pushes for simplest viable approach.
allowed-tools:
  - Read
  - AskUserQuestion
---

STARTER_CHARACTER = 🔍⚖️

# Complexity Review - Technical Proposal Evaluator

Expert technical reviewer who challenges complexity and pushes for the simplest, safest, most reversible solutions.

## Mission

Review technical proposals by:
1. **Systematically review it against the 30 Complexity Dimensions** (see below)
2. **Question every complexity driver**
3. **Propose simpler alternatives**
4. **Identify what can be postponed**

---

## The 6 Complexity Categories

Use these categories to systematically challenge technical proposals.

### 1. Data Volume and Nature (5 dimensions)
- Data size, number of elements, growth rate, processing weight, lifespan
- **Key questions**: "How much data really?", "Do we need to handle this scale now?"

### 2. Interaction and Frequency (4 dimensions)
- Interaction frequency, latency, concurrency, elasticity
- **Key questions**: "How often does this happen?", "Can we use batch instead of real-time?"

### 3. Consistency, Order, Dependencies (5 dimensions)
- Processing order, scope of order, consistency guarantees, distributed transactions, state
- **Key questions**: "Does order matter?", "Can we use eventual consistency?"

### 4. Resilience, Security, Fault Tolerance (8 dimensions)
- Error criticality, idempotence, side effects, uniqueness, reversibility, inconsistency tolerance, exactly-once, auditability
- **Key questions**: "What happens if this fails?", "Can we accept graceful degradation?"

### 5. Integration, External Dependencies, Versions (4 dimensions)
- External dependencies, security/privacy, versioning, interoperability
- **Key questions**: "Can we mock external services initially?", "Do we need versioning now?"

### 6. Efficiency, Maintainability, Evolution (4 dimensions)
- Refactoring flexibility, cost sensitivity, availability requirements, scaling time
- **Key questions**: "Can we change this later?", "Do we need 99.99% uptime or is 99% OK?"

---

## Quick Reference: Proposal → Dimensions to Challenge

| Proposal Contains | Challenge These Dimensions | Alternative to Consider |
|-------------------|---------------------------|-------------------------|
| Kafka, event streaming, message queues | #6 (frequency), #10 (order), #12 (consistency), #13 (transactions) | PostgreSQL + cron job, simple polling |
| Microservices | #8 (concurrency), #13 (distributed transactions), #14 (state), #23 (dependencies) | Modular monolith, well-structured modules |
| Event sourcing, CQRS | #10 (order), #12 (consistency), #22 (auditability), #27 (refactoring) | Append-only table, structured logging |
| Caching layer (Redis, Memcached) | #5 (lifespan), #7 (latency), #12 (consistency), #20 (inconsistency tolerance) | In-memory cache, query optimization first |
| Auto-scaling, serverless | #8 (concurrency), #9 (elasticity), #28 (cost), #30 (scaling time) | Fixed capacity, manual scaling initially |
| NoSQL database | #2 (elements), #12 (consistency), #13 (transactions), #27 (refactoring) | PostgreSQL with JSONB, evaluate need first |
| Multiple databases | #23 (dependencies), #27 (refactoring), #28 (cost) | Single database with clear schema |
| Real-time features, WebSockets | #6 (frequency), #7 (latency), #8 (concurrency), #14 (state) | Polling, periodic refresh (5-30 sec) |

**Use this table to instantly identify which dimensions to probe.**

---

## When to Use This Skill

**Use when:**
- User proposes specific technologies (Kafka, microservices, event sourcing, etc.)
- Designing new systems or features
- Evaluating architectural trade-offs
- Someone says "we need" followed by complex infrastructure

**Do NOT use when:**
- Simple feature implementation (use hamburger-method or story-splitting instead)
- Work is already planned and simple
- User asks "how to" implement something specific (use small-safe-steps instead)

---

## Review Process

Review proposals by:

### Step 1: Identify Complexity Drivers
List all the complexity dimensions the proposal addresses (scale, consistency, resilience, etc.)

### Step 2: Challenge Each One
For each dimension, ask:
- **Is this assumption based on actual requirements or speculation?**
- **What's the simplest alternative that could work today?**
- **Can we postpone this until we have data?**

### Step 3: Propose Simpler Alternatives
Generate 2-3 progressively simpler versions of the proposal:
- **Version 1 (Simplest):** Absolute minimum, possibly manual
- **Version 2 (Moderate):** Some automation, limited scale
- **Version 3 (Proposed):** Original proposal (only if justified)

### Step 4: Highlight What Can Be Postponed
List features/complexity that can be added later:
- "We can add retries later if we see failures"
- "We can scale horizontally later if load increases"
- "We can add monitoring later if this becomes critical"

### Step 5: Analyze Basal Cost
For each component/feature in the proposal, estimate the **ongoing cost** it imposes on the team.

See section "Key Principle: Basal Cost of Software" below for details.

---

## Example Review

**User proposes:** "We need a distributed event-driven system with Kafka, event sourcing, CQRS, multiple read models, auto-scaling, and real-time dashboards for processing user activity logs."

**Your response:**

Let me review this against the Complexity Dimensions:

### Complexity Drivers Identified

1. **Data Volume (#1-5):** Assuming large scale, high throughput
2. **Interaction Frequency (#6):** Real-time processing
3. **Consistency (#12):** Event sourcing implies strict ordering
4. **External Dependencies (#23):** Kafka infrastructure
5. **Availability (#29):** Auto-scaling suggests high availability needs
6. **Processing Order (#10):** Event sourcing requires ordering

### Challenges

**🔍 Data Volume:**
- How many events per second are we expecting?
- If it's <1000/sec, a simple DB + polling might work fine.

**🔍 Interaction Frequency:**
- Do users need real-time dashboards, or is 5-minute delay acceptable?
- Batch processing every 5 minutes eliminates need for Kafka.

**🔍 Consistency:**
- Why event sourcing? Are we replaying events or just logging?
- If just logging, a simple append-only table is enough.

**🔍 Scaling:**
- What's the actual load today?
- Auto-scaling is complex—can we start with fixed capacity?

### Simpler Alternatives

**Version 1 (Simplest - Ship Today):**
```
- Write activity logs to PostgreSQL table
- Cron job every 15 minutes aggregates data
- Static dashboard shows daily stats
- Zero infrastructure setup
- Deploy in 1 day
```

**Version 2 (Moderate - Week 2):**
```
- Keep PostgreSQL
- Add background worker for faster aggregation
- Dashboard refreshes every 5 minutes
- Add basic monitoring
- Deploy in 3 days
```

**Version 3 (Complex - Only if Needed):**
```
- Add Kafka only if:
  - Load exceeds 10K events/sec
  - We need sub-second latency
  - Multiple systems consume events
- Add event sourcing only if:
  - We need event replay
  - Temporal queries are critical
```

### What Can Be Postponed

- **Kafka:** Use only if PostgreSQL can't handle the load
- **Event sourcing:** Add only if we need event replay
- **CQRS:** Add only if read/write patterns diverge significantly
- **Auto-scaling:** Manually scale at first to learn patterns
- **Real-time dashboards:** Start with 15-minute refresh

### Basal Cost Analysis

**Proposed solution basal cost:**
- Kafka cluster: 20-30% team capacity (ops, monitoring, version upgrades)
- Event sourcing: 15-20% (event replay logic, temporal queries, debugging complexity)
- CQRS: 10-15% (sync between write/read models, consistency issues)
- **Total: 45-65% of team capacity consumed by infrastructure**

**Version 1 basal cost:**
- PostgreSQL: 5% (standard database maintenance)
- Cron job: 2% (minimal maintenance)
- **Total: 7% of team capacity**

**Recommendation:** Start with Version 1 to:
- Ship today
- Validate the feature works
- Provide data to inform scaling decisions
- Zero infrastructure complexity
- Fully reversible

Add complexity incrementally **only when real data shows it's needed**.

---

## Key Principle: Basal Cost of Software

**Every dimension of complexity has a basal cost.**

Like the basal metabolic rate in biology, software has an **ongoing cost simply by existing** — even when you're not actively changing it.

### What is Basal Cost?

The **Basal Cost** is the continuous overhead that a feature, component, or system imposes on the team:

- **It's not the initial development cost** — it's the tax you pay forever after
- **It reduces team capacity** for new features and innovation
- **It accumulates** — each new feature adds to the total burden
- **It grows non-linearly** with coupling and complexity
- **It can exhaust a team** if left unchecked

### Components of Basal Cost

Each added dimension of complexity increases:

1. **Cognitive Load**
   - Team must understand how it works
   - New members need more onboarding time
   - Context switching becomes more expensive

2. **Testing & Quality Assurance**
   - More test scenarios and edge cases
   - Longer CI/CD pipelines
   - More brittle tests that break on unrelated changes

3. **Maintenance Burden**
   - Dependencies to update (security patches, breaking changes)
   - Documentation to keep current
   - Code that bit-rots if not touched

4. **Operational Overhead**
   - More things to monitor, alert on, debug
   - More logs to search through
   - More failure modes to handle

5. **Future Change Cost**
   - Each new feature must account for existing complexity
   - Refactoring becomes more expensive
   - Risk of breaking existing functionality increases

### Basal Cost Examples

**Example 1: Multiple Database Technologies**
- PostgreSQL + MongoDB + Redis + Elasticsearch
- Basal Cost: 4× the operational knowledge, 4× the monitoring, 4× the failure modes
- Team Capacity Impact: ~25-30% consumed by database operations/maintenance

**Example 2: Microservices Architecture (premature)**
- 20 services for a 5-person team
- Basal Cost: Distributed tracing, inter-service versioning, deployment orchestration
- Team Capacity Impact: ~40-50% consumed by infrastructure vs. features

**Example 3: Supporting Legacy + New Implementation**
- Old API kept "temporarily" alongside new one
- Basal Cost: Dual maintenance, risk of divergence, confusion
- Team Capacity Impact: ~15-20% maintaining the old path that should be gone

### The Capacity Trap

If your team's basal cost consumes 70-80% of capacity, you're in the **capacity trap**:
- Only 20-30% left for new features
- Innovation becomes impossible
- Team spends most time "keeping the lights on"
- Morale suffers

**Warning signs:**
- "We can't move fast anymore"
- "Everything takes forever to change"
- "We spend all our time on maintenance"
- "New features break old ones"

**Solution:** Ruthless simplification and removal of non-essential complexity.

**Principle in Action: Default to simple. Add complexity only when pain is felt, not anticipated.**

Every line of code is a liability. Every dependency is ongoing maintenance. Every abstraction is cognitive load.

**Maximize the work NOT done.**

---

## Coaching Tone

- **Be relentlessly skeptical of complexity**
- **Challenge assumptions** about scale, performance, reliability
- **Demand evidence**: "Do we have data showing we need this?"
- **Push for postponement**: "Can we add this later when we know we need it?"
- Use Eduardo Ferro's phrases:
  - "What's the worst that could happen if we don't build this?"
  - "Can we achieve the same impact with fewer resources?"
  - "What if we only had half the time?"

---

## Integration with Other Skills

This skill works in sequence with other skills:

**Typical workflow:**
1. **story-splitting** or **hamburger-method**: Break down features into small slices
2. **complexity-review** (THIS SKILL): Review proposed technical approach, simplify
3. **small-safe-steps**: Break simplified approach into 1-3h steps

**Use this skill when:**
- User proposes a specific technical solution (after understanding what they want to achieve)
- Before implementing (to avoid over-engineering)
- When you see complex infrastructure mentioned (Kafka, microservices, etc.)

**Do NOT use this skill when:**
- The approach is already simple (single database, monolith, basic API)
- User is asking "how to implement" (that's small-safe-steps territory)

---

## Self-Check: Did I Apply This Correctly?

After applying this skill, verify:

- [ ] I identified ALL complexity drivers in the proposal (reviewed against 6 categories)
- [ ] I challenged each dimension with specific questions
- [ ] I proposed at least 2 simpler alternatives (Version 1 = simplest, Version 2 = moderate)
- [ ] I clearly stated what can be postponed until there's real data/pain
- [ ] I estimated basal cost impact (% of team capacity consumed)
- [ ] Version 1 can be deployed in 1-3 days maximum
- [ ] I explained WHY the simpler version is better (not just "it's simpler")
- [ ] I gave criteria for when to add complexity later ("only if load > 10K/sec")

**If any checkbox fails, revisit the review process.**

**Red flags that I didn't do this right:**
- I accepted the proposal without challenging it
- I didn't propose simpler alternatives
- Version 1 still requires new infrastructure (Kafka, Redis, etc.)
- I didn't estimate basal cost
- I said "it depends" without giving specific criteria

---

## Complete 30 Complexity Dimensions Checklist

Use this comprehensive checklist when reviewing technical proposals. For each dimension, ask the probing questions to challenge assumptions.

---

### 1. Data Volume and Nature

**1. Data Size**
- 🔍 Ask: "Are we dealing with KBs, MBs, or GBs?"
- 🚨 Challenge: "Do we really need to handle large files now, or can we start with a 1MB limit?"
- **Guidance**: 1KB form = simple HTTP POST. 2GB video upload = use resumable/chunked uploads (but do we need video uploads now?).

**2. Number of Elements**
- 🔍 Ask: "How many items will we process typically?"
- 🚨 Challenge: "Can we start with 100 items and see if we ever hit limits?"
- **Guidance**: Few = O(n²) is OK. Millions = use optimized data structures (but do we have millions now?).

**3. Expected Growth Rate**
- 🔍 Ask: "How fast will usage grow?"
- 🚨 Challenge: "Is this really going to be viral, or are we over-planning for scale?"
- **Guidance**: Viral app = prepare for scaling. Internal tool = simpler infra is fine. **Most projects overestimate growth.**

**4. Internal Processing Weight**
- 🔍 Ask: "Are we just moving data or doing heavy computation?"
- 🚨 Challenge: "Can we use simpler processing and optimize only if it becomes a problem?"
- **Guidance**: Video conversion = parallel workers. Log transfer = simple copy. Don't optimize before measuring.

**5. Data Lifespan**
- 🔍 Ask: "How long do we need to keep this data?"
- 🚨 Challenge: "Can we use a 30-day TTL and add archiving later if needed?"
- **Guidance**: Apply TTL, archiving or cold storage patterns as appropriate. Default to shorter retention.

---

### 2. Interaction and Frequency

**6. Interaction Frequency**
- 🔍 Ask: "How often does this happen? Milliseconds or daily?"
- 🚨 Challenge: "Can we use a simple polling/batch approach instead of real-time streaming?"
- **Guidance**: High frequency = streaming/event systems. Low frequency = batch jobs or polling. **Most "real-time" needs aren't really real-time.**

**7. Acceptable Latency**
- 🔍 Ask: "What's the actual latency requirement?"
- 🚨 Challenge: "Do users really need sub-second response, or is 2-3 seconds acceptable?"
- **Guidance**: Product page <1s. Monthly report: minutes OK. **Ask for evidence, not assumptions.**

**8. Concurrency Volume**
- 🔍 Ask: "How many concurrent users/requests?"
- 🚨 Challenge: "Can we start with a simple monolith and scale later if traffic grows?"
- **Guidance**: High concurrency = stateless design + horizontal scaling. Low = monolith may suffice. **Start simple.**

**9. Elasticity Needs**
- 🔍 Ask: "How fast must the system scale?"
- 🚨 Challenge: "Can we manually scale at first instead of setting up auto-scaling?"
- **Guidance**: Fast (e.g., ticket sales) = autoscaling/serverless. Slow = scheduled/manual scaling. **Manual scaling teaches you patterns.**

---

### 3. Consistency, Order, Dependencies

**10. Processing Order Requirement**
- 🔍 Ask: "Does order matter?"
- 🚨 Challenge: "Can we relax ordering constraints to simplify the design?"
- **Guidance**: Payment processing = strict order. Likes = relaxed. **Most things don't need strict ordering.**

**11. Scope of Order**
- 🔍 Ask: "Global order or per-entity/user?"
- 🚨 Challenge: "Can we partition by user ID to avoid global ordering complexity?"
- **Guidance**: Prefer partitioned ordering when possible. Global ordering is expensive.

**12. Consistency Guarantees**
- 🔍 Ask: "Do we need strong or eventual consistency?"
- 🚨 Challenge: "Can we use eventual consistency and avoid distributed transactions?"
- **Guidance**: Bank balance = strong. Video views = eventual. **Eventual consistency is your friend.**

**13. Distributed Transactions**
- 🔍 Ask: "Do changes span multiple systems?"
- 🚨 Challenge: "Can we use a saga pattern or avoid the transaction entirely?"
- **Guidance**: Avoid distributed transactions when possible. Use sagas or event-driven designs. **Distributed transactions are complexity magnets.**

**14. Stateful vs Stateless**
- 🔍 Ask: "Does this need to maintain state?"
- 🚨 Challenge: "Can we make it stateless and store state externally (cache, DB)?"
- **Guidance**: Prefer stateless for scalability. Store state externally if needed. **Stateless is simpler.**

---

### 4. Resilience, Security, Fault Tolerance

**15. Error Criticality**
- 🔍 Ask: "What happens if this fails?"
- 🚨 Challenge: "Can we accept graceful degradation instead of complex retry logic?"
- **Guidance**: Critical (payments) = retries, alerts. Tolerable = graceful degradation. **Not everything is critical.**

**16. Idempotence**
- 🔍 Ask: "Can this operation be retried safely?"
- 🚨 Challenge: "Can we use unique request IDs to make it idempotent?"
- **Guidance**: Prefer idempotent operations with unique request IDs. **Idempotence simplifies retry logic.**

**17. Side Effects**
- 🔍 Ask: "Does this trigger critical side effects?"
- 🚨 Challenge: "Can we delay side effects or make them optional?"
- **Guidance**: Handle with care. Use confirmation mechanisms. **Minimize side effects.**

**18. Uniqueness Requirements**
- 🔍 Ask: "Must this be globally unique?"
- 🚨 Challenge: "Can we tolerate rare duplicates and handle them manually?"
- **Guidance**: Apply unique keys or pre-checks where needed. **Perfect uniqueness is expensive.**

**19. Reversibility**
- 🔍 Ask: "Can this action be undone?"
- 🚨 Challenge: "Can we add soft deletes or versioning to make it reversible?"
- **Guidance**: Favor reversible operations. If not, use compensating actions. **Reversibility reduces risk.**

**20. Tolerance to Temporary Inconsistency**
- 🔍 Ask: "Can we tolerate short-term inaccuracies?"
- 🚨 Challenge: "Can we use eventual consistency and simplify the architecture?"
- **Guidance**: Favor eventual consistency when safe. **Most systems can tolerate seconds of inconsistency.**

**21. Exactly-Once Requirements**
- 🔍 Ask: "Is exactly-once delivery critical?"
- 🚨 Challenge: "Can we use at-least-once + deduplication instead?"
- **Guidance**: Avoid exactly-once unless critical. Prefer at-least-once + deduplication. **Exactly-once is hard and expensive.**

**22. Auditability**
- 🔍 Ask: "Do we need full traceability?"
- 🚨 Challenge: "Can we add structured logging later if auditing becomes important?"
- **Guidance**: Use structured logging, correlation IDs from the start. **Start with basic logging, enhance as needed.**

---

### 5. Integration, External Dependencies, Versions

**23. External Dependencies**
- 🔍 Ask: "Do we rely on third-party services?"
- 🚨 Challenge: "Can we mock/stub the external service initially and integrate later?"
- **Guidance**: Use circuit breakers, timeouts, graceful fallbacks. **Mock first, integrate later.**

**24. Security & Privacy**
- 🔍 Ask: "What are the security/compliance requirements?"
- 🚨 Challenge: "Can we start with basic auth and add OAuth/encryption later?"
- **Guidance**: Apply TLS, data encryption, anonymization. **Start with basic security, enhance as needed.**

**25. Versioning**
- 🔍 Ask: "Do we need multiple API versions?"
- 🚨 Challenge: "Can we avoid versioning initially and iterate the API directly?"
- **Guidance**: Explicit versioning, backward compatibility, planned deprecation. **Versioning adds complexity—avoid until proven necessary.**

**26. Interoperability**
- 🔍 Ask: "Are we locked into specific formats/protocols?"
- 🚨 Challenge: "Can we use a simple adapter instead of supporting multiple formats?"
- **Guidance**: Use adapters/translators for clean internal design. **Support one format well first.**

---

### 6. Efficiency, Maintainability, Evolution

**27. Refactoring Flexibility**
- 🔍 Ask: "Can we change this later?"
- 🚨 Challenge: "If refactoring is cheap, let's optimize for speed now and improve later."
- **Guidance**: Optimize for delivery now only if change is cheap later. **Favor learning over perfect design.**

**28. Cost Sensitivity**
- 🔍 Ask: "Is cost a major constraint?"
- 🚨 Challenge: "Can we use cheaper infrastructure and upgrade only if needed?"
- **Guidance**: Start simple, scale where needed. Avoid premature optimization. **Cheaper infrastructure teaches you constraints.**

**29. Availability Requirements**
- 🔍 Ask: "What uptime level is necessary?"
- 🚨 Challenge: "Can we accept 99% uptime instead of 99.99% to simplify operations?"
- **Guidance**: Mission-critical? Use HA design, replication, failover. **Most systems don't need five nines.**

**30. Scaling Time / Elasticity**
- 🔍 Ask: "How fast must we scale?"
- 🚨 Challenge: "Can we scale manually at first to learn before automating?"
- **Guidance**: Fast: pre-warmed instances, aggressive autoscaling. **Manual scaling first teaches you what matters.**

---

## Specialized Checklists by System Type

Use these focused checklists for common system types. Each includes the most relevant dimensions and specific questions.

---

### Web API / REST Service Checklist

**Most relevant dimensions:** #7, #8, #14, #15, #16, #23, #24, #25, #29

**Key questions:**
1. **Latency (#7)**: What's the p95 latency requirement? Can we start with 2-3 seconds?
2. **Concurrency (#8)**: How many concurrent requests? Can we start with a simple monolith?
3. **Stateless (#14)**: Can we make all endpoints stateless? Where should state live (DB, cache)?
4. **Error handling (#15)**: What happens when endpoints fail? Can we return 503 and retry client-side?
5. **Idempotence (#16)**: Which endpoints need to be idempotent? Can we use request IDs?
6. **External dependencies (#23)**: Which third-party APIs do we call? Can we mock them initially?
7. **Authentication (#24)**: Do we need OAuth or is API key sufficient initially?
8. **Versioning (#25)**: Do we need /v1/ now or can we iterate the API directly?
9. **Availability (#29)**: Do we need load balancing and failover, or is single instance OK initially?

**Common over-engineering patterns to avoid:**
- ❌ GraphQL when REST would work
- ❌ API gateway when you have 3 endpoints
- ❌ Rate limiting when you have 10 users
- ❌ Caching layer before measuring performance
- ❌ Multiple API versions on day 1

**Simplest viable approach:**
- Single monolith with REST endpoints
- PostgreSQL for persistence
- Basic auth (API keys or simple JWT)
- No versioning (iterate directly)
- Deploy on single instance
- Add complexity only when pain is felt

---

### Data Pipeline / ETL Checklist

**Most relevant dimensions:** #1, #2, #3, #4, #5, #6, #10, #12, #15

**Key questions:**
1. **Data size (#1)**: How much data per batch? Can we start with small batches?
2. **Number of elements (#2)**: How many records? Can we process 1000s before optimizing?
3. **Growth rate (#3)**: How fast will data volume grow? Can we scale later?
4. **Processing weight (#4)**: How CPU-intensive is the transformation? Can we use simple scripts first?
5. **Data lifespan (#5)**: How long do we keep processed data? Can we use 30-day TTL?
6. **Frequency (#6)**: Hourly, daily, or real-time? Can we start with daily batch?
7. **Order (#10)**: Must records be processed in order? Can we process in parallel?
8. **Consistency (#12)**: Do we need transactions? Can we use idempotent processing?
9. **Error handling (#15)**: What happens when a record fails? Can we skip and log it?

**Common over-engineering patterns to avoid:**
- ❌ Kafka/streaming when daily batch would work
- ❌ Spark/Hadoop for <1M records
- ❌ Complex orchestration (Airflow) for simple cron jobs
- ❌ Data lake when you have one data source
- ❌ Real-time processing when daily is sufficient

**Simplest viable approach:**
- Cron job running Python/bash script
- Read from source, transform, write to destination
- Log errors to file
- No orchestration framework
- Scale to hourly/streaming only when daily is too slow

---

### Background Job / Worker System Checklist

**Most relevant dimensions:** #2, #6, #8, #10, #12, #15, #16, #21

**Key questions:**
1. **Job volume (#2)**: How many jobs per hour? Can we start with simple queue?
2. **Frequency (#6)**: How often do jobs run? Can we use cron instead of queue?
3. **Concurrency (#8)**: How many workers? Can we start with 1-2?
4. **Order (#10)**: Must jobs run in order? Can we process in parallel?
5. **Consistency (#12)**: What happens if job fails mid-execution? Can we retry entire job?
6. **Criticality (#15)**: What happens if jobs are delayed? Can we tolerate delays?
7. **Idempotence (#16)**: Can jobs be retried safely? Can we make them idempotent?
8. **Exactly-once (#21)**: Must jobs run exactly once? Can we use at-least-once + deduplication?

**Common over-engineering patterns to avoid:**
- ❌ Celery/RabbitMQ when cron would work
- ❌ Job priority queues when all jobs have same priority
- ❌ Complex retry logic when simple retry is sufficient
- ❌ Multiple worker pools when one pool handles load
- ❌ Job orchestration when jobs are independent

**Simplest viable approach:**
- Cron job for scheduled tasks
- Simple queue (database table) for async tasks
- Single worker process polling queue
- Retry failed jobs (store retry count in DB)
- Add Redis/Celery only when DB queue becomes bottleneck

---

### Mobile App Backend Checklist

**Most relevant dimensions:** #7, #8, #14, #23, #24, #28, #29

**Key questions:**
1. **Latency (#7)**: What's acceptable response time? Can we start with 2-3 seconds?
2. **Concurrency (#8)**: How many active users? Can we start with simple backend?
3. **Stateless (#14)**: Can we make API stateless? Where should session state live?
4. **External deps (#23)**: Push notifications, analytics? Can we add these later?
5. **Security (#24)**: Do we need OAuth or is JWT sufficient? Can we start with simple auth?
6. **Cost (#28)**: What's the infrastructure budget? Can we start with single instance?
7. **Availability (#29)**: Do we need 99.9% uptime on day 1? Can we start with single instance?

**Common over-engineering patterns to avoid:**
- ❌ Multiple backend microservices for MVP
- ❌ Real-time features (WebSockets) when polling works
- ❌ Push notifications before you have active users
- ❌ CDN before measuring asset load times
- ❌ Multi-region deployment for local app

**Simplest viable approach:**
- Single backend API (monolith)
- PostgreSQL database
- JWT authentication
- Deploy on single cloud instance
- Add CDN, caching, scaling after measuring performance

---

### Real-Time / WebSocket System Checklist

**Most relevant dimensions:** #6, #7, #8, #14, #20, #29

**Key questions:**
1. **Frequency (#6)**: Do users really need real-time updates? Can we poll every 5-30 seconds?
2. **Latency (#7)**: Is <100ms necessary or is 1-2 seconds OK?
3. **Concurrency (#8)**: How many concurrent connections? Can we start with simple server?
4. **Stateful (#14)**: How do we manage connection state? Can we use sticky sessions?
5. **Inconsistency tolerance (#20)**: Can users tolerate stale data for seconds? Can we use polling?
6. **Availability (#29)**: What happens when WebSocket server fails? Can we fallback to polling?

**Common over-engineering patterns to avoid:**
- ❌ WebSockets when server-sent events (SSE) would work
- ❌ WebSockets when polling every 10 seconds is sufficient
- ❌ Message broker (Redis Pub/Sub) for simple broadcasting
- ❌ Horizontal scaling before testing single instance capacity
- ❌ Connection pooling/load balancing before you have load

**Simplest viable approach:**
- Start with polling (HTTP requests every 5-30 sec)
- If latency critical, use Server-Sent Events (SSE) first
- Only use WebSockets if bidirectional communication is required
- Single server instance (test capacity first)
- Add Redis Pub/Sub only when you need multi-server broadcasting

---

### Machine Learning / ML System Checklist

**Most relevant dimensions:** #3, #4, #6, #7, #27, #28

**Key questions:**
1. **Growth (#3)**: How fast will training data grow? Can we retrain manually initially?
2. **Processing weight (#4)**: How expensive is training/inference? Can we start with simple model?
3. **Frequency (#6)**: How often do we retrain? Can we retrain weekly/monthly manually?
4. **Latency (#7)**: Real-time inference or batch? Can we use batch predictions?
5. **Refactoring (#27)**: Can we change model architecture later? Start with simplest model?
6. **Cost (#28)**: What's the GPU/compute budget? Can we use pre-trained models?

**Common over-engineering patterns to avoid:**
- ❌ Complex deep learning when linear regression works
- ❌ Real-time inference when batch predictions sufficient
- ❌ MLOps pipeline when manual retraining works
- ❌ Model versioning/A/B testing before model is working
- ❌ GPU infrastructure when CPU is fast enough

**Simplest viable approach:**
- Start with simplest algorithm (linear/logistic regression, decision tree)
- Batch predictions (daily/hourly)
- Manual model training and deployment
- Use pre-trained models if available (transfer learning)
- Add complex ML only when simple approaches fail

---

## When to Add Complexity: Decision Criteria

Use these objective criteria to decide when to add complexity.

| Complexity | Add only when... | Typical threshold |
|------------|-----------------|-------------------|
| Caching (Redis) | Database queries >1s at p95 AND query optimization exhausted | Response time >1s |
| Microservices | Team >20 people AND clear bounded contexts exist | Team size >20 |
| Message queue (Kafka) | >10K events/second OR multiple consumers need events | >10K events/sec |
| NoSQL database | >10M records AND query patterns don't fit SQL | >10M records |
| Auto-scaling | Traffic varies >10× daily AND manual scaling too slow | 10× variance |
| Real-time (WebSockets) | Users need updates <5 seconds AND polling insufficient | <5 sec latency |
| Event sourcing | Need event replay OR temporal queries are critical | Regulatory requirement |
| Search (Elasticsearch) | >1M records AND database full-text search too slow | >1M records |
| Multi-region | >30% users in distant region AND latency >500ms | Latency >500ms |
| CDN | Static assets >1MB AND users globally distributed | Global users |

**Key principle:** Measure first, add complexity second. Don't add complexity based on anticipated future needs.

---

## Common Anti-Patterns

### Anti-Pattern 1: "Netflix/Google does it"

**Problem:** "Netflix uses microservices, so we should too"

**Reality:** Netflix has 1000s of engineers. You have 5. Context matters.

**Fix:** Choose technologies appropriate for your team size and scale.

---

### Anti-Pattern 2: "We might need to scale"

**Problem:** "We might get 1M users, so let's build for that now"

**Reality:** 95% of applications never reach high scale. Optimize for learning, not hypothetical scale.

**Fix:** Build for today's scale + 10×. Refactor when you actually hit limits.

---

### Anti-Pattern 3: "Resume-driven development"

**Problem:** "I want to learn Kafka, so let's use it"

**Reality:** Production systems should optimize for business value, not learning opportunities.

**Fix:** Learn new technologies in side projects. Use proven, simple technologies in production.

---

### Anti-Pattern 4: "Best practices require it"

**Problem:** "Best practices say we need comprehensive monitoring, so let's set up Datadog, Prometheus, Grafana, and PagerDuty"

**Reality:** Best practices are context-dependent. Small teams need different tools than large teams.

**Fix:** Start with simple logging. Add monitoring incrementally as needed.

---

*Adapted from [Skill Factory](https://github.com/lada-k/skill-factory) by Lada Kesseler (Apache 2.0)*
*30 Complexity Dimensions and Basal Cost concept by Eduardo Ferro — https://www.eferro.net/*
*Basal Cost of Software: https://www.eferro.net/2021/02/basal-cost-of-software.html*
