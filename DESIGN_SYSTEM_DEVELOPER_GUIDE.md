# Design System - Developer Quick Reference

## 🎯 How to Use the Design System

This guide helps you maintain UI consistency across the app by using the centralized design system.

---

## 📐 Spacing Guidelines

### When to Use Each Dimension

| Dimension | Use Case | Example |
|-----------|----------|---------|
| `EXTRA_SMALL (4dp)` | Micro spacing between closely related items | Icon padding within button |
| `SMALL (8dp)` | Space between related items | Spacer between icon and text |
| `MEDIUM (16dp)` | Standard padding for content | Card padding, Section padding |
| `LARGE (24dp)` | Space between sections | Gap between form sections |
| `EXTRA_LARGE (32dp)` | Major spacing between main sections | Gap between screens |

### Padding Template

```kotlin
// Screen-level content
LazyColumn(
    modifier = Modifier
        .fillMaxSize()
        .padding(Dimensions.SCREEN_PADDING),  // 16.dp
    verticalArrangement = Arrangement.spacedBy(Dimensions.CARD_SPACING)  // 16.dp
)

// Card-level content
Card(
    modifier = Modifier.fillMaxWidth()
) {
    Column(
        modifier = Modifier.padding(Dimensions.CARD_PADDING)  // 12.dp
    ) {
        // Content here
    }
}

// Between items
Row(
    horizontalArrangement = Arrangement.spacedBy(Dimensions.SMALL)  // 8.dp
)
```

---

## 🎨 Color Guidelines

### Primary Color Palette

```kotlin
// Main Brand Color
Text(color = DeepGreen)           // Primary action, titles
Icon(tint = DeepGreen)            // Primary icons
Button(color = DeepGreen)         // Primary buttons

// Accent Color
Text(color = LeafGreen)           // Success, positive states
Icon(tint = LeafGreen)            // Success indicators
Surface(color = LeafGreen)        // Highlighted areas

// Light Accent (for selected states)
NavigationBar(
    colors = NavigationBarItemDefaults.colors(
        indicatorColor = LightGreen  // Selected indicator
    )
)
```

### Semantic Colors

```kotlin
// For different types of content
Text(color = SoilBrown)           // Secondary text, labels
Icon(tint = SoilBrown)            // Secondary icons

// For special states
Text(color = Amber)               // Warnings
Text(color = Red)                 // Errors
Text(color = LightGray)           // Disabled, placeholder text

// For specific screens
Text(color = HistoryBrown)        // History-related content
Text(color = InsightGreen)        // Insights-related content
Text(color = SoilAccent)          // Soil low-value indicator
```

---

## 📏 Icon Size Guidelines

```kotlin
// Small icons (labels, inline indicators)
Icon(
    modifier = Modifier.size(Dimensions.ICON_SIZE_SMALL)  // 20.dp
)

// Standard icons (most common)
Icon(
    modifier = Modifier.size(Dimensions.ICON_SIZE_MEDIUM)  // 32.dp
)

// Large icons (primary action, hero)
Icon(
    modifier = Modifier.size(Dimensions.ICON_SIZE_LARGE)  // 48.dp
)

// Extra large (splash, branding)
Image(
    modifier = Modifier.size(Dimensions.SPLASH_ICON_SIZE)  // 180.dp
)
```

---

## 🔲 Corner Radius Guidelines

```kotlin
// Tight, compact UI elements
RoundedCornerShape(Dimensions.CORNER_RADIUS_SMALL)    // 8.dp

// Standard cards and buttons
RoundedCornerShape(Dimensions.CORNER_RADIUS_MEDIUM)   // 12.dp

// Large cards, significant elements
RoundedCornerShape(Dimensions.CORNER_RADIUS_LARGE)    // 16.dp
```

---

## 📦 Component Size Guidelines

```kotlin
// Buttons
Button(
    modifier = Modifier.height(Dimensions.BUTTON_HEIGHT)  // 56.dp
)

// Quick action cards
Card(
    modifier = Modifier.height(Dimensions.CARD_HEIGHT)    // 130.dp
)

// Weather card
Card(
    modifier = Modifier.height(Dimensions.WEATHER_CARD_HEIGHT)  // 140.dp
)

// Upload box
Card(
    modifier = Modifier.height(Dimensions.UPLOAD_BOX_HEIGHT)    // 200.dp
)

// Bottom navigation bar
NavigationBar(
    modifier = Modifier.height(Dimensions.NAV_BAR_HEIGHT)  // 80.dp
)
```

---

## ✍️ Text Handling

### Always Handle Text Overflow

```kotlin
// For single-line text
Text(
    text = "Title",
    maxLines = 1,
    overflow = TextOverflow.Ellipsis
)

// For multi-line text
Text(
    text = "Description that might span multiple lines",
    maxLines = 2,
    overflow = TextOverflow.Ellipsis
)

// For centered text
Text(
    text = "Centered content",
    textAlign = TextAlign.Center
)
```

---

## ❌ What NOT to Do

```kotlin
// ❌ DON'T: Hardcode dimensions
Column(modifier = Modifier.padding(16.dp))  // WRONG

// ✅ DO: Use Dimensions
Column(modifier = Modifier.padding(Dimensions.MEDIUM))  // RIGHT

// ❌ DON'T: Use Color(0xFF...) hex codes
Text(color = Color(0xFF2E7D32))  // WRONG

// ✅ DO: Use color constants
Text(color = DeepGreen)  // RIGHT

// ❌ DON'T: Ignore text overflow
Text(text = "Long text")  // WRONG

// ✅ DO: Handle overflow
Text(
    text = "Long text",
    maxLines = 1,
    overflow = TextOverflow.Ellipsis
)  // RIGHT
```

---

## 🔧 Common Patterns

### Screen Layout Pattern

```kotlin
@Composable
fun MyScreen() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimensions.SCREEN_PADDING),
        verticalArrangement = Arrangement.spacedBy(Dimensions.CARD_SPACING)
    ) {
        // Items here
    }
}
```

### Card Pattern

```kotlin
Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(Dimensions.CORNER_RADIUS_LARGE),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(defaultElevation = Dimensions.CARD_ELEVATION)
) {
    Column(modifier = Modifier.padding(Dimensions.CARD_PADDING)) {
        // Content
    }
}
```

### Button Pattern

```kotlin
Button(
    onClick = { /* action */ },
    modifier = Modifier
        .fillMaxWidth()
        .height(Dimensions.BUTTON_HEIGHT),
    shape = RoundedCornerShape(Dimensions.CORNER_RADIUS_LARGE),
    colors = ButtonDefaults.buttonColors(containerColor = DeepGreen)
) {
    Text("Button Text", fontWeight = FontWeight.Bold)
}
```

### Icon Pattern

```kotlin
Icon(
    imageVector = Icons.Default.Home,
    contentDescription = "Home",
    tint = DeepGreen,
    modifier = Modifier.size(Dimensions.ICON_SIZE_LARGE)
)
```

---

## 🚀 Making Global Changes

### Change Button Height Everywhere

1. Open `ui/Dimensions.kt`
2. Change: `val BUTTON_HEIGHT = 56.dp`
3. Done! All buttons update automatically

### Change Primary Brand Color

1. Open `ui/theme/Color.kt`
2. Change: `val DeepGreen = Color(0xFF2E7D32)`
3. Done! All DeepGreen text and icons update

### Update All Card Spacing

1. Open `ui/Dimensions.kt`
2. Change: `val CARD_PADDING = 12.dp`
3. Done! All cards update automatically

---

## 📋 Checklist for New Screens

When creating a new screen, ensure:

- [ ] Column/Row uses `Arrangement.spacedBy(Dimensions.MEDIUM)` or similar
- [ ] Screen padding is `Dimensions.SCREEN_PADDING`
- [ ] Card padding is `Dimensions.CARD_PADDING`
- [ ] All colors are from the constants (no Color(0xFF...) hardcoding)
- [ ] All sizes use Dimensions constants
- [ ] All text has `maxLines` and `overflow` set
- [ ] Icons use appropriate `ICON_SIZE_*` constants
- [ ] Buttons use `Dimensions.BUTTON_HEIGHT`
- [ ] Corner radius uses `CORNER_RADIUS_*` constants

---

## 📚 Reference Files

- **Spacing System**: `app/src/main/java/com/example/krishimitra/ui/Dimensions.kt`
- **Color Palette**: `app/src/main/java/com/example/krishimitra/ui/theme/Color.kt`
- **Material Theme**: `app/src/main/java/com/example/krishimitra/ui/theme/Theme.kt`

---

## ❓ Questions?

Refer to these screens for implementation examples:
- `HomeScreen.kt` - Action cards and layouts
- `UploadScreen.kt` - Complex components
- `InputFormScreen.kt` - Form elements
- `ProfileScreen.kt` - List-based layouts

---

**Remember**: Consistency is key to professional UI! Always use the design system.


