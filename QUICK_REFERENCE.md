# 🎨 Design System - Quick Reference Card

## At a Glance

| Element | Usage | Example |
|---------|-------|---------|
| **Padding** | `Dimensions.MEDIUM` | `padding(Dimensions.SCREEN_PADDING)` |
| **Colors** | `DeepGreen`, `LeafGreen` | `color = DeepGreen` |
| **Icons** | `Dimensions.ICON_SIZE_MEDIUM` | `size(Dimensions.ICON_SIZE_LARGE)` |
| **Buttons** | `Dimensions.BUTTON_HEIGHT` | `height(Dimensions.BUTTON_HEIGHT)` |
| **Radius** | `CORNER_RADIUS_LARGE` | `RoundedCornerShape(Dimensions.CORNER_RADIUS_LARGE)` |
| **Text** | `overflow = TextOverflow.Ellipsis` | `maxLines = 1, overflow = TextOverflow.Ellipsis` |

---

## ✅ Do's & Don'ts

### ✅ DO
```kotlin
// Spacing
.padding(Dimensions.MEDIUM)
Spacer(width = Dimensions.SMALL)

// Colors
Text(color = DeepGreen)
Icon(tint = LeafGreen)

// Text
Text(maxLines = 1, overflow = TextOverflow.Ellipsis)
```

### ❌ DON'T
```kotlin
// Spacing
.padding(16.dp)
Spacer(width = 8.dp)

// Colors
Text(color = Color(0xFF2E7D32))
Icon(tint = Color.Green)

// Text
Text(text = "Long text")  // No overflow handling
```

---

## Quick Copy-Paste Templates

### Screen Template
```kotlin
LazyColumn(
    modifier = Modifier
        .fillMaxSize()
        .padding(Dimensions.SCREEN_PADDING),
    verticalArrangement = Arrangement.spacedBy(Dimensions.CARD_SPACING)
)
```

### Card Template
```kotlin
Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(Dimensions.CORNER_RADIUS_LARGE),
    elevation = CardDefaults.cardElevation(defaultElevation = Dimensions.CARD_ELEVATION)
) {
    Column(modifier = Modifier.padding(Dimensions.CARD_PADDING)) {
        // Content
    }
}
```

### Button Template
```kotlin
Button(
    modifier = Modifier
        .fillMaxWidth()
        .height(Dimensions.BUTTON_HEIGHT),
    shape = RoundedCornerShape(Dimensions.CORNER_RADIUS_LARGE),
    colors = ButtonDefaults.buttonColors(containerColor = DeepGreen)
) {
    Text("Button Text", fontWeight = FontWeight.Bold)
}
```

---

## Color Palette Reference

```
🟢 DeepGreen (0xFF2E7D32)        - Primary brand
🟢 LightGreen (0xFFDCEEDB)       - Selected states
🟤 SoilBrown (0xFF6D4C41)        - Secondary
🟢 LeafGreen (0xFF66BB6A)        - Success/positive
🟤 HistoryBrown (0xFF8B4513)     - History screen
🟢 InsightGreen (0xFF2E8B57)     - Insights screen
🟤 SoilAccent (0xFF916400)       - Low soil value
🟡 Amber (0xFFFFBF00)            - Warnings
🔴 Red (0xFFD32F2F)              - Errors
⚫ DarkCharcoal (0xFF263238)      - Text
```

---

## Spacing Quick Reference

| Size | Value | Uses |
|------|-------|------|
| EXTRA_SMALL | 4.dp | Micro spacing |
| SMALL | 8.dp | Between items |
| MEDIUM | 16.dp | Standard padding |
| LARGE | 24.dp | Section spacing |
| EXTRA_LARGE | 32.dp | Major spacing |

---

## Icon Sizes

| Size | Value | Uses |
|------|-------|------|
| SMALL | 20.dp | Inline icons |
| MEDIUM | 32.dp | Standard (most common) |
| LARGE | 48.dp | Primary action |
| EXTRA_LARGE | 64.dp | Hero/splash |

---

## Files to Remember

```
🎨 Dimensions:  ui/Dimensions.kt
🎨 Colors:      ui/theme/Color.kt
🎨 Theme:       ui/theme/Theme.kt
```

---

## Most Common Changes

### Change all button heights
```kotlin
// ui/Dimensions.kt
val BUTTON_HEIGHT = 56.dp  // ← Change this
```

### Change primary color
```kotlin
// ui/theme/Color.kt
val DeepGreen = Color(0xFF2E7D32)  // ← Change this
```

### Change card padding
```kotlin
// ui/Dimensions.kt
val CARD_PADDING = 12.dp  // ← Change this
```

---

## Text Overflow Pattern

**Always use this pattern:**
```kotlin
Text(
    text = "...",
    maxLines = 1,                              // Or 2 for descriptions
    overflow = TextOverflow.Ellipsis,
    textAlign = TextAlign.Center               // If needed
)
```

---

## Border Widths

```kotlin
BORDER_WIDTH_THIN = 1.dp      // Unselected state
BORDER_WIDTH_MEDIUM = 2.dp    // Selected state
```

---

## Corner Radius Values

```
SMALL   = 8.dp     (compact elements)
MEDIUM  = 12.dp    (cards, buttons)
LARGE   = 16.dp    (large cards)
```

---

## Import All Design System Items

```kotlin
// At the top of your screen file:
import com.example.krishimitra.ui.Dimensions
import com.example.krishimitra.ui.theme.*

// Now you can use:
Dimensions.MEDIUM
DeepGreen
LeafGreen
// ... etc
```

---

## 🚨 Red Flags (Things to Fix)

Spotted these? Replace them immediately:

```kotlin
❌ .padding(16.dp)           → .padding(Dimensions.MEDIUM)
❌ .height(56.dp)            → .height(Dimensions.BUTTON_HEIGHT)
❌ color = Color(0xFF...)    → color = DeepGreen
❌ size(48.dp)               → size(Dimensions.ICON_SIZE_LARGE)
❌ RoundedCornerShape(12.dp) → RoundedCornerShape(Dimensions.CORNER_RADIUS_MEDIUM)
❌ Text("...", maxLines = 1) → Text("...", maxLines = 1, overflow = TextOverflow.Ellipsis)
```

---

## Component Height Reference

```
BUTTON = 56.dp
CARD = 130.dp
WEATHER_CARD = 140.dp
UPLOAD_BOX = 200.dp
NAV_BAR = 80.dp
```

---

## Elevation Options

```kotlin
CARD_ELEVATION = 2.dp
NAV_BAR_ELEVATION = 8.dp
```

---

**Last Updated**: May 5, 2026
**Status**: ✅ Production Ready


