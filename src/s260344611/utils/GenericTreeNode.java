package s260344611.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GenericTreeNode<T> {

    public T data;
    public int val;
    public List<GenericTreeNode<T>> children;
    public GenericTreeNode<T> parent;

    public GenericTreeNode() {
        super();
        children = new ArrayList<GenericTreeNode<T>>();
    }

    public GenericTreeNode(T data, GenericTreeNode<T> parent, int val) {
        this();
        setData(data);
        setParent(parent);
        setValue(val);
    }

    public List<GenericTreeNode<T>> getChildren() {
        return this.children;
    }

    public int getNumberOfChildren() {
        return getChildren().size();
    }

    public boolean hasChildren() {
        return (getNumberOfChildren() > 0);
    }

    public void setChildren(List<GenericTreeNode<T>> children) {
        this.children = children;
    }

    public void addChild(GenericTreeNode<T> child) {
        children.add(child);
    }

    public void addChildAt(int index, GenericTreeNode<T> child) throws IndexOutOfBoundsException {
        children.add(index, child);
    }

    public void removeChildren() {
        this.children = new ArrayList<GenericTreeNode<T>>();
    }

    public void removeChildAt(int index) throws IndexOutOfBoundsException {
        children.remove(index);
    }

    public GenericTreeNode<T> getChildAt(int index) throws IndexOutOfBoundsException {
        return children.get(index);
    }
    
    public GenericTreeNode<T> getParent() {
        return this.parent;
    }

    public void setParent(GenericTreeNode<T> parent) {
        this.parent = parent;
    }

    public T getData() {
        return this.data;
    }

    public void setData(T data) {
        this.data = data;
    }
    
    public int getValue() {
        return this.val;
    }

    public void setValue(int val) {
        this.val = val;
    }

    public String toString() {
        return getData().toString();
    }

    public boolean equals(GenericTreeNode<T> node) {
        return node.getData().equals(getData());
    }

    public int hashCode() {
        return getData().hashCode();
    }

    public String toStringVerbose() {
        String stringRepresentation = getData().toString() + ":[";

        for (GenericTreeNode<T> node : getChildren()) {
            stringRepresentation += node.getData().toString() + ", ";
        }

        //Pattern.DOTALL causes ^ and $ to match. Otherwise it won't. It's retarded.
        Pattern pattern = Pattern.compile(", $", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(stringRepresentation);

        stringRepresentation = matcher.replaceFirst("");
        stringRepresentation += "]";

        return stringRepresentation;
    }
}