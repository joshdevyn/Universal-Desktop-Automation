# Template Images Guide

This directory contains template images used for image-based automation testing. These images serve as reference patterns that the framework uses to identify and interact with UI elements.

## 📁 Directory Structure

```
images/
├── calculator/           # Windows Calculator app images
│   ├── buttons/
│   │   ├── calc_0.png
│   │   ├── calc_1.png
│   │   ├── calc_2.png
│   │   ├── calc_3.png
│   │   ├── calc_4.png
│   │   ├── calc_5.png
│   │   ├── calc_6.png
│   │   ├── calc_7.png
│   │   ├── calc_8.png
│   │   ├── calc_9.png
│   │   ├── calc_plus.png
│   │   ├── calc_minus.png
│   │   ├── calc_multiply.png
│   │   ├── calc_divide.png
│   │   ├── calc_equals.png
│   │   ├── calc_clear.png
│   │   ├── calc_ce.png
│   │   ├── calc_backspace.png
│   │   ├── calc_ms.png
│   │   ├── calc_mr.png
│   │   ├── calc_mc.png
│   │   └── calc_mplus.png
│   ├── display/
│   │   ├── calc_display.png
│   │   └── calc_result_area.png
│   └── menus/
│       ├── calc_menu_view.png
│       ├── calc_menu_edit.png
│       └── calc_menu_help.png
├── sap/                  # SAP GUI application images
│   ├── login/
│   │   ├── sap_username_field.png
│   │   ├── sap_password_field.png
│   │   ├── sap_language_field.png
│   │   ├── sap_client_field.png
│   │   └── sap_login_button.png
│   ├── navigation/
│   │   ├── sap_menu_bar.png
│   │   ├── sap_transaction_field.png
│   │   ├── sap_favorites.png
│   │   └── sap_user_menu.png
│   ├── buttons/
│   │   ├── sap_save.png
│   │   ├── sap_back.png
│   │   ├── sap_exit.png
│   │   ├── sap_cancel.png
│   │   ├── sap_create.png
│   │   ├── sap_change.png
│   │   ├── sap_display.png
│   │   └── sap_execute.png
│   ├── screens/
│   │   ├── sap_easy_access.png
│   │   ├── sap_va01_screen.png
│   │   ├── sap_mm01_screen.png
│   │   └── sap_status_bar.png
│   └── dialogs/
│       ├── sap_popup_ok.png
│       ├── sap_popup_cancel.png
│       ├── sap_confirmation.png
│       └── sap_error_dialog.png
├── mainframe/            # 3270/5250 terminal images
│   ├── screens/
│   │   ├── mf_login_screen.png
│   │   ├── mf_main_menu.png
│   │   ├── mf_customer_inquiry.png
│   │   ├── mf_order_entry.png
│   │   └── mf_report_screen.png
│   ├── indicators/
│   │   ├── mf_insert_mode.png
│   │   ├── mf_caps_lock.png
│   │   ├── mf_num_lock.png
│   │   ├── mf_error_indicator.png
│   │   └── mf_session_indicator.png
│   ├── fields/
│   │   ├── mf_userid_field.png
│   │   ├── mf_password_field.png
│   │   ├── mf_command_line.png
│   │   └── mf_message_area.png
│   └── function_keys/
│       ├── mf_f3_exit.png
│       ├── mf_f4_prompt.png
│       ├── mf_f7_up.png
│       ├── mf_f8_down.png
│       └── mf_f12_cancel.png
├── common/               # Common UI elements across applications
│   ├── buttons/
│   │   ├── ok_button.png
│   │   ├── cancel_button.png
│   │   ├── yes_button.png
│   │   ├── no_button.png
│   │   ├── apply_button.png
│   │   ├── close_button.png
│   │   └── help_button.png
│   ├── windows/
│   │   ├── minimize_button.png
│   │   ├── maximize_button.png
│   │   ├── restore_button.png
│   │   └── close_x_button.png
│   ├── dialogs/
│   │   ├── error_icon.png
│   │   ├── warning_icon.png
│   │   ├── info_icon.png
│   │   └── question_icon.png
│   └── controls/
│       ├── dropdown_arrow.png
│       ├── checkbox_checked.png
│       ├── checkbox_unchecked.png
│       ├── radio_selected.png
│       └── radio_unselected.png
└── screenshots/          # Captured screenshots during test execution
    ├── test_results/
    ├── error_captures/
    └── debug_images/
```

## 🖼️ Image Capture Guidelines

### Best Practices for Template Images

1. **Resolution and DPI**
   - Capture at native screen resolution (typically 96 or 120 DPI)
   - Avoid scaling or resizing after capture
   - Use consistent DPI across all template images

2. **Image Format**
   - Save as PNG format (lossless compression)
   - Avoid JPEG (lossy compression affects matching accuracy)
   - Keep original bit depth (24-bit color recommended)

3. **Content Guidelines**
   - Capture minimal required area (faster matching)
   - Include slight padding around target element
   - Avoid including unnecessary background elements
   - Ensure good contrast between element and background

4. **Naming Convention**
   ```
   [application]_[category]_[element]_[state].png
   
   Examples:
   - calc_button_5.png
   - sap_dialog_save_button.png
   - mf_screen_main_menu.png
   - common_button_ok.png
   ```

5. **Multiple Variants**
   - Create variants for different themes (light/dark)
   - Include different states (normal/hover/pressed)
   - Capture for different screen resolutions if needed

### Capture Tools

#### Windows Snipping Tool
```powershell
# Launch Snipping Tool
snippingtool

# Or use keyboard shortcut
# Windows Key + Shift + S
```

#### PowerShell Screenshot
```powershell
Add-Type -AssemblyName System.Windows.Forms
Add-Type -AssemblyName System.Drawing

$bounds = [System.Windows.Forms.Screen]::PrimaryScreen.Bounds
$bmp = New-Object System.Drawing.Bitmap $bounds.Width, $bounds.Height
$graphics = [System.Drawing.Graphics]::FromImage($bmp)
$graphics.CopyFromScreen($bounds.X, $bounds.Y, 0, 0, $bounds.Size)
$bmp.Save("screenshot.png")
$graphics.Dispose()
$bmp.Dispose()
```

#### SikuliX IDE (Recommended)
- Download SikuliX IDE for precise image capture
- Built-in region selection and preview
- Automatic optimization for image matching

## 🔧 Image Optimization

### Preprocessing for Better Matching

1. **Contrast Enhancement**
   - Improve contrast for better edge detection
   - Useful for low-contrast UI elements

2. **Noise Reduction**
   - Remove visual noise from screenshots
   - Apply Gaussian blur if needed

3. **Color Space Conversion**
   - Convert to grayscale for text matching
   - Use HSV for color-sensitive elements

### ImageUtils Configuration
```java
// Example preprocessing options
ImageUtils.preprocessForMatching(image, true, true, 1.5f);
// Parameters: image, enhanceContrast, reduceNoise, scaleFactor
```

## 📝 Template Image Validation

### Quality Checklist

- [ ] Image saved in PNG format
- [ ] Appropriate file naming convention used
- [ ] Minimal required area captured
- [ ] Good contrast and clarity
- [ ] No visual artifacts or compression
- [ ] Consistent with application theme
- [ ] Proper directory organization

### Testing Template Images
```java
// Test image matching before using in tests
ImageMatcher matcher = new ImageMatcher();
File screenshot = screenCapture.captureScreen();
File template = new File("images/calculator/calc_5.png");
Rectangle match = matcher.findImage(screenshot, template);

if (match != null) {
    System.out.println("Image found at: " + match);
} else {
    System.out.println("Image not found - check template quality");
}
```

## 🎯 Application-Specific Guidelines

### Calculator Application
- Capture number buttons individually
- Include operator buttons (+, -, *, /)
- Capture display area for result verification
- Include memory function buttons if needed

### SAP GUI Application
- Capture login screen elements separately
- Include transaction code entry field
- Capture common toolbar buttons
- Include status bar indicators
- Create variants for different SAP themes

### Mainframe Terminal
- Capture screen layouts (login, menus)
- Include function key indicators
- Capture field labels and input areas
- Include session status indicators
- Account for different terminal emulators

### Common Elements
- Standard Windows dialog buttons
- File dialog elements
- Common error/warning icons
- Window control buttons (min/max/close)

## 🔍 Troubleshooting Image Issues

### Common Problems and Solutions

1. **Image Not Found**
   - Check image file path and name
   - Verify image exists in correct directory
   - Ensure case-sensitive naming is correct

2. **Low Matching Accuracy**
   - Adjust similarity threshold in configuration
   - Recapture image at better quality
   - Check for screen scaling differences

3. **Multiple Matches Found**
   - Capture more specific image region
   - Increase similarity threshold
   - Use region-based matching

4. **Inconsistent Matching**
   - Create multiple template variants
   - Account for different UI states
   - Use color-independent matching for text

### Debug Image Matching
Enable debug mode to save intermediate matching results:

```properties
debug.enabled=true
debug.screenshots=true
image.debug.save.matches=true
```

This will save debug images showing:
- Original screenshot
- Template image
- Matching results overlay
- Similarity scores

## 📊 Performance Considerations

### Image Size Optimization
- Keep template images as small as possible
- Larger images = slower matching
- Aim for 50x50 to 200x200 pixels when possible

### Caching Strategy
- Cache frequently used templates in memory
- Load templates once at test startup
- Use lazy loading for less common images

### Parallel Processing
- Use region-based matching when possible
- Process multiple images in parallel
- Optimize image preprocessing pipeline

## 🔄 Maintenance and Updates

### Regular Maintenance Tasks
- Review and update outdated template images
- Add new templates for UI changes
- Clean up unused template files
- Validate template quality periodically

### Version Control
- Include template images in version control
- Use meaningful commit messages for image updates
- Tag image sets for different application versions
- Document breaking changes in image templates

### Documentation Updates
- Keep this documentation current
- Document any application-specific requirements
- Update troubleshooting section with new issues
- Maintain naming convention consistency

---

**Note**: Template images are crucial for reliable automation. Invest time in creating high-quality, consistent template images for best results.
