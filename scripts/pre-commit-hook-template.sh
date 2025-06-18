#!/bin/bash
# 🛡️ Universal Desktop Automation Framework - Pre-commit Safety Hook
# 
# This script runs safety validation before allowing commits to proceed.
# This prevents dangerous global operations from being committed to version control.
#
# To install this hook:
# 1. Copy this file to .git/hooks/pre-commit
# 2. Make it executable: chmod +x .git/hooks/pre-commit
#
# To bypass in emergencies (NOT RECOMMENDED):
# git commit --no-verify

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

echo -e "${CYAN}🛡️ Pre-commit Safety Validation${NC}"
echo -e "${CYAN}================================${NC}"

# Check if we're in the project root
if [[ ! -f "pom.xml" ]] || [[ ! -f "SAFETY.md" ]]; then
    echo -e "${RED}❌ Not in project root or missing safety documentation${NC}"
    echo -e "${YELLOW}💡 Run this from the project root directory${NC}"
    exit 1
fi

# Check if safety scripts exist
if [[ ! -f "scripts/safety-lint.sh" ]]; then
    echo -e "${RED}❌ Safety lint script not found: scripts/safety-lint.sh${NC}"
    exit 1
fi

if [[ ! -f "scripts/safety-validate.sh" ]]; then
    echo -e "${RED}❌ Safety validation script not found: scripts/safety-validate.sh${NC}"
    exit 1
fi

# Make scripts executable if needed
chmod +x scripts/safety-lint.sh 2>/dev/null || true
chmod +x scripts/safety-validate.sh 2>/dev/null || true

echo -e "${BLUE}🔍 Running basic safety linting...${NC}"

# Run safety linting
if ! ./scripts/safety-lint.sh; then
    echo ""
    echo -e "${RED}🚨 COMMIT BLOCKED - Safety violations detected!${NC}"
    echo -e "${RED}⚠️  Dangerous operations found in feature files.${NC}"
    echo ""
    echo -e "${YELLOW}🔧 Required Actions:${NC}"
    echo -e "   1. Review and fix the safety violations listed above"
    echo -e "   2. Add managed application context to global operations"
    echo -e "   3. Replace dangerous patterns with safe alternatives"
    echo -e "   4. Re-run: git commit (this hook will validate again)"
    echo ""
    echo -e "${BLUE}📖 For detailed guidance, see: SAFETY.md${NC}"
    echo ""
    echo -e "${YELLOW}⚠️  Emergency bypass (NOT RECOMMENDED):${NC}"
    echo -e "   git commit --no-verify"
    echo ""
    exit 1
fi

echo ""
echo -e "${BLUE}🔍 Running comprehensive safety validation...${NC}"

# Run comprehensive safety validation
if ! ./scripts/safety-validate.sh >/dev/null 2>&1; then
    echo ""
    echo -e "${YELLOW}⚠️  Comprehensive validation found additional issues${NC}"
    echo -e "${YELLOW}📋 Running detailed validation for review:${NC}"
    echo ""
    ./scripts/safety-validate.sh
    echo ""
    echo -e "${YELLOW}🔧 Please review and address the issues above${NC}"
    echo -e "${YELLOW}💡 Basic safety lint passed, but comprehensive check found concerns${NC}"
    echo ""
    echo -e "${BLUE}Options:${NC}"
    echo -e "   1. Fix all issues and commit again (recommended)"
    echo -e "   2. Proceed with caution: git commit --no-verify"
    echo ""
    exit 1
fi

echo -e "${GREEN}✅ All safety validations passed!${NC}"
echo -e "${GREEN}🛡️  Your changes are SAFE for execution.${NC}"
echo -e "${GREEN}🚀 Proceeding with commit...${NC}"
echo ""

exit 0
