**File:** `tasks/in-progress/004-release-4-4-1-jitpack.md`
**Status:** in-progress
**Source:** manager direct order (release 4.4.1)
**Type:** chore
**Seat:** Senior Programmer
**Mode:** manual

# Task 004: Release 4.4.1 — README, full worktree commit, JitPack

## Goal

Point README badges/examples at the fork with 4.4.1, un-ignore `docs/` so the whole worktree can commit, stage everything, and prepare the 4.4.1 tag for JitPack. Commit on explicit close word; push + tag push by Manager (push denied to executor).

## Context

- 4 local commits ahead of `origin/master`, nothing pushed (`git log origin/master..HEAD` = 4).
- `gradle.properties` already 4.4.1 (task 003). JitPack builds `com.github.mokhtarabadi.Parse-SDK-Android:<module>:4.4.1` from the tag.
- `.gitignore:37` ignores `docs/` (upstream cruft); our `docs/` holds only 3 new spec files.
- Worktree remainder: 4 pre-existing edits, `.opencode/`, ignored `docs/`.

## Local TODOs

- [ ] README: retarget JitPack badges to fork, note 4.4.1
- [ ] Remove `docs/` line from `.gitignore`
- [ ] Stage entire worktree via stage tool, inject diff
- [ ] Tag `4.4.1` only on approval; push by Manager

## Acceptance Criteria

- [ ] README names fork coordinates + 4.4.1
- [ ] `git status` shows no unintended omissions
- [ ] Task lint passes

## Risk & Rollback

- Risk: medium (release commit + public tag). Rollback: `git tag -d 4.4.1`, reset commit; no push yet so no remote damage.

## Verification Evidence

- Command: `rtk test ./gradlew :parse:assembleDebug --no-daemon`
- Expected: exit 0. Actual: _pending_. Exit code: _pending_.

## Execution Log & Reasoning

- Next: move to in-progress, edit README + .gitignore.

## Factual Git Diff

<!-- BEGIN_GIT_DIFF -->
<!-- END_GIT_DIFF -->
