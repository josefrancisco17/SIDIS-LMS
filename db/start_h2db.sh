#!/usr/bin/env zsh

# Clean up existing database files
echo "🧹 Cleaning up old database files..."
rm -f db/Auth/*.db db/Auth/*.mv.db
rm -f db/Book/*.db db/Book/*.mv.db
rm -f db/Lending/*.db db/Lending/*.mv.db
rm -f db/Reader/*.db db/Reader/*.mv.db
echo "✅ Cleanup complete"

# Define the exact path to the H2 jar file
H2_JAR_PATH="/home/josef/Programming/IntellijProjects/SIDIS-LMS/db/h2-2.3.232.jar"

# Array to store PIDs of H2 processes
declare -a H2_PIDS

# Function to print a separator line
print_separator() {
    echo "----------------------------------------"
}

# Function to start an H2 instance with verbose output
start_h2_instance() {
    local service=$1
    local tcp_port=$2
    local web_port=$3

    echo "📦 Starting $service:"
    echo "   ├─ TCP Port: $tcp_port"
    echo "   └─ Web Port: $web_port"

    # Start H2 in-memory database
    java -cp "$H2_JAR_PATH" org.h2.tools.Server \
        -web -webAllowOthers -webPort "$web_port" \
        -tcp -tcpAllowOthers -tcpPort "$tcp_port" \
        -ifNotExists &

    local pid=$!
    H2_PIDS+=($pid)

    # Give it a moment to start
    sleep 1

    # Check if process is still running
    if ! kill -0 $pid 2>/dev/null; then
        echo "   ❌ Database failed to start"
        return 1
    fi
}

# Function to cleanup all H2 processes
cleanup() {
    print_separator
    echo "🛑 Shutting down H2 databases"
    print_separator
    for pid in "${H2_PIDS[@]}"; do
        if kill -0 $pid 2>/dev/null; then
            echo "Stopping process $pid"
            kill $pid
            wait $pid
        fi
    done
    echo "✅ All databases stopped"
    exit 0
}

# Trap signals for cleanup
trap cleanup SIGINT SIGTERM

print_separator
echo "🚀 Starting H2 Database Instances"
print_separator

# Start each service's databases
echo "🔐 Authentication Service"
start_h2_instance "auth1" 9005 9006 || exit 1
start_h2_instance "auth2" 9007 9008 || exit 1
echo

echo "📚 Book Service"
start_h2_instance "book1" 9015 9016 || exit 1
start_h2_instance "book2" 9017 9018 || exit 1
echo

echo "📋 Lending Service"
start_h2_instance "lending1" 9025 9026 || exit 1
start_h2_instance "lending2" 9027 9028 || exit 1
echo

echo "👤 Reader Service"
start_h2_instance "reader1" 9035 9036 || exit 1
start_h2_instance "reader2" 9037 9038 || exit 1
echo

print_separator
echo "✅ All H2 database instances are running and ready"
echo "ℹ️  Press Ctrl+C to stop all instances"
print_separator

# Wait for all background processes
wait
