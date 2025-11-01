# GTD Database Model — Explanations

This schema is a relational implementation of **Getting Things Done (GTD)**.  
It captures tasks, projects, contexts, areas, and supporting materials, following the GTD workflow:  
**Capture → Clarify → Organize → Review → Do.**

The schema, combined with example inserts, demonstrates how each GTD component maps into relational tables.

---

## 🧍 users
Stores information about the system’s users.

| Column | Description |
|--------|-------------|
| **id** | Primary key. Identifies the user. |
| **email** | Unique email for authentication or identification. |
| **name** | Full name of the user. |
| **created_at** | Timestamp when the user was added to the system. |

---

## 🏷️ contexts
Represents GTD **contexts** such as `@Home`, `@Office`, or `@Phone` — environments or tools needed to perform an action.

| Column | Description |
|--------|-------------|
| **id** | Primary key. |
| **user_id** | Foreign key to `users.id`. Each context belongs to a user. |
| **name** | Context name (e.g., `@Phone`). |
| **description** | Optional extended explanation of when this context applies. |
| **is_location** | Boolean flag (1 if it’s a physical location). |
| **created_at** | When the context was created. |

**Example:**  
`@Home`, `@Office`, `@Computer`, `@Phone`, and `@Errands` contexts for Alex.

---

## ⚙️ areas
Represents **areas of responsibility** (GTD concept). These are ongoing aspects of life, such as Work, Health, or Family.

| Column | Description |
|--------|-------------|
| **id** | Primary key. |
| **user_id** | Owner of this area. |
| **name** | Area title (e.g., “Health”). |
| **description** | A note describing its purpose or scope. |

**Example:**  
Health, Family, and Work are Alex’s three areas.

---

## 📁 projects
Tracks any goal or outcome requiring more than one step.

| Column | Description |
|--------|-------------|
| **id** | Primary key. |
| **user_id** | Owner of the project. |
| **area_id** | Links to `areas.id`. Organizes projects under an area. |
| **title** | Short project title. |
| **outcome** | Desired result — “what done looks like.” |
| **notes** | Supporting notes or materials. |
| **status** | `active`, `on_hold`, `someday`, `completed`, or `dropped`. |
| **due_date** | Target completion date. |
| **created_at** | When the project was created. |
| **completed_at** | When it was finished. |

**Example:**  
- “Redesign company website” (active, Work area)  
- “Run a half marathon” (active, Health area)  
- “Family vacation plan” (someday, Family area)

---

## ✅ tasks
Central table for all actionable and reference items. Each task’s `status` determines its GTD list.

| Column | Description |
|--------|-------------|
| **id** | Primary key. |
| **user_id** | Task owner. |
| **project_id** | Optional FK to `projects.id`. |
| **context_id** | Optional FK to `contexts.id`. |
| **title** | Short, actionable description. |
| **notes** | Extended details or reference. |
| **status** | Defines GTD category: `inbox`, `next`, `waiting`, `scheduled`, `someday`, `reference`, `done`, `dropped`. |
| **priority** | Optional numeric priority (1 = high). |
| **energy** | Estimated effort/energy (1–5). |
| **duration_est_min** | Estimated time in minutes. |
| **due_at** | Deadline or scheduled time. |
| **defer_until** | “Tickler” or start date (when it becomes actionable). |
| **waiting_on** | Who the task depends on. |
| **waiting_since** | When delegation occurred. |
| **created_at** | Creation timestamp. |
| **completed_at** | Completion timestamp. |
| **order_index** | Manual ordering field. |

**Example:**  
- “Buy new running shoes” → inbox item.  
- “Gather website content from marketing” → next action (`@Computer`).  
- “Waiting for design approval from manager” → waiting-for list.  
- “Research car rentals in Rome” → someday.  
- “Call dentist to reschedule appointment” → scheduled.  

---

## 🔗 task_dependencies
Defines blocking relationships between tasks (so “next actions” aren’t shown if something else must be done first).

| Column | Description |
|--------|-------------|
| **task_id** | The dependent task. |
| **depends_on_task_id** | The prerequisite task. |

**Example:** Task 3 depends on task 2, preventing it from appearing in `v_next_actions` until task 2 is complete.

---

## 🏷️ tags
Flexible keywords for grouping tasks across projects and contexts.

| Column | Description |
|--------|-------------|
| **id** | Primary key. |
| **user_id** | Owner of the tag. |
| **name** | Tag label (unique per user). |

**Example:** “Work”, “Health”, “Quick”, and “Follow-up” tags.

---

## 🗂️ task_tags
Join table connecting tasks and tags (many-to-many).

| Column | Description |
|--------|-------------|
| **task_id** | FK → tasks.id |
| **tag_id** | FK → tags.id |

**Example:** Task 2 tagged “Work”, task 4 tagged “Health”, task 7 tagged “Quick”.

---

## 📚 references_store
Non-actionable items to keep for reference.

| Column | Description |
|--------|-------------|
| **id** | Primary key. |
| **user_id** | Owner. |
| **title** | Reference name. |
| **body** | Text content. |
| **url** | External resource link. |
| **file_hint** | Local file path or external reference ID. |
| **created_at** | When the item was stored. |

**Example:** “GTD Weekly Review Checklist” and “Half Marathon Training Plan.”

---

## 👀 Views
Predefined filters matching GTD lists.

| View | Description |
|------|-------------|
| **v_inbox** | Tasks in the Inbox (`status='inbox'`). |
| **v_next_actions** | Available next actions — not deferred or blocked. |
| **v_waiting_for** | Items delegated or awaiting response. |
| **v_someday_tasks** | Tasks postponed indefinitely. |
| **v_someday_projects** | Projects not being pursued now. |
| **v_scheduled** | Time-specific or calendar-like actions. |
| **v_tickler** | Deferred actions that will become available later. |
| **v_active_projects_needing_next_action** | Active projects missing a next action (used during Weekly Review). |

---

**Summary:**  
This GTD schema provides a complete relational model for managing **projects**, **actions**, **contexts**, and **reference material**, making it ideal for building apps or personal productivity dashboards.
