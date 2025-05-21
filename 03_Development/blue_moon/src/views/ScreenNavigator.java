package views;

import java.util.Stack;

public class ScreenNavigator {
	private static final Stack<BaseScreenHandler> screenStack = new Stack<>();
	
	// Push new screen into stack
	public static void push(BaseScreenHandler screen) {
		screenStack.push(screen);
		System.out.println(screenStack + "\n"); // This line aims to test the process of navigation. Comment it if necessary 
	}
	
	// Back to previous screen
	public static void goBack() {
		if (screenStack.size() <= 1) return;
		
		System.out.println(screenStack + "\n"); // This line aims to test the process of navigation. Comment it if necessary
		screenStack.pop();

		// Get the previous screen
		BaseScreenHandler prev = screenStack.peek();
		prev.show();		
		screenStack.pop();
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
