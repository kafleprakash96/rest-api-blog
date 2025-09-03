#!/bin/bash

# Base directory of your project (adjust if script runs from elsewhere)
BASE_DIR="src/main/java/org/prkguides/blog"

# Target packages
PACKAGES=("controller" "entity" "dto")

# Output file
OUTPUT_FILE="collected_files.txt"

# Clear previous output
> "$OUTPUT_FILE"

# Loop through each package
for pkg in "${PACKAGES[@]}"; do
  echo "===============================" >> "$OUTPUT_FILE"
  echo " PACKAGE: $pkg" >> "$OUTPUT_FILE"
  echo "===============================" >> "$OUTPUT_FILE"
  echo "" >> "$OUTPUT_FILE"

  # Find all .java files in the package
  find "$BASE_DIR/$pkg" -type f -name "*.java" | while read -r file; do
    echo "----- FILE: $(basename "$file") -----" >> "$OUTPUT_FILE"
    cat "$file" >> "$OUTPUT_FILE"
    echo "" >> "$OUTPUT_FILE"
    echo "" >> "$OUTPUT_FILE"
  done
done

echo "✅ All files collected into $OUTPUT_FILE"
