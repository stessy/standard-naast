---
schema: clarification/v1
generated_at: "2026-10-25T07:52:01Z"
scope: [frontend, backend, generic]
clarity_score: 0.848485
rounds: 0
gaps:
  - api.contract_preservation
  - auth.framework
  - nfr.sla
  - compliance.accessibility
  - compatibility.browsers
  - responsive.breakpoints
  - i18n.locales
  - state_mgmt.preference
  - routing.preference
  - existing_tests.posture
blocking_gaps: []
---

# Scenario Clarification

## Scenario & Evidence

- Classification: brownfield upgrade, in-place; feasibility supplied as feasible, not independently validated here.
- Working repository: `/Users/stessy/Projects/standard-naast`.
- Current branch observed: `master`; retain it. No commits or branch creation/switching.
- Profile: `artifacts/project-profile.yaml`. Scope is local-mode infrastructure, compatibility, Angular build integration, and desktop packaging—not a whole-repository rewrite.
- Manifest evidence: `standardnaast-ng/package.json` declares Angular `^19.2.25`, PrimeNG `^19.1.4`, existing Angular router, and existing build/test scripts. `standardnaast-backend/pom.xml` declares Spring Boot `4.1.1`, Java `25`, JPA, Spring Security, Liquibase, H2 and PostgreSQL.
- Declared versions are not proof of installed toolchains, dependency availability, compatibility, or passing builds. Preserve requested versions; downstream execution must verify them without silently downgrading.
- No prior clarification state, repository AGENTS.md, `.github/copilot-instructions.md`, rulebook directory, context, board, decisions, PM history/inbox, PM learnings, or fanout artifact was found in this inspection.

## Frontend

- **Target framework**: Retain Angular 19 and existing application; no frontend framework migration.
- **Component library**: Retain existing PrimeNG 19, theme, PrimeFlex and PrimeIcons dependencies. No replacement library.
- **Screenshots**: Omitted from scoring; no UI page reproduction or visual redesign is requested. Existing UI remains the reference.
- **Design system**: Preserve existing appearance and styling; packaging and relative-API changes do not authorize a redesign.
- **Accessibility**: WCAG 2.1 AA (default working target; compliance not audited here).
- **Browser targets**: Modern evergreen Chrome, Firefox, Safari and Edge, latest two major versions (default); default system browser is the LOCAL UI.
- **Responsive strategy**: Preserve existing breakpoints/layout; catalog default is mobile-first using existing detected breakpoints, not authorization for a layout rewrite.
- **i18n locales**: Preserve current locales and existing i18n library, if present (default).
- **State management**: Preserve existing pattern (default).
- **Routing**: Retain Angular router and route behavior; framework-default choice (default). SPA fallback must not replace API errors or missing static-resource responses with successful HTML.
- **Asset/API delivery**: Angular production assets compile into Maven output static resources; LOCAL requests use relative API URLs under the same application origin. Static serving must respect existing security requirements.

## Backend

- **Target framework**: Retain Spring Boot 4.1.1 and Java 25 JVM application as declared; GraalVM is future-only.
- **API contract preservation**: Must preserve existing routes, payloads, status codes and error behavior (safe default). This is a structural migration, not API redesign authorization.
- **Data migration strategy**: Separate new SQLite LOCAL database. Preserve ALL original H2/PostgreSQL data absolutely. No silent, destructive or automatic transfer. Transfer only through an explicit controlled operation with fail-safe preservation of originals; no overwrite/deletion of source databases or implicit cutover.
- **Persistence requirements**: Implement SQLite JDBC/Hibernate dialect support, compatible identifier generation, Liquibase types and foreign-key behavior while preserving legacy paths.
- **Auth framework**: Preserve existing authentication/authorization mechanism and detect its details from source (default); loopback binding is not permission to remove security.
- **SLA targets**: Match current baseline, no regression (default); no numeric SLA has been supplied.
- **LOCAL operation**: No operational dependence on Electron or PostgreSQL. Keep legacy modes/paths available.

## Generic

- **Success definition**: Implement the approved structural migration in the existing repository. LOCAL launches Angular plus Spring Boot JVM plus SQLite, from a jpackage distribution containing its runtime, without an end-user JRE download. Existing user-facing behavior and legacy paths remain preserved.
- **Packaging/lifecycle**: Provide macOS and Windows packaging scripts, external user data storage, loopback binding, browser launch only after readiness, single-instance behavior, and explicit shutdown.
- **Out of scope**: GraalVM implementation; visual redesign; replacement of retained frameworks; removal of legacy paths; destructive/in-place source-data migration; branch switching, branch creation and commits; unrelated cleanup; unrequested Markdown tracking files.
- **Existing test posture**: Must pass (safe default). Distinguish pre-existing failures from migration-caused failures and report both honestly.
- **Additional constraints**: Inspect and preserve all existing worktree changes. Read repository instructions and applicable mandatory skills; check/apply `.github/modernize/rulebook/` if it appears. Use `apply_patch` for edits. Minimize nested delegation except mandatory role tooling. No assessment or preliminary-plan approval round. Make safe choices without questions except indispensable blockers.
- **Execution tooling preference**: User requests `execution_subagent` for terminal/build/tests; downstream execution must discover whether it is available. If unavailable, use available execution tools and disclose the limitation. Availability was not tested by this clarification-only invocation.
- **Validation/reporting requirement for downstream execution**: Run focused validations, relevant builds and available runtime/package smoke tests. Do not claim untested platforms. Return actual implementation summary, changed files, exact commands/results, safeguards, failures, blockers and next steps.
- **Current invocation boundary**: Clarification workflow only. No production source edits, builds, tests, runtime launches, package creation or migration execution.
- **Upgrade location**: In-place is inherent; rewrite/extract output-location field is omitted.

## Gaps & Defaults Applied

All defaults are authorized by the user's instruction to use safe defaults rather than ask optional questions. No indispensable unresolved scenario decision was found.

| Field | Resolution | Value |
|---|---|---|
| api.contract_preservation | default | Must preserve |
| auth.framework | default | Preserve existing; detect from source |
| nfr.sla | default | Match current baseline; no regression |
| compliance.accessibility | default | WCAG 2.1 AA |
| compatibility.browsers | default | Modern evergreen, latest two majors |
| responsive.breakpoints | default | Existing breakpoints; preserve current layout |
| i18n.locales | default | Preserve current |
| state_mgmt.preference | default | Preserve existing pattern |
| routing.preference | default | Angular framework router |
| existing_tests.posture | default | Must pass; classify pre-existing failures |

### Score Evidence

Required decisions are resolved by supplied scope, existing manifest facts and expressly authorized safe defaults. No screenshot field applies because existing UI pages are not being reproduced or redesigned.

- Frontend: total weight 10.5; required fields and optional fields resolved, three recommended evidence gaps contribute half credit: 9 / 10.5 = 0.857143 (pass).
- Backend: total weight 6; required fields resolved, two recommended evidence gaps contribute half credit: 5 / 6 = 0.833333 (pass).
- Generic: required success definition present; upgrade output-location field omitted (pass).
- Overall numeric score: weighted mean of scored kits, 14 / 16.5 = 0.848485. Generic is binary and excluded from the numeric denominator. Defaulted recommended fields retain half credit rather than being presented as explicit user evidence.
- Rounds: zero; no question JSON needed. Blocking gaps: none. This is scenario readiness, not implementation feasibility verification.

## Upstream Artifacts Consumed

- `artifacts/project-profile.yaml` — classification, bounded upgrade scope, retained stack and preservation requirements.
- User-supplied classification, feasibility and scenario decisions — authoritative target and safety constraints.
- Existing package/POM manifests — current declared framework/component versions only.

## Evidence Mapping

- `artifacts/project-profile.yaml#assessment.change_type` → in-place upgrade; no rewrite-output question.
- `artifacts/project-profile.yaml#assessment.transformations` → retained Angular/Spring Boot JVM, SQLite LOCAL and embedded-runtime jpackage outcome.
- `artifacts/project-profile.yaml#project.structure` → preserve existing untracked desktop work and legacy Electron path.
- `artifacts/project-profile.yaml#project.notes` → bounded structural scope, no whole-repository rewrite or regrouping.

## Verification

- Read-only worktree inspection completed before artifact writing; existing modified, deleted and untracked paths were observed and not edited.
- Canonical artifact checked against the clarification template and supplied decisions.
- No source edits, builds or tests performed, as requested. No claim of working runtime, validated database migration, successful packaging or cross-platform behavior.

## Downstream Usage Notes

- Do not re-ask answered decisions or introduce an assessment/preliminary approval gate.
- Defaults are working assumptions, not claims of independently confirmed behavior.
- Runtime/toolchain compatibility and implementation defects remain execution concerns; report genuine blockers with exact evidence rather than substituting stack versions.
- Preserve all original data and user changes even if validation fails.
