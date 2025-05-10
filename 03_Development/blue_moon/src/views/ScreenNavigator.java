package views;

import java.util.Stack;

public class ScreenNavigator {
	private static final Stack<BaseScreenHandler> screenStack = new Stack<>();
	
	// Push new screen into stack
	public static void push(BaseScreenHandler screen) {
		System.out.println(screen + "\n");
		screenStack.push(screen);
	}
	
	// Back to previous screen
	public static void goBack() {
		if (screenStack.size() <= 1) return;
		
		screenStack.pop();
		
		// Get the previous screen
		BaseScreenHandler prev = screenStack.peek();
		prev.show();
	}
	
	// Get the current screen (if necessary)
	public static BaseScreenHandler currentScreen() {
		return screenStack.isEmpty() ? null : screenStack.peek();
	}
	
	// Clear stack
	public static void clear() {
		screenStack.clear();
	}
}
