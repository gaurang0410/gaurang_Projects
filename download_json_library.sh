#!/bin/bash
# Download JSON Library Helper Script for Linux/Mac
# This script downloads the required JSON library for the API

echo ""
echo "==================================="
echo "   JSON Library Downloader"
echo "==================================="
echo ""
echo "This script will download org.json library"
echo "from Maven Central Repository"
echo ""

# Check if curl is available
if ! command -v curl &> /dev/null; then
    echo "Error: curl command not found!"
    echo "Please download manually from:"
    echo "https://repo1.maven.org/maven2/org/json/json/20231013/json-20231013.jar"
    echo ""
    echo "Save it to: lib/json-20231013.jar"
    exit 1
fi

# Check if lib directory exists
if [ ! -d "lib" ]; then
    echo "Creating lib directory..."
    mkdir -p lib
fi

# Download the JSON library
echo "Downloading json-20231013.jar..."
echo ""

curl -L "https://repo1.maven.org/maven2/org/json/json/20231013/json-20231013.jar" -o "lib/json-20231013.jar"

if [ $? -eq 0 ]; then
    echo ""
    echo "==================================="
    echo "   Download Successful!"
    echo "==================================="
    echo ""
    echo "File saved to: lib/json-20231013.jar"
    echo ""
    echo "You can now compile and run your project:"
    echo "   1. Run: ./compile.sh"
    echo "   2. Run: ./run.sh"
    echo ""
else
    echo ""
    echo "==================================="
    echo "   Download Failed!"
    echo "==================================="
    echo ""
    echo "Error: Could not download the file"
    echo ""
    echo "Manual download:"
    echo "   1. Visit: https://repo1.maven.org/maven2/org/json/json/20231013/json-20231013.jar"
    echo "   2. Save to: lib/json-20231013.jar"
    echo ""
fi
