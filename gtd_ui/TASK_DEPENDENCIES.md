# Task Dependencies Feature

## Overview
Task dependencies allow you to manage relationships between tasks in your GTD workflow. You can specify which tasks must be completed before others can be started, helping you maintain proper sequencing in your projects.

## Features Implemented

### 1. Task Dependency Model
- **File**: `lib/models/task_dependency.dart`
- Simple model representing a dependency relationship between two tasks
- Fields:
  - `taskId`: The task that has a dependency
  - `dependsOnTaskId`: The task that must be completed first

### 2. Task Dependency Service
- **File**: `lib/services/task_dependency_service.dart`
- API integration with backend dependency endpoints
- Methods:
  - `getAllDependencies()`: Get all dependencies in the system
  - `getDependenciesForTask(taskId)`: Get tasks that a specific task depends on
  - `getBlockingDependencies(dependsOnTaskId)`: Get tasks that are blocked by a specific task
  - `createDependency()`: Add a new dependency relationship
  - `deleteDependency()`: Remove a specific dependency
  - `deleteDependenciesForTask()`: Remove all dependencies for a task

### 3. Task Detail Screen
- **File**: `lib/screens/task_detail_screen.dart`
- Comprehensive view showing task information and dependencies
- Two dependency sections:
  - **Dependencies**: Tasks that must be completed before this task
  - **Blocking Tasks**: Tasks that are waiting for this task to complete
- Features:
  - Add new dependencies via search dialog
  - Remove existing dependencies
  - Visual indicators showing task completion status
  - Searchable task selection dialog

### 4. Enhanced Inbox Screen
- **File**: `lib/screens/inbox_screen.dart`
- Visual badges showing:
  - Orange link icon with count for tasks with dependencies
  - Red lock icon with count for tasks blocking other tasks
- Tooltips on hover explaining the counts
- Click on any task to view full dependency details

## Usage

### Viewing Dependencies
1. Open the Inbox screen
2. Look for tasks with orange link badges (has dependencies) or red lock badges (blocking other tasks)
3. Tap on any task to see detailed dependency information

### Adding a Dependency
1. Open a task by tapping it in the Inbox
2. In the Dependencies section, tap the + button
3. Search for and select the task that must be completed first
4. The dependency will be created immediately

### Removing a Dependency
1. Open a task with dependencies
2. In the Dependencies section, tap the delete icon next to the dependency
3. The dependency will be removed immediately

## API Endpoints Used

- `GET /api/task-dependencies?taskId={id}` - Get dependencies for a task
- `GET /api/task-dependencies?dependsOnTaskId={id}` - Get blocking tasks
- `POST /api/task-dependencies` - Create a new dependency
- `DELETE /api/task-dependencies/{taskId}/{dependsOnTaskId}` - Remove a dependency
- `DELETE /api/task-dependencies/task/{taskId}` - Remove all dependencies for a task

## Backend Integration

The backend service already supports task dependencies with:
- Database table: `task_dependencies`
- Constraint preventing self-dependencies
- Cascade delete when tasks are removed
- Validation of dependency relationships

## Future Enhancements

Possible improvements:
- Circular dependency detection in UI
- Dependency graph visualization
- Smart task ordering based on dependencies
- Warnings when completing tasks that block others
- Bulk dependency management
- Dependency templates for common task patterns
