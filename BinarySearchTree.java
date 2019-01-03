import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Stack;

import javax.management.InstanceNotFoundException;

/**
 * 
 * Implementation of most of the Set interface operations using a Binary Search
 * Tree
 *
 * @author Joy Stockwell in CSSE 230 of Matt Boutell and Claude Anderson; consulted with Jim Stockwell
 *         referenced website: only read description, didn't look at their code:
 *         http://n00tc0d3r.blogspot.com/2013/08/implement-iterator-for-binarytree-ii.html
 * @param <T>
 */

// tree gets bigger moving left to right
public class BinarySearchTree<T extends Comparable<T>> implements Iterable<T> {
	private BinaryNode root;
	//will get set to true every time modification occurs
	//even if iterator not yet made
	//but set back to false after iterator made
	//so then, if it equals true after being set back to false,
	//you know the modification took place after the iterator was made
	boolean modifiedAfterIter = false;

	// Most of you will prefer to use NULL NODES once you see how to use them.
	private final BinaryNode NULL_NODE = new BinaryNode();

	public BinarySearchTree() {
		root = NULL_NODE;
	}

	// For manual tests only
	void setRoot(BinaryNode n) {
		this.modifiedAfterIter = true;
		this.root = n;
	}

	// Not private, since we need access for manual testing.
	class BinaryNode {
		private T data;
		private BinaryNode left;
		private BinaryNode right;

		public BinaryNode() {
			this.data = null;
			this.left = null;
			this.right = null;
		}

		public BinaryNode(T element) {
			this.data = element;
			this.left = NULL_NODE;
			this.right = NULL_NODE;
		}

		/**
		 * says whether the item is in the subtree starting with this node
		 * 
		 * @param item
		 * @return
		 */
		public boolean contains(T item) {
			int comp;
			try {
				comp = this.data.compareTo(item);
			} catch (NullPointerException e) {
				return false;
			}
			if (comp == 0) {
				return true;
			} else if (comp < 0) {
				return this.right.contains(item);
			} else {
				return this.left.contains(item);
			}
		}

		public T getData() {
			return this.data;
		}

		public BinaryNode getLeft() {
			return this.left;
		}

		public BinaryNode getRight() {
			return this.right;
		}

		// For manual testing
		public void setLeft(BinaryNode left) {
			this.left = left;
		}

		public void setRight(BinaryNode right) {
			this.right = right;
		}

		public int size() {
			if (this == NULL_NODE) {
				return 0;
			}
			return 1 + this.left.size() + this.right.size();
		}

		public int height() {
			if (this == NULL_NODE) {
				return -1;
			}
			return Math.max(1 + this.left.height(), 1 + this.right.height());
		}

		/**
		 * Tells whether item is in subtree starting with this node
		 * 
		 * @param item
		 * @return
		 */
		public boolean containsNonBST(T item) {
			if (this.data == item) {
				return true;
			}
			if (this == NULL_NODE)
				return false;
			return left.containsNonBST(item) | right.containsNonBST(item);
		}

		/**
		 * Returns empty arraylist if this node is NULL_NODE, otherwise returns
		 * arraylist of elements found by InOrder traversal
		 *
		 * @return
		 */
		public ArrayList<T> toArrayList() {
			ArrayList<T> ar = new ArrayList<>();
			if (this == NULL_NODE) {
				return ar;
			}
			ar.addAll(this.getLeft().toArrayList());
			ar.add(this.data);
			ar.addAll(this.getRight().toArrayList());
			return ar;
		}

		/**
		 * Returns empty arraylist if this node is NULL_NODE, otherwise returns
		 * arraylist of elements found by PreOrder traversal
		 * 
		 * @return
		 */
		public ArrayList<T> toPreorderArrayList() {
			ArrayList<T> ar = new ArrayList<T>();
			if (this == NULL_NODE) {
				return ar;
			}
			ar.add(this.data);
			ar.addAll(this.getLeft().toPreorderArrayList());
			ar.addAll(this.getRight().toPreorderArrayList());
			return ar;
		}

		/**
		 * Puts a node in its proper place in the binary tree Returns the
		 * inserted node.
		 * 
		 * @param item
		 * @param isModified
		 * @return
		 */
		public BinaryNode insert(T item, BooleanContainer isModified) {
			if (this == NULL_NODE) {
				isModified.setTrue();
				return new BinaryNode(item);
			}
			int comp = item.compareTo(this.data);
			// puts the item further to the left if it is smaller than the
			// current item
			if (comp < 0) {
				this.left = this.left.insert(item, isModified);
			}
			// puts the item further to the right if it is greater than the
			// current item
			else if (comp > 0) {
				this.right = this.right.insert(item, isModified);
			} else {
				isModified.setFalse();
			}
			return this;
		}

		/**
		 * removes a node from the subtree whose root is t,
		 * not including t, since t is the parameter and therefore
		 * cannot be altered
		 *
		 * @param item
		 * @return
		 * @throws InstanceNotFoundException 
		 */
		public BinaryNode remove(T item, BinaryNode t, BooleanContainer b) throws InstanceNotFoundException {
			if(t == BinarySearchTree.this.NULL_NODE)
			{
				throw new InstanceNotFoundException();
			}
				
			if (item.compareTo(t.getData()) < 0) {
				if(t.left != NULL_NODE)
					t.left = remove(item, t.left, b);
				else
				{
					b.setFalse();
					return BinarySearchTree.this.NULL_NODE;
				}
			} else if (item.compareTo(t.getData()) > 0){
				if(t.right != NULL_NODE)
					t.right = remove(item, t.right, b);
				else
				{
					b.setFalse();
					return BinarySearchTree.this.NULL_NODE;
				}
			}
			//if the item of interest is contained in the current BinaryNode
			else {
				//if there are two children
				if(t.left != NULL_NODE && t.right != NULL_NODE)
				{
					t.data = findMax(t.left).data;
					t.left = removeMax(t.left);
				}
				//if there is one child
				else if (t.left != NULL_NODE)
				{
					return t.left;
				}
				else if (t.right != NULL_NODE)
				{
					return t.right;
				}
				return NULL_NODE;
			}
			return t;
		}
		
		/**
		 * returns the node with the maximum value in the subtree whose root is t
		 * including t in the search for the max
		 */
		public BinaryNode findMax(BinaryNode t) {
			BinaryNode max = t;
			// if there are right children, travel to the last one
			// this last is the max
			if (max != NULL_NODE)
				while (max.right != BinarySearchTree.this.NULL_NODE) {
					max = max.right;
				}
			// if there are no right children, then the current will be the max
			return max;
		}

		/**
		 * Finds the node of max data within the subtree whose root is t helper
		 * method to remove method above
		 *
		 * @return the greatest node in the subtree
		 */
		public BinaryNode removeMax(BinaryNode t) {
			if (t.right != BinarySearchTree.this.NULL_NODE) {
				// copies into t's right child the data of t's right child's
				// greatest child
				// note that t itself remains unchanged
				// t is changed in the remove method
				t.right = removeMax(t.right);
				return t;
			}
			
			return t.left;
		}

	}

	// end of Node class
	
	/**
	 * Removes the node with the item indicated
	 * 
	 * @param item
	 * @return
	 */
	public boolean remove(T item) {
		if(item == null)
			throw new IllegalArgumentException();
		this.modifiedAfterIter = true;
		BooleanContainer b = new BooleanContainer(true);
		if(this.root == this.NULL_NODE)
			return false;
		//if this if clause is entered, true should be returned, so no change made to b
		if(this.root.getData().equals(item))
		{
			if(this.root.left != this.NULL_NODE)
			{
				this.root.data = this.root.findMax(this.root.left).data;
				this.root.left = this.root.removeMax(this.root.left);
			}
			else if (this.root.right != this.NULL_NODE)
			{
				this.root = this.root.right;
			}
			else
				this.root = this.NULL_NODE;
		} else
			try {
				this.root.remove(item, this.root, b);
			} catch (InstanceNotFoundException exception) {
				return false;
			}
		return b.getValue();
	}

	/**
	 * Iterator that needs the tree to be turned into an ArrayList before it
	 * works
	 * 
	 * @author stockwja. Created Dec 30, 2017.
	 */
	private class ArrayListIterator implements Iterator<T> {

		private ArrayList<T> ar;
		private int position;

		public ArrayListIterator() {
			BinarySearchTree.this.modifiedAfterIter = false;
			this.ar = BinarySearchTree.this.root.toArrayList();
			this.position = 0;
		}

		@Override
		public boolean hasNext() {
			return this.position < this.ar.size();
		}

		@Override
		public T next() {
			if(BinarySearchTree.this.modifiedAfterIter == true)
				throw new ConcurrentModificationException();
			if (!this.hasNext())
				throw new NoSuchElementException();
			return this.ar.get(this.position++);
		}

	}

	public Iterator<T> inefficientIterator() {
		BinarySearchTree.this.modifiedAfterIter = false;
		return new ArrayListIterator();
	}

	/**
	 * iterator that traverses PreOrder
	 * Doesn't need tree turned into ArrayList
	 * Lazy, except for exception checking
	 * @author stockwja. Created Dec 30, 2017.
	 */
	private class PreOrderIterator implements Iterator<T> {

		private int position;
		private BinaryNode curr;
		private Stack<BinaryNode> stack = new Stack<>();
		private boolean needToCallNext = true;

		public PreOrderIterator() {
			BinarySearchTree.this.modifiedAfterIter = false;
			this.position = 0;
			this.curr = BinarySearchTree.this.root;
		}

		@Override
		public boolean hasNext() {
			return this.position < BinarySearchTree.this.root.size();
		}

		@Override
		public T next() {
			this.needToCallNext = false;
			if(BinarySearchTree.this.modifiedAfterIter == true)
				throw new ConcurrentModificationException();
			if (!this.hasNext())
				throw new NoSuchElementException();
			if (this.position == 0) {
				this.position++;
				return this.curr.getData();
			}
			this.position++;
			// If there is one, pushes right nodes as it goes down left branch
			if (this.curr.right != BinarySearchTree.this.NULL_NODE) {
				this.stack.push(this.curr.right);
			}
			// Goes to left child of current node if there is one
			if (this.curr.left != BinarySearchTree.this.NULL_NODE) {
				this.curr = this.curr.left;
			}
			// Pops stack if no children
			else {
				this.curr = this.stack.pop();
			}
			return this.curr.getData();
		}
		
		public void remove() {
			if(this.needToCallNext == true)
				throw new IllegalStateException();
			BinarySearchTree.this.remove(this.curr.getData());
			this.needToCallNext = true;
		}
	}

	public Iterator<T> preOrderIterator() {
		BinarySearchTree.this.modifiedAfterIter = false;
		return new PreOrderIterator();
	}

	/**
	 * Iterator that traverses InOrder
	 *
	 * @author stockwja. Created Dec 30, 2017.
	 */
	private class InOrderIterator implements Iterator<T> {

		private int position;
		private BinaryNode curr;
		private Stack<BinaryNode> stack = new Stack<>();
		private boolean needToCallNext = true;

		public InOrderIterator() {
			BinarySearchTree.this.modifiedAfterIter = false;
			this.position = 0;
			this.curr = BinarySearchTree.this.root;
			pushLefts(BinarySearchTree.this.root);
		}
		
		public void pushLefts(BinaryNode t)
		{
			BinaryNode x = t;
			while(x != BinarySearchTree.this.NULL_NODE)
			{
				this.stack.push(x);
				x = x.left;
			}
		}

		@Override
		public boolean hasNext() {
			return !this.stack.isEmpty();
		}

		// ret booleans set right before returning
		// refer to what should be done next time loop starts
		@Override
		public T next() {
			this.needToCallNext = false;
			if(BinarySearchTree.this.modifiedAfterIter == true)
				throw new ConcurrentModificationException();
			BinaryNode retVal;
			if(!this.stack.isEmpty()) {
				retVal = this.stack.pop();
				if(retVal.right != BinarySearchTree.this.NULL_NODE)
					pushLefts(retVal.right);
				return retVal.getData();
			}
			else {
				throw new NoSuchElementException();
			}
			
		}
		
		public void remove() {
			if(this.needToCallNext == true)
				throw new IllegalStateException();
			BinarySearchTree.this.remove(this.curr.getData());
			this.needToCallNext = true;
		}

	}

	@Override
	public Iterator<T> iterator() {
		BinarySearchTree.this.modifiedAfterIter = false;
		return new InOrderIterator();
	}

	public boolean isEmpty() {
		return (this.root == NULL_NODE);
	}

	public int size() {
		return this.root.size();
	}

	public int height() {
		return this.root.height();
	}

	public boolean containsNonBST(T item) {
		return this.root.containsNonBST(item);
	}

	public boolean contains(T item) {
		return this.root.contains(item);
	}

	/**
	 * Puts item into tree by calling BinaryNode's insert() method on the root
	 * Returns true if item was sucessfully inserted, false if not (i.e. the
	 * node was already there).
	 * 
	 * @param item
	 * @return
	 */
	public boolean insert(T item) {
		this.modifiedAfterIter = true;
		if (item == null)
			throw new IllegalArgumentException();

		BooleanContainer isModified = new BooleanContainer(false);
		this.root = this.root.insert(item, isModified);
		return isModified.getValue();
	}

	/**
	 * Object passed into BinaryNode's insert() method so that a record of
	 * whether or not the insertion was done can be kept without making a
	 * boolean to keep track of the insertion be the return value of the
	 * BinaryNode's insert() method
	 * 
	 * @author stockwja. Created Dec 30, 2017.
	 */
	private class BooleanContainer {
		private boolean value;

		public BooleanContainer(boolean value) {
			this.value = value;
		}

		public boolean getValue() {
			return this.value;
		}

		public void setTrue() {
			this.value = true;
		}

		public void setFalse() {
			this.value = false;
		}
	}

	public ArrayList<T> toArrayList() {
		return this.root.toArrayList();
	}

	public Object[] toArray() {
		return this.root.toArrayList().toArray();
	}

	@Override
	public String toString() {
		String result = this.root.toArrayList().toString();
		// System.out.println(result);
		// same as System.out.println(this.root.toPreorderArrayList().toString());
		return result;

	}

}
