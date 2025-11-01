
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

