#!/usr/bin/env zsh

# ------------------------------
#  Library Management System
#  Database Startup Script
# ------------------------------

# Color definitions
RED='\033[0;31m'
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color
BOLD='\033[1m'

# Pretty print function
print_header() {
    echo "\n${BOLD}${BLUE}== $1 ==${NC}\n"
}

print_success() {
    echo "${GREEN}✓ $1${NC}"
}

print_info() {
    echo "${CYAN}ℹ $1${NC}"
}

print_separator() {
    echo "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
}

# Clean up existing database files
print_header "Database Cleanup"
echo "${YELLOW}🧹 Cleaning up old database files...${NC}"
rm -f db/Auth/*.db db/Auth/*.mv.db
rm -f db/Book/*.db db/Book/*.mv.db
rm -f db/Lending/*.db db/Lending/*.mv.db
rm -f db/Reader/*.db db/Reader/*.mv.db
print_success "Cleanup complete"

# Define the exact path to the H2 jar file
H2_JAR_PATH="/home/josef/Programming/IntellijProjects/SIDIS-LMS/db/h2-2.3.232.jar"

# Array to store PIDs of H2 processes
declare -a H2_PIDS

# Function to start an H2 instance with verbose output
start_h2_instance() {
    local service=$1
    local tcp_port=$2

    echo "${CYAN}📦 Starting ${BOLD}$service${NC}${CYAN}:${NC}"
    echo "   └─ TCP Port: ${YELLOW}$tcp_port${NC}"

    # Start H2 in-memory database (TCP only, no web interface)
    java -cp "$H2_JAR_PATH" org.h2.tools.Server \
        -tcp -tcpAllowOthers -tcpPort "$tcp_port" \
        -ifNotExists &

    local pid=$!
    H2_PIDS+=($pid)

    # Give it a moment to start
    sleep 0.5

    # Check if process is still running
    if ! kill -0 $pid 2>/dev/null; then
        echo "   ${RED}❌ Database failed to start${NC}"
        return 1
    fi
}

# Function to cleanup all H2 processes
cleanup() {
    print_separator
    echo "${YELLOW}🛑 Shutting down H2 databases${NC}"
    print_separator
    for pid in "${H2_PIDS[@]}"; do
        if kill -0 $pid 2>/dev/null; then
            echo "${CYAN}↪ Stopping process ${BOLD}$pid${NC}"
            kill $pid
            wait $pid
        fi
    done
    print_success "All databases stopped"
    exit 0
}

# Trap signals for cleanup
trap cleanup SIGINT SIGTERM

print_separator
print_header "Starting H2 Database Instances"
print_separator

# Start each service's databases
echo "${BOLD}${BLUE}🔐 Authentication Service${NC}"
start_h2_instance "auth-query-1" 9005 || exit 1
start_h2_instance "auth-query-2" 9006 || exit 1
start_h2_instance "auth-command-1" 9007 || exit 1
start_h2_instance "auth-command-2" 9008 || exit 1
echo

echo "${BOLD}${BLUE}📚 Book Service${NC}"
start_h2_instance "book-query-1" 9015 || exit 1
start_h2_instance "book-query-2" 9016 || exit 1
start_h2_instance "book-command-1" 9017 || exit 1
start_h2_instance "book-command-2" 9018 || exit 1
echo

echo "${BOLD}${BLUE}📋 Lending Service${NC}"
start_h2_instance "lending-query-1" 9025 || exit 1
start_h2_instance "lending-query-2" 9026 || exit 1
start_h2_instance "lending-command-1" 9027 || exit 1
start_h2_instance "lending-command-2" 9028 || exit 1
echo

echo "${BOLD}${BLUE}👤 Reader Service${NC}"
start_h2_instance "reader-query-1" 9035 || exit 1
start_h2_instance "reader-query-2" 9036 || exit 1
start_h2_instance "reader-command-1" 9037 || exit 1
start_h2_instance "reader-command-2" 9038 || exit 1
echo

print_separator
print_success "All H2 database instances are running and ready"
print_info "Press Ctrl+C to stop all instances"
print_separator

# Wait for all background processes
wait