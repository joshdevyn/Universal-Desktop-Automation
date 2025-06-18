# Universal Desktop Automation Framework

A comprehensive Java-based desktop automation framework designed to test any Windows application through visual validation, OCR text recognition, and intelligent window automation. This framework is completely application-agnostic and works with legacy mainframe terminals, modern desktop applications, SAP GUI, Oracle Forms, and all types of Windows applications.

**🔗 Repository**: [https://github.com/joshdevyn/Universal-Desktop-Automation](https://github.com/joshdevyn/Universal-Desktop-Automation)

**Latest Status (June 18, 2025):**
- ✅ **Universal Windows Automation** - Successfully automated Explorer, Web Browsers, CMD, and legacy applications
- ✅ **Enterprise Win32 API Suite** - Complete wrapper suite with 13 specialized managers for surgical precision control
- ✅ **Professional OCR Maximization** - Configurable temporary window maximization only during OCR operations
- ✅ **Cross-PID Window Discovery** - Robust detection of console windows hosted by Windows Terminal
- ✅ **Enterprise-Grade Safety** - Comprehensive protection against accidental system interference
- ✅ **Managed Application Context** - Unified process and window management with enterprise coding standards


Build:
```bash
# Quick build (compile only, no JARs or tests)
mvn clean compile

# Full build with executables
mvn clean compile package -Pbuild-executables -DskipTests

# Or use the taskfile for convenience
./task.sh build              # Linux/Mac/Git Bash
.\task.bat build             # Windows
```

**New Taskfile Commands:**
```bash
# Build everything with executables
./task.sh build

# Run Cucumber tests by tag
./task.sh test @smoke
./task.sh test @integration
./task.sh test "@calculator and @smoke"
```

## Current Framework Status & Roadmap

### Completed Features
| Component | Status | Description |
|-----------|---------|-------------|
| **Core Window Management** | Complete | Universal Windows API integration with JNA |
| **Explorer Automation** | Complete | ALT+D → CMD launch workflow working perfectly |
| **CMD/Console Detection** | Complete | Cross-PID console window discovery (Windows Terminal, conhost.exe) |
| **Professional OCR Maximization** | Complete | Configurable temporary maximization during OCR only |
| **Safety Framework** | Complete | Enterprise-grade protection against system interference |
| **Multi-Window Support** | Complete | Handle applications with multiple windows (Explorer, SAP) |
| **BDD Integration** | Complete | Comprehensive Cucumber step definitions with managed context |
| **Reporting & Evidence** | Complete | ExtentReports with screenshots, OCR results, performance metrics |

### In Progress
| Component | Status | Next Steps |
|-----------|---------|------------|
| **Image Template Management** | In Progress | Auto-update templates based on DPI/resolution changes |
| **Performance Optimization** | In Progress | Cache optimizations, parallel execution improvements |

### Known Limitations
| Component | Limitation | Impact | Workaround |
|-----------|------------|---------|------------|
| **UWP Applications** | Process delegation causes PID tracking failures | Windows 11 Calculator, Paint, Notepad automation fails | Use legacy versions or window title-based automation |
| **Modern Store Apps** | Cannot track delegated process PIDs | Store apps and PWAs not automatable | Use alternative automation tools (Playwright, WinAppDriver) |
| **Cross-PID Discovery** | Limited window discovery across process boundaries | Some console apps may not be detected properly | Manual window title-based discovery available |

### Planned Features
- **AI-Powered Element Detection** - Machine learning for dynamic UI element recognition
- **Mobile App Integration** - Extend framework to Android/iOS automation
- **Cloud Execution** - Azure/AWS integration for scalable test execution
- **Advanced Image Processing** - OpenCV integration for complex visual validation

### Achievement Metrics
- **Universal Windows Automation** achieved
- **Enterprise Safety Standards** implemented
- **Professional OCR Workflow** with smart maximization

## Key Features

### Core Automation Capabilities
- **Universal Application Support**: Works with traditional Windows desktop applications (legacy mainframes, SAP GUI, Oracle Forms, Win32 apps). *Limited support for UWP/Modern apps due to process delegation architecture.*
- **Comprehensive Win32 API Integration**: Complete wrapper suite providing surgical precision control over Windows processes, windows, memory, handles, threads, modules, registry, and security tokens
- **Enhanced Multi-Window Management**: Sophisticated handling of processes with multiple windows (explorer.exe, SAP sessions, etc.) with advanced window discovery strategies
- **Managed Application Context**: "Focus First, Then Act" pattern ensures every action targets the correct application window with enterprise-grade safety
- **Professional OCR with Smart Maximization**: Configurable temporary window maximization during OCR operations for improved accuracy without permanent window state changes

### Advanced Process Management
- **Enterprise Process Tracker**: Real-time process monitoring with comprehensive state tracking and performance metrics
- **Cross-PID Window Discovery**: Advanced detection of console windows hosted by different processes (Windows Terminal, conhost.exe)
- **Process Lifecycle Management**: Complete control over application startup, monitoring, and graceful termination with validation
- **Memory and Handle Tracking**: Real-time monitoring of process memory usage, handle leaks, and resource utilization
- **Thread and Module Management**: Comprehensive thread enumeration and DLL/module tracking for process intelligence

### Visual and Text Automation
- **Visual Automation**: SikuliX-powered image-based element identification and interaction with computer vision
- **Advanced OCR Text Recognition**: Tesseract integration for extracting and validating text from any screen region with confidence scoring
- **Intelligent Image Processing**: Advanced image preprocessing, template matching, and multi-scale recognition
- **Screenshot and Evidence Capture**: Automated screenshot capture with timestamp and context information

### Testing and Reporting Framework
- **BDD Support**: Comprehensive Cucumber integration with managed application step definitions and safety-first patterns
- **Comprehensive Reporting**: ExtentReports integration with screenshots, OCR results, performance metrics, and execution evidence
- **Data-Driven Testing**: Support for CSV, JSON, YAML, and Excel test data with variable interpolation and transformation
- **Cross-Application Testing**: Test workflows spanning multiple applications with managed context and automatic cleanup

### Configuration and Data Management
- **Flexible Configuration**: YAML-based application configuration with multi-window support and environment-specific settings
- **Variable Management**: Advanced variable storage, retrieval, and interpolation with built-in transformations
- **Test Data Providers**: Multi-format test data support with dynamic loading and caching capabilities

## Enterprise-Grade Win32 API Wrapper Suite

The framework includes a comprehensive Win32 API wrapper suite that provides surgical precision control over Windows processes and system resources:

### Core Win32 Components
- **Win32ApiWrapper**: Central Windows API integration with performance caching and comprehensive process intelligence gathering
- **Win32WindowControl**: Advanced window management with enumeration, manipulation, state tracking, and Z-order control
- **Win32ProcessTerminator**: Professional process termination with graceful shutdown, escalation, and cleanup validation

### Process Intelligence and Monitoring
- **Win32MemoryManager**: Real-time memory usage tracking, working set analysis, and memory leak detection
- **Win32PerformanceMonitor**: CPU usage tracking, I/O statistics, and performance trend analysis
- **Win32HandleTracker**: Handle enumeration, leak detection, and cross-process handle monitoring
- **Win32ThreadManager**: Thread enumeration, validation, and cross-process thread monitoring

### System Integration and Security
- **Win32ModuleManager**: DLL and module enumeration, injection capabilities, and process module mapping
- **Win32RegistryManager**: Registry operations, monitoring, application configuration discovery, and change detection
- **Win32SecurityManager**: Security token management, privilege enumeration, integrity level detection, and administrative validation
- **Win32SystemInfo**: Comprehensive system information gathering including hardware, OS version, and performance metrics
- **Win32FileSystemManager**: File system operations, monitoring, and process file dependency analysis

### Validation and Testing
- **Win32ValidationSuite**: Comprehensive testing framework for the entire Win32 wrapper suite with integration validation, leak detection, and performance benchmarking

This enterprise-grade wrapper suite ensures reliable automation of any Windows application while providing deep system integration and monitoring capabilities essential for production automation environments.

## Critical Safety Features

**IMPORTANT: This framework has been designed with enterprise-grade safety measures to prevent accidental system interference.**

### Managed Application Context Safety
- **NO GLOBAL OPERATIONS**: All keyboard and mouse operations are scoped to specific managed applications
- **System Protection**: Built-in safeguards prevent accidental system shutdown, Explorer termination, or global key broadcasts
- **Application Isolation**: Every action targets a specific application context, preventing cross-application interference
- **Safe Process Management**: System-critical applications (Explorer, Windows services) are protected from automated termination

### Key Safety Principles
1. **"Focus First, Then Act"**: Every operation requires explicit application context before execution
2. **No Dangerous Global Keys**: Framework prohibits global Alt+F4, Win+R, Ctrl+Alt+Del, and other system-level key combinations
3. **Managed Application Registry**: All applications must be registered before automation operations can be performed
4. **Protected System Processes**: Windows Explorer and other critical system processes are exempted from cleanup operations

### Safe BDD Step Pattern
```gherkin
# DANGEROUS (Old Global Pattern - Now Removed)
When I press "ALT+F4" key combination

# SAFE (New Managed Application Pattern)
When I press "ALT+F4" key combination in managed application "calculator"
When I terminate the managed application "calculator"
```

### Enhanced Window Discovery
- **Three-Stage Fallback Strategy**: Ensures reliable window detection for all application types
- **Console Application Support**: Special handling for CMD, PowerShell, and terminal applications
- **Multi-Window Process Support**: Proper management of applications with multiple windows (Explorer, SAP sessions)
- **PID-Driven Tracking**: Robust process identification even with delegated system processes

## Architecture

The framework is built with a modular enterprise architecture designed for scalability and maintainability:

```
src/
├── main/java/com/automation/
│   ├── core/                    # Core automation engines
│   │   ├── WindowController.java        # High-level window automation with Win32 integration
│   │   ├── ProcessManager.java          # Enterprise-grade process lifecycle management
│   │   ├── EnterpriseProcessTracker.java # Advanced process tracking and monitoring
│   │   ├── ScreenCapture.java           # Screenshot and image capture utilities
│   │   ├── OCREngine.java               # Tesseract OCR integration with confidence scoring
│   │   ├── ImageMatcher.java            # SikuliX image matching with preprocessing
│   │   └── win32/                       # Comprehensive Win32 API wrapper suite
│   │       ├── Win32ApiWrapper.java         # Core Win32 API operations
│   │       ├── Win32WindowControl.java      # Advanced window management
│   │       ├── Win32ProcessTerminator.java  # Process termination with validation
│   │       ├── Win32MemoryManager.java      # Memory tracking and analysis
│   │       ├── Win32PerformanceMonitor.java # CPU and I/O performance tracking
│   │       ├── Win32HandleTracker.java      # Handle enumeration and leak detection
│   │       ├── Win32ThreadManager.java      # Thread enumeration and management
│   │       ├── Win32ModuleManager.java      # DLL and module enumeration
│   │       ├── Win32RegistryManager.java    # Registry operations and monitoring
│   │       ├── Win32SecurityManager.java    # Security token and privilege management
│   │       ├── Win32SystemInfo.java         # System information gathering
│   │       ├── Win32FileSystemManager.java  # File system operations
│   │       └── Win32ValidationSuite.java    # Win32 wrapper validation framework
│   ├── config/                  # Configuration management
│   │   └── ConfigManager.java           # YAML and properties configuration handling
│   ├── utils/                   # Utility classes and managers
│   │   ├── ApplicationManager.java      # Application lifecycle and context management
│   │   ├── WaitUtils.java               # Smart waiting strategies with polling
│   │   ├── WaitUtilsStatic.java         # Static wait utilities for common operations
│   │   ├── ImageUtils.java              # Advanced image processing utilities
│   │   ├── ReportUtils.java             # ExtentReports integration and reporting
│   │   ├── TestDataProvider.java        # Multi-format test data management
│   │   ├── TestLogger.java              # Structured test execution logging
│   │   ├── VariableManager.java         # Variable storage and interpolation
│   │   └── WindowLister.java            # Window enumeration and debugging utility
│   ├── models/                  # Data models and representations
│   │   ├── ManagedApplicationContext.java # Unified process and window context model
│   │   ├── ApplicationWindow.java       # Window representation with bounds and properties
│   │   ├── ScreenRegion.java            # Screen region model with validation
│   │   ├── OCRResult.java               # OCR extraction results with confidence
│   │   └── TestResult.java              # Test result with verifications and evidence
│   ├── reporting/               # Reporting infrastructure
│   │   └── ReportManager.java           # Centralized test reporting and metrics
│   └── exceptions/              # Custom exception hierarchy
│       ├── AutomationException.java     # Base automation framework exception
│       ├── WindowNotFoundException.java # Window discovery and access exceptions
│       ├── ImageMatchException.java     # Image matching and template exceptions
│       └── OCRException.java            # OCR processing and extraction exceptions
├── test/java/com/automation/
│   ├── tests/                   # Comprehensive test suites
│   │   └── MultiWindowManagementTest.java # Multi-window functionality validation
│   ├── mock/                    # Mock applications for testing
│   │   ├── AS400TerminalMock.java       # AS/400 terminal simulator
│   │   ├── ExcelMock.java               # Excel application simulator
│   │   ├── OracleFormsMock.java         # Oracle Forms simulator
│   │   └── SAPGUIMock.java              # SAP GUI simulator
│   ├── cucumber/                # BDD framework infrastructure
│   │   ├── CucumberTestRunner.java      # Primary Cucumber test runner
│   │   ├── configuration/               # Test execution configuration
│   │   ├── stepdefinitions/             # Managed application step definitions
│   │   ├── hooks/                       # Test lifecycle hooks and setup
│   │   ├── runners/                     # Specialized test runners (smoke, integration)
│   │   ├── utilities/                   # Cucumber-specific utilities
│   │   └── patterns/                    # Reusable step patterns
│   ├── core/                    # Core framework tests
│   └── utils/                   # Utility tests and validation
└── resources/
    ├── config/                  # Configuration files
    │   ├── automation.properties       # Framework settings and paths
    │   └── applications.yml             # Application configurations with multi-window support
    ├── features/                # Cucumber feature files organized by category
    │   ├── demo/                        # Demonstration features
    │   ├── smoke/                       # Smoke test features
    │   ├── integration/                 # Integration test features
    │   ├── regression/                  # Regression test features
    │   ├── mock-apps/                   # Mock application features
    │   ├── real-apps/                   # Real application automation features
    │   ├── pid-driven/                  # PID-driven process management features
    │   ├── multi-window/                # Multi-window management features
    │   ├── master-suite/                # Master test suite features
    │   └── simplified/                  # Simplified demonstration features
    ├── testdata/                # Test data files (CSV, JSON, YAML, Excel)
    └── images/                  # Template images and screenshots
        ├── templates/                   # Image templates for matching
        └── screenshots/                 # Test execution screenshots
```

## Technologies Used

### Core Framework
- **Java 17+**: Modern Java with enhanced features for enterprise automation
- **Maven 3.6+**: Build and dependency management with multi-profile support
- **JUnit 5.9.3**: Advanced testing framework with parallel execution and comprehensive assertions
- **TestNG**: Comprehensive test framework for data-driven testing scenarios

### Windows Integration and Automation
- **JNA (Java Native Access)**: Direct Windows API integration for enterprise-grade system control
- **SikuliX 2.0.5**: Computer vision and image recognition for visual automation
- **Tesseract4J 5.8.0**: Industry-standard OCR text extraction with confidence scoring and language support

### Testing and BDD Framework
- **Cucumber 7.14.0**: Behavior-driven development with managed application context patterns
- **ExtentReports 5.0.9**: Rich HTML test reporting with multimedia evidence and performance metrics
- **Selenium WebDriver 4.11.0**: Web automation capabilities for hybrid testing scenarios

### Data Processing and Configuration
- **Jackson 2.15.2**: High-performance JSON/YAML processing and object mapping
- **SnakeYAML 2.0**: Advanced YAML configuration parsing and manipulation
- **Apache POI**: Excel file processing for comprehensive test data management

### Logging and Monitoring
- **SLF4J with Logback**: Professional logging framework with configurable appenders and structured output
- **Performance Monitoring**: Custom performance tracking with Win32 API integration

### Development and Safety
- **VS Code Integration**: Pre-configured tasks for build, test, and safety validation
- **Safety Validation Scripts**: Comprehensive code safety checking and enterprise compliance validation
- **Git Integration**: Version control with automated safety hooks and validation

## 📋 Prerequisites

### System Requirements
- **Operating System**: Windows 10/11 (64-bit) - *Required for Win32 API integration*
- **Java Development Kit**: Java 11 or higher (Java 17+ recommended)
- **Build Tool**: Maven 3.6 or higher
- **Memory**: At least 4GB RAM (8GB recommended for large test suites)
- **Storage**: 2GB free disk space for dependencies and artifacts

### Required Software Installation

#### 1. Tesseract OCR Engine
Tesseract is required for text extraction capabilities:
```bash
# Download from GitHub releases
https://github.com/tesseract-ocr/tesseract/releases

# Or install via package manager (Windows)
winget install UB-Mannheim.TesseractOCR

# Verify installation
tesseract --version
```

#### 2. Visual C++ Redistributable
Required for native library dependencies:
```bash
# Download Microsoft Visual C++ Redistributable
https://aka.ms/vs/17/release/vc_redist.x64.exe
```

### Optional Development Tools
- **Visual Studio Code**: Pre-configured tasks and settings included
- **Git**: Version control with safety validation hooks
- **Windows Terminal**: Enhanced console experience for script execution

### Environment Variables (Optional)
```bash
# Add Tesseract to PATH (if not done during installation)
set PATH=%PATH%;C:\Program Files\Tesseract-OCR

# Set JAVA_HOME (if not already set)
set JAVA_HOME=C:\Program Files\Java\jdk-17

# Maven configuration (if using custom settings)
set MAVEN_OPTS=-Xmx2048m -XX:MaxPermSize=256m
```

## Quick Start

### 1. Clone and Setup
```bash
git clone https://github.com/joshdevyn/Universal-Desktop-Automation.git
cd Universal-Desktop-Automation

# Quick compile (recommended for development)
mvn clean compile

# Full build with executables and JAR packages
mvn clean package -Pbuild-executables

# Or use the convenient task scripts
./task.sh build              # Linux/Mac/Git Bash
.\task.bat build              # Windows Command Prompt
```

### 2. Configure Tesseract OCR
Update `src/main/resources/config/automation.properties`:
```properties
# Tesseract OCR Configuration
tesseract.path=C:\\Program Files\\Tesseract-OCR\\tesseract.exe
tesseract.dataPath=C:\\Program Files\\Tesseract-OCR\\tessdata
tesseract.language=eng

# Framework Settings
screenshot.directory=target/screenshots
reports.directory=target/reports
timeout.default=30
```

### 3. Run Tests
```bash
# Run all tests with Maven
mvn test

# Run specific test categories with task scripts
./task.sh test @smoke           # Smoke tests only
./task.sh test @integration     # Integration tests only
./task.sh test @mock-apps       # Mock application tests
./task.sh test "@calculator and @smoke"  # Combined tags

# Run with Maven profiles
mvn test -Psafe-testing         # Safe testing with additional validation
mvn test -Psafety-validation    # Enhanced safety validation
```

### 4. VS Code Tasks (if using VS Code)
The project includes VS Code tasks for convenient development:
- **Build Everything + Executables**: Full build with JAR creation
- **Safety Lint**: Code safety validation
- **Comprehensive Safety Validation**: Full safety check suite
- **Maven with Safety Validation**: Test execution with safety checks

# Run specific test suite
mvn test -Dtest=CalculatorTest

# Run BDD tests with taskfile (easier)
./task.sh test @smoke        # Run smoke tests
./task.sh test @integration  # Run integration tests
./task.sh test @calculator   # Run calculator tests

# Run BDD tests with Maven (traditional)
mvn test -Dtest=CucumberTestRunner -Dcucumber.filter.tags="@smoke"

# Run with specific TestNG suite
mvn test -DsuiteXmlFile=src/test/resources/testng.xml
```

### 5. Configure OCR Maximization (Optional)
The framework includes configurable OCR maximization for improved text recognition:

```properties
# In automation.properties
ocr.maximize.windows=true          # Enable/disable OCR maximization
ocr.restore.windows.after=true     # Restore window state after OCR
```

```bash
# Disable OCR maximization via system property
mvn test -Docr.maximize.windows=false

# Enable OCR maximization (default)
mvn test -Docr.maximize.windows=true
```

**OCR Maximization Behavior:**
- **Enabled (default):** Temporarily maximizes windows during OCR operations for better text recognition, then restores original state
- **Disabled:** Uses current window state for OCR operations - faster but potentially less accurate for small windows

## 📖 Usage Examples

### Enhanced Multi-Window Process Management
```java
// Initialize enhanced window management
ProcessManager processManager = ProcessManager.getInstance();
WindowController windowController = new WindowController();

// Launch and track File Explorer with multi-window support
ProcessInfo explorerProcess = processManager.launchAndTrack("file_explorer");

// Get all windows for the process
List<WindowInfo> windows = explorerProcess.getAllValidWindows();
logger.info("Found {} windows for explorer.exe", windows.size());

// Switch between different windows
for (int i = 0; i < windows.size(); i++) {
    WindowInfo window = windows.get(i);
    explorerProcess.setActiveWindow(window.getUniqueWindowId());
    windowController.focusWindow(explorerProcess);
    
    // Perform operations on this specific window
    windowController.sendKeys("Some input for window " + i);
    
    // Take screenshot of this specific window
    Rectangle bounds = windowController.getWindowBounds(explorerProcess);
    File screenshot = screenCapture.captureRegion(bounds);
}
```

### Managed Application Context Pattern
```java
// The "Focus First, Then Act" pattern ensures every action targets the correct application
ApplicationManager appManager = ApplicationManager.getInstance();

// Launch managed application
ProcessInfo calculator = appManager.ensureApplicationAvailable("calculator");

// All operations now work with managed context
appManager.performManagedAction(calculator, () -> {
    windowController.sendKey(KeyEvent.VK_2);
    windowController.sendKey(KeyEvent.VK_PLUS);
    windowController.sendKey(KeyEvent.VK_3);
    windowController.sendKey(KeyEvent.VK_ENTER);
});

// Verify result within managed context
String result = appManager.extractTextFromManagedApplication(calculator, displayRegion);
assert result.contains("5");
```

### Image-Based Automation
```java
ImageMatcher imageMatcher = new ImageMatcher();

// Find button on screen
File screenshot = screenCapture.captureScreen();
File buttonImage = new File("images/submit_button.png");
Rectangle match = imageMatcher.findImage(screenshot, buttonImage);

// Click the found button
if (match != null) {
    int centerX = match.x + match.width / 2;
    int centerY = match.y + match.height / 2;
    windowController.clickAt(centerX, centerY);
}
```

### Enhanced OCR with Confidence Scoring
```java
OCREngine ocrEngine = new OCREngine();

// Extract text with confidence metrics
Rectangle region = new Rectangle(100, 100, 300, 50);
File regionCapture = screenCapture.captureRegion(region);
OCRResult result = ocrEngine.extractTextWithConfidence(regionCapture);

// Validate result quality
if (result.getConfidence() > 80.0) {
    logger.info("High confidence OCR result: '{}' ({}%)", 
        result.getText(), result.getConfidence());
    assert result.getText().contains("Expected Text");
} else {
    logger.warn("Low confidence OCR result, may need image preprocessing");
    // Apply image enhancement and retry
    BufferedImage enhanced = ImageUtils.preprocessForOCR(regionCapture);
    result = ocrEngine.extractTextWithConfidence(enhanced);
}
```

### BDD Test Example with Managed Application Context
```gherkin
Feature: Enterprise Application Automation with Managed Context
  
  Scenario: Multi-Window File Explorer Management
    When I have the application "file_explorer" open
    And I press key combination "ctrl+n" in managed application "file_explorer"
    And I wait for 2 seconds
    Then the application "file_explorer" should be running
    And managed application "file_explorer" should have multiple windows
    
  Scenario: Cross-Application Data Transfer
    When I have the application "sap_gui" open
    And I type "VA01" in managed application "sap_gui"
    And I press "ENTER" key in managed application "sap_gui"
    And I take a screenshot of managed application "sap_gui" with name "sap_transaction_started"
    Then I should see text "Create Sales Order" in managed application "sap_gui"
    
  Scenario: Calculator with Managed Context
    Given I have the application "calculator" open
    When I press "2" key in managed application "calculator"
    And I press "PLUS" key in managed application "calculator"
    And I press "3" key in managed application "calculator"
    And I press "ENTER" key in managed application "calculator"
    Then I should see text "5" in managed application "calculator"
```

## Configuration

### Application Configuration with Multi-Window Support (`applications.yml`)
```yaml
# Enhanced configuration for multi-window applications
calculator:
  window_title: "Calculator"
  process_name: "calc.exe"
  wait_timeout: 30
  multi_window_support: false
  images:
    number_1: "calc_1.png"
    number_2: "calc_2.png"
    plus: "calc_plus.png"
    equals: "calc_equals.png"
  regions:
    display: {x: 50, y: 50, width: 300, height: 100}

file_explorer:
  window_title: "File Explorer"
  process_name: "explorer.exe"
  wait_timeout: 30
  multi_window_support: true
  delegation_support: true  # Handles system process delegation
  images:
    new_folder: "explorer_new_folder.png"
    address_bar: "explorer_address_bar.png"
  regions:
    content_area: {x: 200, y: 100, width: 800, height: 600}
    navigation_pane: {x: 0, y: 100, width: 200, height: 600}

sap_gui:
  window_title: "SAP Easy Access"
  process_name: "saplogon.exe"
  wait_timeout: 60
  multi_window_support: true
  multi_session_support: true
  images:
    transaction_field: "sap_transaction_field.png"
    login_button: "sap_login_button.png"
    session_manager: "sap_session_manager.png"
  regions:
    status_bar: {x: 0, y: 550, width: 800, height: 30}
    main_content: {x: 0, y: 100, width: 800, height: 450}
  credentials:
    username: "${SAP_USERNAME}"
    password: "${SAP_PASSWORD}"
    client: "100"
  transactions:
    va01: "Create Sales Order"
    mm01: "Create Material"
    se38: "ABAP Editor"
```

### Enhanced Framework Settings (`automation.properties`)
```properties
# OCR Configuration with Advanced Settings
tesseract.path=C:\\Program Files\\Tesseract-OCR\\tesseract.exe
tesseract.dataPath=C:\\Program Files\\Tesseract-OCR\\tessdata
ocr.language=eng
ocr.dpi=300
ocr.psm=6
ocr.preprocessing.enabled=true
ocr.confidence.threshold=70.0
# Professional OCR Maximization - temporarily maximizes windows during OCR for better accuracy
ocr.maximize.windows=true
ocr.restore.windows.after=true

# Image Matching with Computer Vision
image.similarity.threshold=0.8
image.matching.timeout=10
image.preprocessing.enabled=true
image.debug.enabled=false
image.cache.enabled=true
image.cache.size=100

# Enhanced Window Management
window.focus.timeout=30
window.focus.retries=3
window.interaction.delay=500
window.enum.timeout=5
multi.window.support=true

# Process Management
process.launch.timeout=30
process.delegation.handling=true
process.cleanup.on.exit=true
process.force.termination.timeout=10

# Performance and Reliability
parallel.execution.enabled=false
max.parallel.threads=3
test.execution.timeout=300
retry.count=2
retry.delay=1000

# Reporting and Evidence
reports.output.dir=target/reports
reports.screenshots=true
reports.ocr.results=true
reports.performance.metrics=true
evidence.capture.on.failure=true
evidence.capture.on.success=false

# Debug and Development
debug.enabled=false
debug.screenshot.on.step=false
debug.highlight.matches=true
debug.save.intermediate.images=false
```

## Test Data Management

### CSV Test Data
```csv
testCase,application,action,input,expected
TC001,Calculator,addition,2+3,5
TC002,Calculator,multiplication,4*5,20
```

### YAML Test Data
```yaml
test_scenarios:
  login_tests:
    - application: "SAP"
      username: "testuser"
      password: "password123"
      expected: "SAP Easy Access"
```

### Using Test Data in Tests
```java
@DataProvider(name = "calculatorData")
public Object[][] getCalculatorData() {
    List<Map<String, String>> data = TestDataProvider.loadCSVData("calculator_tests.csv");
    return TestDataProvider.toDataProviderFormat(data);
}

@Test(dataProvider = "calculatorData")
public void testCalculatorOperations(Map<String, String> testData) {
    // Use testData.get("input"), testData.get("expected"), etc.
}
```

## Reporting

The framework generates comprehensive HTML reports with:
- Test execution summary
- Screenshots at each step
- OCR extraction results
- Image matching details
- Performance metrics
- Error details and stack traces

Reports are generated in `target/reports/extent-report.html`

### Advanced Features

#### Managed Application Context with Multi-Window Support
```java
// Handle complex applications like SAP GUI with multiple sessions
ApplicationManager appManager = ApplicationManager.getInstance();
ProcessInfo sapGui = appManager.ensureApplicationAvailable("sap_gui");

// Get all SAP windows/sessions
List<WindowInfo> sapSessions = sapGui.getAllValidWindows();
logger.info("Found {} SAP sessions", sapSessions.size());

// Switch to specific session by title pattern
WindowInfo orderSession = sapSessions.stream()
    .filter(w -> w.getCurrentWindowTitle().contains("VA01"))
    .findFirst()
    .orElseThrow(() -> new WindowNotFoundException("VA01 session not found"));

// Activate the specific session
sapGui.setActiveWindow(orderSession.getUniqueWindowId());
windowController.focusWindow(sapGui);

// Now all operations target the correct SAP session
```

#### Enterprise Process Delegation Handling
```java
// Handle system processes that delegate to existing instances (like explorer.exe)
ProcessInfo explorer = processManager.launchAndTrack("file_explorer");

// Framework automatically handles delegation scenarios:
// 1. Launch process exits immediately
// 2. Delegate to existing system explorer.exe
// 3. Track the actual process PID
// 4. Discover and manage all File Explorer windows

logger.info("Explorer PID: {}, Windows: {}", 
    explorer.getProcessId(), 
    explorer.getAllValidWindows().size());
```

#### Smart Waiting Strategies with Managed Context
```java
// Wait for specific application window to appear
WaitUtils.waitForWindow("SAP Easy Access", 30);

// Wait for image to appear in managed application context
appManager.performManagedAction(calculator, () -> {
    WaitUtils.waitForImage("equals_button.png", 15);
});

// Wait for text to appear with confidence threshold
WaitUtils.waitForTextWithConfidence("Transaction completed", region, 20, 80.0);

// Wait for managed application to be ready
WaitUtils.waitForManagedApplicationReady("sap_gui", 60);
```

#### Performance Monitoring and Metrics
```java
// Monitor test execution performance
PerformanceMonitor.measureExecution("SAP_Login", () -> {
    appManager.performManagedAction(sapGui, () -> {
        loginToSAP(username, password);
    });
});

// Track window switching performance
long startTime = System.currentTimeMillis();
for (WindowInfo window : multiWindowProcess.getAllValidWindows()) {
    multiWindowProcess.setActiveWindow(window.getUniqueWindowId());
    windowController.focusWindow(multiWindowProcess);
}
long switchingTime = System.currentTimeMillis() - startTime;
logger.info("Window switching completed in {}ms", switchingTime);
```

### Enterprise Multi-Application Workflows with Managed Context
```java
@Test
public void testEnterpriseWorkflowWithManagedContext() {
    ApplicationManager appManager = ApplicationManager.getInstance();
    
    // Step 1: Extract data from mainframe terminal
    ProcessInfo mainframe = appManager.ensureApplicationAvailable("as400_terminal");
    appManager.performManagedAction(mainframe, () -> {
        windowController.sendKeys("/NINV001");  // Navigate to inventory
        windowController.sendKey(KeyEvent.VK_ENTER);
        // Extract inventory data using OCR
    });
    
    // Step 2: Process data in SAP with multi-session support
    ProcessInfo sapGui = appManager.ensureApplicationAvailable("sap_gui");
    
    // Switch to specific SAP session if multiple exist
    List<WindowInfo> sapWindows = sapGui.getAllValidWindows();
    WindowInfo orderSession = sapWindows.stream()
        .filter(w -> w.getCurrentWindowTitle().contains("VA01"))
        .findFirst().orElse(sapWindows.get(0));
    
    sapGui.setActiveWindow(orderSession.getUniqueWindowId());
    
    appManager.performManagedAction(sapGui, () -> {
        // Create sales order using extracted data
        windowController.sendKeys("VA01");
        windowController.sendKey(KeyEvent.VK_ENTER);
        // Fill order details...
    });
    
    // Step 3: Validate results in Excel with managed context
    ProcessInfo excel = appManager.ensureApplicationAvailable("excel");
    appManager.performManagedAction(excel, () -> {
        // Open report file and validate data
        windowController.sendKey(KeyEvent.VK_CONTROL, KeyEvent.VK_O);
        // Navigate and validate...
    });
    
    // Step 4: Generate comprehensive report with evidence
    generateWorkflowReport(mainframe, sapGui, excel);
}

private void generateWorkflowReport(ProcessInfo... processes) {
    for (ProcessInfo process : processes) {
        // Capture final state of each application
        File evidence = screenCapture.captureProcessWindow(process);
        testResult.addEvidence(process.getApplicationName(), evidence);
    }
}
```

## 🐛 Troubleshooting

### Common Issues

1. **Tesseract Not Found**
   - Verify Tesseract installation path in properties
   - Ensure tessdata directory contains language files
   - Check PATH environment variable

2. **Images Not Matching**
   - Adjust similarity threshold in configuration
   - Capture new template images at correct resolution
   - Check for screen scaling issues (DPI settings)

3. **Window Not Found**
   - Verify exact window title
   - Check if application is running
   - Increase wait timeout

4. **OCR Accuracy Issues**
   - Increase image DPI for capture
   - Use image preprocessing options
   - Verify correct language data files

### Debug Mode
Enable debug logging by adding to `automation.properties`:
```properties
logging.level=DEBUG
debug.screenshots=true
debug.ocr.intermediate=true
```

## ⚠️ UWP/Modern App Limitations and Known Issues

### Critical: UWP Application Delegation Problem

**IMPORTANT**: The framework currently has significant limitations when automating UWP (Universal Windows Platform) and modern Windows applications such as Windows 11 Calculator, Paint, Notepad, Microsoft Store apps, and Progressive Web Apps (PWAs).

### Root Cause: Process Delegation Architecture

UWP and modern Windows applications use a delegation architecture that causes process tracking failures:

1. **Launcher Process**: When you execute `calc.exe` or `mspaint.exe`, Windows starts a launcher process
2. **Immediate Exit**: The launcher process exits immediately (within milliseconds)
3. **Runtime Delegation**: The actual application runs under a different PID managed by the UWP runtime or Windows Application Manager
4. **PID Mismatch**: Our framework tracks the launcher PID, but the real application windows belong to a different process

### Affected Applications

#### Known Problematic Applications:
- **Windows 11 Calculator** (`calc.exe`) - Delegates to UWP Calculator runtime
- **Windows 11 Paint** (`mspaint.exe`) - Delegates to UWP Paint app  
- **Windows 11 Notepad** (`notepad.exe`) - Delegates to UWP Notepad app
- **Microsoft Store Apps** - All Store-distributed applications
- **Progressive Web Apps (PWAs)** - Web applications installed as apps
- **Modern Microsoft Office apps** - Store versions of Office applications

#### Applications That Work Reliably:
- **Legacy Win32 Applications** - Traditional desktop applications (SAP GUI, Oracle Forms, etc.)
- **Windows Explorer** (`explorer.exe`) - System shell application
- **Command Prompt** (`cmd.exe`) - Console applications  
- **PowerShell** - Console applications
- **Third-party applications** - Most non-Microsoft desktop applications

### Technical Details and Error Symptoms

#### Typical Error Pattern in Logs:
```
INFO  - Process launched successfully: calc.exe, PID: 720252
WARN  - Launched process PID 720252 appears to have exited quickly - may have delegated or failed
DEBUG - Attempting to find windows for PID: 720252
DEBUG - Found 0 windows for PID 720252
ERROR - Failed to focus window: No windows found for process PID 720252
WARN  - Failed to extract text using managed application context: Window focus failed
```

#### What Happens:
1. Framework launches `calc.exe` → gets PID 720252
2. Launcher process exits immediately → PID 720252 no longer exists
3. Real Calculator app runs under different PID (e.g., PID 845123) managed by UWP runtime
4. Framework searches for windows belonging to PID 720252 → finds 0 windows
5. All window operations fail because we're tracking the wrong process

### Current Workarounds and Alternatives

#### 1. Legacy Application Mode (Recommended)
For applications that have both legacy and modern versions, prefer the legacy version:

```yaml
# Use legacy Notepad instead of UWP Notepad
notepad_legacy:
  window_title: "Untitled - Notepad"
  process_name: "notepad.exe"
  launch_command: "C:\\Windows\\System32\\notepad.exe"  # Force legacy version
  
# Use alternative applications
text_editor:
  window_title: "WordPad"
  process_name: "wordpad.exe"
  launch_command: "wordpad.exe"
```

#### 2. Window Title-Based Automation
For UWP apps that must be used, rely on window title matching instead of PID tracking:

```java
// Bypass PID tracking for UWP apps
WindowController windowController = new WindowController();
if (windowController.focusWindowByTitle("Calculator")) {
    // Proceed with automation using window title instead of PID
    windowController.sendKeys("2+3=");
} else {
    logger.error("Calculator window not found by title");
}
```

#### 3. Manual Launch Strategy
Launch the application manually and use window detection:

```gherkin
# Manual launch approach for UWP apps
Given the user has manually launched "Calculator"
When I wait for window with title "Calculator" to appear with timeout 10 seconds  
Then I should be able to automate window with title "Calculator"
```

#### 4. Alternative Applications
Use functionally equivalent applications that don't use UWP delegation:

| UWP/Modern App | Alternative | Status |
|----------------|-------------|---------|
| Windows 11 Calculator | SpeedCrunch, Calc.exe (legacy mode) | ✅ Works |
| Windows 11 Paint | Paint.NET, GIMP | ✅ Works |
| Windows 11 Notepad | Notepad++ | ✅ Works |
| Microsoft Store Apps | Desktop equivalent versions | ✅ Works |

### Roadmap for UWP/Modern App Support

#### Phase 1: Enhanced Window Discovery (In Progress)
- **Cross-PID Window Discovery**: Search for windows by title/class regardless of PID
- **Window Class-Based Matching**: Use window class names for application identification
- **Application Model ID Support**: Integrate with Windows Application Model APIs

#### Phase 2: UWP Runtime Integration (Planned)
- **Windows Runtime API Integration**: Direct integration with Windows Runtime for UWP app management
- **Package Family Name Support**: Use package identifiers for UWP application tracking
- **WinUI 3 Support**: Enhanced support for modern Windows applications

#### Phase 3: Modern App Architecture Support (Future)
- **PWA Detection and Automation**: Support for Progressive Web Applications
- **Microsoft Store Integration**: Direct automation of Store-distributed applications
- **Edge WebView2 Integration**: Automation of web-based application components

### Configuration Options for UWP Handling

Add these properties to `automation.properties` for UWP application handling:

```properties
# UWP/Modern App Support (Experimental)
uwp.support.enabled=false                    # Enable experimental UWP support
uwp.window.discovery.by.title=true          # Use window title instead of PID
uwp.process.delegation.timeout=5000         # Wait time for delegation (ms)
uwp.fallback.to.title.matching=true         # Fallback to title-based window finding
uwp.legacy.mode.preferred=true              # Prefer legacy versions when available

# Enhanced Window Discovery
window.discovery.cross.pid=true             # Search windows across all processes
window.discovery.by.class=true              # Enable window class-based discovery
window.matching.fuzzy.title=true            # Enable fuzzy title matching for UWP apps
```

### Developer Guidance for UWP Applications

#### When to Use This Framework:
- **Enterprise Desktop Applications**: SAP GUI, Oracle Forms, legacy applications
- **Development Tools**: IDEs, database tools, legacy enterprise software
- **Cross-Application Workflows**: Integration testing across multiple desktop applications

#### When to Consider Alternatives:
- **UWP/Modern Apps**: Use UI Automation APIs, Power Automate, or Playwright
- **Web Applications**: Use Selenium, Playwright, or similar web automation frameworks
- **Mobile Applications**: Use Appium or platform-specific automation frameworks

#### Alternative Automation Approaches for UWP:
```java
// Consider these alternatives for UWP automation:

// 1. Windows UI Automation API
// https://docs.microsoft.com/en-us/windows/win32/winauto/entry-uiauto-win32

// 2. Playwright for PWAs
// https://playwright.dev/

// 3. Power Automate for Microsoft Store apps
// https://powerautomate.microsoft.com/

// 4. Microsoft Application Driver (WinAppDriver)
// https://github.com/Microsoft/WinAppDriver
```

### Best Practices for Mixed Environments

When working in environments with both traditional and modern applications:

1. **Application Assessment**: Identify which applications are UWP-based before creating automation
2. **Hybrid Approach**: Use this framework for traditional apps, alternative tools for UWP apps
3. **Clear Documentation**: Document which applications require special handling
4. **Testing Strategy**: Separate test suites for different application types
5. **User Training**: Educate users on the limitations and workarounds

### Getting Help with UWP Issues

If you encounter UWP-related issues:

1. **Check Application Type**: Verify if the application is UWP-based using Task Manager
2. **Review Logs**: Look for "delegation" or "process exited quickly" messages
3. **Try Alternatives**: Use legacy versions or alternative applications where possible
4. **Report Issues**: Help us improve UWP support by reporting specific applications and error patterns

This framework excels at automating traditional Windows desktop applications and will continue to evolve to better support modern Windows application architectures.

## 🤝 Contributing

1. Fork the repository
2. Create feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'Add amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Open Pull Request

### Code Standards
- Follow Java naming conventions
- Add JavaDoc comments for public methods
- Include unit tests for new features
- Update documentation for configuration changes

## 🔒 **Safety Best Practices and Guidelines**

### **Critical Safety Rules - ALWAYS FOLLOW**

#### **1. Managed Application Context Requirements**
- **NEVER use global keyboard operations** - All input must target specific managed applications
- **ALWAYS register applications** before performing operations: `When I register the newest running process "app.exe" as managed application "MyApp"`
- **ALWAYS verify application context** before actions: `When I have the application "calculator" open`

#### **2. Prohibited Dangerous Operations**
```gherkin
# ❌ NEVER USE - Dangerous Global Operations
When I press key combination "Alt+F4"           # Could close ANY window
When I press "ENTER" key                        # Could activate anything
When I type "shutdown /s /t 0"                  # Could shut down system
When I press key combination "Win+R"            # Could open Run dialog globally

# ✅ ALWAYS USE - Safe Managed Application Operations  
When I press key combination "Alt+F4" in managed application "calculator"
When I press "ENTER" key in managed application "notepad"
When I type "VA01" in managed application "sap_gui"
When I terminate the managed application "calculator"
```

#### **3. Step Sequencing Requirements**
**CRITICAL**: Always follow this sequence for launching applications:
1. Launch or focus application
2. Verify process is running
3. Register as managed application
4. THEN perform operations

```gherkin
# ✅ CORRECT Sequence
Given I have the application "file_explorer" open
And I press "ALT+D" key combination in managed application "WindowsExplorer"
And I type "cmd" in managed application "WindowsExplorer"  
And I press "ENTER" key in managed application "WindowsExplorer"
Then the process "cmd.exe" should be running
When I register the newest running process "cmd.exe" as managed application "CommandPrompt"
And I wait for text ">" to appear in managed application "CommandPrompt" with timeout 5 seconds

# ❌ WRONG Sequence (Will Fail)
And I wait for text ">" to appear in managed application "CommandPrompt"  # ERROR: CommandPrompt not registered yet
Then the process "cmd.exe" should be running
When I register the newest running process "cmd.exe" as managed application "CommandPrompt"
```

#### **4. System Process Protection**
The framework automatically protects these critical system processes:
- **Windows Explorer** (`explorer.exe`) - Protected from termination
- **System Services** - Cannot be managed or terminated
- **Windows Shell** - Protected from interference

#### **5. Error Recovery and Cleanup**
- Framework automatically handles process cleanup on test completion
- Use `When I terminate the managed application "AppName"` for clean closure
- Avoid force-killing system processes
- Always capture evidence before terminating applications

#### **6. Multi-Window Application Safety**
```gherkin
# Safe multi-window management
When I have the application "file_explorer" open
Then managed application "file_explorer" should have multiple windows
When I focus window 0 of managed application "file_explorer"
When I type "documents" in managed application "file_explorer" window 0
```

#### **7. Cross-Application Workflow Safety**
```gherkin
# Safe workflow across multiple applications
Given I have the application "sap_gui" open
And I have the application "excel" open
When I extract data from managed application "sap_gui"
And I process data in managed application "excel"
Then I validate results in managed application "excel"
# Clean termination
When I terminate the managed application "excel"
When I terminate the managed application "sap_gui"
```

#### **8. Testing and Validation Requirements**
- **ALWAYS test on non-production systems first**
- **NEVER run automation on systems with critical data**
- **ALWAYS use test data, never production data**
- **VERIFY step definitions** before creating new scenarios
- **REVIEW feature files** for dangerous global operations before execution

#### **9. Emergency Safety Measures**
If you accidentally create dangerous automation:
1. **Immediately stop test execution** (Ctrl+C in terminal)
2. **Check all feature files** for global operations
3. **Convert to managed application context** before running again
4. **Test in isolated environment** before production use

### **Framework Safety Validation Commands**
```bash
# Validate feature files for safety (recommended before execution)
grep -r "I press.*key" src/test/resources/features/ | grep -v "managed application"
grep -r "I type" src/test/resources/features/ | grep -v "managed application"  
grep -r "key combination" src/test/resources/features/ | grep -v "managed application"

# Should return NO results if all operations are properly scoped
```

### **Technical Safety Enhancements Implemented**

#### **Enhanced Window Discovery (WindowController.java)**
- **Three-Stage Fallback Strategy**: 
  1. Standard window enumeration for regular applications
  2. Enhanced console window detection for CMD/PowerShell
  3. Less restrictive window finder for problematic applications
- **Robust PID-to-Window Mapping**: Ensures reliable window discovery even for console applications
- **Window Context Validation**: Verifies window belongs to target process before operations

#### **System Protection (ProcessManager.java)**
- **Critical Application Protection**: Automatically exempts system-critical applications from termination
- **Safe Cleanup Operations**: `terminateAll()` method protects Windows Explorer and system services
- **Process Delegation Handling**: Proper management of system processes that delegate to existing instances

```java
// Example of protected system processes
private boolean isSystemCriticalApplication(String applicationName) {
    return "WindowsExplorer".equals(applicationName) || 
           "explorer.exe".equals(applicationName) ||
           "winlogon.exe".equals(applicationName) ||
           "csrss.exe".equals(applicationName);
}
```

#### **Application Context Safety (ApplicationStepDefinitions.java)**
- **Managed Application Registry**: All operations require registered application context
- **Context Validation**: Every action validates target application exists and is accessible
- **Safe Termination Methods**: Graceful application closure with fallback to controlled termination

#### **Input Safety (InputStepDefinitions.java)**
- **Application-Scoped Operations**: All keyboard/mouse operations target specific managed applications
- **Context Focus Verification**: Ensures target application has focus before input operations
- **Key Combination Validation**: Prevents dangerous system-level key combinations outside managed context

## Managed Application Context BDD Steps

The framework provides comprehensive BDD step definitions that follow the "Focus First, Then Act" pattern for enterprise-grade automation:

### Application Lifecycle Management
```gherkin
# Launch and manage applications
When I have the application "calculator" open
When I have the application "sap_gui" open
Then the application "file_explorer" should be running
Then managed application "sap_gui" should have multiple windows

# Application window management
When I maximize managed application "calculator"
When I minimize managed application "file_explorer"
When I resize managed application "sap_gui" to 1024x768
When I move managed application "calculator" to position 100, 100
```

### Input Operations with Managed Context
```gherkin
# Text input with managed context
When I type "Hello World" in managed application "notepad"
When I type "VA01" in managed application "sap_gui"

# Key operations with managed context
When I press "ENTER" key in managed application "calculator"
When I press "F8" key in managed application "sap_gui"
When I press key combination "ctrl+n" in managed application "file_explorer"
When I press key combination "alt+f4" in managed application "calculator"

# Mouse operations with managed context
When I click at coordinates 150, 200 in managed application "calculator"
```

### Multi-Window Operations
```gherkin
# Multi-window support
When I focus window 0 of managed application "file_explorer"
When I focus window 1 of managed application "sap_gui"
When I type "data" in managed application "file_explorer" window 0
When I press "F5" key in managed application "sap_gui" window 1
```

### Verification and Validation
```gherkin
# Text validation with managed context
Then I should see text "5" in managed application "calculator"
Then I should see text "Transaction completed" in managed application "sap_gui"

# Wait operations with managed context
When I wait for text "Ready" to appear in managed application "sap_gui"
When I wait for text "Ready" to appear in managed application "sap_gui" with timeout 30 seconds
```

### Evidence and Reporting
```gherkin
# Screenshot capture with managed context
When I take a screenshot of managed application "sap_gui"
When I take a screenshot of managed application "file_explorer" window 1
When I take a screenshot of managed application "sap_gui" with name "transaction_complete"
When I capture evidence of managed application "calculator" with description "Final calculation result"
```

## 🧪 License

This project is not for personal nor commercial use.

## 🙏 Acknowledgments

- SikuliX team for computer vision capabilities
- Tesseract OCR team for text recognition
- ExtentReports for beautiful test reporting
- TestNG team for robust testing framework

## Support

For questions, issues, or contributions:
- Create an issue in the repository
- Check existing documentation
- Review troubleshooting section

---

**Happy Testing!**

*This framework makes desktop automation accessible for any Windows application, from legacy mainframes to modern cloud applications.*
