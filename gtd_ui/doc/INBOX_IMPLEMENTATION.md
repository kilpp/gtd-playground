# GTD Inbox Implementation

This implementation provides the complete GET functionality for tasks in the inbox for both the backend service and Flutter UI.

## Backend (gtd-service)

### API Endpoints

#### Get All Tasks with Filters
```
GET /api/tasks
```

Query Parameters:
- `userId` (optional): Filter tasks by user ID
- `status` (optional): Filter tasks by status (e.g., "inbox", "next", "waiting", "scheduled", "someday", "reference", "done", "dropped")
- `projectId` (optional): Filter tasks by project ID
- `contextId` (optional): Filter tasks by context ID

**Note:** You can now combine `userId` and `status` parameters together:
- `GET /api/tasks?status=inbox` - Get all inbox tasks
- `GET /api/tasks?userId=1&status=inbox` - Get inbox tasks for user ID 1

#### Example Response
```json
[
  {
    "id": 1,
    "userId": 1,
    "projectId": null,
    "contextId": null,
    "title": "Review project proposal",
    "notes": "Check the budget section carefully",
    "status": "inbox",
    "priority": 1,
    "energy": 3,
    "durationEstMin": 30,
    "dueAt": "2025-12-20T10:00:00Z",
    "deferUntil": null,
    "waitingOn": null,
    "waitingSince": null,
    "createdAt": "2025-12-18T08:30:00Z",
    "completedAt": null,
    "orderIndex": 1
  }
]
```

### New Methods Added

#### TaskRepository
- `findByUserIdAndStatus(Long userId, String status)` - Find tasks by both user ID and status

#### TaskService & TaskServiceImpl
- `findByUserIdAndStatus(Long userId, String status)` - Service layer method for combined filtering

#### TaskController
- Updated to support combined `userId` and `status` query parameters

## Frontend (gtd_ui)

### Project Structure
```
lib/
├── main.dart                      # App entry point with navigation
├── models/
│   └── task.dart                  # Task model class
├── services/
│   └── task_service.dart          # HTTP service for task API
└── screens/
    └── inbox_screen.dart          # Inbox UI screen
```

### Key Features

#### 1. Task Model (`lib/models/task.dart`)
- Complete Task class with all GTD properties
- JSON serialization/deserialization
- Type-safe with proper null handling

#### 2. Task Service (`lib/services/task_service.dart`)
- `getInboxTasks()` - Fetch all inbox tasks
- `getTasksByStatus(String status)` - Fetch tasks by any status
- `getTasksByUserId(int userId)` - Fetch tasks for a specific user
- `getTaskById(int id)` - Fetch single task
- `createTask(Map<String, dynamic>)` - Create new task
- `updateTask(int id, Map<String, dynamic>)` - Update existing task
- `deleteTask(int id)` - Delete task

**Configuration:** Update the `baseUrl` in `task_service.dart` to match your backend URL:
```dart
static const String baseUrl = 'http://localhost:8080/api/tasks';
```

#### 3. Inbox Screen (`lib/screens/inbox_screen.dart`)
Features:
- Pull-to-refresh functionality
- Loading state with spinner
- Error handling with retry button
- Empty state with friendly message
- Task cards showing:
  - Title and notes
  - Priority indicator (with color coding)
  - Energy level
  - Estimated duration
- Task actions menu (edit/delete)
- Delete confirmation dialog
- Floating action button for creating new tasks

### UI Components

#### Task Card Display
Each task shows:
- Icon badge with inbox symbol
- Task title (bold)
- Notes (if available, max 2 lines)
- Metadata row:
  - Priority flag (P1, P2, P3) with color coding
  - Energy level (bolt icon)
  - Duration estimate in minutes (clock icon)

#### Color Coding
- Priority 1: Red
- Priority 2: Orange
- Priority 3: Yellow
- Other priorities: Default color

### Navigation
Two entry points to the Inbox screen:
1. Click "Inbox" feature card on landing page
2. Click "Get Started" button on landing page

## Setup Instructions

### Backend Setup
1. Ensure MySQL/MariaDB is running with the GTD schema
2. Start the Spring Boot service:
   ```bash
   cd gtd-service
   ./gradlew bootRun
   ```
3. Service will be available at `http://localhost:8080`

### Frontend Setup
1. Install Flutter dependencies:
   ```bash
   cd gtd_ui
   flutter pub get
   ```

2. Update the API base URL in `lib/services/task_service.dart` if your backend runs on a different host/port

3. Run the Flutter app:
   ```bash
   flutter run
   ```

### For Android Emulator
If running on Android emulator, use `http://10.0.2.2:8080` instead of `http://localhost:8080` to access the host machine.

### For iOS Simulator
Use `http://localhost:8080` or your machine's IP address.

### For Web
Use `http://localhost:8080` or configure CORS in the backend.

## Testing the Implementation

1. **Start the backend service** and ensure it's running
2. **Add some test data** to the database with status='inbox'
3. **Run the Flutter app**
4. **Navigate to Inbox** by clicking the Inbox card or Get Started button
5. **Verify the tasks load** and display correctly
6. **Test pull-to-refresh** by pulling down on the list
7. **Test delete functionality** by tapping the menu and selecting delete

## Example Test Data (SQL)
```sql
INSERT INTO gtd.tasks (user_id, title, notes, status, priority, energy, duration_est_min, created_at)
VALUES 
(1, 'Review project proposal', 'Check the budget section carefully', 'inbox', 1, 3, 30, NOW()),
(1, 'Call dentist for appointment', NULL, 'inbox', 2, 1, 5, NOW()),
(1, 'Research new framework options', 'Compare React vs Vue vs Angular', 'inbox', 3, 4, 120, NOW());
```

## Next Steps
- Implement task creation form
- Implement task edit functionality
- Add task detail view
- Implement other GTD lists (Next Actions, Waiting, etc.)
- Add filtering and sorting options
- Implement search functionality
- Add user authentication
