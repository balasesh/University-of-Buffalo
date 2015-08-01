import java.util.Stack;

class Tree<T extends Comparable<T>> {
	public Tree(T v) {
		value = v;
		left = null;
		right = null;
	}

	public void insert(T v) {
		if (value.compareTo(v) == 0)
			return;
		if (value.compareTo(v) > 0)
			if (left == null)
				left = new Tree<T>(v);
			else
				left.insert(v);
		else if (value.compareTo(v) < 0)
			if (right == null)
				right = new Tree<T>(v);
			else
				right.insert(v);
	}

	protected T value;
	protected Tree<T> left;
	protected Tree<T> right;

	public T getValue() {
		return value;
	}

	public Tree<T> getLeft() {
		return left;
	}

	public Tree<T> getRight() {
		return right;
	}
}

class Iter<T extends Comparable<T>> {

	Stack<Tree<T>> st;
	/*
	 * Creating a Constructor and pushing all nodes on
	 * the left to the stack until it reaches null
	 */
	public Iter(Tree<T> tree) {
		st = new Stack<Tree<T>>();
		// Getting root value from tree.
		Tree<T> root  = tree;			
		while(root != null)
		{
			st.push(root);
			//System.out.println("Pushed stack:" + root.value);
			if(root.left != null)
				root = root.left;
			else
				break;
		}
	}
	/*
	 * Checks is the stack is empty and if 
	 * the traversal is done
	 */
	public boolean done() {
		if(st.isEmpty())
			return true;
		else
			return false;
	}
	/*
	 * Popping all the values from the stack before 
	 * a greater value is pushed onto the stack
	 */
	public Tree<T> Next(Tree<T> current) {
		Tree<T> ele = current;
		current = st.pop();
		current = current.right;
		while(current != null)
		{
			st.push(current);
			//System.out.println("Pushed stack:" + current.value);
			if(current.left != null)
				current = current.left;
			else
				break;
		}
		return ele;
	}
}

public  class GenericTreeEquality {

	static <T extends Comparable<T>> boolean equals(Tree<T> tree1, Tree<T> tree2) {

		Tree<T>popele1;
		Tree<T>popele2;

		Iter<T> t1 = new Iter<T>(tree1);
		Iter<T> t2 = new Iter<T>(tree2);
		while(t1.done() == t2.done())
		{
			/*
			 * checking for the top of the stack and 
			 * popping out if the conditions are satisfied
			 */
			popele1 = t1.Next(t1.st.peek());
			popele2 = t2.Next(t2.st.peek());

			if(popele1.value.compareTo(popele2.value) == 0 )
			{
				//System.out.println("Popped Stack: "+popele1.value+" && "+popele2.value);
				if(t1.st.isEmpty() && t2.st.isEmpty())
					break;
			}
			else
				return false;
		}
		return true;
	}

	public static void main(String[] args) {

		Tree<Integer> tree1 = new Tree<Integer>(50);
		tree1.insert(70);
		tree1.insert(80);	
		tree1.insert(90);
		tree1.insert(100);

		Tree<Integer> tree2 = new Tree<Integer>(100);
		tree2.insert(90);
		tree2.insert(80);	
		tree2.insert(70);
		tree2.insert(50);

		System.out.println(equals(tree1, tree2 ));
		
		/* In Case of testing and printing the pushed and popped values
		 * Uncomment the Sysouts
		 * Tested for different sequences of same elements
		 * and tested for different elemnets of different 
		 * trees of varying sizes
		 */
	}
}