# Bruno API Tests for GTD Service

This directory contains Bruno API automation tests for the GTD Service APIs.

## Setup

1. Install Bruno CLI:
   ```
   npm install -g @usebruno/cli
   ```

2. Start the GTD Service application on localhost:8080 (default port).

3. Run the tests:
   ```
   bru run --env dev
   ```

   Or to generate a report:
   ```
   bru run --env dev --output results.html
   ```

## Test Structure

- `bruno.json`: Collection configuration
- `environments/dev.bru`: Environment variables (host = http://localhost:8080)
- Various `.bru` files: Individual API requests with tests

Tests cover:
- Users: CRUD operations
- Contexts: CRUD operations, list by user
- Areas: CRUD operations, list by user

## Notes

- Tests assume the app is running with an empty database initially.
- Tests create and delete resources to clean up.
- Uses JavaScript assertions in the Tests tab for validation.
