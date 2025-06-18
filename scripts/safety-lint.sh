#!/bin/bash

# 🛡️ Universal Desktop Automation Framework - Safety Linting Script
# This script validates feature files for dangerous global operations
# Usage: ./scripts/safety-lint.sh [feature-file-path]

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# Script configuration
FEATURE_DIR="${1:-src/test/resources/features}"
EXIT_CODE=0
VIOLATIONS_FOUND=0
TOTAL_FILES_CHECKED=0

# Create temporary files for tracking violations
TEMP_DIR=$(mktemp -d)
VIOLATIONS_FILE="$TEMP_DIR/violations.txt"
FILES_CHECKED_FILE="$TEMP_DIR/files_checked.txt"

# Cleanup function
cleanup() {
    rm -rf "$TEMP_DIR"
}
trap cleanup EXIT

echo -e "${CYAN}🛡️  UNIVERSAL DESKTOP AUTOMATION FRAMEWORK - SAFETY LINTER${NC}"
echo -e "${CYAN}================================================================${NC}"
echo -e "${BLUE}Scanning: ${FEATURE_DIR}${NC}"
echo -e "${BLUE}Target: Feature files (*.feature)${NC}"
echo ""

# Function to report violations
report_violation() {
    local file="$1"
    local line="$2"
    local content="$3"
    local severity="$4"
    local explanation="$5"
    
    VIOLATIONS_FOUND=$((VIOLATIONS_FOUND + 1))
    
    # Also write to temp file for subshell persistence
    echo "1" >> "$VIOLATIONS_FILE"
    
    case $severity in
        "CRITICAL")
            echo -e "${RED}🚨 CRITICAL SAFETY VIOLATION:${NC}"
            ;;
        "HIGH")
            echo -e "${YELLOW}⚠️  HIGH RISK OPERATION:${NC}"
            ;;
        "MEDIUM")
            echo -e "${PURPLE}⚡ MEDIUM RISK OPERATION:${NC}"
            ;;
    esac
    
    echo -e "   ${BLUE}File:${NC} $file"
    echo -e "   ${BLUE}Line:${NC} $line"
    echo -e "   ${BLUE}Code:${NC} $content"
    echo -e "   ${BLUE}Risk:${NC} $explanation"
    echo ""
}

# Function to check for dangerous global key combinations
check_global_key_combinations() {
    local file="$1"
    
    # Critical system key combinations
    grep -n "I press.*key combination.*\"ALT+F4\"" "$file" | while read -r line; do
        line_num=$(echo "$line" | cut -d: -f1)
        content=$(echo "$line" | cut -d: -f2-)
        if [[ ! "$content" =~ "managed application" ]]; then
            report_violation "$file" "$line_num" "$content" "CRITICAL" "Global ALT+F4 can close any window including system dialogs"
        fi
    done
    
    grep -n "I press.*key combination.*\"CTRL+ALT+DEL\"" "$file" | while read -r line; do
        line_num=$(echo "$line" | cut -d: -f1)
        content=$(echo "$line" | cut -d: -f2-)
        report_violation "$file" "$line_num" "$content" "CRITICAL" "CTRL+ALT+DEL opens system security screen"
    done
    
    grep -n "I press.*key combination.*\"WIN+R\"" "$file" | while read -r line; do
        line_num=$(echo "$line" | cut -d: -f1)
        content=$(echo "$line" | cut -d: -f2-)
        if [[ ! "$content" =~ "managed application" ]]; then
            report_violation "$file" "$line_num" "$content" "CRITICAL" "WIN+R opens Run dialog globally - can execute system commands"
        fi
    done
    
    grep -n "I press.*key combination.*\"ALT+TAB\"" "$file" | while read -r line; do
        line_num=$(echo "$line" | cut -d: -f1)
        content=$(echo "$line" | cut -d: -f2-)
        if [[ ! "$content" =~ "managed application" ]]; then
            report_violation "$file" "$line_num" "$content" "HIGH" "ALT+TAB performs global window switching"
        fi
    done
    
    # Generic key combination check
    grep -n "I press.*key combination" "$file" | while read -r line; do
        line_num=$(echo "$line" | cut -d: -f1)
        content=$(echo "$line" | cut -d: -f2-)
        if [[ ! "$content" =~ "managed application" ]]; then
            report_violation "$file" "$line_num" "$content" "HIGH" "Key combination without managed application context"
        fi
    done
}

# Function to check for dangerous global typing
check_global_typing() {
    local file="$1"
    
    # Global typing without application context
    grep -n "I type \"" "$file" | while read -r line; do
        line_num=$(echo "$line" | cut -d: -f1)
        content=$(echo "$line" | cut -d: -f2-)
        if [[ ! "$content" =~ "managed application" ]] && [[ ! "$content" =~ "in the active field" ]]; then
            report_violation "$file" "$line_num" "$content" "HIGH" "Global typing without application context"
        fi
    done
    
    # Dangerous system commands in typing
    grep -n "I type.*shutdown" "$file" | while read -r line; do
        line_num=$(echo "$line" | cut -d: -f1)
        content=$(echo "$line" | cut -d: -f2-)
        report_violation "$file" "$line_num" "$content" "CRITICAL" "Contains system shutdown command"
    done
    
    grep -n "I type.*format.*C:" "$file" | while read -r line; do
        line_num=$(echo "$line" | cut -d: -f1)
        content=$(echo "$line" | cut -d: -f2-)
        report_violation "$file" "$line_num" "$content" "CRITICAL" "Contains disk format command"
    done
    
    grep -n "I type.*taskkill.*explorer" "$file" | while read -r line; do
        line_num=$(echo "$line" | cut -d: -f1)
        content=$(echo "$line" | cut -d: -f2-)
        report_violation "$file" "$line_num" "$content" "CRITICAL" "Contains Explorer termination command"
    done
    
    grep -n "I type.*del.*C:" "$file" | while read -r line; do
        line_num=$(echo "$line" | cut -d: -f1)
        content=$(echo "$line" | cut -d: -f2-)
        report_violation "$file" "$line_num" "$content" "CRITICAL" "Contains system file deletion command"
    done
}

# Function to check for global key presses
check_global_key_presses() {
    local file="$1"
    
    # Global ENTER key presses
    grep -n "I press \"ENTER\" key$" "$file" | while read -r line; do
        line_num=$(echo "$line" | cut -d: -f1)
        content=$(echo "$line" | cut -d: -f2-)
        if [[ ! "$content" =~ "managed application" ]]; then
            report_violation "$file" "$line_num" "$content" "MEDIUM" "Global ENTER key press without application context"
        fi
    done
    
    # Other critical global key presses
    grep -n "I press \"WIN\" key" "$file" | while read -r line; do
        line_num=$(echo "$line" | cut -d: -f1)
        content=$(echo "$line" | cut -d: -f2-)
        if [[ ! "$content" =~ "managed application" ]]; then
            report_violation "$file" "$line_num" "$content" "HIGH" "Global Windows key press opens Start menu"
        fi
    done
}

# Function to check for unscoped text waiting
check_unscoped_text_waiting() {
    local file="$1"
    
    grep -n "I wait for text.*to appear" "$file" | while read -r line; do
        line_num=$(echo "$line" | cut -d: -f1)
        content=$(echo "$line" | cut -d: -f2-)
        if [[ ! "$content" =~ "managed application" ]] && [[ ! "$content" =~ "in region" ]]; then
            report_violation "$file" "$line_num" "$content" "MEDIUM" "Text waiting without application context - may fail or target wrong window"
        fi
    done
}

# Function to check step sequencing issues
check_step_sequencing() {
    local file="$1"
    
    # Look for managed application operations before registration
    local in_scenario=false
    local scenario_apps=()
    local line_num=0
    
    while IFS= read -r line; do
        line_num=$((line_num + 1))
        
        # Track scenario boundaries
        if [[ "$line" =~ ^[[:space:]]*Scenario: ]]; then
            in_scenario=true
            scenario_apps=()
        elif [[ "$line" =~ ^[[:space:]]*@.*$ ]] && [ "$in_scenario" = true ]; then
            continue
        elif [[ "$line" =~ ^[[:space:]]*$ ]] && [ "$in_scenario" = true ]; then
            continue
        elif [[ "$line" =~ ^[[:space:]]*Feature: ]] || [[ "$line" =~ ^[[:space:]]*Scenario: ]]; then
            in_scenario=false
        fi
        
        if [ "$in_scenario" = true ]; then
            # Track application registrations
            if [[ "$line" =~ "register.*as managed application \"([^\"]+)\"" ]]; then
                app_name=$(echo "$line" | sed -n 's/.*as managed application "\([^"]*\)".*/\1/p')
                scenario_apps+=("$app_name")
            fi
            
            # Check for operations on unregistered apps
            if [[ "$line" =~ "managed application \"([^\"]+)\"" ]]; then
                app_name=$(echo "$line" | sed -n 's/.*managed application "\([^"]*\)".*/\1/p')
                if [[ ! " ${scenario_apps[@]} " =~ " ${app_name} " ]]; then
                    report_violation "$file" "$line_num" "$line" "HIGH" "Operation on managed application '$app_name' before registration"
                fi
            fi
        fi
    done < "$file"
}

# Function to check a single feature file
check_feature_file() {
    local file="$1"
    
    echo -e "${BLUE}📄 Checking: $file${NC}"
    
    if [[ ! -f "$file" ]]; then
        echo -e "${RED}❌ File not found: $file${NC}"
        return 1
    fi
    
    check_global_key_combinations "$file"
    check_global_typing "$file"
    check_global_key_presses "$file"
    check_unscoped_text_waiting "$file"
    check_step_sequencing "$file"
    
    # Mark file as checked
    echo "1" >> "$FILES_CHECKED_FILE"
    TOTAL_FILES_CHECKED=$((TOTAL_FILES_CHECKED + 1))
}

# Main execution
if [[ -f "$FEATURE_DIR" ]]; then
    # Single file check
    check_feature_file "$FEATURE_DIR"
elif [[ -d "$FEATURE_DIR" ]]; then
    # Directory check
    echo -e "${BLUE}📁 Scanning directory: $FEATURE_DIR${NC}"
    echo ""
    
    # Use process substitution to avoid subshell variable issues
    while IFS= read -r -d '' file; do
        check_feature_file "$file"
    done < <(find "$FEATURE_DIR" -name "*.feature" -type f -print0)
else
    echo -e "${RED}❌ Path not found: $FEATURE_DIR${NC}"
    exit 1
fi

# Final report
# Count violations and files from temp files
if [[ -f "$VIOLATIONS_FILE" ]]; then
    VIOLATIONS_FOUND=$(wc -l < "$VIOLATIONS_FILE")
else
    VIOLATIONS_FOUND=0
fi

if [[ -f "$FILES_CHECKED_FILE" ]]; then
    TOTAL_FILES_CHECKED=$(wc -l < "$FILES_CHECKED_FILE")
else
    TOTAL_FILES_CHECKED=0
fi

echo -e "${CYAN}================================================================${NC}"
echo -e "${CYAN}📊 SAFETY LINT REPORT${NC}"
echo -e "${CYAN}================================================================${NC}"
echo -e "${BLUE}Files Checked:${NC} $TOTAL_FILES_CHECKED"
echo -e "${BLUE}Violations Found:${NC} $VIOLATIONS_FOUND"

if [ $VIOLATIONS_FOUND -eq 0 ]; then
    echo -e "${GREEN}✅ ALL CLEAR - No safety violations detected!${NC}"
    echo -e "${GREEN}🛡️  Your feature files are SAFE for execution.${NC}"
    exit 0
else
    echo -e "${RED}🚨 SAFETY VIOLATIONS DETECTED!${NC}"
    echo -e "${RED}⚠️  Review and fix violations before running tests.${NC}"
    echo ""
    echo -e "${YELLOW}💡 Quick Fixes:${NC}"
    echo -e "   • Add 'in managed application \"app_name\"' to scoped operations"
    echo -e "   • Replace global key combinations with managed application context"
    echo -e "   • Ensure applications are registered before operations"
    echo -e "   • Use 'terminate managed application' instead of ALT+F4"
    echo ""
    echo -e "${BLUE}📖 For detailed guidance, see: SAFETY.md${NC}"
    exit 1
fi
