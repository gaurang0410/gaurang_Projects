#!/bin/bash
# Run Script for Vehicle Service Management App with REST API

echo "Starting Vehicle Service Management App..."
echo ""
echo "Note: API Server will start on http://localhost:8080"
echo ""

cd src

# Run the application with API support
java -cp .:../lib/mysql-connector-j-8.0.33.jar:../lib/json-20231013.jar Main

if [ $? -ne 0 ]; then
    echo ""
    echo "Error running application!"
    echo "Make sure:"
    echo "1. MySQL server is running"
    echo "2. Database 'vehicle_service' exists"
    echo "3. Application was compiled successfully"
    echo "4. lib/json-20231013.jar is present in lib folder"
fi
