
## 🔗 task_dependencies
Defines blocking relationships between tasks (so “next actions” aren’t shown if something else must be done first).

| Column | Description |
|--------|-------------|
| **task_id** | The dependent task. |
| **depends_on_task_id** | The prerequisite task. |

**Example:** Task 3 depends on task 2, preventing it from appearing in `v_next_actions` until task 2 is complete.

---
