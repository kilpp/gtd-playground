# 🧠 Getting Things Done (GTD): How It Works

**Getting Things Done (GTD)** is a productivity system created by **David Allen**, designed to help you achieve *stress-free productivity* by getting all tasks out of your head and into a trusted system.  
The process is cyclical and consists of **five key steps**.

---

## 1. Capture – Collect Everything

Anything that grabs your attention — ideas, tasks, emails, notes, goals — goes straight into your **Inbox**.

- **In your app:** captured as `tasks` with `status = 'inbox'`.
- **Purpose:** to free your mind from remembering and tracking open loops.
- **Examples:**
  - “Call the bank about my credit card.”
  - “Plan the team retreat.”
  - “Buy new running shoes.”

> Don’t decide what it means yet — just collect it.

---

## 2. Clarify – Decide What It Means

Go through each item in your Inbox and decide:
- Is it actionable?
  - **No:** trash it, store as **reference**, or move to **Someday/Maybe**.
  - **Yes:** decide the **next action**.

When you clarify:
- Define the **desired outcome** (the project goal).
- Define the **next physical action** required.

- **In your app:** update the task’s `status`:
  - `next` → Ready to do
  - `waiting` → You’re waiting on someone
  - `scheduled` → Do on/after a specific date
  - `someday` → No commitment yet
  - `reference` → For future info only

---

## 3. Organize – Put Things Where They Belong

Once clarified, you categorize your tasks so you can easily find and act on them.

- **Projects** → multi-step outcomes (`projects` table)
- **Contexts** → where/how tasks can be done (`contexts` table)
  - e.g., @Computer, @Home, @Errands
- **Areas** → life domains (`areas` table)
  - e.g., Health, Work, Family
- **Tags** → optional metadata for filtering
- **Tickler** → deferred tasks (`defer_until > NOW()`)

> Organizing is about giving every item a *home* in your system.

---

## 4. Reflect – Review Regularly

The **Weekly Review** is the heart of GTD. You check:
- Your **Inbox** — is everything clarified?
- All **Projects** — does each have a **next action**?
- Your **Waiting For** list — follow up if needed.
- Your **Someday/Maybe** list — promote what’s ready.
- Your **Calendar/Tickler** — upcoming commitments.

- **In your app:**
  - Use `v_active_projects_needing_next_action` to spot projects missing next steps.
  - Use `v_waiting_for` to see what’s blocked.
  - Use `v_tickler` for future reminders.

---

## 5. Engage – Do the Work

Now you trust your system.  
When it’s time to work, you look at your **Next Actions** list.

You decide what to do based on:
1. **Context** — where you are / what tools you have
2. **Time available**
3. **Energy available**
4. **Priority**

- **In your app:**
  - `v_next_actions` view = tasks ready to execute now
  - Filter by context or tag
  - Mark done when completed (`status = 'done'`)

---

## 🌀 The Continuous GTD Cycle

```text
Capture → Clarify → Organize → Reflect → Engage → (repeat)
```

Each step reinforces the others:
- You **capture** new inputs daily.
- You **clarify** and **organize** them regularly.
- You **reflect** weekly.
- You **engage** confidently — knowing everything’s under control.

---

## ⚙️ How It Fits Our System

| GTD Concept     | Database Table/View           | Purpose |
|-----------------|-------------------------------|----------|
| Inbox           | `v_inbox` / `tasks (status='inbox')` | Unprocessed items |
| Next Actions    | `v_next_actions`              | Do now |
| Waiting For     | `v_waiting_for`               | Blocked by others |
| Tickler / Scheduled | `v_tickler`              | Deferred |
| Projects        | `projects`                    | Multi-step goals |
| Someday/Maybe   | `v_someday_projects` / `v_someday_tasks` | Future possibilities |
| Reference       | `references_store`            | Info-only |
| Weekly Review   | `v_active_projects_needing_next_action` | Quality control |
| Contexts/Areas  | `contexts`, `areas`           | Organize & filter |
| Tags            | `tags`, `task_tags`           | Flexible metadata |

---
