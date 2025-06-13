package views;

import java.util.Stack;

public class ScreenNavigator {
	private static final Stack<BaseScreenHandler> screenStack = new Stack<>();
	private static boolean isNavigatingBack = false;
	
	// Push new screen into stack
	public static void push(BaseScreenHandler screen) {
		screenStack.push(screen); 
	}
	
	// Back to previous screen
	public static void goBack() {
		if (screenStack.size() <= 1) return;
		
		screenStack.pop();
		
		isNavigatingBack = true;
		BaseScreenHandler prev = screenStack.peek();
		prev.show();
		isNavigatingBack = false;
	}
	
	public static BaseScreenHandler currentScreen() {
		return screenStack.isEmpty() ? null : screenStack.peek();
	}
	
	public static void clear() {
		screenStack.clear();
	}
	
	public static boolean isNavigatingBack() {
		return isNavigatingBack;
	}
}