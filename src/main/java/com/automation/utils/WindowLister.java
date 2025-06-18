package com.automation.utils;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.ptr.IntByReference;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple utility to list all current windows with detailed information
 * for debugging window selection issues.
 */
public class WindowLister {
    
    private static final User32 user32 = User32.INSTANCE;
    
    public static class WindowInfo {
        public WinDef.HWND handle;
        public String title;
        public String className;
        public int processId;
        public boolean isVisible;
        public boolean isEnabled;
        public Rectangle bounds;
        
        @Override
        public String toString() {
            return String.format("HWND: %-10s | PID: %-6d | Visible: %-5s | Enabled: %-5s | Title: %-30s | Class: %-20s | Bounds: %dx%d at (%d,%d)",
                handle.toString(), processId, isVisible, isEnabled, 
                title.length() > 30 ? title.substring(0, 27) + "..." : title,
                className.length() > 20 ? className.substring(0, 17) + "..." : className,
                bounds.width, bounds.height, bounds.x, bounds.y);
        }
    }
    
    public static List<WindowInfo> getAllWindows() {
        List<WindowInfo> windows = new ArrayList<>();
        
        user32.EnumWindows(new WinUser.WNDENUMPROC() {
            @Override
            public boolean callback(WinDef.HWND hWnd, com.sun.jna.Pointer data) {
                WindowInfo info = new WindowInfo();
                info.handle = hWnd;
                
                // Get window title
                char[] titleBuffer = new char[512];
                user32.GetWindowText(hWnd, titleBuffer, 512);
                info.title = Native.toString(titleBuffer);
                
                // Get window class name
                char[] classBuffer = new char[256];                user32.GetClassName(hWnd, classBuffer, 256);
                info.className = Native.toString(classBuffer);
                
                // Get process ID
                IntByReference processIdRef = new IntByReference();
                user32.GetWindowThreadProcessId(hWnd, processIdRef);
                info.processId = processIdRef.getValue();
                
                // Get window state
                info.isVisible = user32.IsWindowVisible(hWnd);
                info.isEnabled = user32.IsWindowEnabled(hWnd);                // Get window bounds - use client area for console windows, full window for others
                boolean isConsoleWindow = info.className != null && (
                    info.className.equals("CASCADIA_HOSTING_WINDOW_CLASS") ||
                    info.className.equals("ConsoleWindowClass") ||
                    info.className.contains("Console")
                );
                  if (isConsoleWindow) {
                    // For console windows, calculate client area bounds manually
                    WinDef.RECT windowRect = new WinDef.RECT();
                    user32.GetWindowRect(hWnd, windowRect);
                    
                    WinDef.RECT clientRect = new WinDef.RECT();
                    if (user32.GetClientRect(hWnd, clientRect)) {
                        // Calculate client area in screen coordinates
                        // Client rect is relative to window, so we need to offset by window position
                        // and account for window decorations
                        
                        int windowWidth = windowRect.right - windowRect.left;
                        int windowHeight = windowRect.bottom - windowRect.top;
                        int clientWidth = clientRect.right - clientRect.left;
                        int clientHeight = clientRect.bottom - clientRect.top;
                        
                        // Calculate border sizes
                        int horizontalBorder = (windowWidth - clientWidth) / 2;
                        int titleBarHeight = windowHeight - clientHeight - horizontalBorder;
                        
                        // Calculate client area position in screen coordinates
                        int clientX = windowRect.left + horizontalBorder;
                        int clientY = windowRect.top + titleBarHeight;
                        
                        info.bounds = new Rectangle(clientX, clientY, clientWidth, clientHeight);
                    } else {
                        // Fallback to window bounds with estimated adjustment if GetClientRect fails
                        int titleBarHeight = (info.className.equals("CASCADIA_HOSTING_WINDOW_CLASS")) ? 30 : 25;
                        int borderWidth = 8;
                        
                        info.bounds = new Rectangle(
                            windowRect.left + borderWidth, 
                            windowRect.top + titleBarHeight, 
                            Math.max(100, (windowRect.right - windowRect.left) - (2 * borderWidth)), 
                            Math.max(100, (windowRect.bottom - windowRect.top) - titleBarHeight - borderWidth)
                        );
                    }
                } else {
                    // For non-console windows, use full window bounds
                    WinDef.RECT rect = new WinDef.RECT();
                    user32.GetWindowRect(hWnd, rect);
                    info.bounds = new Rectangle(rect.left, rect.top, rect.right - rect.left, rect.bottom - rect.top);
                }
                
                windows.add(info);
                return true; // Continue enumeration
            }
        }, null);
        
        return windows;
    }
    
    public static void main(String[] args) {
        System.out.println("=== ALL WINDOWS ENUMERATION ===");
        System.out.println();
        
        List<WindowInfo> allWindows = getAllWindows();
        
        // Sort by process ID for easier reading
        allWindows.sort((w1, w2) -> Integer.compare(w1.processId, w2.processId));
        
        int totalWindows = allWindows.size();
        int visibleWindows = 0;
        int enabledWindows = 0;
        
        for (WindowInfo window : allWindows) {
            if (window.isVisible) visibleWindows++;
            if (window.isEnabled) enabledWindows++;
            
            System.out.println(window.toString());
        }
        
        System.out.println();
        System.out.println("=== SUMMARY ===");
        System.out.println("Total windows: " + totalWindows);
        System.out.println("Visible windows: " + visibleWindows);
        System.out.println("Enabled windows: " + enabledWindows);
        
        // Show Explorer windows specifically
        System.out.println();
        System.out.println("=== EXPLORER WINDOWS ===");
        boolean foundExplorer = false;
        for (WindowInfo window : allWindows) {
            if (window.className.contains("Cabinet") || 
                window.className.contains("Explorer") ||
                window.className.contains("SysListView32") ||
                window.title.toLowerCase().contains("explorer") ||
                window.title.toLowerCase().contains("file") ||
                window.title.toLowerCase().contains("folder")) {
                System.out.println("🔍 EXPLORER: " + window.toString());
                foundExplorer = true;
            }
        }
        
        if (!foundExplorer) {
            System.out.println("No obvious Explorer windows found.");
        }
        
        // Show windows by specific process if requested
        if (args.length > 0) {
            try {
                int targetPid = Integer.parseInt(args[0]);
                System.out.println();
                System.out.println("=== WINDOWS FOR PID " + targetPid + " ===");
                boolean foundForPid = false;
                for (WindowInfo window : allWindows) {
                    if (window.processId == targetPid) {
                        System.out.println("🎯 PID " + targetPid + ": " + window.toString());
                        foundForPid = true;
                    }
                }
                if (!foundForPid) {
                    System.out.println("No windows found for PID " + targetPid);
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid PID argument: " + args[0]);
            }
        }
        
        // Show foreground window
        WinDef.HWND foregroundWindow = user32.GetForegroundWindow();
        if (foregroundWindow != null) {
            System.out.println();
            System.out.println("=== FOREGROUND WINDOW ===");
            for (WindowInfo window : allWindows) {
                if (window.handle.equals(foregroundWindow)) {
                    System.out.println("⭐ FOREGROUND: " + window.toString());
                    break;
                }
            }
        }
    }
}
