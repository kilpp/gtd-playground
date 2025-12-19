# GTD Inbox Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                         Flutter UI (gtd_ui)                      │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  ┌──────────────┐    ┌──────────────┐    ┌────────────────┐   │
│  │ LandingPage  │───>│ InboxScreen  │───>│ (Future screens)│   │
│  │              │    │              │    │                 │   │
│  │ - Get Started│    │ - Task List  │    │ - Create Task   │   │
│  │ - Features   │    │ - Pull Refresh│   │ - Edit Task     │   │
│  └──────────────┘    │ - Delete     │    │ - Task Details  │   │
│                      └──────┬───────┘    └────────────────┘   │
│                             │                                   │
│                             │ uses                              │
│                             ▼                                   │
│                    ┌─────────────────┐                         │
│                    │  TaskService    │                         │
│                    │                 │                         │
│                    │ - getInboxTasks │                         │
│                    │ - createTask    │                         │
│                    │ - updateTask    │                         │
│                    │ - deleteTask    │                         │
│                    └────────┬────────┘                         │
│                             │                                   │
│                             │ HTTP                              │
│                             │                                   │
└─────────────────────────────┼───────────────────────────────────┘
                              │
                              │ GET /api/tasks?status=inbox
                              │ POST /api/tasks
                              │ PUT /api/tasks/{id}
                              │ DELETE /api/tasks/{id}
                              │
┌─────────────────────────────▼───────────────────────────────────┐
│                    Spring Boot Service (gtd-service)             │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │               TaskController                              │  │
│  │                                                           │  │
│  │  @GetMapping                                             │  │
│  │  list(userId, projectId, contextId, status)             │  │
│  │    ├─> Supports: ?status=inbox                          │  │
│  │    ├─> Supports: ?userId=1                              │  │
│  │    └─> Supports: ?userId=1&status=inbox                 │  │
│  │                                                           │  │
│  │  @GetMapping("/{id}")                                    │  │
│  │  @PostMapping                                            │  │
│  │  @PutMapping("/{id}")                                    │  │
│  │  @DeleteMapping("/{id}")                                 │  │
│  └───────────────────────┬───────────────────────────────────┘  │
│                          │                                       │
│                          ▼                                       │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │               TaskService / TaskServiceImpl               │  │
│  │                                                           │  │
│  │  - findByStatus(status)                                  │  │
│  │  - findByUserId(userId)                                  │  │
│  │  - findByUserIdAndStatus(userId, status) [NEW]          │  │
│  │  - findByProjectId(projectId)                            │  │
│  │  - findByContextId(contextId)                            │  │
│  │  - create(CreateTaskDto)                                 │  │
│  │  - update(id, CreateTaskDto)                             │  │
│  │  - delete(id)                                            │  │
│  └───────────────────────┬───────────────────────────────────┘  │
│                          │                                       │
│                          ▼                                       │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │               TaskRepository                              │  │
│  │                                                           │  │
│  │  - findAll()                                             │  │
│  │  - findByUserId(userId)                                  │  │
│  │  - findByStatus(status)                                  │  │
│  │  - findByUserIdAndStatus(userId, status) [NEW]          │  │
│  │  - findByProjectId(projectId)                            │  │
│  │  - findByContextId(contextId)                            │  │
│  │  - findById(id)                                          │  │
│  │  - create(CreateTaskDto)                                 │  │
│  │  - update(id, CreateTaskDto)                             │  │
│  │  - delete(id)                                            │  │
│  └───────────────────────┬───────────────────────────────────┘  │
│                          │                                       │
│                          │ JDBC                                  │
│                          ▼                                       │
└─────────────────────────────────────────────────────────────────┘
                          │
┌─────────────────────────▼───────────────────────────────────────┐
│                      MySQL Database                              │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │  gtd.tasks                                                │  │
│  │                                                           │  │
│  │  Columns:                                                 │  │
│  │  - id (PK)                                                │  │
│  │  - user_id (FK -> users.id)                              │  │
│  │  - project_id (FK -> projects.id)                        │  │
│  │  - context_id (FK -> contexts.id)                        │  │
│  │  - title                                                  │  │
│  │  - notes                                                  │  │
│  │  - status ('inbox', 'next', 'waiting', etc.)             │  │
│  │  - priority                                               │  │
│  │  - energy                                                 │  │
│  │  - duration_est_min                                       │  │
│  │  - due_at                                                 │  │
│  │  - defer_until                                            │  │
│  │  - waiting_on                                             │  │
│  │  - waiting_since                                          │  │
│  │  - created_at                                             │  │
│  │  - completed_at                                           │  │
│  │  - order_index                                            │  │
│  └──────────────────────────────────────────────────────────┘  │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘


Data Flow for Getting Inbox Tasks:
═════════════════════════════════

1. User clicks "Inbox" or "Get Started" button
   ↓
2. Navigator pushes InboxScreen
   ↓
3. InboxScreen.initState() calls _loadInboxTasks()
   ↓
4. TaskService.getInboxTasks() makes HTTP GET request
   GET http://localhost:8080/api/tasks?status=inbox
   ↓
5. TaskController.list(status="inbox") receives request
   ↓
6. TaskService.findByStatus("inbox") is called
   ↓
7. TaskRepository.findByStatus("inbox") queries database
   SELECT * FROM gtd.tasks WHERE status = 'inbox'
   ↓
8. Results mapped to Task objects
   ↓
9. Converted to TaskDto objects
   ↓
10. JSON response returned to Flutter app
   ↓
11. TaskService parses JSON to Task objects
   ↓
12. InboxScreen setState() updates UI
   ↓
13. ListView displays task cards


Query Examples:
══════════════

Get all inbox tasks:
  GET /api/tasks?status=inbox

Get inbox tasks for specific user:
  GET /api/tasks?userId=1&status=inbox

Get all tasks for a user:
  GET /api/tasks?userId=1

Get tasks by project:
  GET /api/tasks?projectId=5

Get tasks by context:
  GET /api/tasks?contextId=2
```
