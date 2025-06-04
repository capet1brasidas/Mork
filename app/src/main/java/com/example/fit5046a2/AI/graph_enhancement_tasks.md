# Graph Enhancement Tasks

## Pending Implementation Tasks

1. **UI Components**
   - [ ] Implement content card above chat box in AIChatPage.kt
   - [ ] Add GraphContextCard composable
   - [ ] Add loading and error states UI
   - [ ] Implement smooth transitions between graph and chat views

2. **UserContext Enhancement**
   - [ ] Add graph context to UserContext class:
     ```kotlin
     data class UserContext(
         // Existing fields...
         val currentGraphType: String = "",
         val currentGraphData: String = "",
         val graphInsights: GraphInsights? = null
     )
     ```
   - [ ] Create GraphInsights data class
   - [ ] Add graph type enum/constants

3. **GraphViewModel Integration**
   - [ ] Add JSON parsing for Gemini responses using kotlinx.serialization
   - [ ] Implement caching mechanism for graph descriptions
   - [ ] Add error handling and retry logic
   - [ ] Connect GraphViewModel to graph screens

4. **Chat Integration**
   - [ ] Update ChatViewModel to handle graph context
   - [ ] Modify chat prompts to include graph context
   - [ ] Add graph-specific chat suggestions
   - [ ] Store graph insights in chat history


## Current Implementation

### Temporary Solution
Currently using comment attribute in UserContext for graph context:
```kotlin
// Example usage in ChatViewModel
val graphContext = "Type: Bar Chart, Data: {performance: 85%, tasks: 12}"
```

### GraphViewModel Features
- State management for graph descriptions
- Gemini API integration for insights
- Basic error handling
- Loading state management

### Next Steps
1. Implement GraphContextCard composable
2. Add graph context to chat prompts
3. Test basic flow with temporary solution
4. Plan proper UserContext integration

### Future Considerations
- Persistent storage for graph insights
- Real-time updates
- Advanced analytics features
- Performance optimization
- Accessibility improvements
