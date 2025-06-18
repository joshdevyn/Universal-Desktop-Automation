#!/bin/bash

# 🛡️ Universal Desktop Automation Framework - Comprehensive Safety Validator
# This script performs deep safety analysis including step sequencing validation
# Usage: ./scripts/safety-validate.sh [feature-file-or-directory]

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# Configuration
TARGET="${1:-src/test/resources/features}"
VIOLATIONS_FOUND=0
WARNINGS_FOUND=0
FILES_CHECKED=0
SCENARIOS_ANALYZED=0

# Violation tracking arrays
declare -a CRITICAL_VIOLATIONS
declare -a HIGH_VIOLATIONS
declare -a MEDIUM_VIOLATIONS
declare -a WARNINGS

echo -e "${CYAN}🛡️  COMPREHENSIVE SAFETY VALIDATOR${NC}"
echo -e "${CYAN}================================================${NC}"
echo -e "${BLUE}Target: ${TARGET}${NC}"
echo -e "${BLUE}Analysis: Deep safety and sequencing validation${NC}"
echo ""

# Function to add violation
add_violation() {
    local severity="$1"
    local file="$2"
    local line="$3"
    local issue="$4"
    local recommendation="$5"
    
    local violation_entry="$file:$line - $issue | Fix: $recommendation"
    
    case $severity in
        "CRITICAL")
            CRITICAL_VIOLATIONS+=("$violation_entry")
            ;;
        "HIGH")
            HIGH_VIOLATIONS+=("$violation_entry")
            ;;
        "MEDIUM")
            MEDIUM_VIOLATIONS+=("$violation_entry")
            ;;
        "WARNING")
            WARNINGS+=("$violation_entry")
            ;;
    esac
    
    if [[ "$severity" != "WARNING" ]]; then
        VIOLATIONS_FOUND=$((VIOLATIONS_FOUND + 1))
    else
        WARNINGS_FOUND=$((WARNINGS_FOUND + 1))
    fi
}

# Function to analyze step sequencing within a scenario
analyze_scenario_sequencing() {
    local file="$1"
    local scenario_start="$2"
    local scenario_end="$3"
    local scenario_name="$4"
    
    SCENARIOS_ANALYZED=$((SCENARIOS_ANALYZED + 1))
    
    # Extract scenario content
    local scenario_content=$(sed -n "${scenario_start},${scenario_end}p" "$file")
    
    # Track managed applications in this scenario
    declare -a registered_apps
    declare -a app_operations
    
    local line_num=$scenario_start
    while IFS= read -r line; do
        # Skip empty lines and comments
        [[ -z "$line" || "$line" =~ ^[[:space:]]*# ]] && { line_num=$((line_num + 1)); continue; }
        
        # Track application registrations
        if [[ "$line" =~ register.*as[[:space:]]+managed[[:space:]]+application[[:space:]]+\"([^\"]+)\" ]]; then
            local app_name="${BASH_REMATCH[1]}"
            registered_apps+=("$app_name")
        fi
        
        # Track application launches
        if [[ "$line" =~ launch.*application.*as[[:space:]]+\"([^\"]+)\" ]]; then
            local app_name="${BASH_REMATCH[1]}"
            registered_apps+=("$app_name")
        fi
        
        # Check operations on managed applications
        if [[ "$line" =~ managed[[:space:]]+application[[:space:]]+\"([^\"]+)\" ]]; then
            local app_name="${BASH_REMATCH[1]}"
            local operation_found=false
            
            for registered_app in "${registered_apps[@]}"; do
                if [[ "$registered_app" == "$app_name" ]]; then
                    operation_found=true
                    break
                fi
            done
            
            if [[ "$operation_found" == false ]]; then
                add_violation "HIGH" "$file" "$line_num" \
                    "Operation on unregistered managed application '$app_name'" \
                    "Register application before operations: When I register ... as managed application \"$app_name\""
            fi
        fi
        
        line_num=$((line_num + 1))
    done <<< "$scenario_content"
}

# Function to perform comprehensive analysis of a feature file
analyze_feature_file() {
    local file="$1"
    
    echo -e "${BLUE}🔍 Analyzing: $file${NC}"
    FILES_CHECKED=$((FILES_CHECKED + 1))
    
    if [[ ! -f "$file" ]]; then
        add_violation "HIGH" "$file" "N/A" "File not found" "Verify file path exists"
        return 1
    fi
    
    local line_num=0
    local in_scenario=false
    local scenario_start=0
    local scenario_name=""
    
    # Pattern analysis
    while IFS= read -r line; do
        line_num=$((line_num + 1))
        
        # Track scenario boundaries for sequencing analysis
        if [[ "$line" =~ ^[[:space:]]*Scenario:[[:space:]]*(.+)$ ]]; then
            if [[ "$in_scenario" == true ]]; then
                # Analyze previous scenario
                analyze_scenario_sequencing "$file" "$scenario_start" "$((line_num - 1))" "$scenario_name"
            fi
            in_scenario=true
            scenario_start=$line_num
            scenario_name="${BASH_REMATCH[1]}"
        elif [[ "$line" =~ ^[[:space:]]*Feature: ]] && [[ "$in_scenario" == true ]]; then
            # End of previous scenario
            analyze_scenario_sequencing "$file" "$scenario_start" "$((line_num - 1))" "$scenario_name"
            in_scenario=false
        fi
        
        # CRITICAL VIOLATIONS
        
        # System shutdown commands
        if [[ "$line" =~ shutdown|restart|logoff ]]; then
            add_violation "CRITICAL" "$file" "$line_num" \
                "System shutdown/restart command detected" \
                "Remove system commands from test data"
        fi
        
        # Security-critical key combinations
        if [[ "$line" =~ CTRL\+ALT\+DEL|Ctrl\+Alt\+Del ]]; then
            add_violation "CRITICAL" "$file" "$line_num" \
                "CTRL+ALT+DEL security key combination" \
                "Remove security key combinations"
        fi
        
        # Disk format commands
        if [[ "$line" =~ format.*[Cc]: ]]; then
            add_violation "CRITICAL" "$file" "$line_num" \
                "Disk format command detected" \
                "Remove disk format commands"
        fi
        
        # Explorer termination
        if [[ "$line" =~ taskkill.*explorer ]]; then
            add_violation "CRITICAL" "$file" "$line_num" \
                "Explorer termination command" \
                "Use managed application termination instead"
        fi
        
        # HIGH VIOLATIONS
        
        # Global key combinations without context
        if [[ "$line" =~ press.*key[[:space:]]+combination && ! "$line" =~ managed[[:space:]]+application ]]; then
            if [[ "$line" =~ ALT\+F4|Alt\+F4 ]]; then
                add_violation "HIGH" "$file" "$line_num" \
                    "Global ALT+F4 without managed application context" \
                    "Add: in managed application \"app_name\" or use terminate managed application"
            elif [[ "$line" =~ WIN\+R|Win\+R ]]; then
                add_violation "HIGH" "$file" "$line_num" \
                    "Global WIN+R (Run dialog) without context" \
                    "Add: in managed application \"app_name\""
            elif [[ "$line" =~ ALT\+TAB|Alt\+Tab ]]; then
                add_violation "HIGH" "$file" "$line_num" \
                    "Global ALT+TAB (window switching) without context" \
                    "Add: in managed application \"app_name\""
            else
                add_violation "HIGH" "$file" "$line_num" \
                    "Global key combination without managed application context" \
                    "Add: in managed application \"app_name\""
            fi
        fi
        
        # Global typing without context
        if [[ "$line" =~ I[[:space:]]+type.*\" && ! "$line" =~ managed[[:space:]]+application && ! "$line" =~ in[[:space:]]+the[[:space:]]+active[[:space:]]+field ]]; then
            add_violation "HIGH" "$file" "$line_num" \
                "Global typing without application context" \
                "Add: in managed application \"app_name\""
        fi
        
        # Global Windows key
        if [[ "$line" =~ press.*\"WIN\"|press.*\"Win\" && ! "$line" =~ managed[[:space:]]+application ]]; then
            add_violation "HIGH" "$file" "$line_num" \
                "Global Windows key press" \
                "Add: in managed application \"app_name\""
        fi
        
        # MEDIUM VIOLATIONS
        
        # Global ENTER key
        if [[ "$line" =~ press.*\"ENTER\".*key && ! "$line" =~ managed[[:space:]]+application ]]; then
            add_violation "MEDIUM" "$file" "$line_num" \
                "Global ENTER key press without context" \
                "Add: in managed application \"app_name\""
        fi
        
        # Unscoped text waiting
        if [[ "$line" =~ wait[[:space:]]+for[[:space:]]+text.*to[[:space:]]+appear && ! "$line" =~ managed[[:space:]]+application && ! "$line" =~ in[[:space:]]+region ]]; then
            add_violation "MEDIUM" "$file" "$line_num" \
                "Text waiting without application or region context" \
                "Add: in managed application \"app_name\" or in region \"region_name\""
        fi
        
        # WARNINGS
        
        # Hardcoded timeouts that might be too short
        if [[ "$line" =~ timeout[[:space:]]+[12][[:space:]]+second ]]; then
            add_violation "WARNING" "$file" "$line_num" \
                "Very short timeout (1-2 seconds) may cause flaky tests" \
                "Consider increasing timeout for reliability"
        fi
        
        # Use of active field (less robust)
        if [[ "$line" =~ in[[:space:]]+the[[:space:]]+active[[:space:]]+field ]]; then
            add_violation "WARNING" "$file" "$line_num" \
                "Using 'active field' is less robust than managed application context" \
                "Consider: in managed application \"app_name\" for better reliability"
        fi
        
    done < "$file"
    
    # Analyze final scenario if we ended in one
    if [[ "$in_scenario" == true ]]; then
        analyze_scenario_sequencing "$file" "$scenario_start" "$line_num" "$scenario_name"
    fi
}

# Main execution
if [[ -f "$TARGET" ]]; then
    # Single file analysis
    analyze_feature_file "$TARGET"
elif [[ -d "$TARGET" ]]; then
    # Directory analysis
    echo -e "${BLUE}📁 Scanning directory: $TARGET${NC}"
    
    while IFS= read -r -d '' file; do
        analyze_feature_file "$file"
    done < <(find "$TARGET" -name "*.feature" -type f -print0)
else
    echo -e "${RED}❌ Target not found: $TARGET${NC}"
    exit 1
fi

# Generate comprehensive report
echo ""
echo -e "${CYAN}================================================================${NC}"
echo -e "${CYAN}📊 COMPREHENSIVE SAFETY VALIDATION REPORT${NC}"
echo -e "${CYAN}================================================================${NC}"
echo -e "${BLUE}Files Analyzed:${NC} $FILES_CHECKED"
echo -e "${BLUE}Scenarios Analyzed:${NC} $SCENARIOS_ANALYZED"
echo -e "${BLUE}Total Violations:${NC} $VIOLATIONS_FOUND"
echo -e "${BLUE}Warnings:${NC} $WARNINGS_FOUND"
echo ""

# Report violations by severity
if [[ ${#CRITICAL_VIOLATIONS[@]} -gt 0 ]]; then
    echo -e "${RED}🚨 CRITICAL VIOLATIONS (${#CRITICAL_VIOLATIONS[@]}):${NC}"
    for violation in "${CRITICAL_VIOLATIONS[@]}"; do
        echo -e "${RED}   ❌ $violation${NC}"
    done
    echo ""
fi

if [[ ${#HIGH_VIOLATIONS[@]} -gt 0 ]]; then
    echo -e "${YELLOW}⚠️  HIGH RISK VIOLATIONS (${#HIGH_VIOLATIONS[@]}):${NC}"
    for violation in "${HIGH_VIOLATIONS[@]}"; do
        echo -e "${YELLOW}   ⚠️  $violation${NC}"
    done
    echo ""
fi

if [[ ${#MEDIUM_VIOLATIONS[@]} -gt 0 ]]; then
    echo -e "${PURPLE}⚡ MEDIUM RISK VIOLATIONS (${#MEDIUM_VIOLATIONS[@]}):${NC}"
    for violation in "${MEDIUM_VIOLATIONS[@]}"; do
        echo -e "${PURPLE}   ⚡ $violation${NC}"
    done
    echo ""
fi

if [[ ${#WARNINGS[@]} -gt 0 ]]; then
    echo -e "${BLUE}💡 WARNINGS (${#WARNINGS[@]}):${NC}"
    for warning in "${WARNINGS[@]}"; do
        echo -e "${BLUE}   💡 $warning${NC}"
    done
    echo ""
fi

# Final assessment
if [[ $VIOLATIONS_FOUND -eq 0 ]]; then
    echo -e "${GREEN}✅ SAFETY VALIDATION PASSED!${NC}"
    echo -e "${GREEN}🛡️  All feature files are SAFE for execution.${NC}"
    
    if [[ $WARNINGS_FOUND -gt 0 ]]; then
        echo -e "${YELLOW}📝 Consider addressing warnings for improved reliability.${NC}"
    fi
    exit 0
else
    echo -e "${RED}🚨 SAFETY VALIDATION FAILED!${NC}"
    echo -e "${RED}⛔ Fix all violations before running tests.${NC}"
    echo ""
    echo -e "${CYAN}🔧 REMEDIATION GUIDE:${NC}"
    echo -e "   1. Review each violation listed above"
    echo -e "   2. Apply recommended fixes"
    echo -e "   3. Re-run safety validation"
    echo -e "   4. Consult SAFETY.md for detailed guidance"
    echo ""
    exit 1
fi
