package com.automation.tests;

import com.automation.core.*;
import com.automation.models.ManagedApplicationContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Diagnostic test to investigate Notepad launch issues
 */
public class NotepadDiagnosticTest {
    
    @Test
    @DisplayName("Diagnostic: Manual Notepad Launch Test")
    public void testManualNotepadLaunch() {
        System.out.println("=== NOTEPAD DIAGNOSTIC TEST ===");
        
        try {
            // Test 1: Try ProcessManager launch
            System.out.println("\n1. Testing ProcessManager launch...");
            ManagedApplicationContext result = ProcessManager.getInstance().launchAndTrackApplication("notepad");
            
            if (result != null) {
                System.out.println("✓ ProcessManager claims success:");
                System.out.println("  - PID: " + result.getProcessId());
                System.out.println("  - Window Title: " + result.getWindowTitle());
                System.out.println("  - Is Active: " + result.isActive());
                System.out.println("  - Is Terminated: " + result.isTerminated());
                
                // Test 2: Try to focus the window
                System.out.println("\n2. Testing window focus...");
                WindowController windowController = new WindowController();
                boolean focused = windowController.focusWindow(result);
                System.out.println("  - Focus Result: " + focused);
                
                // Test 3: Check if process is actually running
                System.out.println("\n3. Testing process validation...");
                boolean stillRunning = result.isProcessStillRunning();
                System.out.println("  - Process Still Running: " + stillRunning);
                
                // Test 4: Try direct process launch
                System.out.println("\n4. Testing direct process launch...");
                try {
                    ProcessBuilder pb = new ProcessBuilder("notepad.exe");
                    Process directProcess = pb.start();
                    Thread.sleep(2000); // Give it time to start
                    System.out.println("  - Direct launch alive: " + directProcess.isAlive());
                    if (directProcess.isAlive()) {
                        System.out.println("  - Direct launch PID: " + directProcess.pid());
                        directProcess.destroyForcibly(); // Clean up
                    }
                } catch (Exception e) {
                    System.out.println("  - Direct launch failed: " + e.getMessage());
                }
                
            } else {
                System.out.println("✗ ProcessManager returned null - launch failed");
                fail("ProcessManager failed to launch Notepad");
            }
            
        } catch (Exception e) {
            System.out.println("✗ Test failed with exception: " + e.getMessage());
            e.printStackTrace();
            fail("Diagnostic test failed: " + e.getMessage());
        }
        
        System.out.println("\n=== END DIAGNOSTIC ===");
    }
    
    @Test
    @DisplayName("Diagnostic: Check Current Running Processes")
    public void testCheckRunningProcesses() {
        System.out.println("\n=== PROCESS CHECK ===");
        
        try {
            // Check what notepad processes are currently running
            ProcessBuilder pb = new ProcessBuilder("tasklist", "/FI", "IMAGENAME eq notepad.exe");
            Process process = pb.start();
            process.waitFor();
            
            System.out.println("Current notepad.exe processes:");
            java.util.Scanner scanner = new java.util.Scanner(process.getInputStream());
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.contains("notepad") || line.contains("INFO:") || line.contains("===")) {
                    System.out.println("  " + line);
                }
            }
            scanner.close();
            
            // Also check for TextInputHost (modern Notepad)
            ProcessBuilder pb2 = new ProcessBuilder("tasklist", "/FI", "IMAGENAME eq TextInputHost.exe");
            Process process2 = pb2.start();
            process2.waitFor();
            
            System.out.println("\nCurrent TextInputHost.exe processes:");
            java.util.Scanner scanner2 = new java.util.Scanner(process2.getInputStream());
            while (scanner2.hasNextLine()) {
                String line = scanner2.nextLine();
                if (line.contains("TextInputHost") || line.contains("INFO:") || line.contains("===")) {
                    System.out.println("  " + line);
                }
            }
            scanner2.close();
            
        } catch (Exception e) {
            System.out.println("Process check failed: " + e.getMessage());
        }
        
        System.out.println("=== END PROCESS CHECK ===");
    }
}
