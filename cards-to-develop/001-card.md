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
