## User Input

Execute the approved structural migration directly and persist implementation in /Users/stessy/Projects/standard-naast: local Angular + Spring Boot JVM + SQLite, Angular compiled into Maven output static assets, jpackage embedded runtime, default system-browser UI, removing operational dependence on Electron/PostgreSQL for LOCAL mode while preserving legacy paths. No assessment/preliminary plan approval; no commits or branch switching. Inspect worktree first, preserve all existing user changes, read repository instructions/applicable mandatory skills, use apply_patch for edits, minimize nested delegation except mandatory role tooling. Preserve ALL original H2/Postgres data absolutely; no silent/destructive migration; transfer only explicit controlled to new DB with fail-safe preservation. Implement SQLite JDBC/Hibernate dialect, IDs/Liquibase types/foreign keys; SPA fallback must preserve API errors, relative APIs/static security; macOS/Windows packaging scripts, external user data, loopback binding, browser after readiness, single-instance and explicit shutdown; no end-user JRE downloads. GraalVM future only. User requests execution_subagent for terminal/build/tests; if unavailable use available execution tools and report limitation. No unrequested Markdown tracking files (direct product docs allowed). Make safe choices without questions except indispensable blockers. Run focused validations, relevant builds and available runtime/package smoke tests; never claim untested platforms. Return precise structured actual implementation summary, changed files, commands/results, safeguards, failures, blockers and next steps. Backend reportedly Boot4.1.1 Java25 JPA/Hibernate/Liquibase/H2/Postgres/native profile; Angular19; existing build-desktop.sh/.bat Electron. BRANCH: retain current branch, do not create/switch or commit. Check/apply .github/modernize/rulebook/ if present. This is the sole structural migration execution delegation; implement rather than only plan.

**Project started**: 2026-10-25T07:50:56Z

## State

Full DAG generated using dag-generation Stage 1; deep_planning=false.
Clarification READY; no blocking gaps.
Grouping: none; bounded LOCAL-mode change, not a full repository rewrite.
Execution mode: saved, pending required execution-mode checkpoint.
Branch: master. Preserve all existing user changes and data.
No commits, staging, branch switching, destructive data access, or implicit legacy data transfer authorized.
execution_subagent unavailable; workers must use available execution tools and disclose this limitation.
No source implementation, build, test, runtime or package validation has started.
Skill-based coarse whole-repository source-line count: Java 55189; JavaScript/TypeScript 347494; includes legacy/generated content.
Rulebook path was not present in initial existence check; workers must read applicable repository instructions and recheck rulebook applicability.

## Tasks

### Phase 0: Architecture and Target Environment
- ⏳ t1 [architect] Inspect existing local architecture and preservation boundaries; define integration contracts
- ⏳ t2 [devops] Prepare and verify actual Java25, Maven, Angular19 and jpackage target environment

### Phase 1: Data, Client and Validation Strategy
- ⏳ t3 [dba] Model and implement isolated SQLite JDBC/Hibernate IDs, Liquibase types and foreign keys with fail-safe legacy-data preservation [deps: t1, t2]
- ⏳ t4 [frontend] Preserve Angular UI and implement relative LOCAL API integration and static-build compatibility [deps: t1, t2]
- ⏳ t5 [teamlead] Define focused migration and package validation strategy from approved scope [deps: t1]

### Phase 2: Local JVM Application
- ⏳ t6 [backend] Implement loopback LOCAL startup, safe SPA routing/security, readiness browser launch, single-instance and explicit shutdown [deps: t3]

### Phase 3: Embedded Runtime Packaging
- ⏳ t7 [devops] Integrate Angular into Maven output and macOS/Windows jpackage scripts with embedded runtime and external data; preserve legacy launch paths [deps: t4, t6]

### Phase 4: Dependency Verification
- ⏳ t8 [backend] Scan changed dependency manifests and remediate CVEs via skill(cve-remediation) [deps: t7]

### Phase 5: Independent Review and Smoke
- ⏳ t9 [architect] Run relevant complete local build and startup/package smoke-test; report exact command evidence [deps: t8]
- ⏳ t10 [security] Audit LOCAL static/API boundaries, lifecycle control and database protection [deps: t8]

### Phase 6: Runtime Validation
- ⏳ t11 [tester] Execute SQLite, API/SPA, lifecycle, regression and available macOS package runtime validation; mark Windows unverified unless executed [deps: t5, t9, t10]

### Phase 7: Conformance and Completion
- ⏳ t12 [teamlead] Verify conformance, evidence, safeguards, changed-file accounting and remaining platform blockers [deps: t11]

## DAG

{"deep_planning":false,"tasks":[{"id":"t1","role":"architect","title":"Inspect existing local architecture and preservation boundaries; define integration contracts","depends_on":[],"phase_label":"Architecture and Target Environment","model":"default"},{"id":"t2","role":"devops","title":"Prepare and verify actual Java25, Maven, Angular19 and jpackage target environment","depends_on":[],"phase_label":"Architecture and Target Environment","model":"default"},{"id":"t3","role":"dba","title":"Model and implement isolated SQLite JDBC/Hibernate IDs, Liquibase types and foreign keys with fail-safe legacy-data preservation","depends_on":["t1","t2"],"phase_label":"Data, Client and Validation Strategy","model":"default"},{"id":"t4","role":"frontend","title":"Preserve Angular UI and implement relative LOCAL API integration and static-build compatibility","depends_on":["t1","t2"],"phase_label":"Data, Client and Validation Strategy","model":"default"},{"id":"t5","role":"teamlead","title":"Define focused migration and package validation strategy from approved scope","depends_on":["t1"],"phase_label":"Data, Client and Validation Strategy","model":"default"},{"id":"t6","role":"backend","title":"Implement loopback LOCAL startup, safe SPA routing/security, readiness browser launch, single-instance and explicit shutdown","depends_on":["t3"],"phase_label":"Local JVM Application","model":"default"},{"id":"t7","role":"devops","title":"Integrate Angular into Maven output and macOS/Windows jpackage scripts with embedded runtime and external data; preserve legacy launch paths","depends_on":["t4","t6"],"phase_label":"Embedded Runtime Packaging","model":"default"},{"id":"t8","role":"backend","title":"Scan changed dependency manifests and remediate CVEs via skill(cve-remediation)","depends_on":["t7"],"phase_label":"Dependency Verification","model":"default"},{"id":"t9","role":"architect","title":"Run relevant complete local build and startup/package smoke-test; report exact command evidence","depends_on":["t8"],"phase_label":"Independent Review and Smoke","model":"default"},{"id":"t10","role":"security","title":"Audit LOCAL static/API boundaries, lifecycle control and database protection","depends_on":["t8"],"phase_label":"Independent Review and Smoke","model":"default"},{"id":"t11","role":"tester","title":"Execute SQLite, API/SPA, lifecycle, regression and available macOS package runtime validation; mark Windows unverified unless executed","depends_on":["t5","t9","t10"],"phase_label":"Runtime Validation","model":"default"},{"id":"t12","role":"teamlead","title":"Verify conformance, evidence, safeguards, changed-file accounting and remaining platform blockers","depends_on":["t11"],"phase_label":"Conformance and Completion","model":"default"}]}

## Hook Verification

before_all floor-check: PASS. Dependency manifests will change; t8 explicitly schedules cve-remediation. Conformance rollup scheduled as t12. No feature inventory warranted for preserved same-stack UI.
No git hooks executed; before_all registry contains only floor-check. User forbids commits and branch changes.
