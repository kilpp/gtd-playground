#!/usr/bin/env bash
set -euo pipefail

# Simple Podman launcher for a local MySQL container (development use only).
NAME="gtd-mysql"
DATA_DIR="$(pwd)/mysql-data"

# Create host data dir
mkdir -p "$DATA_DIR"

# Stop & remove existing container if present (safe restart)
if podman container exists "$NAME"; then
  podman rm -f "$NAME"
fi

# Run the container
podman run -d --name "$NAME" \
  -e MYSQL_ROOT_PASSWORD="rootpassword" \
  -e MYSQL_DATABASE="gtd" \
  -e MYSQL_USER="gtduser" \
  -e MYSQL_PASSWORD="gtdpass" \
  -p 3306:3306 \
  -v "$DATA_DIR":/var/lib/mysql:Z \
  docker.io/library/mysql:8.0

echo "MySQL container '$NAME' started. Data directory: $DATA_DIR"
# Dockerfile.mysql
# Minimal Dockerfile that uses the official MySQL image and copies optional init scripts.
# Place any .sql/.sh init files under ./docker/initdb/ in the project root to have them executed
# on first container startup.
FROM docker.io/library/mysql:8.0

# Default credentials and database (suitable for local development only)
ENV MYSQL_ROOT_PASSWORD=rootpassword
ENV MYSQL_DATABASE=gtd
ENV MYSQL_USER=gtduser
ENV MYSQL_PASSWORD=gtdpass

# Copy initialization scripts (if any). The official image executes scripts in this directory
# only on first initialization.
COPY docker/initdb/ /docker-entrypoint-initdb.d/

EXPOSE 3306
CMD ["mysqld"]

