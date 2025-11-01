
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

