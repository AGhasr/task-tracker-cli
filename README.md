# Task Tracker CLI

A simple command-line task tracker built in Java. Tasks are stored in a local JSON file (`tasks.json`) and can be added, updated, deleted, or listed by status.

## Usage

### Add a task
```bash
java TaskTracker add "Buy groceries"
```

### Update a task
```bash
java TaskTracker update 1 "Buy groceries and cook dinner"
```

### Delete a task
```bash
java TaskTracker delete 1
```

### Mark a task as in-progress
```bash
java TaskTracker mark-in-progress 1
```

### Mark a task as done
```bash
java TaskTracker mark-done 1
```

### List all tasks
```bash
java TaskTracker list
```

### List tasks by status
```bash
# List todo tasks
java TaskTracker list todo

# List in-progress tasks
java TaskTracker list in-progress

# List completed tasks
java TaskTracker list done
```

## Task Properties

Each task contains the following properties:
- **ID**: Unique identifier (auto-generated)
- **Description**: Task description
- **Status**: `todo`, `in-progress`, or `done`
- **Created At**: Timestamp when task was created
- **Updated At**: Timestamp when task was last modified


## Possible Future Features

- Search tasks by keyword
- Support for priorities (high/medium/low)
- Due dates and reminders
- Task completion statistics



