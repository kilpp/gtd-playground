
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
